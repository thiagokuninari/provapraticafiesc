package br.com.xbrain.autenticacao.modules.usuario.service;

import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.comum.dto.EmpresaResponse;
import br.com.xbrain.autenticacao.modules.comum.dto.PageRequest;
import br.com.xbrain.autenticacao.modules.comum.dto.SelectResponse;
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
import br.com.xbrain.autenticacao.modules.equipevenda.dto.EquipeVendaUsuarioResponse;
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
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoDepartamento;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoNivel;
import br.com.xbrain.autenticacao.modules.usuario.enums.ECanal;
import br.com.xbrain.autenticacao.modules.usuario.model.*;
import br.com.xbrain.autenticacao.modules.usuario.predicate.UsuarioPredicate;
import br.com.xbrain.autenticacao.modules.usuario.rabbitmq.*;
import br.com.xbrain.autenticacao.modules.usuario.repository.*;
import br.com.xbrain.xbrainutils.CsvUtils;
import com.google.common.collect.Sets;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.NumberUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static br.com.xbrain.autenticacao.modules.comum.enums.RelatorioNome.USUARIOS_CSV;
import static br.com.xbrain.autenticacao.modules.usuario.enums.CodigoMotivoInativacao.DEMISSAO;
import static br.com.xbrain.xbrainutils.NumberUtils.getOnlyNumbers;
import static com.google.common.collect.Lists.partition;
import static java.util.Collections.emptyList;
import static org.springframework.util.CollectionUtils.isEmpty;
import static org.springframework.util.ObjectUtils.isEmpty;

@Service
@Slf4j
@SuppressWarnings("PMD.TooManyStaticImports")
public class UsuarioService {

    private static final Integer QTD_MAX_IN_NO_ORACLE = 1000;
    private static final int POSICAO_ZERO = 0;
    private static final int MAX_CARACTERES_SENHA = 6;
    private static final ValidacaoException EX_NAO_ENCONTRADO = new ValidacaoException("Usuário não encontrado.");
    private static final int MAXIMO_PARAMETROS_IN = 1000;
    private static final ESituacao ATIVO = ESituacao.A;
    private static final ESituacao INATIVO = ESituacao.I;
    private static final String MSG_ERRO_AO_ATIVAR_USUARIO =
        "Erro ao ativar, o agente autorizado está inativo ou descredenciado.";
    private static ValidacaoException EMAIL_CADASTRADO_EXCEPTION = new ValidacaoException("Email já cadastrado.");
    private static ValidacaoException EMAIL_ATUAL_INCORRETO_EXCEPTION
        = new ValidacaoException("Email atual está incorreto.");
    private static ValidacaoException SENHA_ATUAL_INCORRETA_EXCEPTION
        = new ValidacaoException("Senha atual está incorreta.");

    @Autowired
    @Setter
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
    private MotivoInativacaoService motivoInativacaoService;
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
    private EntityManager entityManager;
    @Autowired
    private FileService fileService;
    @Autowired
    private FuncionalidadeService funcionalidadeService;
    @Autowired
    private UsuarioEquipeVendaMqSender equipeVendaMqSender;
    @Autowired
    private EquipeVendaService equipeVendaService;
    @Autowired
    private UsuarioFeriasService usuarioFeriasService;
    @Autowired
    private UsuarioAfastamentoService usuarioAfastamentoService;
    @Autowired
    private UsuarioFeederCadastroSucessoMqSender usuarioFeederCadastroSucessoMqSender;

    public Usuario findComplete(Integer id) {
        Usuario usuario = repository.findComplete(id).orElseThrow(() -> EX_NAO_ENCONTRADO);
        usuario.forceLoad();
        return usuario;
    }

    @Transactional
    public Usuario findByIdCompleto(int id) {
        return repository.findOne(
            new UsuarioPredicate()
                .ignorarAa(true)
                .comId(id)
                .build())
            .forceLoad();
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
        return repository.findOne(
            new UsuarioPredicate()
                .comId(id)
                .build())
            .forceLoad();
    }

    public List<UsuarioResponse> buscarColaboradoresAtivosOperacaoComericialPorCargo(Integer cargoId) {
        return repository.findUsuariosAtivosOperacaoComercialByCargoId(cargoId);
    }

