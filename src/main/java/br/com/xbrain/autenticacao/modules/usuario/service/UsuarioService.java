package br.com.xbrain.autenticacao.modules.usuario.service;

import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.comum.dto.EmpresaResponse;
import br.com.xbrain.autenticacao.modules.comum.dto.PageRequest;
import br.com.xbrain.autenticacao.modules.comum.enums.CodigoEmpresa;
import br.com.xbrain.autenticacao.modules.comum.enums.CodigoUnidadeNegocio;
import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.comum.enums.Eboolean;
import br.com.xbrain.autenticacao.modules.comum.exception.ValidacaoException;
import br.com.xbrain.autenticacao.modules.comum.model.Empresa;
import br.com.xbrain.autenticacao.modules.comum.model.UnidadeNegocio;
import br.com.xbrain.autenticacao.modules.comum.repository.EmpresaRepository;
import br.com.xbrain.autenticacao.modules.comum.repository.UnidadeNegocioRepository;
import br.com.xbrain.autenticacao.modules.comum.service.EmailService;
import br.com.xbrain.autenticacao.modules.permissao.dto.FuncionalidadeResponse;
import br.com.xbrain.autenticacao.modules.permissao.filtros.FuncionalidadePredicate;
import br.com.xbrain.autenticacao.modules.permissao.model.CargoDepartamentoFuncionalidade;
import br.com.xbrain.autenticacao.modules.permissao.model.PermissaoEspecial;
import br.com.xbrain.autenticacao.modules.permissao.repository.CargoDepartamentoFuncionalidadeRepository;
import br.com.xbrain.autenticacao.modules.permissao.repository.PermissaoEspecialRepository;
import br.com.xbrain.autenticacao.modules.usuario.dto.*;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoDepartamento;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoFuncionalidade;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoNivel;
import br.com.xbrain.autenticacao.modules.usuario.model.*;
import br.com.xbrain.autenticacao.modules.usuario.predicate.UsuarioPredicate;
import br.com.xbrain.autenticacao.modules.usuario.rabbitmq.UsuarioAtualizacaoMqSender;
import br.com.xbrain.autenticacao.modules.usuario.rabbitmq.UsuarioCadastroMqSender;
import br.com.xbrain.autenticacao.modules.usuario.rabbitmq.UsuarioRecuperacaoMqSender;
import br.com.xbrain.autenticacao.modules.usuario.repository.*;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.NumberUtils;
import org.thymeleaf.context.Context;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class UsuarioService {

    private static final int RADIX = 36;
    private static final int POSICAO_ZERO = 0;
    private static final int MAX_CARACTERES_SENHA = 6;
    private static final ValidacaoException EX_NAO_ENCONTRADO = new ValidacaoException("Usuário não encontrado.");

    private final Logger log = LoggerFactory.getLogger(UsuarioService.class);

    @Getter
    @Autowired
    private UsuarioRepository repository;

    @Autowired
    private AutenticacaoService autenticacaoService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    @Autowired
    private MotivoInativacaoRepository motivoInativacaoRepository;

    @Autowired
    private CargoRepository cargoRepository;

    @Autowired
    private DepartamentoRepository departamentoRepository;

    @Autowired
    private NivelRepository nivelRepository;

    @Autowired
    private UnidadeNegocioRepository unidadeNegocioRepository;

    @Autowired
    private EmpresaRepository empresaRepository;

    @Autowired
    private CargoDepartamentoFuncionalidadeRepository cargoDepartamentoFuncionalidadeRepository;

    @Autowired
    private PermissaoEspecialRepository permissaoEspecialRepository;

    @Autowired
    private UsuarioCadastroMqSender usuarioMqSender;

    @Autowired
    private UsuarioRecuperacaoMqSender usuarioRecuperacaoMqSender;

    @Autowired
    private UsuarioAtualizacaoMqSender usuarioAtualizacaoMqSender;

    private Usuario findComplete(Integer id) {
        Usuario usuario = repository.findComplete(id).orElseThrow(() -> EX_NAO_ENCONTRADO);
        usuario.forceLoad();
        return usuario;
    }

    @Transactional
    public Usuario findById(int id) {
        UsuarioPredicate predicate = new UsuarioPredicate();
        predicate.ignorarAa();
        predicate.comId(id);
        Usuario usuario = repository.findOne(predicate.build());
        usuario.forceLoad();
        return usuario;
    }

    public Usuario findByIdComAa(int id) {
        UsuarioPredicate predicate = new UsuarioPredicate();
        predicate.comId(id);
        return repository.findOne(predicate.build());
    }

    public List<CidadeResponse> findCidadesByUsuario(int usuarioId) {
        Usuario usuario = repository.findComCidade(usuarioId).orElseThrow(() -> EX_NAO_ENCONTRADO);
        return usuario.getCidades()
                .stream()
                .map(c -> CidadeResponse.parse(c.getCidade()))
                .collect(Collectors.toList());
    }

    public Usuario findComHierarquia(int id) {
        return repository.findComHierarquia(id).orElseThrow(() -> EX_NAO_ENCONTRADO);
    }

    @Transactional
    public UsuarioDto findByEmail(String email) {
        return UsuarioDto.parse(repository.findByEmail(email).orElseThrow(() -> EX_NAO_ENCONTRADO));
    }

    public UsuarioResponse findByEmailAa(String email) {
        Optional<Usuario> usuarioOptional = repository.findByEmail(email);

        if (usuarioOptional.isPresent()) {
            return UsuarioResponse.convertFrom(usuarioOptional.get());
        }
        return null;
    }

    public UsuarioResponse findByCpfAa(String cpf) {
        Optional<Usuario> usuarioOptional = repository.findTop1UsuarioByCpf(cpf);

        if (usuarioOptional.isPresent()) {
            return UsuarioResponse.convertFrom(usuarioOptional.get());
        }
        return null;
    }

    public List<EmpresaResponse> findEmpresasDoUsuario(Integer idUsuario) {
        Usuario usuario = findComplete(idUsuario);
        return usuario.getEmpresas().stream().map(EmpresaResponse::convertFrom).collect(Collectors.toList());
    }

    public Page<Usuario> getAll(PageRequest pageRequest, UsuarioFiltros filtros) {
        UsuarioPredicate predicate = filtros.toPredicate();
        predicate.filtraPermitidos(autenticacaoService.getUsuarioAutenticado(), this);
        return repository.findAll(predicate.build(), pageRequest);
    }

    private UsuarioCidade criarUsuarioCidade(Usuario usuario, Integer idCidade) {
        return UsuarioCidade.criar(usuario, idCidade, autenticacaoService.getUsuarioId());
    }

    public UsuarioDto saveUsuarioConfiguracao(UsuarioConfiguracaoSaveDto usuarioHierarquiaSaveDto) {
        Usuario usuario = findComplete(usuarioHierarquiaSaveDto.getUsuarioId());
        Usuario usuarioAutenticado = autenticacaoService.getUsuarioAutenticado().getUsuario();
        if (usuario.hasConfiguracao()) {
            usuario.configurarRamal(usuarioHierarquiaSaveDto.getRamal());
        } else {
            usuario.setConfiguracao(
                    new Configuracao(
                            usuario, usuarioAutenticado, LocalDateTime.now(), usuarioHierarquiaSaveDto.getRamal()));
        }
        return UsuarioDto.parse(repository.save(usuario));
    }

    private UsuarioHierarquia criarUsuarioHierarquia(Usuario usuario, Integer idHierarquia) {
        return UsuarioHierarquia.criar(usuario, idHierarquia, autenticacaoService.getUsuarioId());
    }

    public List<Integer> getIdDosUsuariosPorCidade(Integer usuarioId) {
        return repository.getUsuariosPorCidade(usuarioId);
    }

    public List<Integer> getIdDosUsuariosSubordinados(Integer usuarioId, Boolean incluirProprio) {
        List<Integer> usuariosSubordinados = repository.getUsuariosSubordinados(usuarioId);
        if (incluirProprio) {
            usuariosSubordinados.add(usuarioId);
        }
        return usuariosSubordinados;
    }

    @Transactional
    public UsuarioDto save(UsuarioDto usuarioDto) {
        Usuario usuario = Usuario.parse(usuarioDto);
        validar(usuario);
        boolean enviarEmail = false;
        String senhaDescriptografada = getSenhaRandomica(MAX_CARACTERES_SENHA);
        if (usuario.isNovoCadastro()) {
            configurar(usuario, senhaDescriptografada);
            enviarEmail = true;
        }
        usuario = repository.save(usuario);
        adicionarUsuarioHierarquia(usuario, usuarioDto.getHierarquiasId());
        adicionarCidadeParaUsuario(usuario, usuarioDto.getCidadesId());
        repository.save(usuario);
        if (enviarEmail) {
            enviarEmailDadosDeAcesso(usuario, senhaDescriptografada);
        }
        return UsuarioDto.parse(usuario);
    }

    private void adicionarUsuarioHierarquia(Usuario usuario, List<Integer> hierarquiasId) {
        removerUsuarioSuperior(usuario, hierarquiasId);
        adicionarUsuarioSuperior(usuario, hierarquiasId);
        repository.save(usuario);
    }

    private void removerUsuarioSuperior(Usuario usuario, List<Integer> hierarquiasId) {
        if (CollectionUtils.isEmpty(hierarquiasId)) {
            usuario.getUsuariosHierarquia().clear();
        } else {
            usuario.getUsuariosHierarquia()
                    .removeIf(h -> !hierarquiasId.contains(h.getUsuarioSuperiorId()));
        }
    }

    private void adicionarUsuarioSuperior(Usuario usuario, List<Integer> hierarquiasId) {
        if (!CollectionUtils.isEmpty(hierarquiasId)) {
            hierarquiasId
                    .forEach(idHierarquia -> usuario.adicionarHierarquia(criarUsuarioHierarquia(usuario, idHierarquia)));
        }
    }

    private void adicionarCidadeParaUsuario(Usuario usuario, List<Integer> cidadesId) {
        if (!CollectionUtils.isEmpty(cidadesId)) {
            cidadesId.forEach(idCidade -> usuario.adicionarCidade(
                    criarUsuarioCidade(usuario, idCidade)));
            repository.save(usuario);
        }
    }

    private void configurar(Usuario usuario, String senhaDescriptografada) {
        usuario.setSenha(passwordEncoder.encode(senhaDescriptografada));
        usuario.setDataCadastro(LocalDateTime.now());
        usuario.setAlterarSenha(Eboolean.V);
        usuario.setSituacao(ESituacao.A);
        if (!usuario.hasUsuarioCadastro()) {
            usuario.setUsuarioCadastro(new Usuario(autenticacaoService.getUsuarioId()));
        }
    }

    @Transactional
    public void saveFromQueue(UsuarioMqRequest usuarioMqRequest) {
        try {
            UsuarioDto usuarioDto = UsuarioDto.parse(usuarioMqRequest);
            configurarUsuario(usuarioMqRequest, usuarioDto);
            usuarioDto = save(usuarioDto);
            enviarParaFilaDeUsuariosSalvos(usuarioDto);
        } catch (Exception ex) {
            usuarioMqRequest.setException(ex.getMessage());
            enviarParaFilaDeErro(usuarioMqRequest);
            log.error("Erro ao salvar usuário da fila.", ex);
        }
    }

    @Transactional
    public void atualizarUsuariosAgentesAutorizados(UsuarioMqAtualizacaoRequest usuariosAtualizacao) {
        try {
            usuariosAtualizacao.getUsuariosIds().forEach(u -> {
                Optional<Usuario> usuarioOptional = repository.findById(u);
                if (usuarioOptional.isPresent()) {
                    Usuario usuario = usuarioOptional.get();
                    atualizarEmpresas(usuario, usuariosAtualizacao.getEmpresasIds());
                    atualizarUnidadesNegocio(usuario, usuariosAtualizacao.getUnidadeId());
                } else {
                    log.error("Não foi possível atualizar o usuário: " + u + " - não encontrado");
                }
            });
        } catch (Exception ex) {
            enviarParaFiladeErrosUsuariosAtualizados(usuariosAtualizacao);
            log.error("Erro ao atualizar usuários da fila.", ex);
        }
    }

    private void atualizarEmpresas(Usuario usuario, List<Integer> empresasIds) {
        usuario.setEmpresas(empresasIds.stream().map(e -> empresaRepository.findOne(e)).collect(Collectors.toList()));
    }

    private void atualizarUnidadesNegocio(Usuario usuario, Integer unidadeId) {
        usuario.setUnidadesNegocios(Collections.singletonList(unidadeNegocioRepository.findOne(unidadeId)));
    }

    private void enviarParaFiladeErrosUsuariosAtualizados(UsuarioMqAtualizacaoRequest usuariosAtualizacao) {
        usuarioAtualizacaoMqSender.sendWithFailure(usuariosAtualizacao);
    }

    @Transactional
    public void recuperarUsuariosAgentesAutorizados(UsuarioMqRequest usuarioMqRequest) {
        try {
            Usuario usuario = repository.findOne(usuarioMqRequest.getId());
            usuario = usuario.parse(usuarioMqRequest);
            usuario.setEmpresas(empresaRepository.findByCodigoIn(usuarioMqRequest.getEmpresa()));
            usuario.setUnidadesNegocios(unidadeNegocioRepository.findByCodigoIn(usuarioMqRequest.getUnidadesNegocio()));
            usuario.setCargo(cargoRepository.findByCodigo(usuarioMqRequest.getCargo()));
            usuario.setDepartamento(departamentoRepository.findByCodigo(usuarioMqRequest.getDepartamento()));
            usuario.setAlterarSenha(Eboolean.V);

            String senhaDescriptografada = getSenhaRandomica(MAX_CARACTERES_SENHA);

            enviarEmailDadosDeAcesso(usuario, senhaDescriptografada);

            repository.updateSenha(passwordEncoder.encode(senhaDescriptografada), usuario.getId());
            repository.updateEmail(usuario.getEmail(), usuario.getId());

            repository.save(usuario);
        } catch (Exception ex) {
            enviarParaFiladeErrosUsuariosRecuperacao(usuarioMqRequest);
            log.error("Erro ao recuperar usuário da fila.", ex);
        }
    }

    private void enviarParaFiladeErrosUsuariosRecuperacao(UsuarioMqRequest usuarioMqRequest) {
        usuarioRecuperacaoMqSender.sendWithFailure(usuarioMqRequest);
    }

    private void enviarParaFilaDeUsuariosSalvos(UsuarioDto usuarioDto) {
        usuarioMqSender.sendSuccess(usuarioDto);
    }

    private void enviarParaFilaDeErro(UsuarioMqRequest usuarioMqRequest) {
        usuarioMqSender.sendWithFailure(usuarioMqRequest);
    }

    private void configurarUsuario(UsuarioMqRequest usuarioMqRequest, UsuarioDto usuarioDto) {
        configurarCargo(usuarioMqRequest, usuarioDto);
        configurarDepartamento(usuarioMqRequest, usuarioDto);
        configurarNivel(usuarioMqRequest, usuarioDto);
        configurarUnidadesNegocio(usuarioMqRequest, usuarioDto);
        configurarEmpresas(usuarioMqRequest, usuarioDto);
    }

    private void configurarCargo(UsuarioMqRequest usuarioMqRequest, UsuarioDto usuarioDto) {
        Cargo cargo = getCargo(usuarioMqRequest.getCargo());
        usuarioDto.setCargoId(cargo.getId());
    }

    private Cargo getCargo(CodigoCargo codigoCargo) {
        return cargoRepository.findByCodigo(codigoCargo);
    }

    private void configurarDepartamento(UsuarioMqRequest usuarioMqRequest, UsuarioDto usuarioDto) {
        Departamento departamento = departamentoRepository.findByCodigo(usuarioMqRequest.getDepartamento());
        usuarioDto.setDepartamentoId(departamento.getId());
    }

    private void configurarNivel(UsuarioMqRequest usuarioMqRequest, UsuarioDto usuarioDto) {
        Nivel nivel = nivelRepository.findByCodigo(usuarioMqRequest.getNivel());
        usuarioDto.setNivelId(nivel.getId());
    }

    private void configurarUnidadesNegocio(UsuarioMqRequest usuarioMqRequest, UsuarioDto usuarioDto) {
        List<UnidadeNegocio> unidadesNegocios = unidadeNegocioRepository
                .findByCodigoIn(usuarioMqRequest.getUnidadesNegocio());
        usuarioDto.setUnidadesNegociosId(unidadesNegocios.stream()
                .map(UnidadeNegocio::getId).collect(Collectors.toList()));
    }

    private void configurarEmpresas(UsuarioMqRequest usuarioMqRequest, UsuarioDto usuarioDto) {
        List<Empresa> empresas = empresaRepository.findByCodigoIn(usuarioMqRequest.getEmpresa());
        usuarioDto.setEmpresasId(empresas.stream().map(Empresa::getId).collect(Collectors.toList()));
    }

    private void validar(Usuario usuario) {
        validarCpfExistente(usuario);
        validarEmailExistente(usuario);
        usuario.removerCaracteresDoCpf();
        usuario.tratarEmails();
    }

    public void enviarEmailDadosDeAcesso(Usuario usuario, String senhaDescriptografada) {
        Context context = new Context();
        context.setVariable("nome", usuario.getNome());
        context.setVariable("email", usuario.getEmail());
        context.setVariable("senha", senhaDescriptografada);

        emailService.enviarEmailTemplate(
                Arrays.asList(usuario.getEmail()),
                "Nova Conta",
                "confirmacao-cadastro",
                context);
    }

    public String getSenhaRandomica(int size) {
        String tag = Long.toString(Math.abs(new Random().nextLong()), RADIX);
        return tag.substring(0, size);
    }

    private void validarCpfExistente(Usuario usuario) {
        usuario.removerCaracteresDoCpf();
        repository
                .findTop1UsuarioByCpf(usuario.getCpf())
                .ifPresent(u -> {
                    if (usuario.isNovoCadastro() || !u.getId().equals(usuario.getId())) {
                        throw new ValidacaoException("Cpf já cadastrado.");
                    }
                });
    }

    private void validarEmailExistente(Usuario usuario) {
        repository
                .findTop1UsuarioByEmailIgnoreCase(usuario.getEmail())
                .ifPresent(u -> {
                    if (usuario.isNovoCadastro() || !u.getId().equals(usuario.getId())) {
                        throw new ValidacaoException("Email já cadastrado.");
                    }
                });
    }

    @Transactional
    public void ativar(UsuarioAtivacaoDto dto) {
        Usuario usuario = findComplete(dto.getIdUsuario());
        usuario.setSituacao(ESituacao.A);
        usuario.adicionar(UsuarioHistorico.builder()
                .dataCadastro(LocalDateTime.now())
                .usuario(usuario)
                .usuarioAlteracao(new Usuario(autenticacaoService.getUsuarioId()))
                .observacao(dto.getObservacao())
                .situacao(ESituacao.A)
                .build());
        repository.save(usuario);
    }

    @Transactional
    public void inativar(UsuarioInativacaoDto dto) {
        Usuario usuario = findComplete(dto.getIdUsuario());
        usuario.setSituacao(ESituacao.I);
        MotivoInativacao motivoInativacao = carregarMotivoInativacao(dto);
        usuario.adicionar(UsuarioHistorico.builder()
                .dataCadastro(LocalDateTime.now())
                .motivoInativacao(motivoInativacao)
                .usuario(usuario)
                .usuarioAlteracao(new Usuario(autenticacaoService.getUsuarioId()))
                .observacao(dto.getObservacao())
                .situacao(ESituacao.I)
                .build());
        repository.save(usuario);
    }

    //TODO melhorar código
    private MotivoInativacao carregarMotivoInativacao(UsuarioInativacaoDto dto) {
        if (dto.getIdMotivoInativacao() != null) {
            return new MotivoInativacao(dto.getIdMotivoInativacao());
        }
        return motivoInativacaoRepository.findByCodigo(dto.getCodigoMotivoInativacao())
                .orElseThrow(() -> new ValidacaoException("Motivo de inativação não encontrado."));
    }

    public List<UsuarioHierarquiaResponse> getUsuariosHierarquia(Integer nivelId) {
        UsuarioPredicate usuarioPredicate = new UsuarioPredicate();
        usuarioPredicate.filtraPermitidos(autenticacaoService.getUsuarioAutenticado(), this);
        usuarioPredicate.comNivel(Collections.singletonList(nivelId));
        return ((List<Usuario>) repository.findAll(usuarioPredicate.build()))
                .stream()
                .map(UsuarioHierarquiaResponse::new)
                .collect(Collectors.toList());
    }

    public List<UsuarioDto> getUsuariosFiltros(UsuarioFiltrosDto usuarioFiltrosDto) {
        UsuarioPredicate usuarioPredicate = new UsuarioPredicate()
                .comEmpresas(usuarioFiltrosDto.getEmpresasIds())
                .comUnidadesNegocio(usuarioFiltrosDto.getUnidadesNegocioIds())
                .comNivel(usuarioFiltrosDto.getNivelIds())
                .comCargo(usuarioFiltrosDto.getCargoIds())
                .comDepartamento(usuarioFiltrosDto.getDepartamentoIds())
                .comCidade(usuarioFiltrosDto.getCidadesIds())
                .comIds(usuarioFiltrosDto.getUsuariosIds())
                .isAtivo(usuarioFiltrosDto.getAtivo());

        List<Usuario> usuarioList = repository.getUsuariosFilter(usuarioPredicate.build());

        return usuarioList.stream()
                .map(UsuarioDto::parse)
                .collect(Collectors.toList());
    }

    public List<UsuarioResponse> getUsuariosByIds(List<Integer> idsUsuarios) {
        List<Usuario> usuarios = repository.findBySituacaoAndIdIn(ESituacao.A, idsUsuarios);
        return usuarios.stream()
                .map(UsuarioResponse::convertFrom)
                .collect(Collectors.toList());
    }

    @Transactional
    public void alterarCargoUsuario(UsuarioAlteracaoRequest usuarioAlteracaoRequest) {
        repository.updateCargo(getCargo(usuarioAlteracaoRequest.getCargo()), usuarioAlteracaoRequest.getId());
    }

    @Transactional
    public void alterarEmailUsuario(UsuarioAlteracaoRequest usuarioAlteracaoRequest) {
        repository.updateEmail(usuarioAlteracaoRequest.getEmail(), usuarioAlteracaoRequest.getId());
    }

    public List<UsuarioResponse> getUsuariosSuperiores(UsuarioFiltrosHierarquia usuarioFiltrosHierarquia) {
        List<Object[]> objects = repository.getUsuariosSuperiores(usuarioFiltrosHierarquia);
        return objects.stream().map(this::criarUsuarioResponse).collect(Collectors.toList());
    }

    private UsuarioResponse criarUsuarioResponse(Object[] param) {
        int indice = POSICAO_ZERO;
        return UsuarioResponse.builder()
                .id(objectToInteger(param[indice++]))
                .nome(objectToString(param[indice++]))
                .cpf(objectToString(param[indice++]))
                .email(objectToString(param[indice++]))
                .codigoNivel(CodigoNivel.valueOf(objectToString(param[indice++])))
                .codigoDepartamento(CodigoDepartamento.valueOf(objectToString(param[indice++])))
                .codigoCargo(CodigoCargo.valueOf(objectToString(param[indice++])))
                .codigoEmpresas(tratarEmpresas(param[indice++]))
                .codigoUnidadesNegocio(tratarUnidadesNegocios(param[indice]))
                .build();
    }

    private List<CodigoEmpresa> tratarEmpresas(Object arg) {
        return Arrays.stream(objectToString(arg).split(","))
                .map(CodigoEmpresa::valueOf).collect(Collectors.toList());
    }

    private List<CodigoUnidadeNegocio> tratarUnidadesNegocios(Object arg) {
        return Arrays.stream(objectToString(arg).split(","))
                .map(CodigoUnidadeNegocio::valueOf).collect(Collectors.toList());
    }

    private Integer objectToInteger(Object arg) {
        return NumberUtils.parseNumber(arg.toString(), Integer.class);
    }

    private String objectToString(Object arg) {
        return arg != null ? arg.toString() : "";
    }

    public UsuarioResponse getUsuarioSuperior(Integer idUsuario) {
        UsuarioHierarquia usuarioHierarquia = repository.getUsuarioSuperior(idUsuario)
                .orElse(null);
        if (usuarioHierarquia == null) {
            return new UsuarioResponse();
        }
        return UsuarioResponse.convertFrom(usuarioHierarquia.getUsuarioSuperior());
    }

    public List<UsuarioResponse> getUsuarioSuperiores(Integer idUsuario) {
        List<UsuarioHierarquia> usuariosHierarquia = repository.getUsuarioSuperiores(idUsuario);
        return usuariosHierarquia
                .stream()
                .map(uh -> UsuarioResponse.convertFrom(uh.getUsuarioSuperior()))
                .collect(Collectors.toList());
    }

    public List<UsuarioResponse> getUsuarioByPermissao(CodigoFuncionalidade codigoFuncionalidade) {
        List<PermissaoEspecial> permissoes = repository.getUsuariosByPermissao(codigoFuncionalidade);
        return permissoes.stream()
                .map(PermissaoEspecial::getUsuario)
                .map(UsuarioResponse::convertFrom)
                .collect(Collectors.toList());
    }

    @Transactional
    public void alterarSenhaEReenviarPorEmail(Integer idUsuario) {
        Usuario usuario = findComplete(idUsuario);
        String senhaDescriptografada = getSenhaRandomica(MAX_CARACTERES_SENHA);
        repository.updateSenha(passwordEncoder.encode(senhaDescriptografada), usuario.getId());
        enviarEmailComSenhaNova(usuario, senhaDescriptografada);
    }

    @Transactional
    public void alterarSenhaAa(UsuarioAlterarSenhaDto usuarioAlterarSenhaDto) {
        Usuario usuario = findComplete(usuarioAlterarSenhaDto.getUsuarioId());
        usuario.setAlterarSenha(usuarioAlterarSenhaDto.getAlterarSenha());
        String senhaDescriptografada = getSenhaRandomica(MAX_CARACTERES_SENHA);
        repository.updateSenha(passwordEncoder.encode(senhaDescriptografada), usuario.getId());
        repository.save(usuario);
        enviarEmailComSenhaNova(usuario, senhaDescriptografada);
    }

    private void enviarEmailComSenhaNova(Usuario usuario, String senhaDescriptografada) {
        Context context = new Context();
        context.setVariable("nome", usuario.getNome());
        context.setVariable("email", usuario.getEmail());
        context.setVariable("senha", senhaDescriptografada);

        emailService.enviarEmailTemplate(
                Arrays.asList(usuario.getEmail()),
                "Alteração de Senha",
                "reenvio-senha",
                context);
    }

    @Transactional
    public void alterarDadosAcessoEmail(UsuarioDadosAcessoRequest usuarioDadosAcessoRequest) {
        Usuario usuario = findComplete(usuarioDadosAcessoRequest.getUsuarioId());
        confirmarEmailAtual(usuario.getEmail(), usuarioDadosAcessoRequest.getEmailAtual());
        repository.updateEmail(usuarioDadosAcessoRequest.getEmailNovo(), usuario.getId());
        enviarEmailComEmailNovo(usuario, usuarioDadosAcessoRequest);
    }

    private void enviarEmailComEmailNovo(Usuario usuario, UsuarioDadosAcessoRequest usuarioDadosAcessoRequest) {
        Context context = new Context();
        context.setVariable("nome", usuario.getNome());
        context.setVariable("emailNovo", usuarioDadosAcessoRequest.getEmailNovo());
        context.setVariable("emailAntigo", usuarioDadosAcessoRequest.getEmailAtual());

        emailService.enviarEmailTemplate(
                Arrays.asList(usuario.getEmail()),
                "Alteração de E-mail",
                "alteracao-email",
                context);
    }

    private void confirmarEmailAtual(String emailAtual, String emailAtualRequest) {
        if (!emailAtual.equalsIgnoreCase(emailAtualRequest)) {
            throw new ValidacaoException("O e-mail atual está incorreto.");
        }
    }

    @Transactional
    public void alterarDadosAcessoSenha(UsuarioDadosAcessoRequest usuarioDadosAcessoRequest) {
        Usuario usuario = findComplete(usuarioDadosAcessoRequest.getUsuarioId());
        confirmarSenhaAtual(usuario.getSenha(), usuarioDadosAcessoRequest.getSenhaAtual());
        repository.updateSenha(passwordEncoder.encode(usuarioDadosAcessoRequest.getSenhaNova()), usuario.getId());
        enviarEmailComSenhaNova(usuario, usuarioDadosAcessoRequest.getSenhaNova());
    }

    public ConfiguracaoResponse getConfiguracaoByUsuario() {
        Usuario usuario = repository.findComConfiguracao(autenticacaoService.getUsuarioId()).orElse(null);
        return usuario != null
                ? ConfiguracaoResponse.convertFrom(usuario.getConfiguracao())
                : new ConfiguracaoResponse();
    }

    private void confirmarSenhaAtual(String senhaAtual, String senhaAtualRequest) {
        if (!new BCryptPasswordEncoder().matches(senhaAtualRequest, senhaAtual)) {
            throw new ValidacaoException("A senha atual está incorreta.");
        }
    }

    public List<FuncionalidadeResponse> getFuncionalidadeByUsuario(Integer idUsuario) {
        Usuario usuario = findComplete(idUsuario);
        FuncionalidadePredicate predicate = getFuncionalidadePredicate(usuario);
        List<CargoDepartamentoFuncionalidade> funcionalidades = cargoDepartamentoFuncionalidadeRepository
                .findFuncionalidadesPorCargoEDepartamento(predicate.build());
        return Stream.concat(
                funcionalidades
                        .stream()
                        .map(CargoDepartamentoFuncionalidade::getFuncionalidade),
                permissaoEspecialRepository
                        .findPorUsuario(usuario.getId()).stream())
                .distinct()
                .map(FuncionalidadeResponse::convertFrom)
                .collect(Collectors.toList());
    }

    private FuncionalidadePredicate getFuncionalidadePredicate(Usuario usuario) {
        FuncionalidadePredicate predicate = new FuncionalidadePredicate();
        predicate.comCargo(usuario.getCargoId()).comDepartamento(usuario.getDepartamentoId()).build();
        return predicate;
    }

    public List<UsuarioResponse> getUsuarioByNivel(CodigoNivel codigoNivel) {
        return repository.getUsuariosByNivel(codigoNivel).stream()
                .map(UsuarioResponse::convertFrom).collect(Collectors.toList());
    }

    public List<UsuarioCidadeDto> getCidadeByUsuario(Integer usuarioId) {
        Usuario usuario = findComplete(usuarioId);
        return usuario.getCidades().stream()
                .map(c -> UsuarioCidadeDto.parse(c.getCidade()))
                .collect(Collectors.toList());
    }

}
