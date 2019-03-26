package br.com.xbrain.autenticacao.modules.usuario.service;

import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.comum.dto.EmpresaResponse;
import br.com.xbrain.autenticacao.modules.comum.dto.PageRequest;
import br.com.xbrain.autenticacao.modules.comum.enums.CodigoEmpresa;
import br.com.xbrain.autenticacao.modules.comum.enums.CodigoUnidadeNegocio;
import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.comum.enums.Eboolean;
import br.com.xbrain.autenticacao.modules.comum.exception.NotFoundException;
import br.com.xbrain.autenticacao.modules.comum.exception.ValidacaoException;
import br.com.xbrain.autenticacao.modules.comum.model.Empresa;
import br.com.xbrain.autenticacao.modules.comum.model.UnidadeNegocio;
import br.com.xbrain.autenticacao.modules.comum.repository.EmpresaRepository;
import br.com.xbrain.autenticacao.modules.comum.repository.UnidadeNegocioRepository;
import br.com.xbrain.autenticacao.modules.comum.service.FileService;
import br.com.xbrain.autenticacao.modules.comum.util.ListUtil;
import br.com.xbrain.autenticacao.modules.comum.util.StringUtil;
import br.com.xbrain.autenticacao.modules.equipevenda.service.EquipeVendaService;
import br.com.xbrain.autenticacao.modules.notificacao.service.NotificacaoService;
import br.com.xbrain.autenticacao.modules.parceirosonline.service.AgenteAutorizadoClient;
import br.com.xbrain.autenticacao.modules.parceirosonline.service.AgenteAutorizadoService;
import br.com.xbrain.autenticacao.modules.permissao.dto.FuncionalidadeResponse;
import br.com.xbrain.autenticacao.modules.permissao.filtros.FuncionalidadePredicate;
import br.com.xbrain.autenticacao.modules.permissao.model.CargoDepartamentoFuncionalidade;
import br.com.xbrain.autenticacao.modules.permissao.model.PermissaoEspecial;
import br.com.xbrain.autenticacao.modules.permissao.repository.CargoDepartamentoFuncionalidadeRepository;
import br.com.xbrain.autenticacao.modules.permissao.repository.PermissaoEspecialRepository;
import br.com.xbrain.autenticacao.modules.permissao.service.FuncionalidadeService;
import br.com.xbrain.autenticacao.modules.usuario.dto.*;
import br.com.xbrain.autenticacao.modules.usuario.enums.*;
import br.com.xbrain.autenticacao.modules.usuario.model.*;
import br.com.xbrain.autenticacao.modules.usuario.predicate.UsuarioPredicate;
import br.com.xbrain.autenticacao.modules.usuario.rabbitmq.*;
import br.com.xbrain.autenticacao.modules.usuario.repository.*;
import br.com.xbrain.xbrainutils.CsvUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.NumberUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static br.com.xbrain.autenticacao.modules.comum.enums.RelatorioNome.USUARIOS_CSV;
import static br.com.xbrain.xbrainutils.NumberUtils.getOnlyNumbers;

@Service
public class UsuarioService {

    private static final int POSICAO_ZERO = 0;
    private static final int MAX_CARACTERES_SENHA = 6;
    private static final ValidacaoException EX_NAO_ENCONTRADO = new ValidacaoException("Usuário não encontrado.");
    private static final int MAXIMO_PARAMETROS_IN = 1000;
    private static final ESituacao ATIVO = ESituacao.A;
    private static final ESituacao INATIVO = ESituacao.I;
    private static final Integer MAXIMO = 1000;
    private static final Integer MINIMO = 1001;
    private static ValidacaoException EMAIL_CADASTRADO_EXCEPTION = new ValidacaoException("Email já cadastrado.");
    private static ValidacaoException EMAIL_ATUAL_INCORRETO_EXCEPTION
            = new ValidacaoException("Email atual está incorreto.");
    private static ValidacaoException SENHA_ATUAL_INCORRETA_EXCEPTION
            = new ValidacaoException("Senha atual está incorreta.");
    private final Logger log = LoggerFactory.getLogger(UsuarioService.class);


    @Autowired
    private UsuarioRepository repository;
    @Autowired
    private AgenteAutorizadoClient agenteAutorizadoClient;
    @Autowired
    private AutenticacaoService autenticacaoService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private NotificacaoService notificacaoService;
    @Autowired
    private MotivoInativacaoRepository motivoInativacaoRepository;
    @Autowired
    private CargoRepository cargoRepository;
    @Autowired
    private CargoService cargoService;
    @Autowired
    private DepartamentoRepository departamentoRepository;
    @Autowired
    private UsuarioCidadeRepository usuarioCidadeRepository;
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
    private UsuarioAaAtualizacaoMqSender usuarioAaAtualizacaoMqSender;
    @Autowired
    private UsuarioRecuperacaoMqSender usuarioRecuperacaoMqSender;
    @Autowired
    private ConfiguracaoRepository configuracaoRepository;
    @Autowired
    private UsuarioAtualizacaoMqSender usuarioAtualizacaoMqSender;
    @Autowired
    private AtualizarUsuarioMqSender atualizarUsuarioMqSender;
    @Autowired
    private UsuarioHierarquiaRepository usuarioHierarquiaRepository;
    @Autowired
    private AgenteAutorizadoService agenteAutorizadoService;
    @Autowired
    private UsuarioHistoricoRepository usuarioHistoricoRepository;
    @Autowired
    private EntityManager entityManager;
    @Autowired
    private FileService fileService;
    @Autowired
    private EquipeVendaService equipeVendaService;
    @Autowired
    private FuncionalidadeService funcionalidadeService;