    public List<CidadeResponse> findCidadesByUsuario(int usuarioId) {
        return repository.findComCidade(usuarioId)
                .orElseThrow(() -> EX_NAO_ENCONTRADO)
            .stream()
            .map(CidadeResponse::parse)
            .collect(Collectors.toList());
    }

    public Usuario findCompleteById(int id) {
        return repository.findComplete(id).orElseThrow(() -> EX_NAO_ENCONTRADO);
    }

    @Transactional
    public UsuarioDto findByEmail(String email) {
        return UsuarioDto.of(repository.findByEmail(email).orElseThrow(() -> EX_NAO_ENCONTRADO));
    }

    public Optional<UsuarioResponse> findByEmailAa(String email) {
        Optional<Usuario> usuarioOptional = repository.findByEmail(email);

        return usuarioOptional.map(UsuarioResponse::of);
    }

    public Optional<UsuarioResponse> findByCpfAa(String cpf) {
        return repository
            .findTop1UsuarioByCpf(getOnlyNumbers(cpf))
            .map(UsuarioResponse::of);
    }

    public List<EmpresaResponse> findEmpresasDoUsuario(Integer idUsuario) {
        Usuario usuario = findComplete(idUsuario);
        return usuario.getEmpresas().stream().map(EmpresaResponse::convertFrom).collect(Collectors.toList());
    }

