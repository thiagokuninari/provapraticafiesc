package br.com.xbrain.autenticacao.modules.usuario.service;

import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.comum.dto.EmpresaResponse;
import br.com.xbrain.autenticacao.modules.comum.dto.PageRequest;
import br.com.xbrain.autenticacao.modules.comum.dto.ValidacaoException;
import br.com.xbrain.autenticacao.modules.comum.enums.CodigoEmpresa;
import br.com.xbrain.autenticacao.modules.comum.enums.CodigoUnidadeNegocio;
import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.comum.enums.Eboolean;
import br.com.xbrain.autenticacao.modules.comum.model.Empresa;
import br.com.xbrain.autenticacao.modules.comum.model.UnidadeNegocio;
import br.com.xbrain.autenticacao.modules.comum.repository.EmpresaRepository;
import br.com.xbrain.autenticacao.modules.comum.repository.UnidadeNegocioRepository;
import br.com.xbrain.autenticacao.modules.comum.service.EmailService;
import br.com.xbrain.autenticacao.modules.permissao.model.CargoDepartamentoFuncionalidade;
import br.com.xbrain.autenticacao.modules.permissao.predicate.FuncionalidadePredicate;
import br.com.xbrain.autenticacao.modules.permissao.repository.CargoDepartamentoFuncionalidadeRepository;
import br.com.xbrain.autenticacao.modules.permissao.repository.PermissaoEspecialRepository;
import br.com.xbrain.autenticacao.modules.usuario.dto.*;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoDepartamento;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoNivel;
import br.com.xbrain.autenticacao.modules.usuario.model.*;
import br.com.xbrain.autenticacao.modules.usuario.predicate.UsuarioPredicate;
import br.com.xbrain.autenticacao.modules.usuario.repository.*;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.NumberUtils;
import org.thymeleaf.context.Context;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class UsuarioService {

    private static final int RADIX = 36;
    private static final int POSICAO_ZERO = 0;
    private static final int MAX_CARACTERES_SENHA = 6;
    private static final ValidacaoException EX_NAO_ENCONTRADO = new ValidacaoException("Usuário não encontrado.");

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
    private UsuarioMqSender usuarioMqSender;

    public Usuario findById(int id) {
        return repository
                .findComplete(id)
                .orElseThrow(() -> EX_NAO_ENCONTRADO);
    }

    //FIXME refatorar esse método
    public List<CidadeResponse> findCidadesByUsuario(int usuarioId) {
        Usuario usuario = repository.findComCidade(usuarioId)
                .orElse(null);
        if (usuario != null) {
            return usuario.getCidades()
                    .stream()
                    .map(c -> CidadeResponse.parse(c.getCidade()))
                    .collect(Collectors.toList());
        } else {
            return new ArrayList<>();
        }
    }

    public Usuario findComHierarquia(int id) {
        return repository.findComHierarquia(id).orElseThrow(() -> EX_NAO_ENCONTRADO);
    }

    public UsuarioDto findByEmail(String email) {
        return UsuarioDto.parse(repository.findByEmail(email).orElseThrow(() -> EX_NAO_ENCONTRADO));
    }

    public UsuarioResponse findByEmailAa(String email) {
        return UsuarioResponse.convertFrom(repository.findByEmail(email).orElseThrow(() -> EX_NAO_ENCONTRADO));
    }

    public List<EmpresaResponse> findEmpresasDoUsuario(Integer idUsuario) {
        Usuario usuario = repository.findComplete(idUsuario).orElseThrow(() -> EX_NAO_ENCONTRADO);
        return usuario.getEmpresas().stream().map(EmpresaResponse::convertFrom).collect(Collectors.toList());
    }

    public Page<Usuario> getAll(PageRequest pageRequest, UsuarioFiltros filtros) {
        UsuarioPredicate predicate = filtros.toPredicate();
        return repository.findAll(predicate.build(), pageRequest);
    }

    public UsuarioDto saveUsuarioCidades(UsuarioCidadeSaveDto usuarioCidadeSaveDto) {
        Usuario usuario = findById(usuarioCidadeSaveDto.getUsuarioId());
        adicionarCidadeParaUsuario(usuarioCidadeSaveDto, usuario);
        return UsuarioDto.parse(repository.save(usuario));
    }

    private void adicionarCidadeParaUsuario(UsuarioCidadeSaveDto usuarioCidadeSaveDto, Usuario usuario) {
        usuarioCidadeSaveDto.getCidadesId().forEach(idCidade -> usuario.adicionarCidade(
                criarUsuarioCidade(usuario, idCidade)));
    }

    private UsuarioCidade criarUsuarioCidade(Usuario usuario, Integer idCidade) {
        return UsuarioCidade.criar(usuario, idCidade, autenticacaoService.getUsuarioId());
    }

    public UsuarioDto saveUsuarioHierarquia(UsuarioHierarquiaSaveDto usuarioHierarquiaSaveDto) {
        Usuario usuario = findComHierarquia(usuarioHierarquiaSaveDto.getUsuarioId());
        removerUsuarioSuperior(usuarioHierarquiaSaveDto, usuario);
        adicionarUsuarioSuperior(usuarioHierarquiaSaveDto, usuario);
        return UsuarioDto.parse(repository.save(usuario));
    }

    private void adicionarUsuarioSuperior(UsuarioHierarquiaSaveDto usuarioHierarquiaSaveDto, Usuario usuario) {
        usuarioHierarquiaSaveDto.getHierarquiasId()
                .forEach(idHierarquia -> usuario.adicionarHierarquia(criarUsuarioHierarquia(usuario, idHierarquia)));
    }

    private UsuarioHierarquia criarUsuarioHierarquia(Usuario usuario, Integer idHierarquia) {
        return UsuarioHierarquia.criar(usuario, idHierarquia, autenticacaoService.getUsuarioId());
    }

    private void removerUsuarioSuperior(UsuarioHierarquiaSaveDto usuarioHierarquiaSaveDto, Usuario usuario) {
        usuario.getUsuariosHierarquia()
                .removeIf(h -> !usuarioHierarquiaSaveDto.getHierarquiasId().contains(h.getUsuarioSuperiorId()));
    }

    public List<Integer> getIdDosUsuariosSubordinados(Integer usuarioId, Boolean incluirProprio) {
        List<Integer> usuariosSubordinados = repository.getUsuariosSubordinados(usuarioId);
        if (incluirProprio) {
            usuariosSubordinados.add(usuarioId);
        }
        return usuariosSubordinados;
    }

    public UsuarioDto save(UsuarioDto usuarioDto) {
        Usuario usuario = Usuario.parse(usuarioDto);
        validar(usuario);
        if (usuario.isNovoCadastro()) {
            String senhaDescriptografada = getSenhaRandomica(MAX_CARACTERES_SENHA);
            configurar(usuario, senhaDescriptografada);
            usuario = repository.save(usuario);
            enviarEmailDadosDeAcesso(usuario, senhaDescriptografada);
            return UsuarioDto.parse(usuario);
        }
        return UsuarioDto.parse(repository.save(usuario));
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
        } catch (Exception exception) {
            enviarParaFilaDeErro(usuarioMqRequest);
            throw exception;
        }
    }

    private void enviarParaFilaDeUsuariosSalvos(UsuarioDto usuarioDto) {
        usuarioMqSender.send(usuarioDto);
    }

    private void enviarParaFilaDeErro(UsuarioMqRequest usuarioMqRequest) {
        usuarioMqSender.sendWithBug(usuarioMqRequest);
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

    public void ativar(UsuarioAtivacaoDto dto) {
        Usuario usuario = repository.findComplete(dto.getIdUsuario()).get();
        usuario.setSituacao(ESituacao.A);
        usuario.adicionar(UsuarioHistorico.builder()
                .dataCadastro(LocalDateTime.now())
                .usuario(usuario)
                .usuarioAlteracao(findById(autenticacaoService.getUsuarioId()))
                .observacao(dto.getObservacao())
                .situacao(ESituacao.A)
                .build());
        repository.save(usuario);
    }

    public void inativar(UsuarioInativacaoDto dto) {
        Usuario usuario = repository.findComplete(dto.getIdUsuario()).get();
        usuario.setSituacao(ESituacao.I);
        MotivoInativacao motivoInativacao = carregarMotivoInativacao(dto);
        usuario.adicionar(UsuarioHistorico.builder()
                .dataCadastro(dto.getDataCadastro())
                .motivoInativacao(motivoInativacao)
                .usuario(usuario)
                .usuarioAlteracao(findById(autenticacaoService.getUsuarioId()))
                .observacao(dto.getObservacao())
                .situacao(ESituacao.I)
                .build());
        repository.save(usuario);
    }

    private MotivoInativacao carregarMotivoInativacao(UsuarioInativacaoDto dto) {
        if (dto.getIdMotivoInativacao() != null) {
            return new MotivoInativacao(dto.getIdMotivoInativacao());
        }
        return motivoInativacaoRepository.findByCodigo(dto.getCodigoMotivoInativacao())
                .orElseThrow(() -> new ValidacaoException("Motivo de inativação não encontrado."));
    }

    public List<UsuarioDto> getUsuariosFiltros(UsuarioFiltrosDto usuarioFiltrosDto) {
        UsuarioPredicate usuarioPredicate = new UsuarioPredicate()
                .comEmpresas(usuarioFiltrosDto.getEmpresasIds())
                .comUnidadesNegocio(usuarioFiltrosDto.getUnidadesNegocioIds())
                .comNivel(usuarioFiltrosDto.getCodigoNivelList())
                .comCargo(usuarioFiltrosDto.getCodigoCargoList())
                .comDepartamento(usuarioFiltrosDto.getCodigoDepartamentoList())
                .comCidade(usuarioFiltrosDto.getCidadesIds())
                .comIds(usuarioFiltrosDto.getUsuariosAAsNacionais())
                .isAtivo();

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

    public void alterarCargoUsuario(Integer id, CodigoCargo codigoCargo) {
        Usuario usuario = repository.findComplete(id).orElseThrow(() -> EX_NAO_ENCONTRADO);
        usuario.setCargo(getCargo(codigoCargo));
        repository.save(usuario);
    }

    public void alterarEmailUsuario(Integer id, String email) {
        Usuario usuario = repository.findComplete(id).orElseThrow(() -> EX_NAO_ENCONTRADO);
        usuario.setEmail(email);
        repository.save(usuario);
    }

    //FIXME O banco em memória não tem suporte para o LISTAGG do oracle. Dar um jeito de testar esse método.
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
                .orElseThrow(() -> EX_NAO_ENCONTRADO);
        return UsuarioResponse.convertFrom(usuarioHierarquia.getUsuarioSuperior());
    }

    public List<FuncionalidadeResponse> getFuncionalidadeByUsuario(Integer idUsuario) {
        //TODO melhorar o código
        Usuario usuario = repository.findComplete(idUsuario).orElseThrow(() -> EX_NAO_ENCONTRADO);
        FuncionalidadePredicate predicate = new FuncionalidadePredicate();
        predicate.comCargo(usuario.getCargoId()).comDepartamento(usuario.getDepartamentoId()).build();
        List<CargoDepartamentoFuncionalidade> funcionalidades = cargoDepartamentoFuncionalidadeRepository
                .findFuncionalidadesPorCargoEDepartamento(predicate);

        List<Empresa> empresasUsuario = usuario.getEmpresas();
        List<UnidadeNegocio> unidadesUsuario = usuario.getUnidadesNegocios();

        return Stream.concat(
                funcionalidades
                        .stream()
                        .filter(semEmpresaEUnidadeDeNegocio
                                .or(possuiEmpresa(empresasUsuario))
                                .or(possuiUnidadeNegocio(unidadesUsuario))
                                .or(possuiEmpresaEUnidadeNegocio(unidadesUsuario, empresasUsuario)))
                        .map(CargoDepartamentoFuncionalidade::getFuncionalidade),
                permissaoEspecialRepository
                        .findPorUsuario(usuario.getId()).stream())
                .distinct()
                .map(FuncionalidadeResponse::convertFrom)
                .collect(Collectors.toList());
    }

    private Predicate<CargoDepartamentoFuncionalidade> semEmpresaEUnidadeDeNegocio = f -> f.getEmpresa() == null
            && f.getUnidadeNegocio() == null;

    private Predicate<CargoDepartamentoFuncionalidade> possuiEmpresa(List<Empresa> empresasUsuario) {
        return f -> f.getEmpresa() != null && f.getUnidadeNegocio() == null && empresasUsuario.contains(f.getEmpresa());
    }

    private Predicate<CargoDepartamentoFuncionalidade> possuiUnidadeNegocio(List<UnidadeNegocio> unidadesUsuario) {
        return f -> f.getUnidadeNegocio() != null
                && f.getEmpresa() == null
                && unidadesUsuario.contains(f.getUnidadeNegocio());
    }

    private Predicate<CargoDepartamentoFuncionalidade> possuiEmpresaEUnidadeNegocio(List<UnidadeNegocio> unidadesUsuario,
                                                                                    List<Empresa> empresasUsuario) {
        return f -> f.getUnidadeNegocio() != null
                && f.getEmpresa() != null
                && unidadesUsuario.contains(f.getUnidadeNegocio()) && empresasUsuario.contains(f.getEmpresa());
    }

}