    public Usuario findComplete(Integer id) {
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

    @Transactional
    public Usuario findByIdEmulacao(int id) {
        UsuarioPredicate predicate = new UsuarioPredicate();
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

    public Usuario findCompleteById(int id) {
        return repository.findComplete(id).orElseThrow(() -> EX_NAO_ENCONTRADO);
    }

    @Transactional
    public UsuarioDto findByEmail(String email) {
        return UsuarioDto.convertTo(repository.findByEmail(email).orElseThrow(() -> EX_NAO_ENCONTRADO));
    }

    public Optional<UsuarioResponse> findByEmailAa(String email) {
        Optional<Usuario> usuarioOptional = repository.findByEmail(email);

        return usuarioOptional.map(UsuarioResponse::convertFrom);
    }

    public Optional<UsuarioResponse> findByCpfAa(String cpf) {
        return repository
                .findTop1UsuarioByCpf(getOnlyNumbers(cpf))
                .map(UsuarioResponse::convertFrom);
    }

    public List<EmpresaResponse> findEmpresasDoUsuario(Integer idUsuario) {
        Usuario usuario = findComplete(idUsuario);
        return usuario.getEmpresas().stream().map(EmpresaResponse::convertFrom).collect(Collectors.toList());
    }

    public Page<Usuario> getAll(PageRequest pageRequest, UsuarioFiltros filtros) {
        UsuarioPredicate predicate = filtrarUsuariosPermitidos(filtros);
        Page<Usuario> pages = repository.findAll(predicate.build(), pageRequest);
        if (!CollectionUtils.isEmpty(pages.getContent())) {
            popularUsuarios(pages.getContent());
        }
        return pages;
    }

    private void popularUsuarios(List<Usuario> usuarios) {
        usuarios.forEach(c -> {
            c.setEmpresas(repository.findEmpresasById(c.getId()));
            c.setUnidadesNegocios(repository.findUnidadesNegociosById(c.getId()));
        });
    }

    private void obterUsuariosAa(String cnpjAa, UsuarioPredicate predicate, Boolean buscarInativos) {
        List<Integer> lista = agenteAutorizadoService.getIdUsuariosPorAa(cnpjAa, buscarInativos);
        predicate.comIds(lista);
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
        return UsuarioDto.convertTo(repository.save(usuario));
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

    public List<UsuarioSubordinadoDto> getSubordinadosDoUsuario(Integer usuarioId) {
        List<Object[]> usuariosCompletoSubordinados = repository.getUsuariosCompletoSubordinados(usuarioId);
        return usuariosCompletoSubordinados.stream()
                .map(this::criarUsuarioSubordinadoResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public UsuarioDto save(Usuario request, MultipartFile foto) {
        if (!ObjectUtils.isEmpty(foto)) {
            fileService.uploadFotoUsuario(request, foto);
        }
        return save(request);
    }

    @Transactional
    public UsuarioDto save(Usuario usuario, boolean realocado) {
        validar(usuario);
        usuario.setAlterarSenha(Eboolean.F);
        if (realocado) {
            salvarUsuarioRealocado(usuario);
            usuario = criaNovoUsuarioAPartirDoRealocado(usuario);
        } else {
            atualizarUsuariosParceiros(usuario);
        }
        repository.save(usuario);
        entityManager.flush();
        if (realocado) {
            enviarParaFilaDeUsuariosColaboradores(usuario);
        }
        return UsuarioDto.convertTo(usuario);
    }

    @Transactional
    public UsuarioDto save(Usuario usuario) {
        try {
            validar(usuario);

            Cargo cargoOld = null;
            boolean enviarEmail = false;
            String senhaDescriptografada = getSenhaRandomica(MAX_CARACTERES_SENHA);
            if (usuario.isNovoCadastro()) {
                configurar(usuario, senhaDescriptografada);
                enviarEmail = true;
            } else {
                cargoOld = cargoService.findByUsuarioId(usuario.getId());
                atualizarUsuariosParceiros(usuario);
                usuario.setAlterarSenha(Eboolean.F);
            }
            repository.save(usuario);
            entityManager.flush();

            validarCargoEquipeVendas(usuario, cargoOld);
            tratarHierarquiaUsuario(usuario, usuario.getHierarquiasId());
            tratarCidadesUsuario(usuario, usuario.getCidadesId());

            if (enviarEmail) {
                notificacaoService.enviarEmailDadosDeAcesso(usuario, senhaDescriptografada);
            }
            return UsuarioDto.convertTo(usuario);
        } catch (PersistenceException ex) {
            log.error("Erro de persistência ao salvar o Usuario.", ex.getMessage());
            throw new ValidacaoException("Erro ao cadastrar usuário.");
        } catch (Exception ex) {
            log.error("Erro ao salvar Usuário.", ex);
            throw ex;
        }
    }

    public void salvarUsuarioRealocado(Usuario usuario) {
        Usuario usuarioARealocar = repository.findById(usuario.getId()).orElseThrow(() -> EX_NAO_ENCONTRADO);
        usuarioARealocar.setSituacao(ESituacao.R);
        repository.save(usuarioARealocar);
    }

    private void validarCargoEquipeVendas(Usuario usuario, Cargo cargoOld) {
        if (!ObjectUtils.isEmpty(cargoOld) && !usuario.getCargoId().equals(cargoOld.getId())) {
            if (cargoOld.getCodigo().equals(CodigoCargo.SUPERVISOR_OPERACAO)) {
                equipeVendaService.inativarSupervidor(usuario.getId());
            } else if (cargoOld.getCodigo().equals(CodigoCargo.VENDEDOR_OPERACAO)
                    || cargoOld.getCodigo().equals(CodigoCargo.ASSISTENTE_OPERACAO)) {
                equipeVendaService.inativarUsuario(usuario.getId());
            }
        }
    }

    private Usuario criaNovoUsuarioAPartirDoRealocado(Usuario usuario) {
        Usuario usuarioCopia = new Usuario();
        BeanUtils.copyProperties(usuario, usuarioCopia);
        if (!ObjectUtils.isEmpty(repository.findAllByCpf(usuario.getCpf()))
                && usuario.getSituacao().equals(ESituacao.A)) {
            usuarioCopia.setSenha(repository.findById(usuario.getId())
                    .orElseThrow(() -> new NotFoundException("Usuário não encontrado")).getSenha());
            usuarioCopia.setDataCadastro(LocalDateTime.now());
            usuarioCopia.setSituacao(ESituacao.A);
            usuarioCopia.setId(null);
        }
        return usuarioCopia;
    }

    public void vincularUsuario(List<Integer> idUsuarioNovo, Integer idUsuarioSuperior) {
        Usuario usuarioSuperior = repository.findById(idUsuarioSuperior).orElseThrow(() ->
                new NotFoundException("Usuário não encontrado"));
        idUsuarioNovo.stream()
                .map(id -> {
                    UsuarioHierarquia usuario = usuarioHierarquiaRepository.findOne(id);
                    usuario.setUsuarioSuperior(usuarioSuperior);
                    usuarioHierarquiaRepository.save(usuario);
                    return usuario;
                })
                .collect(Collectors.toList());
    }

    private void atualizarUsuariosParceiros(Usuario usuario) {
        cargoRepository.findById(usuario.getCargoId()).ifPresent(cargo -> {
            Optional<Usuario> usuarioAtualizar = repository.findById(usuario.getId());
            if (isSocioPrincipal(cargo.getCodigo()) && usuarioAtualizar.isPresent()) {
                UsuarioDto usuarioDto = UsuarioDto.convertTo(usuarioAtualizar.get());
                try {
                    enviarParaFilaDeAtualizarUsuariosPol(usuarioDto);
                } catch (Exception ex) {
                    log.error("Erro ao enviar usuario para atualizar no Parceiros Online", ex.getMessage());
                }
            }
        });
    }

    private boolean isSocioPrincipal(CodigoCargo cargoCodigo) {
        return CodigoCargo.AGENTE_AUTORIZADO_SOCIO.equals(cargoCodigo);
    }

    private void validar(Usuario usuario) {
        validarCpfExistente(usuario);
        validarEmailExistente(usuario);
        usuario.removerCaracteresDoCpf();
        usuario.tratarEmails();
    }

    private void tratarHierarquiaUsuario(Usuario usuario, List<Integer> hierarquiasId) {
        removerUsuarioSuperior(usuario, hierarquiasId);
        removerHierarquiaSubordinados(usuario);
        adicionarUsuarioSuperior(usuario, hierarquiasId);
        hierarquiaIsValida(usuario);

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

    private void removerHierarquiaSubordinados(Usuario usuario) {
        Set<UsuarioHierarquia> subordinados = usuarioHierarquiaRepository.findAllByIdUsuarioSuperior(usuario.getId())
                .stream().filter(hierarquia -> !hierarquia.isSuperior(usuario.getCargoId()))
                .collect(Collectors.toSet());
        if (!CollectionUtils.isEmpty(subordinados)) {
            usuarioHierarquiaRepository.delete(subordinados);
        }
    }

    private void adicionarUsuarioSuperior(Usuario usuario, List<Integer> hierarquiasId) {
        if (!CollectionUtils.isEmpty(hierarquiasId)) {
            hierarquiasId
                    .forEach(idHierarquia -> usuario.adicionarHierarquia(criarUsuarioHierarquia(usuario, idHierarquia)));
        }
    }

    public void hierarquiaIsValida(Usuario usuario) {
        if (!ObjectUtils.isEmpty(usuario)
                && !ObjectUtils.isEmpty(usuario.getUsuariosHierarquia())) {

            usuario.getUsuariosHierarquia()
                    .forEach(user -> processarHierarquia(usuario, user, new ArrayList<>()));
        }
    }

    private boolean processarHierarquia(final Usuario usuarioParaAchar,
                                        UsuarioHierarquia usuario,
                                        ArrayList<Usuario> valores) {
        boolean existeId = false;

        if (validarUsuarios(usuarioParaAchar, usuario)) {
            existeId = verificarUsuariosHierarquia(usuarioParaAchar, usuario);
            valores.add(usuario.getUsuario());

            if (!existeId) {
                List<Integer> superiores = getIdSuperiores(usuario.getUsuario());
                Set<UsuarioHierarquia> usuarios = getUsuariosSuperioresPorId(superiores);
                existeId = validarHierarquia(usuarioParaAchar, usuarios, valores);
            }
            if (existeId) {
                String mensagem = montarMensagemDeErro(valores, usuarioParaAchar);
                throw new ValidacaoException(mensagem);
            }

        }
        return existeId;
    }

    private String montarMensagemDeErro(ArrayList<Usuario> usuarios, Usuario usuarioParaAchar) {
        List<Usuario> valores = usuarios.stream().distinct().collect(Collectors.toList());
        return valores.size() == 1
                ? "Não é possível atrelar o próprio usuário em sua Hierarquia."
                : "Não é possível adicionar o usuário "
                + valores.get(1).getNome()
                + " como superior, pois o usuário "
                + usuarioParaAchar.getNome()
                + " é superior a ele em sua hierarquia.";
    }

    private boolean validarUsuarios(Usuario usuarioParaAchar, UsuarioHierarquia usuario) {
        return !ObjectUtils.isEmpty(usuarioParaAchar)
                && !ObjectUtils.isEmpty(usuarioParaAchar.getUsuariosHierarquia())
                && !ObjectUtils.isEmpty(usuario)
                && !ObjectUtils.isEmpty(usuario.getUsuarioSuperior());
    }

    private boolean verificarUsuariosHierarquia(Usuario usuarioParaAchar, UsuarioHierarquia usuario) {
        return usuarioParaAchar.getId().equals(usuario.getUsuarioSuperiorId());
    }

    private List<Integer> getIdSuperiores(Usuario usuario) {

        return usuario.getUsuariosHierarquia()
                .stream()
                .map(UsuarioHierarquia::getUsuarioSuperiorId)
                .filter(item -> !ObjectUtils.isEmpty(item))
                .collect(Collectors.toList());
    }

    private Set<UsuarioHierarquia> getUsuariosSuperioresPorId(List<Integer> hierarquiasId) {
        return usuarioHierarquiaRepository.findByUsuarioIdIn(hierarquiasId);
    }

    private boolean validarHierarquia(Usuario usuarioParaAchar,
                                      Set<UsuarioHierarquia> usuarios,
                                      ArrayList<Usuario> valores) {
        return usuarios.stream().anyMatch(usuario -> {
            boolean existe = verificarUsuariosHierarquia(usuarioParaAchar, usuario);
            if (!existe && !valores.contains(usuario.getUsuario())) {
                valores.add(usuario.getUsuario());

                existe = processarHierarquia(usuarioParaAchar, usuario, valores);
            }

            valores.add(usuario.getUsuario());

            return existe;
        });
    }

    private void tratarCidadesUsuario(Usuario usuario, List<Integer> cidadesId) {
        removerUsuarioCidade(usuario, cidadesId);
        adicionarUsuarioCidade(usuario, cidadesId);
    }

    private void removerUsuarioCidade(Usuario usuario, List<Integer> cidadesId) {
        if (CollectionUtils.isEmpty(cidadesId) && !CollectionUtils.isEmpty(usuario.getCidades())) {
            usuarioCidadeRepository.deleteByUsuario(usuario.getId());

        } else if (!CollectionUtils.isEmpty(usuario.getCidades())) {
            usuario.getCidades().forEach(c -> {
                if (!cidadesId.contains(c.getCidade().getId())) {
                    usuarioCidadeRepository.deleteByCidadeAndUsuario(c.getCidade().getId(), usuario.getId());
                }
            });
        }
    }

    private void adicionarUsuarioCidade(Usuario usuario, List<Integer> cidadesId) {
        if (!CollectionUtils.isEmpty(cidadesId)) {
            cidadesId.forEach(idCidade -> usuario.adicionarCidade(
                    criarUsuarioCidade(usuario, idCidade)));
            repository.save(usuario);
        }
    }

    public void configurar(Usuario usuario, String senhaDescriptografada) {
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
            usuarioDto = save(UsuarioDto.convertFrom(usuarioDto));
            enviarParaFilaDeUsuariosSalvos(usuarioDto);
        } catch (Exception ex) {
            usuarioMqRequest.setException(ex.getMessage());
            enviarParaFilaDeErroCadastroUsuarios(usuarioMqRequest);
            log.error("Erro ao salvar usuário da fila.", ex);
        }
    }

    @Transactional
    public void updateFromQueue(UsuarioMqRequest usuarioMqRequest) {
        try {
            UsuarioDto usuarioDto = UsuarioDto.parse(usuarioMqRequest);
            configurarUsuario(usuarioMqRequest, usuarioDto);
            save(UsuarioDto.convertFrom(usuarioDto), usuarioMqRequest.isRealocado());
        } catch (Exception ex) {
            usuarioMqRequest.setException(ex.getMessage());
            enviarParaFilaDeErroAtualizacaoUsuarios(usuarioMqRequest);
            log.error("erro ao atualizar usuário da fila.", ex);
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
            repository.updateSenha(passwordEncoder.encode(senhaDescriptografada), usuario.getId());
            repository.updateEmail(usuario.getEmail(), usuario.getId());
            repository.save(usuario);
            entityManager.flush();

            notificacaoService.enviarEmailDadosDeAcesso(usuario, senhaDescriptografada);

        } catch (Exception ex) {
            enviarParaFiladeErrosUsuariosRecuperacao(usuarioMqRequest);
            log.error("Erro ao recuperar usuário da fila.", ex);
        }
    }

    private void enviarParaFiladeErrosUsuariosRecuperacao(UsuarioMqRequest usuarioMqRequest) {
        usuarioRecuperacaoMqSender.sendWithFailure(usuarioMqRequest);
    }

    private void enviarParaFilaDeUsuariosColaboradores(Usuario usuario) {
        List<Usuario> usuarios = repository.findAllByCpf(usuario.getCpf());
        if (!usuarios.isEmpty()) {
            usuarios
                    .stream()
                    .filter(usuarioColaborador -> usuarioColaborador.getSituacao().equals(ESituacao.A))
                    .map(UsuarioDto::convertTo)
                    .forEach(usuarioAtualizarColaborador -> usuarioMqSender
                            .sendColaboradoresSuccess(usuarioAtualizarColaborador));

        }
    }

    private void enviarParaFilaDeUsuariosSalvos(UsuarioDto usuarioDto) {
        usuarioMqSender.sendSuccess(usuarioDto);
    }

    private void enviarParaFilaDeAtualizarUsuariosPol(UsuarioDto usuarioDto) {
        atualizarUsuarioMqSender.sendSuccess(usuarioDto);
    }

    private void enviarParaFilaDeErroCadastroUsuarios(UsuarioMqRequest usuarioMqRequest) {
        usuarioMqSender.sendWithFailure(usuarioMqRequest);
    }

    private void enviarParaFilaDeErroAtualizacaoUsuarios(UsuarioMqRequest usuarioMqRequest) {
        usuarioAaAtualizacaoMqSender.sendWithFailure(usuarioMqRequest);
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

    private String getSenhaRandomica(int size) {
        return StringUtil.getSenhaRandomica(size);
    }

    private void validarCpfExistente(Usuario usuario) {
        usuario.removerCaracteresDoCpf();
        repository
                .findTop1UsuarioByCpfAndSituacaoNot(usuario.getCpf(), ESituacao.R)
                .ifPresent(u -> {
                    if (ObjectUtils.isEmpty(usuario.getId())
                            || !usuario.getId().equals(u.getId())) {
                        throw new ValidacaoException("CPF já cadastrado.");
                    }
                });
    }

    private void validarEmailExistente(Usuario usuario) {
        repository
                .findTop1UsuarioByEmailIgnoreCaseAndSituacaoNot(usuario.getEmail(), ESituacao.R)
                .ifPresent(u -> {
                    if (ObjectUtils.isEmpty(usuario.getId())
                            || !usuario.getId().equals(u.getId())) {
                        throw new ValidacaoException("Email já cadastrado.");
                    }
                });
    }

    @Transactional
    public void ativar(UsuarioAtivacaoDto dto) {
        Usuario usuario = findComplete(dto.getIdUsuario());
        usuario.setSituacao(ESituacao.A);

        Usuario usuarioInativacao = dto.getIdUsuarioAtivacao() != null ? new Usuario(dto.getIdUsuarioAtivacao())
                : new Usuario(autenticacaoService.getUsuarioId());

        if (!ObjectUtils.isEmpty(usuario.getCpf())) {
            if (situacaoAtiva(usuario.getEmail())) {
                usuario.adicionar(UsuarioHistorico.builder()
                        .dataCadastro(LocalDateTime.now())
                        .usuario(usuario)
                        .usuarioAlteracao(usuarioInativacao)
                        .observacao(dto.getObservacao())
                        .situacao(ESituacao.A)
                        .build());
                repository.save(usuario);
            } else {
                throw new ValidacaoException("O usuário não pode ser ativo, porque o Agente Autorizado está inativo.");
            }
        } else {
            throw new ValidacaoException("O usuário não pode ser ativado por não possuir CPF.");
        }
    }

    public void limparCpfUsuario(Integer id) {
        Usuario usuario = limpaCpf(id);
        agenteAutorizadoClient.limparCpfAgenteAutorizado(usuario.getEmail());
    }

    @Transactional
    public Usuario limpaCpf(Integer id) {
        Usuario usuario = findComplete(id);
        usuario.setCpf(null);
        return repository.save(usuario);
    }

    @Transactional
    public void inativar(UsuarioInativacaoDto dto) {
        Usuario usuario = findComplete(dto.getIdUsuario());
        usuario.setSituacao(ESituacao.I);
        MotivoInativacao motivoInativacao = carregarMotivoInativacao(dto);

        Usuario usuarioInativacao = dto.getIdUsuarioInativacao() != null ? new Usuario(dto.getIdUsuarioInativacao())
                : new Usuario(autenticacaoService.getUsuarioId());

        usuario.adicionar(UsuarioHistorico.builder()
                .dataCadastro(LocalDateTime.now())
                .motivoInativacao(motivoInativacao)
                .usuario(usuario)
                .usuarioAlteracao(usuarioInativacao)
                .observacao(dto.getObservacao())
                .situacao(ESituacao.I)
                .build());
        repository.save(usuario);
    }

    @Transactional
    public void inativarUsuariosSemAcesso() {
        MotivoInativacao motivo = motivoInativacaoRepository.findByCodigo(CodigoMotivoInativacao.INATIVADO_SEM_ACESSO).get();
        List<Usuario> usuarios = getUsuariosSemAcesso();
        usuarios.forEach(usuario -> {
            usuario = findComplete(usuario.getId());
            usuario.setSituacao(ESituacao.I);
            usuario.adicionar(UsuarioHistorico.builder()
                    .dataCadastro(LocalDateTime.now())
                    .motivoInativacao(motivo)
                    .usuario(usuario)
                    .usuarioAlteracao(usuario)
                    .observacao("Inativado por falta de acesso")
                    .situacao(ESituacao.I)
                    .build());
            repository.save(usuario);
        });
    }

    public List<Usuario> getUsuariosSemAcesso() {
        return usuarioHistoricoRepository.getUsuariosSemAcesso();
    }

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
        return repository.findAllUsuariosHierarquia(usuarioPredicate.build());
    }

    public List<Usuario> getUsuariosCargoSuperior(Integer cargoId) {
        Cargo cargo = cargoService.findById(cargoId);
        UsuarioPredicate usuarioPredicate = new UsuarioPredicate();
        usuarioPredicate.filtraPermitidos(autenticacaoService.getUsuarioAutenticado(), this);
        usuarioPredicate.comCargos(cargo.getCargosSuperioresId());
        return repository.getUsuariosFilter(usuarioPredicate.build());
    }

    public List<UsuarioDto> getUsuariosFiltros(UsuarioFiltrosDto usuarioFiltrosDto) {
        UsuarioPredicate usuarioPredicate = new UsuarioPredicate()
                .comEmpresas(usuarioFiltrosDto.getEmpresasIds())
                .comUnidadesNegocio(usuarioFiltrosDto.getUnidadesNegocioIds())
                .comNivel(usuarioFiltrosDto.getNivelIds())
                .comCargo(usuarioFiltrosDto.getCargoIds())
                .comDepartamento(usuarioFiltrosDto.getDepartamentoIds())
                .comIds(usuarioFiltrosDto.getUsuariosIds())
                .isAtivo(usuarioFiltrosDto.getAtivo());

        montarPredicateComCidade(usuarioPredicate, usuarioFiltrosDto);

        List<Usuario> usuarioList = repository.getUsuariosFilter(usuarioPredicate.build());

        return usuarioList.stream()
                .map(UsuarioDto::convertTo)
                .collect(Collectors.toList());
    }

    private void montarPredicateComCidade(UsuarioPredicate predicate, UsuarioFiltrosDto filtro) {

        List<List<Integer>> listaPartes = ListUtil.divideListaEmListasMenores(filtro.getCidadesIds(), MAXIMO_PARAMETROS_IN);

        listaPartes.forEach(lista -> predicate.comCidade(lista));
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

    private UsuarioSubordinadoDto criarUsuarioSubordinadoResponse(Object[] param) {
        int indice = POSICAO_ZERO;
        return UsuarioSubordinadoDto.builder()
                .id(objectToInteger(param[indice++]))
                .nome(objectToString(param[indice++]))
                .cpf(objectToString(param[indice++]))
                .email(objectToString(param[indice++]))
                .codigoNivel(CodigoNivel.valueOf(objectToString(param[indice++])))
                .codigoDepartamento(CodigoDepartamento.valueOf(objectToString(param[indice++])))
                .codigoCargo(CodigoCargo.valueOf(objectToString(param[indice++])))
                .nomeCargo(objectToString(param[indice++]))
                .build();
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

    public List<UsuarioResponse> getUsuarioByPermissao(String funcionalidade) {
        List<PermissaoEspecial> permissoes = repository.getUsuariosByPermissao(funcionalidade);
        return permissoes.stream()
                .map(PermissaoEspecial::getUsuario)
                .map(UsuarioResponse::convertFrom)
                .collect(Collectors.toList());
    }

    @Transactional
    public void alterarSenhaEReenviarPorEmail(Integer idUsuario) {
        Usuario usuario = findComplete(idUsuario);
        updateSenha(usuario, Eboolean.V);
    }

    @Transactional
    public void alterarSenhaAa(UsuarioAlterarSenhaDto usuarioAlterarSenhaDto) {
        Usuario usuario = findComplete(usuarioAlterarSenhaDto.getUsuarioId());
        usuario.setAlterarSenha(usuarioAlterarSenhaDto.getAlterarSenha());
        updateSenha(usuario, usuarioAlterarSenhaDto.getAlterarSenha());
        repository.save(usuario);
    }

    @Transactional
    public void alterarDadosAcessoEmail(UsuarioDadosAcessoRequest usuarioDadosAcessoRequest) {
        Usuario usuario = findComplete(usuarioDadosAcessoRequest.getUsuarioId());
        validarEmail(usuario, usuarioDadosAcessoRequest.getEmailAtual(), usuarioDadosAcessoRequest.getEmailNovo());
        usuario.setEmail(usuarioDadosAcessoRequest.getEmailNovo());
        repository.updateEmail(usuarioDadosAcessoRequest.getEmailNovo(), usuario.getId());
        notificacaoService.enviarEmailAtualizacaoEmail(usuario, usuarioDadosAcessoRequest);
        updateSenha(usuario, Eboolean.V);
    }

    private void updateSenha(Usuario usuario, Eboolean alterarSenha) {
        String senhaDescriptografada = getSenhaRandomica(MAX_CARACTERES_SENHA);
        repository.updateSenha(passwordEncoder.encode(senhaDescriptografada), alterarSenha, usuario.getId());
        notificacaoService.enviarEmailAtualizacaoSenha(usuario, senhaDescriptografada);
    }

    private void validarEmail(Usuario usuario, String emailAtual, String emailNovo) {
        if (!usuario.getEmail().equalsIgnoreCase(emailAtual)) {
            throw EMAIL_ATUAL_INCORRETO_EXCEPTION;
        }
        repository.findAllUsuarioByEmailIgnoreCase(emailNovo).forEach(u -> {
            if (usuario.isNovoCadastro() || !u.getId().equals(usuario.getId())) {
                throw EMAIL_CADASTRADO_EXCEPTION;
            }
        });
    }

    @Transactional
    public Integer alterarDadosAcessoSenha(UsuarioDadosAcessoRequest usuarioDadosAcessoRequest) {
        Usuario usuario;
        if (ObjectUtils.isEmpty(usuarioDadosAcessoRequest.getUsuarioId())) {
            usuario = autenticacaoService.getUsuarioAutenticado().getUsuario();
        } else {
            usuario = findComplete(usuarioDadosAcessoRequest.getUsuarioId());
        }
        if (ObjectUtils.isEmpty(usuarioDadosAcessoRequest.getIgnorarSenhaAtual())
                || !usuarioDadosAcessoRequest.getIgnorarSenhaAtual()) {
            validarSenhaAtual(usuario, usuarioDadosAcessoRequest.getSenhaAtual());
        }
        repository.updateSenha(passwordEncoder.encode(usuarioDadosAcessoRequest.getSenhaNova()),
                usuarioDadosAcessoRequest.getAlterarSenha(), usuario.getId());
        notificacaoService.enviarEmailAtualizacaoSenha(usuario, usuarioDadosAcessoRequest.getSenhaNova());
        return usuario.getId();
    }

    private void validarSenhaAtual(Usuario usuario, final String senhaAtual) {
        if (!BCrypt.checkpw(senhaAtual, usuario.getSenha())) {
            throw SENHA_ATUAL_INCORRETA_EXCEPTION;
        }
    }

    public ConfiguracaoResponse getConfiguracaoByUsuario() {
        Usuario usuario = repository.findComConfiguracao(autenticacaoService.getUsuarioId()).orElse(null);
        return usuario != null
                ? ConfiguracaoResponse.convertFrom(usuario.getConfiguracao())
                : new ConfiguracaoResponse();
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

    public UsuarioPermissaoResponse findPermissoesByUsuario(Integer idUsuario) {
        Usuario usuario = findComplete(idUsuario);

        return UsuarioPermissaoResponse.of(
                cargoDepartamentoFuncionalidadeRepository
                        .findFuncionalidadesPorCargoEDepartamento(
                                new FuncionalidadePredicate()
                                        .comCargo(usuario.getCargoId())
                                        .comDepartamento(usuario.getDepartamentoId()).build()),
                permissaoEspecialRepository.findPorUsuario(usuario.getId()));
    }

    private FuncionalidadePredicate getFuncionalidadePredicate(Usuario usuario) {
        return new FuncionalidadePredicate()
                .comCargo(usuario.getCargoId())
                .comDepartamento(usuario.getDepartamentoId());
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

    @Transactional
    public ConfiguracaoResponse adicionarConfiguracao(UsuarioConfiguracaoDto dto) {
        Configuracao configuracao = configuracaoRepository
                .findByUsuario(new Usuario(dto.getUsuario()))
                .orElse(new Configuracao());
        configuracao.configurar(dto);
        configuracao = configuracaoRepository.save(configuracao);
        return ConfiguracaoResponse.convertFrom(configuracao);
    }

    @Transactional
    public void removerConfiguracao(UsuarioConfiguracaoDto dto) {
        List<Configuracao> configuracao = configuracaoRepository.findByRamal(dto.getRamal());
        configuracao.forEach(c -> configuracaoRepository.delete(c));
    }

    @Transactional
    public void removerRamalConfiguracao(UsuarioConfiguracaoDto dto) {
        List<Configuracao> configuracao = configuracaoRepository.findByRamal(dto.getRamal());
        configuracao.forEach((config) -> {
            config.removerRamal();
            configuracaoRepository.save(config);
        });
    }

    @Transactional
    public void saveUsuarioHierarquia(List<UsuarioHierarquiaCarteiraDto> novasHierarquias) {
        List<UsuarioHierarquiaCarteiraDto> novasHierarquiasValidas = validaUsuarioHierarquiaExistente(novasHierarquias);

        novasHierarquiasValidas.forEach(u -> {
            UsuarioHierarquia usuarioHierarquia
                    = UsuarioHierarquia.criar(new Usuario(u.getUsuarioId()), u.getUsuarioSuperiorId(), u.getUsuarioCadastroId());
            usuarioHierarquiaRepository.save(usuarioHierarquia);
        });
    }

    private List<UsuarioHierarquiaCarteiraDto> validaUsuarioHierarquiaExistente(
            List<UsuarioHierarquiaCarteiraDto> novasHierarquias) {
        List<UsuarioHierarquia> usuarioHierarquiasExistentes
                = (List<UsuarioHierarquia>) usuarioHierarquiaRepository.findAll();
        return novasHierarquias
                .stream()
                .filter(c -> !validaUsuarioHierarquiaExistente(usuarioHierarquiasExistentes, c))
                .distinct()
                .collect(Collectors.toList());
    }

    private <T> boolean validaUsuarioHierarquiaExistente(List<UsuarioHierarquia> hierarquiasExistentes,
                                                         UsuarioHierarquiaCarteiraDto novaHierarquia) {
        return hierarquiasExistentes
                .stream()
                .anyMatch(e -> e.getUsuarioSuperior().getId().equals(novaHierarquia.getUsuarioSuperiorId())
                        && e.getUsuario().getId().equals(novaHierarquia.getUsuarioId()));
    }

    @Transactional
    public void alterarSituacao(UsuarioMqRequest usuario) {
        repository.updateSituacao(usuario.getSituacao(), usuario.getId());
    }

    public void ativarSocioPrincipal(String email) {
        Optional<UsuarioResponse> usuario = findByEmailAa(email);
        usuario.ifPresent(u -> {
            Optional<Usuario> usuarioCompleto = repository.findById(u.getId());
            usuarioCompleto.ifPresent(user -> {
                user.setSituacao(ATIVO);
                repository.save(user);
            });
        });
    }

    public void inativarSocioPrincipal(String email) {
        Optional<UsuarioResponse> usuario = findByEmailAa(email);
        usuario.ifPresent(u -> {
            Optional<Usuario> usuarioCompleto = repository.findById(usuario.get().getId());
            usuarioCompleto.ifPresent(user -> {
                user.setSituacao(INATIVO);
                repository.save(user);
            });
        });
    }

    public void inativarColaboradores(String cnpj) {
        List<String> emailColaboradores = agenteAutorizadoClient.recuperarColaboradoresDoAgenteAutorizado(cnpj);
        emailColaboradores.forEach(colaborador -> {
            Usuario usuario = repository.findByEmail(colaborador)
                    .orElseThrow(() -> new NotFoundException("Usuário não encontrado"));
            usuario.setSituacao(INATIVO);
            repository.save(usuario);
        });
    }

    public boolean situacaoAtiva(String email) {
        return agenteAutorizadoClient.recuperarSituacaoAgenteAutorizado(email);
    }

    public List<UsuarioHierarquiaResponse> getVendedoresOperacaoDaHierarquia(Integer usuarioId) {
        return repository.getSubordinadosPorCargo(usuarioId, CodigoCargo.VENDEDOR_OPERACAO.name())
                .stream()
                .map(this::criarUsuarioHierarquiaVendedoresResponse)
                .collect(Collectors.toList());
    }

    public List<Integer> getIdsVendedoresOperacaoDaHierarquia(Integer usuarioId) {
        return getVendedoresOperacaoDaHierarquia(usuarioId).stream()
                .map(UsuarioHierarquiaResponse::getId)
                .collect(Collectors.toList());
    }

    private UsuarioHierarquiaResponse criarUsuarioHierarquiaVendedoresResponse(Object[] param) {
        int indice = POSICAO_ZERO;
        return UsuarioHierarquiaResponse.builder()
                .id(objectToInteger(param[indice++]))
                .nome(objectToString(param[indice++]))
                .cargoNome(objectToString(param[indice++]))
                .build();
    }

    public List<UsuarioCsvResponse> getAllForCsv(UsuarioFiltros filtros) {
        UsuarioPredicate predicate = filtrarUsuariosPermitidos(filtros);
        return repository.getUsuariosCsv(predicate.build());
    }

    public void exportUsuariosToCsv(List<UsuarioCsvResponse> usuarios, HttpServletResponse response) {
        if (!CsvUtils.setCsvNoHttpResponse(
                getCsv(usuarios),
                CsvUtils.createFileName(USUARIOS_CSV.name()),
                response)) {
            throw new ValidacaoException("Falha ao tentar baixar relatório de usuários!");
        }
    }

    private UsuarioPredicate filtrarUsuariosPermitidos(UsuarioFiltros filtros) {
        UsuarioPredicate predicate = filtros.toPredicate();
        predicate.filtraPermitidos(autenticacaoService.getUsuarioAutenticado(), this);
        if (!StringUtils.isEmpty(filtros.getCnpjAa())) {
            obterUsuariosAa(filtros.getCnpjAa(), predicate, true);
        }
        return predicate;
    }

    private String getCsv(List<UsuarioCsvResponse> usuarios) {
        return UsuarioCsvResponse.getCabecalhoCsv()
                + (!usuarios.isEmpty()
                ? usuarios
                .stream()
                .map(UsuarioCsvResponse::toCsv)
                .collect(Collectors.joining("\n"))
                : "Registros não encontrados.");
    }

    public List<UsuarioResponse> getSupervisoresByCidades(List<Integer> cidades) {
        try {
            List<UsuarioResponse> usuariosExistenteEmEquipesVendas = equipeVendaService.getAllUsuariosEquipeVendas().stream()
                    .map(UsuarioResponse::convertEquipeVendasUsuario)
                    .collect(Collectors.toList());
            List<UsuarioResponse> usuarios = repository.getUsuariosByCidades(cidades).stream()
                    .map(UsuarioResponse::convertFrom)
                    .filter(usuario -> usuario.getCodigoCargo().name().equals(CodigoCargoOperacao.SUPERVISOR_OPERACAO.name()))
                    .distinct()
                    .collect(Collectors.toList());
            return retornarVendedoresSemEquipeVendas(usuarios, usuariosExistenteEmEquipesVendas);
        } catch (Exception ex) {
            log.error("Erro - Cargo Inválido.", ex);
            throw new ValidacaoException("Erro - Cargo Inválido");
        }
    }

    public List<UsuarioResponse> getUsuariosByCidades(List<Integer> cidades) {
        try {
            List<UsuarioResponse> usuariosExistenteEmEquipesVendas = equipeVendaService.getAllUsuariosEquipeVendas().stream()
                    .map(UsuarioResponse::convertEquipeVendasUsuario)
                    .collect(Collectors.toList());
            if (cidades.size() > MAXIMO) {
                List<Integer> segundaLista = cidades.subList(MINIMO, cidades.size());
                cidades = cidades.subList(0, MAXIMO);
                List<UsuarioResponse> listaUsuario = getUsuariosByCidades(segundaLista);
                List<UsuarioResponse> listaUsuarioComplementar = getUsuariosByCidades(cidades);
                listaUsuario.addAll(listaUsuarioComplementar);
            }
            List<UsuarioResponse> usuarios = repository.getUsuariosByCidades(cidades).stream()
                    .map(UsuarioResponse::convertFrom)
                    .filter(usuario -> !usuario.getCodigoCargo().name().equals(CodigoCargoOperacao.SUPERVISOR_OPERACAO.name()))
                    .distinct()
                    .collect(Collectors.toList());
            return retornarVendedoresSemEquipeVendas(usuarios, usuariosExistenteEmEquipesVendas);
        } catch (Exception ex) {
            log.error("Erro - Cargo Inválido.", ex);
            throw new ValidacaoException("Erro - Cargo Inválido");
        }
    }

    public List<UsuarioResponse> retornarVendedoresSemEquipeVendas(List<UsuarioResponse> usuarios,
                                                                   List<UsuarioResponse> usuariosExistenteEmEquipesVendas) {
        return usuarios.stream()
                .filter(usuario -> !usuariosExistenteEmEquipesVendas.stream()
                        .anyMatch(usuarioExistente ->
                                usuarioExistente.getId().equals(usuario.getId())
                                        && usuarioExistente.getCodigoCargo().name()
                                        .equalsIgnoreCase(CodigoCargoOperacao.VENDEDOR_OPERACAO.name())))
                .collect(Collectors.toList());
    }

    public List<UsuarioPermissaoCanal> getPermissoesUsuarioAutenticadoPorCanal() {
        return funcionalidadeService
                .getFuncionalidadesPermitidasAoUsuarioComCanal(
                        findCompleteById(autenticacaoService.getUsuarioId()))
                .stream()
                .map(UsuarioPermissaoCanal::of)
                .collect(Collectors.toList());
    }

    public List<Integer> getIdsSubordinadosDaHierarquia(Integer usuarioId, String codigoCargo) {
        return repository.getSubordinadosPorCargo(usuarioId, codigoCargo)
                .stream()
                .map(row -> objectToInteger(row[POSICAO_ZERO]))
                .collect(Collectors.toList());
    }

    public List<UsuarioResponse> getUsuariosBySupervisor(Integer id) {
        Usuario usuario = repository.findById(id).orElseThrow(() -> EX_NAO_ENCONTRADO);
        List<Integer> cidades = usuario.getCidades().stream()
                .map(user -> user.getCidade().getId())
                .collect(Collectors.toList());

        return getUsuariosByCidades(cidades);
    }
}