    public Page<Usuario> getAll(PageRequest pageRequest, UsuarioFiltros filtros) {
        UsuarioPredicate predicate = filtrarUsuariosPermitidos(filtros);
        Page<Usuario> pages = repository.findAll(predicate.build(), pageRequest);
        if (!isEmpty(pages.getContent())) {
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
        return UsuarioDto.of(repository.save(usuario));
    }

    private UsuarioHierarquia criarUsuarioHierarquia(Usuario usuario, Integer idHierarquia) {
        return UsuarioHierarquia.criar(usuario, idHierarquia, autenticacaoService.getUsuarioId());
    }

    public List<Integer> getIdDosUsuariosSubordinados(Integer usuarioId, Boolean incluirProprio) {
        List<Integer> usuariosSubordinados = repository.getUsuariosSubordinados(usuarioId);
        if (incluirProprio) {
            usuariosSubordinados.add(usuarioId);
        }
        return usuariosSubordinados;
    }

    public List<UsuarioSubordinadoDto> getSubordinadosDoUsuario(Integer usuarioId) {
        return repository.getUsuariosCompletoSubordinados(usuarioId);
    }

    public List<UsuarioAutoComplete> getSubordinadosDoGerenteComCargoExecutivoOrExecutivoHunter(Integer usuarioId) {
        return repository.getSubordinadosDoGerenteComCargoExecutivoOrExecutivoHunter(usuarioId);
    }

    public List<UsuarioAutoComplete> findAllExecutivosOperacaoDepartamentoComercial() {
        return repository.findAllExecutivosOperacaoDepartamentoComercial();
    }

    public List<UsuarioAutoComplete> findExecutivosPorIds(List<Integer> idsPermitidos) {
        var usuarioLogado = autenticacaoService.getUsuarioAutenticado();
        if (usuarioLogado.isCoordenadorOperacao() || usuarioLogado.isGerenteOperacao()) {
            return repository
                .findAllExecutivosDosIdsCoordenadorGerente(idsPermitidos, usuarioLogado.getId());
        }
        return repository.findAllExecutivosDosIds(idsPermitidos);
    }

    public List<UsuarioHierarquiaResponse> getSuperioresDoUsuario(Integer usuarioId) {
        return repository.getSuperioresDoUsuario(usuarioId)
            .stream().map(UsuarioHierarquiaResponse::new)
            .collect(Collectors.toList());
    }

    public List<UsuarioHierarquiaResponse> getSuperioresDoUsuarioPorCargo(Integer usuarioId, CodigoCargo codigoCargo) {
        return repository.getSuperioresDoUsuarioPorCargo(usuarioId, codigoCargo)
            .stream().map(UsuarioHierarquiaResponse::new)
            .collect(Collectors.toList());
    }

    @Transactional
    public UsuarioDto save(Usuario request, MultipartFile foto) {
        if (!isEmpty(foto)) {
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
        return UsuarioDto.of(usuario);
    }

    @Transactional
    public UsuarioDto save(Usuario usuario) {
        try {
            validar(usuario);

            boolean enviarEmail = false;
            String senhaDescriptografada = getSenhaRandomica(MAX_CARACTERES_SENHA);
            if (usuario.isNovoCadastro()) {
                configurar(usuario, senhaDescriptografada);
                enviarEmail = true;
            } else {
                atualizarUsuariosParceiros(usuario);
                usuario.setAlterarSenha(Eboolean.F);
            }
            repository.save(usuario);
            entityManager.flush();

            tratarHierarquiaUsuario(usuario, usuario.getHierarquiasId());
            tratarCidadesUsuario(usuario);

            if (enviarEmail) {
                notificacaoService.enviarEmailDadosDeAcesso(usuario, senhaDescriptografada);
            }
            return UsuarioDto.of(usuario);
        } catch (PersistenceException ex) {
            log.error("Erro de persistência ao salvar o Usuario.", ex.getMessage());
            throw new ValidacaoException("Erro ao cadastrar usuário.");
        } catch (Exception ex) {
            log.error("Erro ao salvar Usuário.", ex);
            throw ex;
        }
    }

    @Transactional
    public void salvarUsuarioFeeder(UsuarioFeederMqDto usuarioDto) {
        try {
            validarCpfCadastrado(usuarioDto.getCpf(), usuarioDto.getUsuarioId());
            validarEmailCadastrado(usuarioDto.getEmail(), usuarioDto.getUsuarioId());

            var usuario = new Usuario();
            boolean enviarEmail = false;
            String senhaDescriptografada = getSenhaRandomica(MAX_CARACTERES_SENHA);

            if (usuarioDto.isNovoCadastro()) {
                usuario = criarUsuarioFeederNovo(usuarioDto);
                configurarSenhaUsuarioFeeder(usuario, senhaDescriptografada);
                enviarEmail = true;
            } else {
                usuario = criarUsuarioFeeder(usuarioDto);
            }

            usuario = repository.save(usuario);
            salvarUsuarioCadastroCasoAutocadastro(usuario);
            entityManager.flush();

            if (enviarEmail) {
                notificacaoService.enviarEmailDadosDeAcesso(usuario, senhaDescriptografada);
                usuarioFeederCadastroSucessoMqSender.sendCadastroSuccessoMensagem(
                    UsuarioCadastroSucessoMqDto.of(usuario, usuarioDto));
            }

        } catch (PersistenceException ex) {
            log.error("Erro de persistência ao salvar o Usuario. ", ex);
            throw new ValidacaoException("Erro ao cadastrar usuário.");
        } catch (Exception ex) {
            log.error("Erro ao salvar Usuário.", ex);
            throw ex;
        }
    }

    private Usuario criarUsuarioFeeder(UsuarioFeederMqDto usuarioDto) {
        var usuario = findCompleteById(usuarioDto.getUsuarioId());
        BeanUtils.copyProperties(usuarioDto, usuario);
        return usuario;
    }

    private Usuario criarUsuarioFeederNovo(UsuarioFeederMqDto usuarioDto) {
        var usuario = UsuarioFeederMqDto.criarUsuarioNovo(usuarioDto);
        usuario.setCargo(getCargo(usuarioDto.getTipoGerador()));
        usuario.setDepartamento(departamentoRepository.findByCodigo(CodigoDepartamento.FEEDER));
        usuario.setUnidadesNegocios(unidadeNegocioRepository
            .findByCodigoIn(List.of(CodigoUnidadeNegocio.RESIDENCIAL_COMBOS)));
        usuario.setEmpresas(empresaRepository.findByCodigoIn(List.of(CodigoEmpresa.NET, CodigoEmpresa.CLARO_TV)));
        usuario.setCanais(Sets.newHashSet(ECanal.AGENTE_AUTORIZADO));
        return usuario;
    }

    private void salvarUsuarioCadastroCasoAutocadastro(Usuario usuario) {
        if (isEmpty(usuario.getUsuarioCadastro())) {
            usuario.setUsuarioCadastro(new Usuario(usuario.getId()));
            repository.save(usuario);
        }
    }

    private void configurarSenhaUsuarioFeeder(Usuario usuario, String senhaDescriptografada) {
        usuario.setSenha(passwordEncoder.encode(senhaDescriptografada));
        usuario.setAlterarSenha(Eboolean.V);
    }

    public void salvarUsuarioRealocado(Usuario usuario) {
        Usuario usuarioARealocar = repository.findById(usuario.getId()).orElseThrow(() -> EX_NAO_ENCONTRADO);
        usuarioARealocar.setSituacao(ESituacao.R);
        repository.save(usuarioARealocar);
    }

    private Usuario criaNovoUsuarioAPartirDoRealocado(Usuario usuario) {
        Usuario usuarioCopia = new Usuario();
        BeanUtils.copyProperties(usuario, usuarioCopia);
        if (!isEmpty(repository.findAllByCpf(usuario.getCpf()))
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

    private Usuario getUsuarioAtivacao(UsuarioAtivacaoDto usuarioAtivacaoDto) {
        return Objects.nonNull(usuarioAtivacaoDto.getIdUsuarioAtivacao())
            ? new Usuario(usuarioAtivacaoDto.getIdUsuarioAtivacao())
            : new Usuario(autenticacaoService.getUsuarioId());
    }

    private void atualizarUsuariosParceiros(Usuario usuario) {
        cargoRepository.findById(usuario.getCargoId()).ifPresent(cargo -> {
            Optional<Usuario> usuarioAtualizar = repository.findById(usuario.getId());
            if (isSocioPrincipal(cargo.getCodigo()) && usuarioAtualizar.isPresent()) {
                UsuarioDto usuarioDto = UsuarioDto.of(usuarioAtualizar.get());
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

    public boolean validarSeUsuarioCpfEmailNaoCadastrados(UsuarioExistenteValidacaoRequest usuario) {
        validarCpfCadastrado(usuario.getCpf(), usuario.getId());
        validarEmailCadastrado(usuario.getEmail(), usuario.getId());
        return true;
    }

    private void validarCpfCadastrado(String cpf, Integer usuarioId) {
        repository.findTop1UsuarioByCpfAndSituacaoNot(getOnlyNumbers(cpf), ESituacao.R)
            .ifPresent(usuario -> {
                if (isEmpty(usuarioId)
                    || !usuarioId.equals(usuario.getId())) {
                    throw new ValidacaoException("CPF já cadastrado.");
                }
            });
    }

    private void validarEmailCadastrado(String email, Integer usuarioId) {
        repository.findTop1UsuarioByEmailIgnoreCaseAndSituacaoNot(email, ESituacao.R)
            .ifPresent(usuario -> {
                if (isEmpty(usuarioId)
                    || !usuarioId.equals(usuario.getId())) {
                    throw new ValidacaoException("Email já cadastrado.");
                }
            });
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
        if (isEmpty(hierarquiasId)) {
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
        if (!isEmpty(subordinados)) {
            usuarioHierarquiaRepository.delete(subordinados);
        }
    }

    private void adicionarUsuarioSuperior(Usuario usuario, List<Integer> hierarquiasId) {
        if (!isEmpty(hierarquiasId)) {
            hierarquiasId
                .forEach(idHierarquia -> usuario.adicionarHierarquia(criarUsuarioHierarquia(usuario, idHierarquia)));
        }
    }

    public void hierarquiaIsValida(Usuario usuario) {
        if (!isEmpty(usuario)
            && !isEmpty(usuario.getUsuariosHierarquia())) {

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
        return !isEmpty(usuarioParaAchar)
            && !isEmpty(usuarioParaAchar.getUsuariosHierarquia())
            && !isEmpty(usuario)
            && !isEmpty(usuario.getUsuarioSuperior());
    }

    private boolean verificarUsuariosHierarquia(Usuario usuarioParaAchar, UsuarioHierarquia usuario) {
        return usuarioParaAchar.getId().equals(usuario.getUsuarioSuperiorId());
    }

    private List<Integer> getIdSuperiores(Usuario usuario) {

        return usuario.getUsuariosHierarquia()
            .stream()
            .map(UsuarioHierarquia::getUsuarioSuperiorId)
            .filter(item -> !isEmpty(item))
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

    private void tratarCidadesUsuario(Usuario usuario) {
        var cidadesAtuais = Sets.newHashSet(usuarioCidadeRepository.findCidadesIdByUsuarioId(usuario.getId()));
        var cidadesModificadas = Sets.newHashSet(isEmpty(usuario.getCidadesId()) ? emptyList() : usuario.getCidadesId());
        var cidadesRemovidas = Sets.difference(cidadesAtuais, cidadesModificadas);
        var cidadesAdicionadas = Sets.difference(cidadesModificadas, cidadesAtuais);
        removerUsuarioCidade(usuario, cidadesRemovidas);
        adicionarUsuarioCidade(usuario, cidadesAdicionadas);
    }

    private void removerUsuarioCidade(Usuario usuario, Set<Integer> cidadesId) {
        cidadesId.forEach(cidadeId -> usuarioCidadeRepository.deleteByCidadeAndUsuario(cidadeId, usuario.getId()));
    }

    private void adicionarUsuarioCidade(Usuario usuario, Set<Integer> cidadesId) {
        if (!isEmpty(cidadesId)) {
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
            if (!isAlteracaoCpf(UsuarioDto.convertFrom(usuarioDto))) {
                configurarUsuario(usuarioMqRequest, usuarioDto);
                save(UsuarioDto.convertFrom(usuarioDto), usuarioMqRequest.isRealocado());
            } else {
                saveUsuarioAlteracaoCpf(UsuarioDto.convertFrom(usuarioDto));
            }
        } catch (Exception ex) {
            usuarioMqRequest.setException(ex.getMessage());
            enviarParaFilaDeErroAtualizacaoUsuarios(usuarioMqRequest);
            log.error("erro ao atualizar usuário da fila.", ex);
        }
    }

    public boolean isAlteracaoCpf(Usuario usuario) {
        Usuario usuarioCpfAntigo = repository.findById(usuario.getId())
            .orElseThrow(() -> new ValidacaoException("Usuário não encontrado"));
        usuario.removerCaracteresDoCpf();
        return !isEmpty(usuario.getCpf()) && !usuario.getCpf().equals(usuarioCpfAntigo.getCpf());
    }

    @Transactional
    public void saveUsuarioAlteracaoCpf(Usuario usuario) {
        validarCpfExistente(usuario);
        usuario.removerCaracteresDoCpf();
        repository.updateCpf(usuario.getCpf(), usuario.getId());
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
                .map(UsuarioDto::of)
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
                if (isEmpty(usuario.getId())
                    || !usuario.getId().equals(u.getId())) {
                    throw new ValidacaoException("CPF já cadastrado.");
                }
            });
    }

    private void validarEmailExistente(Usuario usuario) {
        repository
            .findTop1UsuarioByEmailIgnoreCaseAndSituacaoNot(usuario.getEmail(), ESituacao.R)
            .ifPresent(u -> {
                if (isEmpty(usuario.getId())
                    || !usuario.getId().equals(u.getId())) {
                    throw new ValidacaoException("Email já cadastrado.");
                }
            });
    }

    @Transactional
    public void ativar(UsuarioAtivacaoDto dto) {
        var usuario = findComplete(dto.getIdUsuario());
        usuario.setSituacao(ESituacao.A);
        validarAtivacao(usuario);
        usuario.adicionarHistorico(
            UsuarioHistorico.criarHistoricoAtivacao(
                getUsuarioAtivacao(dto),
                dto.getObservacao(),
                usuario));
        repository.save(usuario);
        usuarioAfastamentoService.atualizaDataFimAfastamento(usuario.getId());

    }

    private void validarAtivacao(Usuario usuario) {
        if (isEmpty(usuario.getCpf())) {
            throw new ValidacaoException("O usuário não pode ser ativado por não possuir CPF.");
        } else if (usuario.isSocioPrincipal() && !encontrouAgenteAutorizadoBySocioEmail(usuario.getEmail())) {
            throw new ValidacaoException(MSG_ERRO_AO_ATIVAR_USUARIO
                + " Ou email do sócio está divergente do que está inserido no agente autorizado.");
        } else if (!usuario.isSocioPrincipal() && usuario.isAgenteAutorizado()
            && !encontrouAgenteAutorizadoByUsuarioId(usuario.getId())) {
            throw new ValidacaoException(MSG_ERRO_AO_ATIVAR_USUARIO);
        }
        repository.save(usuario);
    }

    private boolean encontrouAgenteAutorizadoByUsuarioId(Integer usuarioId) {
        return agenteAutorizadoService.existeAaAtivoByUsuarioId(usuarioId);
    }

    private boolean encontrouAgenteAutorizadoBySocioEmail(String usuarioEmail) {
        return agenteAutorizadoService.existeAaAtivoBySocioEmail(usuarioEmail);
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
    public void inativar(UsuarioInativacaoDto usuarioInativacao) {
        Usuario usuario = findComplete(usuarioInativacao.getIdUsuario());
        usuario.setSituacao(ESituacao.I);
        usuario.adicionarHistorico(UsuarioHistorico.builder()
                .dataCadastro(LocalDateTime.now())
                .motivoInativacao(carregarMotivoInativacao(usuarioInativacao))
                .usuario(usuario)
                .usuarioAlteracao(getUsuarioInativacaoTratado(usuarioInativacao))
                .observacao(usuarioInativacao.getObservacao())
                .situacao(ESituacao.I)
                .ferias(usuarioFeriasService
                        .save(usuario, usuarioInativacao).orElse(null))
                .afastamento(usuarioAfastamentoService
                        .save(usuario, usuarioInativacao).orElse(null))
                .build());
        inativarUsuarioNaEquipeVendas(usuario, carregarMotivoInativacao(usuarioInativacao));
        removerHierarquiaDoUsuarioEquipe(usuario, carregarMotivoInativacao(usuarioInativacao));
        repository.save(usuario);
    }

    private void removerHierarquiaDoUsuarioEquipe(Usuario usuario, MotivoInativacao motivoInativacao) {
        if (usuario.isUsuarioEquipeVendas() && motivoInativacao.getCodigo().equals(DEMISSAO)) {
            repository.deleteUsuarioHierarquia(usuario.getId());
        }
    }

    private Usuario getUsuarioInativacaoTratado(UsuarioInativacaoDto usuario) {
        return autenticacaoService
            .getUsuarioAutenticadoId()
            .map(Usuario::new)
            .orElseGet(() -> new Usuario(usuario.getIdUsuarioInativacao()));
    }

    private void inativarUsuarioNaEquipeVendas(Usuario usuario, MotivoInativacao motivoInativacao) {
        if (usuario.isUsuarioEquipeVendas() && motivoInativacao.getCodigo().equals(DEMISSAO)) {
            equipeVendaMqSender.sendInativar(UsuarioEquipeVendasDto.createFromUsuario(usuario));
        }
    }

    private MotivoInativacao carregarMotivoInativacao(UsuarioInativacaoDto dto) {
        return motivoInativacaoService.findByCodigoMotivoInativacao(dto.getCodigoMotivoInativacao());
    }

    public List<UsuarioHierarquiaResponse> getUsuariosHierarquia(Integer nivelId) {
        UsuarioPredicate usuarioPredicate = new UsuarioPredicate();
        usuarioPredicate.filtraPermitidos(autenticacaoService.getUsuarioAutenticado(), this);
        usuarioPredicate.comNivel(Collections.singletonList(nivelId));
        return repository.findAllUsuariosHierarquia(usuarioPredicate.build());
    }

    public List<Usuario> getUsuariosCargoSuperior(Integer cargoId, List<Integer> cidadesId) {
        return repository.getUsuariosFilter(
            new UsuarioPredicate()
                .filtraPermitidos(autenticacaoService.getUsuarioAutenticado(), this)
                .comCargos(cargoService.findById(cargoId).getCargosSuperioresId())
                .comCidade(cidadesId)
                .build());
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
            .map(UsuarioDto::of)
            .collect(Collectors.toList());
    }

    private void montarPredicateComCidade(UsuarioPredicate predicate, UsuarioFiltrosDto filtro) {

        List<List<Integer>> listaPartes = ListUtil.divideListaEmListasMenores(filtro.getCidadesIds(), MAXIMO_PARAMETROS_IN);

        listaPartes.forEach(lista -> predicate.comCidade(lista));
    }

    public List<UsuarioResponse> getUsuariosByIds(List<Integer> idsUsuarios) {
        List<Usuario> usuarios = repository.findBySituacaoAndIdIn(ESituacao.A, idsUsuarios);
        return usuarios.stream()
            .map(UsuarioResponse::of)
            .collect(Collectors.toList());
    }

    public List<UsuarioResponse> getUsuariosInativosByIds(List<Integer> usuariosInativosIds) {
        var usuarios = repository.findBySituacaoAndIdIn(ESituacao.I, usuariosInativosIds);

        return usuarios.stream()
                .map(UsuarioResponse::of)
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
        return repository.getUsuariosSuperiores(usuarioFiltrosHierarquia);
    }

    public List<UsuarioAutoComplete> findAllLideresComerciaisDoExecutivo(Integer executivoId) {
        return repository.findAllLideresComerciaisDoExecutivo(executivoId)
            .stream()
            .map(UsuarioAutoComplete::of)
            .collect(Collectors.toList());
    }

    public List<UsuarioSuperiorAutoComplete> getUsuariosSupervisoresDoAaAutoComplete(Integer executivoId) {
        return repository.getUsuariosSuperioresDoExecutivoDoAa(executivoId)
            .stream()
            .map(UsuarioSuperiorAutoComplete::of)
            .collect(Collectors.toList());
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
        return UsuarioResponse.of(usuarioHierarquia.getUsuarioSuperior());
    }

    public List<UsuarioResponse> getUsuarioSuperiores(Integer idUsuario) {
        List<UsuarioHierarquia> usuariosHierarquia = repository.getUsuarioSuperiores(idUsuario);
        return usuariosHierarquia
            .stream()
            .map(uh -> UsuarioResponse.of(uh.getUsuarioSuperior()))
            .collect(Collectors.toList());
    }

    public List<UsuarioResponse> getUsuarioByPermissao(String funcionalidade) {
        List<PermissaoEspecial> permissoes = repository.getUsuariosByPermissao(funcionalidade);
        return permissoes.stream()
            .map(PermissaoEspecial::getUsuario)
            .map(UsuarioResponse::of)
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
        if (isEmpty(usuarioDadosAcessoRequest.getUsuarioId())) {
            usuario = autenticacaoService.getUsuarioAutenticado().getUsuario();
        } else {
            usuario = findComplete(usuarioDadosAcessoRequest.getUsuarioId());
        }
        if (isEmpty(usuarioDadosAcessoRequest.getIgnorarSenhaAtual())
            || !usuarioDadosAcessoRequest.getIgnorarSenhaAtual()) {
            validarSenhaAtual(usuario, usuarioDadosAcessoRequest.getSenhaAtual());
        }
        repository.updateSenha(passwordEncoder.encode(usuarioDadosAcessoRequest.getSenhaNova()),
            usuarioDadosAcessoRequest.getAlterarSenha(), usuario.getId());
        notificacaoService.enviarEmailAtualizacaoSenha(usuario, usuarioDadosAcessoRequest.getSenhaNova());
        autenticacaoService.forcarLogoutGeradorLeads(usuario);
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

        return findPermissoesByUsuario(usuario);
    }

    public UsuarioPermissaoResponse findPermissoesByUsuario(Usuario usuario) {
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
            .map(UsuarioResponse::of).collect(Collectors.toList());
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
        configuracao.forEach(config -> {
            config.removerRamal();
            configuracaoRepository.save(config);
        });
    }

    @Transactional
    public void removerRamaisDeConfiguracao(List<UsuarioConfiguracaoDto> usuarioConfiguracaoDtoList) {
        if (!usuarioConfiguracaoDtoList.isEmpty()) {
            usuarioConfiguracaoDtoList.forEach(usuarioConfig -> {
                var configuracao = configuracaoRepository.findByRamal(usuarioConfig.getRamal());
                configuracao.forEach(config -> {
                    config.removerRamal();
                    configuracaoRepository.save(config);
                });
            });
        }
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

    public List<Integer> getUsuariosPermitidosPelaEquipeDeVenda() {
        return IntStream.concat(
            equipeVendaService
                .getUsuariosPermitidos(List.of(
                    CodigoCargo.SUPERVISOR_OPERACAO,
                    CodigoCargo.ASSISTENTE_OPERACAO,
                    CodigoCargo.VENDEDOR_OPERACAO
                ))
                .stream()
                .mapToInt(EquipeVendaUsuarioResponse::getUsuarioId),
            IntStream.of(autenticacaoService.getUsuarioId()))
            .boxed()
            .collect(Collectors.toList());
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

    public List<SelectResponse> getSubclusterUsuario(Integer usuarioId) {
        return repository
            .getSubclustersUsuario(usuarioId)
            .stream()
            .map(s -> SelectResponse.convertFrom(s.getId(), s.getNomeComMarca()))
            .collect(Collectors.toList());
    }

    public List<UsuarioPermissoesResponse> findUsuariosByPermissoes(UsuarioPermissoesRequest usuarioPermissoesRequest) {
        return repository.getUsuariosIdAndPermissoes(usuarioPermissoesRequest.getUsuariosId(),
            usuarioPermissoesRequest.getPermissoesWithoutPrefixRole());
    }

    public void reativarUsuariosInativosComFeriasTerminando(LocalDate dataFinalFerias) {
        usuarioFeriasService.getUsuariosInativosComFeriasEmAberto(dataFinalFerias)
            .forEach(usuario -> ativar(
                UsuarioAtivacaoDto
                    .builder()
                    .idUsuario(usuario.getId())
                    .observacao("USUÁRIO REATIVADO AUTOMATICAMENTE DEVIDO AO TÉRMINO DE FÉRIAS")
                    .idUsuarioAtivacao(usuario.getId())
                    .build()));
    }

    public void reativarUsuariosInativosComAfastamentoTerminando(LocalDate dataFimAfastamento) {
        usuarioAfastamentoService.getUsuariosInativosComAfastamentoEmAberto(dataFimAfastamento)
                .forEach(usuario -> ativar(
                        UsuarioAtivacaoDto
                                .builder()
                                .idUsuario(usuario.getId())
                                .observacao("USUÁRIO REATIVADO AUTOMATICAMENTE DEVIDO AO TÉRMINO DO AFASTAMENTO")
                                .idUsuarioAtivacao(usuario.getId())
                                .build()
                ));
    }

    @Transactional
    public void atualizarDataUltimoAcesso(Integer id) {
        var dataUltimoAcesso = LocalDateTime.now();
        repository.atualizarDataUltimoAcesso(dataUltimoAcesso, id);
        atualizarUsuarioMqSender.sendUltimoAcessoPol(new UsuarioUltimoAcessoPol(id, dataUltimoAcesso));
    }

    public List<UsuarioExecutivoResponse> buscarExecutivosPorSituacao(ESituacao situacao) {
        return repository.findAllExecutivosBySituacao(situacao);
    }

    public List<UsuarioSituacaoResponse> findUsuariosByIds(List<Integer> usuariosIds) {
        return partition(usuariosIds, QTD_MAX_IN_NO_ORACLE)
                .stream()
                .map(ids -> repository.findUsuariosByIds(ids))
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    public UsuarioResponse findById(Integer id) {
        return repository.findById(id)
            .map(UsuarioResponse::of)
            .orElseThrow(() -> EX_NAO_ENCONTRADO);
    }

    public List<UsuarioResponse> findUsuariosByCodigoCargo(CodigoCargo codigoCargo) {
        return repository.findUsuariosByCodigoCargo(codigoCargo).stream()
            .map(UsuarioResponse::of)
            .collect(Collectors.toList());
    }
}
