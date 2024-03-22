package br.com.xbrain.autenticacao.modules.usuario.service;

import br.com.xbrain.autenticacao.modules.agenteautorizado.dto.UsuarioDtoVendas;
import br.com.xbrain.autenticacao.modules.agenteautorizado.service.AgenteAutorizadoService;
import br.com.xbrain.autenticacao.modules.agenteautorizado.service.PermissaoTecnicoIndicadorService;
import br.com.xbrain.autenticacao.modules.autenticacao.dto.UsuarioAutenticado;
import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.comum.dto.EmpresaResponse;
import br.com.xbrain.autenticacao.modules.comum.dto.PageRequest;
import br.com.xbrain.autenticacao.modules.comum.dto.SelectResponse;
import br.com.xbrain.autenticacao.modules.comum.enums.*;
import br.com.xbrain.autenticacao.modules.comum.exception.NotFoundException;
import br.com.xbrain.autenticacao.modules.comum.exception.ValidacaoException;
import br.com.xbrain.autenticacao.modules.comum.model.Empresa;
import br.com.xbrain.autenticacao.modules.comum.model.UnidadeNegocio;
import br.com.xbrain.autenticacao.modules.comum.repository.EmpresaRepository;
import br.com.xbrain.autenticacao.modules.comum.repository.UnidadeNegocioRepository;
import br.com.xbrain.autenticacao.modules.comum.service.FileService;
import br.com.xbrain.autenticacao.modules.comum.service.RegionalService;
import br.com.xbrain.autenticacao.modules.comum.util.ListUtil;
import br.com.xbrain.autenticacao.modules.equipevenda.dto.EquipeVendaUsuarioResponse;
import br.com.xbrain.autenticacao.modules.equipevenda.service.EquipeVendaD2dService;
import br.com.xbrain.autenticacao.modules.equipevenda.service.EquipeVendasUsuarioService;
import br.com.xbrain.autenticacao.modules.feeder.dto.VendedoresFeederFiltros;
import br.com.xbrain.autenticacao.modules.feeder.dto.VendedoresFeederResponse;
import br.com.xbrain.autenticacao.modules.feeder.service.FeederService;
import br.com.xbrain.autenticacao.modules.gestaocolaboradorespol.service.ColaboradorVendasService;
import br.com.xbrain.autenticacao.modules.mailing.service.MailingService;
import br.com.xbrain.autenticacao.modules.notificacao.service.NotificacaoService;
import br.com.xbrain.autenticacao.modules.organizacaoempresa.model.OrganizacaoEmpresa;
import br.com.xbrain.autenticacao.modules.organizacaoempresa.service.OrganizacaoEmpresaService;
import br.com.xbrain.autenticacao.modules.parceirosonline.dto.AgenteAutorizadoResponse;
import br.com.xbrain.autenticacao.modules.parceirosonline.dto.UsuarioAgenteAutorizadoResponse;
import br.com.xbrain.autenticacao.modules.permissao.dto.FuncionalidadeResponse;
import br.com.xbrain.autenticacao.modules.permissao.filtros.FuncionalidadePredicate;
import br.com.xbrain.autenticacao.modules.permissao.model.CargoDepartamentoFuncionalidade;
import br.com.xbrain.autenticacao.modules.permissao.model.Funcionalidade;
import br.com.xbrain.autenticacao.modules.permissao.model.PermissaoEspecial;
import br.com.xbrain.autenticacao.modules.permissao.repository.CargoDepartamentoFuncionalidadeRepository;
import br.com.xbrain.autenticacao.modules.permissao.repository.PermissaoEspecialRepository;
import br.com.xbrain.autenticacao.modules.permissao.service.FuncionalidadeService;
import br.com.xbrain.autenticacao.modules.permissao.service.PermissaoEspecialService;
import br.com.xbrain.autenticacao.modules.site.model.Site;
import br.com.xbrain.autenticacao.modules.site.service.SiteService;
import br.com.xbrain.autenticacao.modules.usuario.dto.*;
import br.com.xbrain.autenticacao.modules.usuario.enums.*;
import br.com.xbrain.autenticacao.modules.usuario.event.UsuarioSubCanalEvent;
import br.com.xbrain.autenticacao.modules.usuario.model.*;
import br.com.xbrain.autenticacao.modules.usuario.predicate.CargoPredicate;
import br.com.xbrain.autenticacao.modules.usuario.predicate.UsuarioPredicate;
import br.com.xbrain.autenticacao.modules.usuario.rabbitmq.*;
import br.com.xbrain.autenticacao.modules.usuario.repository.*;
import br.com.xbrain.xbrainutils.CsvUtils;
import com.google.common.collect.Sets;
import com.querydsl.core.types.Predicate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Lazy;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.NumberUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Nullable;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static br.com.xbrain.autenticacao.modules.comum.enums.RelatorioNome.USUARIOS_CSV;
import static br.com.xbrain.autenticacao.modules.comum.util.Constantes.QTD_MAX_IN_NO_ORACLE;
import static br.com.xbrain.autenticacao.modules.comum.util.Constantes.ROLE_SHB;
import static br.com.xbrain.autenticacao.modules.comum.util.StringUtil.atualizarEmailInativo;
import static br.com.xbrain.autenticacao.modules.comum.util.StringUtil.getRandomPassword;
import static br.com.xbrain.autenticacao.modules.feeder.service.FeederUtil.*;
import static br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo.*;
import static br.com.xbrain.autenticacao.modules.usuario.enums.CodigoFuncionalidade.AUT_VISUALIZAR_GERAL;
import static br.com.xbrain.autenticacao.modules.usuario.enums.CodigoMotivoInativacao.DEMISSAO;
import static br.com.xbrain.autenticacao.modules.usuario.enums.EObservacaoHistorico.*;
import static br.com.xbrain.autenticacao.modules.usuario.service.CidadeService.getListaCidadeResponseOrdenadaPorNome;
import static br.com.xbrain.autenticacao.modules.usuario.service.CidadeService.hasFkCidadeSemNomeCidadePai;
import static br.com.xbrain.autenticacao.modules.usuario.util.UsuarioConstantesUtils.*;
import static br.com.xbrain.xbrainutils.NumberUtils.getOnlyNumbers;
import static com.google.common.collect.Lists.partition;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.springframework.data.domain.Sort.Direction.ASC;
import static org.springframework.util.ObjectUtils.isEmpty;
import static org.thymeleaf.util.StringUtils.concat;

@Service
@Slf4j
@SuppressWarnings({"PMD.TooManyStaticImports", "PMD.UnusedImports", "VariableDeclarationUsageDistance"})
public class UsuarioService {

    private static final int POSICAO_ZERO = 0;
    private static final int MAX_CARACTERES_SENHA = 6;
    private static final ValidacaoException EX_NAO_ENCONTRADO = new ValidacaoException("Usuário não encontrado.");
    private static final String MSG_ERRO_AO_ATIVAR_USUARIO =
        "Erro ao ativar, o agente autorizado está inativo ou descredenciado.";
    private static final String CONTRATO_ATIVO = "CONTRATO ATIVO";
    private static final String MSG_ERRO_AO_REMOVER_CANAL_ATIVO_LOCAL =
        "Não é possível remover o canal Ativo Local, pois o usuário possui vínculo com o(s) Site(s): %s.";
    private static final String MSG_ERRO_AO_REMOVER_CANAL_AGENTE_AUTORIZADO =
        "Não é possível remover o canal Agente Autorizado, pois o usuário possui vínculo com o(s) AA(s): %s.";
    private static final String MSG_ERRO_AO_ALTERAR_CARGO_SITE =
        "Não é possível alterar o cargo, pois o usuário possui vínculo com o(s) Site(s): %s.";
    private static final String EX_USUARIO_POSSUI_OUTRA_EQUIPE =
        "Usuário já está cadastrado em outra equipe";
    private static final List<CodigoCargo> CARGOS_OPERADORES_BACKOFFICE
        = List.of(BACKOFFICE_OPERADOR_TRATAMENTO, BACKOFFICE_ANALISTA_TRATAMENTO);
    private static final ValidacaoException USUARIO_NAO_POSSUI_LOGIN_NET_SALES_EX = new ValidacaoException(
        "Usuário não possui login NetSales válido."
    );
    private static final ValidacaoException COLABORADOR_NAO_ATIVO = new ValidacaoException(
        "O colaborador não se encontra mais com a situação Ativo. Favor verificar seu cadastro."
    );
    private static final String OPERACAO = "Operação";
    private static final String AGENTE_AUTORIZADO = "Agente Autorizado";
    private static final String MSG_ERRO_ATIVAR_USUARIO_INATIVADO_POR_MUITAS_SIMULACOES =
        "Usuário inativo por excesso de consultas, não foi possível reativá-lo. Para reativação deste usuário é"
            + " necessário a abertura de um incidente no CA, anexando a liberação do diretor comercial.";
    public static final int NUMERO_MAXIMO_TENTATIVAS_LOGIN_SENHA_INCORRETA = 3;
    private static final List<CodigoCargo> LISTA_CARGOS_VALIDACAO_PROMOCAO = List.of(
        SUPERVISOR_OPERACAO, VENDEDOR_OPERACAO, ASSISTENTE_OPERACAO, OPERACAO_EXECUTIVO_VENDAS, COORDENADOR_OPERACAO);
    private static final List<CodigoCargo> LISTA_CARGOS_LIDERES_EQUIPE = List.of(
        SUPERVISOR_OPERACAO, COORDENADOR_OPERACAO);
    private static final ValidacaoException EMAIL_CADASTRADO_EXCEPTION = new ValidacaoException("Email já cadastrado.");
    private static final ValidacaoException EMAIL_ATUAL_INCORRETO_EXCEPTION
        = new ValidacaoException("Email atual está incorreto.");
    private static final ValidacaoException SENHA_ATUAL_INCORRETA_EXCEPTION
        = new ValidacaoException("Senha atual está incorreta.");
    private static final ValidacaoException USUARIO_NOT_FOUND_EXCEPTION
        = new ValidacaoException("O usuário não foi encontrado.");
    private static final List<CodigoCargo> CARGOS_PARA_INTEGRACAO_D2D = List.of(SUPERVISOR_OPERACAO, ASSISTENTE_OPERACAO,
        VENDEDOR_OPERACAO);
    private static final ValidacaoException USUARIO_ATIVO_LOCAL_POSSUI_AGENDAMENTOS_EX = new ValidacaoException(
        "Não foi possível inativar usuario Ativo Local com agendamentos"
    );
    private static final List<CodigoCargo> CARGOS_PARA_INTEGRACAO_ATIVO_LOCAL =
        List.of(SUPERVISOR_OPERACAO, ASSISTENTE_OPERACAO, OPERACAO_TELEVENDAS);
    private static final List<Integer> FUNCIONALIDADES_EQUIPE_TECNICA = List.of(16101);
    private static final String MSG_ERRO_ATIVAR_USUARIO_COM_AA_ESTRUTURA_NAO_LOJA_FUTURO =
        "O usuário não pode ser ativado pois a estrutura do agente autorizado não é Loja do Futuro.";
    public static final Set<CodigoCargo> CARGOS_PERMITIDOS_INTERNET_SUPERVISOR = Set.of(INTERNET_BACKOFFICE,
        INTERNET_VENDEDOR, INTERNET_COORDENADOR);
    public static final Set<CodigoCargo> CARGOS_PERMITIDOS_INTERNET_COODERNADOR = Set.of(INTERNET_BACKOFFICE,
        INTERNET_VENDEDOR);
    private static final String MSG_ERRO_ATIVAR_USUARIO_COM_FORNECEDOR_INATIVO =
        "O usuário não pode ser ativado pois o fornecedor está inativo.";
    private static final String MSG_ERRO_SALVAR_USUARIO_COM_FORNECEDOR_INATIVO =
        "O usuário não pode ser salvo pois o fornecedor está inativo.";
    private static final List<Integer> FUNCIONALIDADES_SOCIAL_HUB = List.of(30000);

    @Autowired
    private UsuarioRepository repository;
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
    private PermissaoEspecialService permissaoEspecialService;
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
    private EquipeVendaD2dService equipeVendaD2dService;
    @Autowired
    private UsuarioFeriasService usuarioFeriasService;
    @Autowired
    private UsuarioAfastamentoService usuarioAfastamentoService;
    @Autowired
    private UsuarioFeederCadastroSucessoMqSender usuarioFeederCadastroSucessoMqSender;
    @Autowired
    private FeederService feederService;
    @Autowired
    private UsuarioHistoricoService usuarioHistoricoService;
    @Autowired
    private SiteService siteService;
    @Autowired
    private MailingService mailingService;
    @Autowired
    private CargoSuperiorRepository cargoSuperiorRepository;
    @Autowired
    private RegionalService regionalService;
    @Autowired
    private EquipeVendasUsuarioService equipeVendasUsuarioService;
    @Lazy
    @Autowired
    private SubCanalService subCanalService;
    @Autowired
    private InativarColaboradorMqSender inativarColaboradorMqSender;
    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;
    @Autowired
    private ColaboradorVendasService colaboradorVendasService;
    @Value("${app-config.url-foto-usuario}")
    private String urlDir;
    @Autowired
    private PermissaoTecnicoIndicadorService permissaoTecnicoIndicadorService;
    @Autowired
    private CidadeService cidadeService;
    @Lazy
    @Autowired
    private OrganizacaoEmpresaService organizacaoEmpresaService;
    @Value("#{'${app-config.dominios-social-hub}'.split(',')}")
    private Set<String> dominiosPermitidos;

    public Usuario findComplete(Integer id) {
        var usuario = repository.findComplete(id).orElseThrow(() -> EX_NAO_ENCONTRADO);
        usuario.forceLoad();

        return usuario;
    }

    private Usuario findOneById(Integer id) {
        return repository.findById(id).orElseThrow(() -> EX_NAO_ENCONTRADO);
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
        var predicate = new UsuarioPredicate();
        predicate.comId(id);

        var usuario = repository.findOne(predicate.build());
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

    public UsuarioDto getUsuarioById(int id) {
        var usuario = findByIdComAa(id);
        return UsuarioDto.of(
            usuario,
            usuario.permiteEditar(autenticacaoService.getUsuarioAutenticado()));
    }

    public List<UsuarioResponse> buscarColaboradoresAtivosOperacaoComericialPorCargo(Integer cargoId) {
        return repository.findUsuariosAtivosOperacaoComercialByCargoId(cargoId);
    }

    public List<CidadeResponse> findCidadesByUsuario(int usuarioId) {
        var cidades = repository
            .findComCidade(usuarioId)
            .orElseThrow(() -> EX_NAO_ENCONTRADO);

        if (!cidades.isEmpty()) {
            var cidadesResponse = getListaCidadeResponseOrdenadaPorNome(cidades);
            var distritos = cidadeService.getCidadesDistritos(Eboolean.V);

            cidadesResponse
                .forEach(cidadeResponse -> CidadeResponse.definirNomeCidadePaiPorDistritos(cidadeResponse, distritos));

            return cidadesResponse;
        }

        return List.of();
    }

    public Usuario findCompleteById(int id) {
        return repository.findComplete(id).orElseThrow(() -> EX_NAO_ENCONTRADO);
    }

    public Usuario findCompleteByIdComLoginNetSales(int id) {
        return Optional.of(findCompleteById(id))
            .filter(Usuario::hasLoginNetSales)
            .orElseThrow(() -> USUARIO_NAO_POSSUI_LOGIN_NET_SALES_EX);
    }

    @Transactional
    public UsuarioDto findByEmail(String email) {
        return UsuarioDto.of(repository.findByEmail(email).orElseThrow(() -> EX_NAO_ENCONTRADO));
    }

    public Optional<UsuarioResponse> findByEmailAa(String email, Boolean buscarAtivo) {
        if (Boolean.TRUE.equals(buscarAtivo)) {
            return repository.findByEmailAndSituacao(email, ESituacao.A)
                .map(UsuarioResponse::of);
        }

        return repository.findByEmail(email)
            .map(UsuarioResponse::of);
    }

    public Optional<UsuarioResponse> findByCpfAa(String cpf, Boolean buscarAtivo) {
        if (Boolean.TRUE.equals(buscarAtivo)) {
            return repository.findTop1UsuarioByCpfAndSituacao(getOnlyNumbers(cpf), ESituacao.A)
                .map(UsuarioResponse::of);
        }

        return repository.findTop1UsuarioByCpf(getOnlyNumbers(cpf))
            .map(UsuarioResponse::of);
    }

    public UsuarioResponse findUsuarioByCpfComSituacaoAtivoOuInativo(String cpf) {
        return repository.findTop1UsuarioByCpfAndSituacaoIn(getOnlyNumbers(cpf), List.of(ESituacao.A, ESituacao.I))
            .map(UsuarioResponse::of)
            .orElse(null);
    }

    public UsuarioResponse findUsuarioByEmailComSituacaoAtivoOuInativo(String email) {
        return repository.findTop1UsuarioByEmailAndSituacaoIn(email, List.of(ESituacao.A, ESituacao.I))
            .map(UsuarioResponse::of)
            .orElse(null);
    }

    public UsuarioResponse buscarAtualByCpf(String cpf) {
        return UsuarioResponse.of(repository
            .findTop1UsuarioByCpfAndSituacaoNotOrderByDataCadastroDesc(getOnlyNumbers(cpf), ESituacao.R)
            .orElseThrow(() -> USUARIO_NOT_FOUND_EXCEPTION));
    }

    public UsuarioResponse buscarNaoRealocadoByCpf(String cpf) {
        return UsuarioResponse.of(repository
            .findTop1UsuarioByCpfAndSituacaoNotOrderByDataCadastroDesc(getOnlyNumbers(cpf), ESituacao.R)
            .orElse(null));
    }

    public UsuarioResponse buscarAtualByEmail(String email) {
        return UsuarioResponse.of(repository
            .findTop1UsuarioByEmailAndSituacaoNotOrderByDataCadastroDesc(email, ESituacao.R)
            .orElseThrow(() -> USUARIO_NOT_FOUND_EXCEPTION));
    }

    public List<EmpresaResponse> findEmpresasDoUsuario(Integer idUsuario) {
        var usuario = findComplete(idUsuario);

        return usuario.getEmpresas()
            .stream()
            .map(EmpresaResponse::convertFrom)
            .collect(toList());
    }

    public Page<UsuarioConsultaDto> getAll(PageRequest pageRequest, UsuarioFiltros filtros) {
        var predicate = filtrarUsuariosPermitidos(filtros);
        validarCargoUsuarioAutenticado(predicate);

        var pages = repository.findAll(predicate.build(), pageRequest);
        if (!isEmpty(pages.getContent())) {
            popularUsuarios(pages.getContent());
        }

        return pages.map(UsuarioConsultaDto::convertFrom);
    }

    private void validarCargoUsuarioAutenticado(UsuarioPredicate predicate) {
        var usuario = autenticacaoService.getUsuarioAutenticado();

        if (usuario.isAssistenteOperacao()) {
            predicate.semCargoCodigo(COORDENADOR_OPERACAO);
        }
    }

    public List<UsuarioConsultaDto> getAllXbrainMsoAtivos(Integer idNivel) {
        var filtro = new UsuarioFiltros();
        filtro.setNivelId(idNivel);
        filtro.setSituacoes(List.of(ESituacao.A));
        var usuarios = repository.findAll(filtro.toPredicate().build());
        return StreamSupport.stream(usuarios.spliterator(), false)
            .map(UsuarioConsultaDto::convertFrom).collect(toList());
    }

    private void popularUsuarios(List<Usuario> usuarios) {
        usuarios.forEach(c -> {
            c.setEmpresas(repository.findEmpresasById(c.getId()));
            c.setUnidadesNegocios(repository.findUnidadesNegociosById(c.getId()));
            c.setTiposFeeder(Sets.newHashSet(repository.findTiposFeederById(c.getId())));
        });
    }

    private void obterUsuariosAa(String cnpjAa, UsuarioPredicate predicate, Boolean buscarInativos) {
        var lista = agenteAutorizadoService.getIdUsuariosPorAa(cnpjAa, buscarInativos);
        predicate.comIds(lista);
    }

    private UsuarioCidade criarUsuarioCidade(Usuario usuario, Integer idCidade) {
        return UsuarioCidade.criar(usuario, idCidade, autenticacaoService.getUsuarioId());
    }

    public UsuarioDto saveUsuarioConfiguracao(UsuarioConfiguracaoSaveDto usuarioHierarquiaSaveDto) {
        var usuario = findComplete(usuarioHierarquiaSaveDto.getUsuarioId());
        var usuarioAutenticado = autenticacaoService.getUsuarioAutenticado().getUsuario();
        if (usuario.hasConfiguracao()) {
            usuario.configurarRamal(usuarioHierarquiaSaveDto.getRamal());
        } else {
            usuario.setConfiguracao(
                new Configuracao(
                    usuario, usuarioAutenticado, LocalDateTime.now(), usuarioHierarquiaSaveDto.getRamal()));
        }
        usuario.removerCaracteresDoCpf();

        return UsuarioDto.of(repository.save(usuario));
    }

    private UsuarioHierarquia criarUsuarioHierarquia(Usuario usuario, Integer idHierarquia) {
        return UsuarioHierarquia.criar(usuario, idHierarquia, autenticacaoService.getUsuarioId());
    }

    public List<Integer> getIdDosUsuariosSubordinados(Integer usuarioId, Boolean incluirProprio) {
        var usuariosSubordinados = repository.getUsuariosSubordinados(usuarioId);
        if (incluirProprio) {
            usuariosSubordinados.add(usuarioId);
        }

        return usuariosSubordinados;
    }

    public List<Integer> getIdDosUsuariosSubordinadosDoPol(UsuarioAutenticado usuario) {
        if (!usuario.haveCanalAgenteAutorizado() || usuario.hasPermissao(AUT_VISUALIZAR_GERAL)) {
            return List.of();
        }

        return Stream.of(
                agenteAutorizadoService.getIdsUsuariosSubordinados(false),
                repository.getUsuariosSubordinados(usuario.getId()))
            .flatMap(Collection::stream)
            .distinct()
            .collect(toList());
    }

    public List<Integer> getIdDosUsuariosSubordinadosDoPol(UsuarioAutenticado usuario, PublicoAlvoComunicadoFiltros filtros) {
        if (!usuario.haveCanalAgenteAutorizado() || usuario.hasPermissao(AUT_VISUALIZAR_GERAL)) {
            return List.of();
        }
        var usuariosPol = CollectionUtils.isEmpty(filtros.getUsuariosFiltradosPorCidadePol())
            ? getIdDosUsuariosParceiros(filtros)
            : filtros.getUsuariosFiltradosPorCidadePol();
        var usuariosSubordinados = Sets.newHashSet(repository.getUsuariosSubordinados(usuario.getId()));
        usuariosSubordinados.addAll(usuariosPol);

        return List.copyOf(usuariosSubordinados);
    }

    public List<Integer> getIdDosUsuariosParceiros(PublicoAlvoComunicadoFiltros filtros) {
        return agenteAutorizadoService.getIdsUsuariosSubordinadosByFiltros(filtros);
    }

    public List<UsuarioSubordinadoDto> getSubordinadosDoUsuario(Integer usuarioId) {
        return repository.getUsuariosCompletoSubordinados(usuarioId);
    }

    @Transactional(readOnly = true)
    public List<UsuarioHierarquiaDto> getSubordinadosAndAasDoUsuario(boolean incluirInativos) {
        var usuario = UsuarioHierarquiaDto
            .of(repository.findById(autenticacaoService.getUsuarioId())
                .orElseThrow(() -> USUARIO_NOT_FOUND_EXCEPTION));

        var subordinados = UsuarioHierarquiaDto.ofUsuarioSubordinadoDtoList(
            repository.getUsuariosCompletoSubordinados(usuario.getId()));

        subordinados.add(usuario);
        adicionarAas(subordinados);
        return validarInativos(subordinados, incluirInativos);
    }

    private List<UsuarioHierarquiaDto> validarInativos(List<UsuarioHierarquiaDto> subordinados, boolean incluirInativos) {
        if (!incluirInativos) {
            return subordinados.stream().filter(subordinado ->
                    subordinado.getSituacao().equalsIgnoreCase(ESituacao.A.getDescricao())
                        || subordinado.getSituacao().equalsIgnoreCase(CONTRATO_ATIVO))
                .collect(toList());
        }
        return subordinados;
    }

    private List<UsuarioHierarquiaDto> adicionarAas(List<UsuarioHierarquiaDto> subordinados) {
        var usuariosIds = subordinados.stream()
            .map(UsuarioHierarquiaDto::getId).collect(toList());

        subordinados.addAll(UsuarioHierarquiaDto
            .ofAgenteAutorizadoResponseList(
                agenteAutorizadoService.findAgentesAutorizadosByUsuariosIds(usuariosIds, true)));

        return subordinados;
    }

    public List<UsuarioAutoComplete> getSubordinadosDoGerenteComCargoExecutivoOrExecutivoHunter(Integer usuarioId) {
        return repository.getSubordinadosDoGerenteComCargoExecutivoOrExecutivoHunter(usuarioId);
    }

    public List<UsuarioAutoComplete> findAllExecutivosOperacaoDepartamentoComercial(@Nullable CodigoCargo cargo) {
        var predicate = new UsuarioPredicate()
            .comCargo(cargo)
            .build();
        return repository.findAllExecutivosOperacaoDepartamentoComercial(predicate);
    }

    public List<UsuarioAutoComplete> findAllResponsaveisDdd(@Nullable CodigoCargo cargo) {
        var predicate = new UsuarioPredicate()
            .comCargo(cargo)
            .build();
        return repository.findAllExecutivosAndAssistenteOperacaoDepartamentoComercial(predicate);
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
            .collect(toList());
    }

    public List<UsuarioHierarquiaResponse> getSuperioresDoUsuarioPorCargo(Integer usuarioId, CodigoCargo codigoCargo) {
        return repository.getSuperioresDoUsuarioPorCargo(usuarioId, codigoCargo)
            .stream().map(UsuarioHierarquiaResponse::new)
            .collect(toList());
    }

    @Transactional
    public UsuarioDto save(UsuarioDto usuario, MultipartFile foto) {
        var request = UsuarioDto.convertFrom(usuario);
        if (!isEmpty(foto)) {
            fileService.salvarArquivo(request, foto);
        }
        return save(request);
    }

    @Transactional
    public UsuarioDto save(Usuario usuario) {
        try {
            validar(usuario);
            validarEdicao(usuario);
            validarPromocaoCargo(usuario);
            var situacaoAnterior = recuperarSituacaoAnterior(usuario);
            tratarCadastroUsuario(usuario);
            var enviarEmail = usuario.isNovoCadastro();
            atualizarUsuarioCadastroNulo(usuario);
            removerPermissoes(usuario);
            configurarDataReativacao(usuario, situacaoAnterior);
            repository.saveAndFlush(usuario);
            adicionarPermissoes(usuario);
            configurarCadastro(usuario);
            gerarHistoricoAlteracaoCadastro(usuario, situacaoAnterior);
            enviarEmailDadosAcesso(usuario, enviarEmail);
            processarUsuarioParaSocialHub(usuario);

            return UsuarioDto.of(usuario);
        } catch (PersistenceException | DataIntegrityViolationException ex) {
            log.error("Erro de persistência ao salvar o Usuario.", ex.getMessage());
            throw new ValidacaoException("Erro ao cadastrar usuário.");
        } catch (Exception ex) {
            log.error("Erro ao salvar Usuário.", ex.getMessage());
            throw ex;
        }
    }

    private void removerPermissoes(Usuario usuario) {
        subCanalService.removerPermissaoIndicacaoPremium(usuario);
        subCanalService.removerPermissaoIndicacaoInsideSalesPme(usuario);
        feederService.removerPermissaoFeederUsuarioAtualizadoMso(usuario);
        permissaoTecnicoIndicadorService
            .removerPermissaoTecnicoIndicadorDoUsuario(UsuarioDto.of(usuario));
    }

    private void adicionarPermissoes(Usuario usuario) {
        subCanalService.adicionarPermissaoIndicacaoPremium(usuario);
        subCanalService.adicionarPermissaoIndicacaoInsideSalesPme(usuario);
        feederService.adicionarPermissaoFeederParaUsuarioNovoMso(usuario);
    }

    private void atualizarUsuarioCadastroNulo(Usuario usuario) {
        if (usuario.hasUsuarioCadastroNulo()) {
            autenticacaoService.getUsuarioAutenticadoId()
                .ifPresent(usuarioCadastroId -> usuario.setUsuarioCadastro(new Usuario(usuarioCadastroId)));
        }
    }

    private void validarEdicao(Usuario usuario) {
        if (!usuario.isNovoCadastro()) {
            repository.findById(usuario.getId())
                .ifPresent(usuarioOriginal -> {
                    validarVinculoComSite(usuarioOriginal, usuario);
                    validarVinculoComAa(usuarioOriginal, usuario);
                });
        }
    }

    private Usuario getUsuario(Integer id) {
        return repository.findById(id).orElseThrow(() -> USUARIO_NOT_FOUND_EXCEPTION);
    }

    private void validarVinculoComAa(Usuario usuarioOriginal, Usuario usuarioAlterado) {
        if (usuarioOriginal.isNivelOperacao() && usuarioOriginal.isCanalAgenteAutorizadoRemovido(usuarioAlterado.getCanais())) {
            var aas = agenteAutorizadoService.findAgenteAutorizadoByUsuarioId(usuarioOriginal.getId());
            if (!CollectionUtils.isEmpty(aas)) {
                throw new ValidacaoException(String.format(MSG_ERRO_AO_REMOVER_CANAL_AGENTE_AUTORIZADO, obterDadosAa(aas)));
            }
        }
    }

    private void validarPromocaoCargo(Usuario usuario) {
        if (!usuario.isNovoCadastro()) {
            repository.findById(usuario.getId()).ifPresent(usuarioAnterior -> {
                if (verificarUsuarioNecessitaValidacaoMudancaCargo(usuarioAnterior)
                    && verificarCargosDiferentes(usuario, usuarioAnterior)) {
                    verificarCadastroEmOutraEquipe(usuarioAnterior);
                }
            });
        }
    }

    private void verificarCadastroEmOutraEquipe(Usuario usuarioAnterior) {
        verificarSeUsuarioLiderEquipe(usuarioAnterior);
        var result = equipeVendasUsuarioService.buscarUsuarioEquipeVendasPorId(usuarioAnterior.getId());
        if (!result.isEmpty()) {
            throw new ValidacaoException(EX_USUARIO_POSSUI_OUTRA_EQUIPE);
        }
    }

    private boolean verificarUsuarioNecessitaValidacaoMudancaCargo(Usuario usuario) {
        return verificarCanalNecessitaValidacao(usuario)
            && verificarCargoNecessitaValidacao(usuario) && verificarDepartamentoNecessitaValidacao(usuario);
    }

    private boolean verificarCargosDiferentes(Usuario usuarioAtual, Usuario usuarioAnterior) {
        return !usuarioAtual.getCargoId().equals(usuarioAnterior.getCargoId());
    }

    private boolean verificarDepartamentoNecessitaValidacao(Usuario usuario) {
        return usuario.getDepartamentoCodigo() == CodigoDepartamento.COMERCIAL;
    }

    private boolean verificarCargoNecessitaValidacao(Usuario usuario) {
        return LISTA_CARGOS_VALIDACAO_PROMOCAO.stream().anyMatch(cargoCodigo -> cargoCodigo == usuario.getCargoCodigo());
    }

    private boolean verificarCanalNecessitaValidacao(Usuario usuario) {
        return repository.getCanaisByUsuarioIds(List.of(usuario.getId())).stream()
            .anyMatch(canalUsuario -> canalUsuario.getCanal() == ECanal.D2D_PROPRIO);
    }

    public Set<SubCanal> buscarSubCanaisPorUsuarioId(Integer usuarioId) {
        return repository.getSubCanaisByUsuarioIds(List.of(usuarioId));
    }

    private void verificarSeUsuarioLiderEquipe(Usuario usuario) {
        if (verificarSeCargoLiderEquipe(usuario)) {
            var listaDeEquipes = equipeVendaD2dService.getEquipeVendasBySupervisorId(usuario.getId());
            if (!listaDeEquipes.isEmpty()) {
                throw new ValidacaoException(EX_USUARIO_POSSUI_OUTRA_EQUIPE);
            }
        }
    }

    private boolean verificarSeCargoLiderEquipe(Usuario usuario) {
        return LISTA_CARGOS_LIDERES_EQUIPE.stream().anyMatch(codigoCargo -> codigoCargo == usuario.getCargoCodigo());
    }

    private void validarVinculoComSite(Usuario usuarioOriginal, Usuario usuarioAlterado) {
        var sitesVinculados = siteService.buscarSitesAtivosPorCoordenadorOuSupervisor(usuarioOriginal.getId());

        if (!CollectionUtils.isEmpty(sitesVinculados)) {
            validarRemocaoCanalAtivoLocal(usuarioOriginal, usuarioAlterado, sitesVinculados);
            validarAlteracaoDeCargo(usuarioOriginal, usuarioAlterado, sitesVinculados);
        }
    }

    private void validarRemocaoCanalAtivoLocal(Usuario usuarioOriginal, Usuario usuarioAlterado, List<Site> sites) {
        if (usuarioOriginal.isCoordenadorOuSupervisorOperacao()
            && usuarioOriginal.isCanalAtivoLocalRemovido(usuarioAlterado.getCanais())) {
            throw new ValidacaoException(String.format(MSG_ERRO_AO_REMOVER_CANAL_ATIVO_LOCAL, obterSitesNome(sites)));
        }
    }

    private void validarAlteracaoDeCargo(Usuario usuarioOriginal, Usuario usuarioAlterado, List<Site> sites) {
        if (usuarioOriginal.isCoordenadorOuSupervisorOperacao()
            && !usuarioOriginal.getCargoId().equals(usuarioAlterado.getCargoId())) {
            throw new ValidacaoException(String.format(MSG_ERRO_AO_ALTERAR_CARGO_SITE, obterSitesNome(sites)));
        }
    }

    public String obterSitesNome(List<Site> sites) {
        return sites
            .stream()
            .map(Site::getNome)
            .collect(Collectors.joining(", "));
    }

    private String obterDadosAa(List<AgenteAutorizadoResponse> agenteAutorizadoResponses) {
        return agenteAutorizadoResponses
            .stream()
            .map(aas -> aas.getRazaoSocial() + " " + aas.getCnpj())
            .collect(Collectors.joining(", "));
    }

    public Usuario salvarUsuarioBackoffice(Usuario usuario) {
        tratarUsuarioBackoffice(usuario);
        validar(usuario);
        validarOrganizacaoEmpresaInativa(usuario);
        tratarCadastroUsuario(usuario);
        var enviarEmail = usuario.isNovoCadastro();
        repository.save(usuario);

        processarUsuarioParaSocialHub(usuario);
        enviarEmailDadosAcesso(usuario, enviarEmail);
        return usuario;
    }

    private void validarOrganizacaoEmpresaInativa(Usuario usuario) {
        if (usuario.getOrganizacaoEmpresa() != null) {
            var organizacaoEmpresa = organizacaoEmpresaService.findById(usuario.getOrganizacaoEmpresa().getId());
            if (!organizacaoEmpresa.isAtivo()) {
                throw new ValidacaoException(MSG_ERRO_SALVAR_USUARIO_COM_FORNECEDOR_INATIVO);
            }
        }
    }

    private void configurarCadastro(Usuario usuario) {
        tratarHierarquiaUsuario(usuario, usuario.getHierarquiasId());
        tratarCidadesUsuario(usuario);
    }

    private void validarSupervisorNaHierarquia(Usuario usuario) {
        if (!usuario.isCargoAgenteAutorizado() && !usuario.isCargoLojaFuturo() && !usuario.isCargoImportadorCargas()) {
            var usuarioAutenticado = autenticacaoService.getUsuarioAutenticado();
            var isSupervisorOuAssistente = usuarioAutenticado.isSupervisorOperacao()
                || usuarioAutenticado.isAssistenteOperacao();
            var isUsuarioAssistenteOuSupervisor = usuario.isSupervisorOperacao() || usuario.isAssistenteOperacao();

            if (isEmpty(usuario.getHierarquiasId())
                && isSupervisorOuAssistente
                && !isUsuarioAssistenteOuSupervisor) {
                usuario.setHierarquiasId(List.of(usuarioAutenticado.getId()));
            }
        }
    }

    private void tratarUsuarioBackoffice(Usuario usuario) {
        usuario.setOrganizacaoEmpresa(Optional.ofNullable(usuario.getOrganizacaoEmpresa())
            .orElse(new OrganizacaoEmpresa(autenticacaoService.getUsuarioAutenticado().getOrganizacaoId())));
        usuario.setEmpresas(empresaRepository.findAllAtivo());
        usuario.setUnidadesNegocios(unidadeNegocioRepository.findAllAtivo());
    }

    private void enviarEmailDadosAcesso(Usuario usuario, boolean enviarEmail) {
        if (enviarEmail) {
            notificacaoService.enviarEmailDadosDeAcesso(usuario, usuario.getSenhaDescriptografada());
        }
    }

    private void tratarCadastroUsuario(Usuario usuario) {
        if (usuario.isNovoCadastro()) {
            configurar(usuario, getSenhaRandomica(MAX_CARACTERES_SENHA));
        } else {
            atualizarUsuariosParceiros(usuario);
            usuario.setAlterarSenha(Eboolean.F);
            usuario.setDataUltimoAcesso(getUsuario(usuario.getId()).getDataUltimoAcesso());
            usuario.setDataReativacao(getUsuario(usuario.getId()).getDataReativacao());
        }
    }

    @Transactional
    public void salvarUsuarioFeeder(UsuarioFeederMqDto usuarioDto) {
        try {
            validarCpfCadastrado(usuarioDto.getCpf(), usuarioDto.getUsuarioId());
            validarEmailCadastrado(usuarioDto.getEmail(), usuarioDto.getUsuarioId());

            var usuario = new Usuario();
            boolean enviarEmail = false;
            var senhaDescriptografada = getSenhaRandomica(MAX_CARACTERES_SENHA);

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
        var usuarioARealocar = repository.findById(usuario.getId()).orElseThrow(() -> EX_NAO_ENCONTRADO);
        usuarioARealocar.setSituacao(ESituacao.R);
        repository.save(usuarioARealocar);
    }

    private ESituacao recuperarSituacaoAnterior(Usuario usuario) {
        return usuario.isNovoCadastro()
            ? ESituacao.A
            : repository.findById(usuario.getId()).orElseThrow(() -> USUARIO_NOT_FOUND_EXCEPTION).getSituacao();
    }

    private void gerarHistoricoAlteracaoCadastro(Usuario usuario, ESituacao situacaoAnterior) {
        usuario.adicionarHistorico(usuario.getSituacao().equals(situacaoAnterior) && usuario.isAtivo()
            ? UsuarioHistorico.gerarHistorico(usuario, ALTERACAO_CADASTRO)
            : UsuarioHistorico.gerarHistorico(usuario, ATIVACAO_POL));
        repository.save(usuario);
    }

    public void vincularUsuario(List<Integer> idUsuarioNovo, Integer idUsuarioSuperior) {
        var usuarioSuperior = repository.findById(idUsuarioSuperior)
            .orElseThrow(() -> EX_NAO_ENCONTRADO);

        idUsuarioNovo.stream()
            .map(id -> {
                var usuario = usuarioHierarquiaRepository.findOne(id);
                usuario.setUsuarioSuperior(usuarioSuperior);
                return usuario;
            }).forEach(usuarioHierarquiaRepository::save);
    }

    @Transactional
    public void vincularUsuarioParaNovaHierarquia(AlteraSuperiorRequest superiorRequest) {
        var usuarioSuperiorNovo = repository.findById(superiorRequest.getSuperiorNovo()).orElseThrow(() ->
            new NotFoundException("Usuário não encontrado"));

        var usuarioAutenticado = autenticacaoService.getUsuarioAutenticado();

        superiorRequest.getUsuarioIds()
            .forEach(id -> {
                var usuarioHierarquia = usuarioHierarquiaRepository.findByUsuarioHierarquia(id,
                    superiorRequest.getSuperiorAntigo());

                if (!isEmpty(usuarioHierarquia) && !isEmpty(usuarioAutenticado)) {
                    usuarioHierarquiaRepository.delete(usuarioHierarquia);
                }
                if (!isEmpty(usuarioAutenticado)) {
                    usuarioHierarquiaRepository.save(
                        criarHierarquia(id, usuarioSuperiorNovo, superiorRequest, usuarioAutenticado));
                }
            });
    }

    private UsuarioHierarquia criarHierarquia(Integer id,
                                              Usuario superiorNovo,
                                              AlteraSuperiorRequest request,
                                              UsuarioAutenticado usuarioAutenticado) {
        var usuario = repository.findOne(id);

        return UsuarioHierarquia.builder()
            .usuario(usuario)
            .usuarioSuperior(superiorNovo)
            .usuarioHierarquiaPk(criarUsuarioHierarquiaPk(id, request))
            .dataCadastro(superiorNovo.getDataCadastro())
            .usuarioCadastro(usuarioAutenticado.getUsuario())
            .build();
    }

    private UsuarioHierarquiaPk criarUsuarioHierarquiaPk(Integer id, AlteraSuperiorRequest superiorRequest) {
        return UsuarioHierarquiaPk
            .builder()
            .usuario(id)
            .usuarioSuperior(superiorRequest.getSuperiorNovo())
            .build();
    }

    private Usuario getUsuarioAtivacao(UsuarioAtivacaoDto usuarioAtivacaoDto) {
        return Objects.nonNull(usuarioAtivacaoDto.getIdUsuarioAtivacao())
            ? new Usuario(usuarioAtivacaoDto.getIdUsuarioAtivacao())
            : new Usuario(autenticacaoService.getUsuarioId());
    }

    private void atualizarUsuariosParceiros(Usuario usuario) {
        cargoRepository.findById(usuario.getCargoId()).ifPresent(cargo -> {
            var usuarioAtualizar = repository.findById(usuario.getId());
            if (isSocioPrincipal(cargo.getCodigo()) && usuarioAtualizar.isPresent()) {
                var usuarioDto = UsuarioDto.of(usuarioAtualizar.get());
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

    private static boolean isSocioPrincipal(Usuario usuario) {
        return usuario.isSocioPrincipal() && usuario.isAgenteAutorizado();
    }

    public boolean validarSeUsuarioCpfEmailNaoCadastrados(UsuarioExistenteValidacaoRequest usuario) {
        validarCpfCadastrado(usuario.getCpf(), usuario.getId());
        validarEmailCadastrado(usuario.getEmail(), usuario.getId());
        return true;
    }

    public void validarSeUsuarioCpfEmailNaoCadastrados(String cpf, String email) {
        validarCpfCadastrado(cpf, null);
        validarEmailCadastrado(email, null);
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
                    throw EMAIL_CADASTRADO_EXCEPTION;
                }
            });
    }

    private void validar(Usuario usuario) {
        validarSupervisorNaHierarquia(usuario);
        validarCpfExistente(usuario);
        validarEmailExistente(usuario);
        validarCanalD2dProprioESubCanais(usuario);
        validarOrganizacaoEmpresaReceptivoInternet(usuario);
        usuario.verificarPermissaoCargoSobreCanais();
        usuario.removerCaracteresDoCpf();
        usuario.tratarEmails();
        validarPadraoEmail(usuario.getEmail());
    }

    private void validarOrganizacaoEmpresaReceptivoInternet(Usuario usuario) {
        if (usuario.isNivelReceptivo() || usuario.isNivelOperacao() && usuario.hasCanal(ECanal.INTERNET)) {
            validarOrganizacaoEmpresaInativa(usuario);
        }
    }

    private void validarCanais(Usuario usuario, Set<UsuarioHierarquia> usuariosHierarquia) {
        var superioresId = getUsuarioHierarquias(usuariosHierarquia);
        if (!superioresId.isEmpty()) {
            var contains = usuariosHierarquia.stream()
                .map(u -> repository.findById(u.getUsuarioSuperiorId()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .anyMatch(usuarioHierarquia -> usuarioHierarquia.getCanais().stream()
                    .anyMatch(canal -> usuario.getCanais().contains(canal)));
            if (!contains) {
                throw new ValidacaoException("Usuário não possui canal em comum com usuários da hierarquia.");
            }
        }
    }

    private List<UsuarioHierarquia> getUsuarioHierarquias(Set<UsuarioHierarquia> usuariosHierarquia) {
        return usuariosHierarquia.stream()
            .filter(usuarioHierarquia -> usuarioHierarquia.getUsuarioSuperior() != null)
            .collect(toList());
    }

    private void validarPadraoEmail(String email) {
        var pattern = Pattern.compile("^([a-zA-Z0-9_\\-\\.\\+]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)"
            + "|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})$");
        var matcher = pattern.matcher(email);
        if (!matcher.find()) {
            throw new ValidacaoException("Email inválido.");
        }
    }

    private void validarCanalD2dProprioESubCanais(Usuario usuario) {
        if (usuario.hasCanal(ECanal.D2D_PROPRIO)) {
            Optional.ofNullable(cargoService.findById(usuario.getCargoId()))
                .ifPresent(cargo -> {
                    validarSubCanaisUsuario(usuario, cargo);
                    validarSubCanaisHierarquia(usuario, cargo);
                    validarSubCanaisSubordinados(usuario);
                });
        }
    }

    private void validarSubCanaisUsuario(Usuario usuario, Cargo cargo) {
        if (!CollectionUtils.isEmpty(usuario.getSubCanais())) {
            if (usuario.getSubCanais().size() > 1
                && !CARGOS_COM_MAIS_SUBCANAIS.contains(cargo.getCodigo())) {
                throw MSG_ERRO_USUARIO_CARGO_SOMENTE_UM_SUBCANAL;
            }
        } else {
            throw MSG_ERRO_USUARIO_NAO_POSSUI_SUBCANAIS;
        }
    }

    private void validarSubCanaisHierarquia(Usuario usuario, Cargo cargo) {
        if (!cargo.isDiretorOperacao() && usuario.hasHierarquia()) {
            var hierarquiaSubCanalIds = repository.getSubCanaisByUsuarioIds(usuario.getHierarquiasId())
                .stream()
                .map(SubCanal::getId)
                .collect(Collectors.toSet());

            if (!usuario.hasSubCanaisDaHierarquia(hierarquiaSubCanalIds)) {
                throw MSG_ERRO_USUARIO_SEM_SUBCANAL_DA_HIERARQUIA;
            }
        }
    }

    private void validarSubCanaisSubordinados(Usuario usuario) {
        if (!usuario.isNovoCadastro()) {
            var subordinadosComSubCanalId = repository.getAllSubordinadosComSubCanalId(usuario.getId());
            if (!subordinadosComSubCanalId.isEmpty() && !usuario.hasAllSubCanaisDosSubordinados(subordinadosComSubCanalId)) {
                var subordinadosComSubCanaisDiferentes =
                    getSubordinadosComSubCanaisDiferentes(usuario, subordinadosComSubCanalId);
                applicationEventPublisher.publishEvent(
                    new UsuarioSubCanalEvent(this, subordinadosComSubCanaisDiferentes));
                throw MSG_ERRO_USUARIO_SEM_SUBCANAL_DOS_SUBORDINADOS;
            }
        }
    }

    private List<UsuarioSubCanalId> getSubordinadosComSubCanaisDiferentes(Usuario usuario,
                                                                          List<UsuarioSubCanalId> usuariosComSubCanalId) {
        return usuariosComSubCanalId.stream()
            .filter(subordinado -> !usuario.getSubCanaisId().contains(subordinado.getSubCanalId()))
            .collect(toList());
    }

    private void tratarHierarquiaUsuario(Usuario usuario, List<Integer> hierarquiasId) {
        removerUsuarioSuperior(usuario, hierarquiasId);
        removerHierarquiaSubordinados(usuario);
        adicionarUsuarioSuperior(usuario, hierarquiasId);
        hierarquiaIsValida(usuario);
        validarCanais(usuario, usuario.getUsuariosHierarquia());

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
        var subordinados = usuarioHierarquiaRepository.findAllByIdUsuarioSuperior(usuario.getId())
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
        if (!isEmpty(usuario)
            && !CollectionUtils.isEmpty(usuario.getUsuariosHierarquia())) {

            usuario.getUsuariosHierarquia()
                .forEach(user -> processarHierarquia(usuario, user, new ArrayList<>()));
        }
    }

    private boolean processarHierarquia(final Usuario usuarioParaAchar, UsuarioHierarquia usuario, ArrayList<Usuario> valores) {
        boolean existeId = false;

        if (validarUsuarios(usuarioParaAchar, usuario)) {
            existeId = verificarUsuariosHierarquia(usuarioParaAchar, usuario);
            valores.add(usuario.getUsuario());

            if (!existeId) {
                var superiores = getIdSuperiores(usuario.getUsuario());
                var usuarios = getUsuariosSuperioresPorId(superiores);
                existeId = validarHierarquia(usuarioParaAchar, usuarios, valores);
            }
            if (existeId) {
                var mensagem = montarMensagemDeErro(valores, usuarioParaAchar);
                throw new ValidacaoException(mensagem);
            }
        }

        return existeId;
    }

    private String montarMensagemDeErro(ArrayList<Usuario> usuarios, Usuario usuarioParaAchar) {
        var valores = usuarios.stream().distinct().collect(toList());
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
            && !CollectionUtils.isEmpty(usuarioParaAchar.getUsuariosHierarquia())
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
            .collect(toList());
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
        var cidadesModificadas = Sets.newHashSet(
            CollectionUtils.isEmpty(usuario.getCidadesId()) ? emptyList() : usuario.getCidadesId());
        var cidadesRemovidas = Sets.difference(cidadesAtuais, cidadesModificadas);
        var cidadesAdicionadas = Sets.difference(cidadesModificadas, cidadesAtuais);
        removerUsuarioCidade(usuario, cidadesRemovidas);
        adicionarUsuarioCidade(usuario, cidadesAdicionadas);
    }

    private void removerUsuarioCidade(Usuario usuario, Set<Integer> cidadesId) {
        cidadesId.forEach(cidadeId -> usuarioCidadeRepository.deleteByCidadeAndUsuario(cidadeId, usuario.getId()));
    }

    private void adicionarUsuarioCidade(Usuario usuario, Set<Integer> cidadesId) {
        if (!CollectionUtils.isEmpty(cidadesId)) {
            cidadesId.forEach(idCidade -> usuario.adicionarCidade(
                criarUsuarioCidade(usuario, idCidade)));
            repository.save(usuario);
        }
    }

    public void configurar(Usuario usuario, String senhaDescriptografada) {
        usuario.setSenha(passwordEncoder.encode(senhaDescriptografada));
        usuario.setSenhaDescriptografada(senhaDescriptografada);
        usuario.setDataCadastro(LocalDateTime.now());
        usuario.setAlterarSenha(Eboolean.V);
        usuario.setSituacao(ESituacao.A);
        if (!usuario.hasUsuarioCadastro()) {
            usuario.setUsuarioCadastro(new Usuario(autenticacaoService.getUsuarioId()));
        }
    }

    @Transactional
    public void saveFromQueue(UsuarioMqRequest usuarioMqRequest) {
        var usuarioDto = UsuarioDto.parse(usuarioMqRequest);
        configurarUsuario(usuarioMqRequest, usuarioDto);
        usuarioDto = save(UsuarioDto.convertFrom(usuarioDto));

        enviarParaFilaDeAtualizarSocioPrincipal(usuarioDto);

        if (usuarioMqRequest.isNovoCadastroSocioPrincipal()) {
            enviarParaFilaDeSocioPrincipalSalvo(usuarioDto);
        } else if (CLIENTE_LOJA_FUTURO.equals(usuarioMqRequest.getCargo())) {
            enviarParaFilaDeLojaFuturoSalvo(usuarioDto);
        } else {
            enviarParaFilaDeUsuariosSalvos(usuarioDto);
        }

        feederService.adicionarPermissaoFeederParaUsuarioNovo(usuarioDto, usuarioMqRequest);
        permissaoTecnicoIndicadorService
            .adicionarPermissaoTecnicoIndicadorParaUsuarioNovo(usuarioDto, usuarioMqRequest, false);
        criarPermissaoEspecialEquipeTecnica(usuarioDto, usuarioMqRequest);
    }

    private void enviarParaFilaDeAtualizarSocioPrincipal(UsuarioDto socio) {
        if (socio.isAtualizarSocioPrincipal()) {
            enviarParaFilaDeAtualizarSocioPrincipalSalvo(socio);
            permissaoEspecialService.atualizarPermissoesEspeciaisNovoSocioPrincipal(socio);
        }
    }

    @Transactional
    public void updateFromQueue(UsuarioMqRequest usuarioMqRequest) {
        var usuarioDto = UsuarioDto.parse(usuarioMqRequest);
        if (!isAlteracaoCpf(UsuarioDto.convertFrom(usuarioDto))) {
            configurarUsuario(usuarioMqRequest, usuarioDto);
            save(UsuarioDto.convertFrom(usuarioDto));
            removerPermissoesFeeder(usuarioMqRequest);
            feederService.adicionarPermissaoFeederParaUsuarioNovo(usuarioDto, usuarioMqRequest);
            permissaoTecnicoIndicadorService
                .adicionarPermissaoTecnicoIndicadorParaUsuarioNovo(usuarioDto, usuarioMqRequest, false);
            enviarParaFilaDeUsuariosSalvos(usuarioDto);
        } else {
            saveUsuarioAlteracaoCpf(UsuarioDto.convertFrom(usuarioDto));
        }
    }

    @Transactional
    public void updateUsuarioLojaFuturoFromQueue(UsuarioLojaFuturoMqRequest usuarioLojaFuturoMqRequest) {
        try {
            validarEmailCadastrado(usuarioLojaFuturoMqRequest.getEmail(), usuarioLojaFuturoMqRequest.getId());
            var usuario = findComplete(usuarioLojaFuturoMqRequest.getId());
            usuario.setEmail(usuarioLojaFuturoMqRequest.getEmail());
        } catch (Exception ex) {
            log.error("erro ao atualizar usuário da fila.", ex);
        }
    }

    private void removerPermissoesFeeder(UsuarioMqRequest usuarioMqRequest) {
        if (usuarioMqRequest.getAgenteAutorizadoFeeder() == ETipoFeeder.RESIDENCIAL
            || usuarioMqRequest.getAgenteAutorizadoFeeder() == ETipoFeeder.EMPRESARIAL) {

            var funcionalidades = new ArrayList<>(FUNCIONALIDADES_FEEDER_PARA_AA);
            if (usuarioMqRequest.getAgenteAutorizadoFeeder() == ETipoFeeder.EMPRESARIAL) {
                funcionalidades.addAll(FUNCIONALIDADES_FEEDER_PARA_COLABORADORES_AA_RESIDENCIAL);
                funcionalidades.add(FUNCIONALIDADE_TRABALHAR_ALARME_ID);
            }
            feederService.removerPermissoesEspeciais(List.of(usuarioMqRequest.getId()), funcionalidades);
        }
    }

    public void inativarPorAgenteAutorizado(UsuarioDto usuario) {
        try {
            inativarUsuario(repository.findById(usuario.getId())
                .orElseThrow(() -> USUARIO_NOT_FOUND_EXCEPTION));
        } catch (Exception ex) {
            log.error("Erro ao inativar o usuário " + usuario.getId(), ex);
        }
    }

    private void inativarUsuario(Usuario usuario) {
        if (usuario.isAtivo()) {
            usuario.setSituacao(ESituacao.I);
            repository.save(usuario);
            usuarioHistoricoService.gerarHistoricoDeInativacaoPorAgenteAutorizado(usuario.getId());
            autenticacaoService.logout(usuario.getId());
        }
    }

    @Async
    public void inativarPorOrganizacaoEmpresa(Integer organizacaoId) {
        var usuarios = repository.findByOrganizacaoEmpresaId(organizacaoId);

        if (!usuarios.isEmpty()) {
            usuarios.forEach(this::inativarUsuarioDaOrganizacao);
        }
    }

    private void inativarUsuarioDaOrganizacao(Usuario usuario) {
        if (usuario.isAtivo()) {
            try {
                usuario.setSituacao(ESituacao.I);
                repository.save(usuario);
                usuarioHistoricoService.gerarHistoricoDeInativacaoPorOrganizacaoEmpresa(usuario.getId());
                autenticacaoService.logout(usuario.getId());
            } catch (Exception ex) {
                log.error("Erro ao inativar o usuário " + usuario.getId(), ex);
            }
        }
    }

    @Transactional
    public void remanejarUsuario(UsuarioMqRequest usuarioMqRequest) {
        var usuarioDto = UsuarioDto.parse(usuarioMqRequest);
        configurarUsuario(usuarioMqRequest, usuarioDto);
        var usuarioNovo = duplicarUsuarioERemanejarAntigo(UsuarioDto.convertFrom(usuarioDto), usuarioMqRequest);
        gerarHistoricoAtivoAposRemanejamento(usuarioNovo);
        adicionarPermissoesEspeciais(usuarioNovo, usuarioMqRequest);
    }

    private Usuario duplicarUsuarioERemanejarAntigo(Usuario usuario, UsuarioMqRequest usuarioMqRequest) {
        log.info("Inicia processo de remanejamento para usuário {}.", usuarioMqRequest.getId());
        usuario.removerCaracteresDoCpf();
        var usuarioAntigoId = usuario.getId();
        salvarUsuarioRemanejado(usuario);
        permissaoTecnicoIndicadorService.removerPermissaoTecnicoIndicadorDoUsuario(UsuarioDto.of(usuario));
        var usuarioNovo = repository.save(criaNovoUsuarioAPartirDoRemanejado(usuario));
        var remanejamentoRequest = UsuarioRemanejamentoRequest.of(usuarioNovo, usuarioMqRequest, usuarioAntigoId);
        colaboradorVendasService.atualizarUsuarioRemanejado(remanejamentoRequest);
        log.info("Usuário remanejado com sucesso. Usuário Antigo {} | Usuário Novo {} | Agente Autorizado: {}",
            usuarioMqRequest.getId(), usuarioNovo.getId(), usuarioMqRequest.getAgenteAutorizadoId());

        return usuarioNovo;
    }

    private void salvarUsuarioRemanejado(Usuario usuarioRemanejado) {
        log.info("Remanejando usuário {}.", usuarioRemanejado.getId());
        usuarioRemanejado.setAlterarSenha(Eboolean.F);
        usuarioRemanejado.setSituacao(ESituacao.R);
        usuarioRemanejado.setSenha(repository.findById(usuarioRemanejado.getId())
            .orElseThrow(() -> EX_NAO_ENCONTRADO).getSenha());
        usuarioRemanejado.adicionarHistorico(UsuarioHistorico.gerarHistorico(usuarioRemanejado, REMANEJAMENTO));
        repository.save(usuarioRemanejado);
        log.info("Usuário remanejado com sucesso.");
    }

    private Usuario criaNovoUsuarioAPartirDoRemanejado(Usuario usuario) {
        log.info("Criando novo usuário a partir do usuário {} remanejado.", usuario.getId());
        validarUsuarioComCpfDiferenteRemanejado(usuario);
        usuario.setDataCadastro(LocalDateTime.now());
        usuario.setSituacao(ESituacao.A);
        usuario.setHistoricos(null);
        usuario.setId(null);
        log.info("Novo usuário criado com sucesso.");

        return usuario;
    }

    public void validarUsuarioComCpfDiferenteRemanejado(Usuario usuario) {
        if (repository.existsByCpfAndSituacaoNot(usuario.getCpf(), ESituacao.R)) {
            throw new ValidacaoException("Não é possível remanejar o usuário pois já existe outro usuário "
                + "para este CPF.");
        }
    }

    private void gerarHistoricoAtivoAposRemanejamento(Usuario usuario) {
        log.info("Adicionando histórico de remanejamento para usuário {}.", usuario.getId());
        Optional.ofNullable(usuario.getHistoricos()).ifPresent(List::clear);
        usuario.adicionarHistorico(UsuarioHistorico.gerarHistorico(usuario, REMANEJAMENTO));
        repository.save(usuario);
        log.info("Histórico de remanejamento adicionado com sucesso.");
    }

    private void adicionarPermissoesEspeciais(Usuario usuarioNovo, UsuarioMqRequest usuarioMqRequest) {
        log.info("Adicionando permissões especiais para usuário {}.", usuarioNovo.getId());
        feederService.adicionarPermissaoFeederParaUsuarioNovo(UsuarioDto.of(usuarioNovo), usuarioMqRequest);
        permissaoTecnicoIndicadorService
            .adicionarPermissaoTecnicoIndicadorParaUsuarioNovo(UsuarioDto.of(usuarioNovo), usuarioMqRequest, true);
        log.info("Permissões especiais adicionadas com sucesso.");
    }

    private boolean isAlteracaoCpf(Usuario usuario) {
        if (usuario.isNovoCadastro()) {
            return false;
        }

        var usuarioCpfAntigo = repository.findById(usuario.getId())
            .orElseThrow(() -> EX_NAO_ENCONTRADO);
        usuario.removerCaracteresDoCpf();

        return !isEmpty(usuario.getCpf()) && !usuario.getCpf().equals(usuarioCpfAntigo.getCpf());
    }

    public void saveUsuarioAlteracaoCpf(Usuario usuario) {
        var usuarioExistente = repository.findComplete(usuario.getId())
            .orElseThrow(() -> USUARIO_NOT_FOUND_EXCEPTION);
        usuarioExistente.setCpf(usuario.getCpf());
        validarCpfExistente(usuarioExistente);
        usuarioExistente.removerCaracteresDoCpf();
        usuarioExistente.adicionarHistorico(UsuarioHistorico.gerarHistorico(usuarioExistente, ALTERACAO_CPF));
        repository.save(usuarioExistente);
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
        usuario.setEmpresas(empresasIds.stream().map(e -> empresaRepository.findOne(e)).collect(toList()));
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
            var usuario = repository.findOne(usuarioMqRequest.getId());
            usuario = usuario.parse(usuarioMqRequest);
            usuario.setEmpresas(empresaRepository.findByCodigoIn(usuarioMqRequest.getEmpresa()));
            usuario.setUnidadesNegocios(unidadeNegocioRepository.findByCodigoIn(usuarioMqRequest.getUnidadesNegocio()));
            usuario.setCargo(cargoRepository.findByCodigo(usuarioMqRequest.getCargo()));
            usuario.setDepartamento(departamentoRepository.findByCodigo(usuarioMqRequest.getDepartamento()));
            usuario.setAlterarSenha(Eboolean.V);
            usuario.removerCaracteresDoCpf();

            var senhaDescriptografada = getSenhaRandomica(MAX_CARACTERES_SENHA);
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

    private void enviarParaFilaDeUsuariosSalvos(UsuarioDto usuarioDto) {
        usuarioMqSender.sendSuccess(usuarioDto);
    }

    private void enviarParaFilaDeSocioPrincipalSalvo(UsuarioDto usuarioDto) {
        usuarioMqSender.sendSuccessSocioPrincipal(usuarioDto);
    }

    private void enviarParaFilaDeLojaFuturoSalvo(UsuarioDto usuarioDto) {
        usuarioMqSender.sendSuccessLojaFuturo(usuarioDto);
    }

    private void enviarParaFilaDeAtualizarSocioPrincipalSalvo(UsuarioDto usuarioDto) {
        usuarioMqSender.sendSuccessAtualizarSocioPrincipal(usuarioDto);
    }

    private void enviarParaFilaDeAtualizarUsuariosPol(UsuarioDto usuarioDto) {
        atualizarUsuarioMqSender.sendSuccess(usuarioDto);
    }

    public void enviarParaFilaDeErroCadastroUsuarios(UsuarioMqRequest usuarioMqRequest) {
        usuarioMqSender.sendWithFailure(usuarioMqRequest);
    }

    public void enviarParaFilaDeErroAtualizacaoUsuarios(UsuarioMqRequest usuarioMqRequest) {
        usuarioAaAtualizacaoMqSender.sendWithFailure(usuarioMqRequest);
    }

    public void enviarParaFilaDeErroRemanejarUsuarios(UsuarioMqRequest usuarioMqRequest) {
        usuarioMqSender.sendRemanejamentoWithFailure(usuarioMqRequest);
    }

    private void configurarUsuario(UsuarioMqRequest usuarioMqRequest, UsuarioDto usuarioDto) {
        configurarCargo(usuarioMqRequest, usuarioDto);
        configurarDepartamento(usuarioMqRequest, usuarioDto);
        configurarNivel(usuarioMqRequest, usuarioDto);
        configurarUnidadesNegocio(usuarioMqRequest, usuarioDto);
        configurarEmpresas(usuarioMqRequest, usuarioDto);
    }

    private void configurarCargo(UsuarioMqRequest usuarioMqRequest, UsuarioDto usuarioDto) {
        var cargo = getCargo(usuarioMqRequest.getCargo());
        usuarioDto.setCargoId(cargo.getId());
        usuarioDto.setCargoCodigo(cargo.getCodigo());
    }

    private Cargo getCargo(CodigoCargo codigoCargo) {
        return cargoRepository.findByCodigo(codigoCargo);
    }

    private void configurarDepartamento(UsuarioMqRequest usuarioMqRequest, UsuarioDto usuarioDto) {
        var departamento = departamentoRepository.findByCodigo(usuarioMqRequest.getDepartamento());
        usuarioDto.setDepartamentoId(departamento.getId());
    }

    private void configurarNivel(UsuarioMqRequest usuarioMqRequest, UsuarioDto usuarioDto) {
        var nivel = nivelRepository.findByCodigo(usuarioMqRequest.getNivel());
        usuarioDto.setNivelId(nivel.getId());
    }

    private void configurarUnidadesNegocio(UsuarioMqRequest usuarioMqRequest, UsuarioDto usuarioDto) {
        var unidadesNegocios = unidadeNegocioRepository
            .findByCodigoIn(usuarioMqRequest.getUnidadesNegocio());
        usuarioDto.setUnidadesNegociosId(unidadesNegocios.stream()
            .map(UnidadeNegocio::getId).collect(toList()));
    }

    private void configurarEmpresas(UsuarioMqRequest usuarioMqRequest, UsuarioDto usuarioDto) {
        var empresas = empresaRepository.findByCodigoIn(usuarioMqRequest.getEmpresa());
        usuarioDto.setEmpresasId(empresas.stream().map(Empresa::getId).collect(toList()));
    }

    private String getSenhaRandomica(int size) {
        return getRandomPassword(size);
    }

    private void validarCpfExistente(Usuario usuario) {
        if (usuario.getCpf() == null) {
            return;
        }

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
                    throw EMAIL_CADASTRADO_EXCEPTION;
                }
            });
    }

    @Transactional
    public void ativar(UsuarioAtivacaoDto dto) {
        var usuario = findComplete(dto.getIdUsuario());
        usuario.setDataReativacao(LocalDateTime.now());
        usuario.setSituacao(ESituacao.A);
        validarAtivacao(usuario);
        validarSubcanal(usuario);
        usuario.adicionarHistorico(
            UsuarioHistorico.criarHistoricoAtivacao(
                getUsuarioAtivacao(dto),
                dto.getObservacao(),
                usuario));
        repository.save(usuario);
        usuarioAfastamentoService.atualizaDataFimAfastamento(usuario.getId());
        ativarSocio(usuario);
    }

    public void ativar(Integer id) {
        repository.findById(id)
            .ifPresent(user -> {
                agenteAutorizadoService.ativarUsuario(id);
                user.setSituacao(ESituacao.A);
                repository.save(user);
            });
    }

    private void validarAtivacao(Usuario usuario) {
        var isUsuarioAdmin = autenticacaoService.getUsuarioAutenticado().getNivel().equals("XBRAIN")
            || autenticacaoService.getUsuarioAutenticado().getNivel().equals("MSO");
        var usuarioInativoPorMuitasSimulacoes = usuarioHistoricoService
            .findMotivoInativacaoByUsuarioId(usuario.getId())
            .map(motivoInativacao -> motivoInativacao.equals("INATIVADO POR REALIZAR MUITAS SIMULAÇÕES"))
            .orElse(false);
        var isClienteLojaFuturo = CLIENTE_LOJA_FUTURO.equals(usuario.getCargo().getCodigo());
        var isAaEstruturaLojaFuturo = "LOJA_FUTURO".equals(agenteAutorizadoService.getEstruturaByUsuarioId(usuario.getId()));

        if (isEmpty(usuario.getCpf()) && !isClienteLojaFuturo) {
            throw new ValidacaoException("O usuário não pode ser ativado por não possuir CPF.");
        } else if (isClienteLojaFuturo && !isAaEstruturaLojaFuturo) {
            throw new ValidacaoException(MSG_ERRO_ATIVAR_USUARIO_COM_AA_ESTRUTURA_NAO_LOJA_FUTURO);
        } else if (usuario.isSocioPrincipal() && !encontrouAgenteAutorizadoBySocioEmail(usuario.getEmail())) {
            throw new ValidacaoException(MSG_ERRO_AO_ATIVAR_USUARIO
                + " Ou email do sócio está divergente do que está inserido no agente autorizado.");
        } else if (!usuario.isSocioPrincipal() && usuario.isAgenteAutorizado()
            && !encontrouAgenteAutorizadoByUsuarioId(usuario.getId())) {
            throw new ValidacaoException(MSG_ERRO_AO_ATIVAR_USUARIO);
        } else if (!isUsuarioAdmin && usuarioInativoPorMuitasSimulacoes) {
            throw new ValidacaoException(MSG_ERRO_ATIVAR_USUARIO_INATIVADO_POR_MUITAS_SIMULACOES);
        } else if (!isEmpty(usuario.getOrganizacaoEmpresa())
            && !usuario.getOrganizacaoEmpresa().isAtivo()) {
            throw new ValidacaoException(MSG_ERRO_ATIVAR_USUARIO_COM_FORNECEDOR_INATIVO);
        }

        repository.save(usuario);
    }

    private void validarSubcanal(Usuario usuario) {
        if (validarCanalEOperador(usuario)) {
            var superior = validarSuperior(usuario);
            var subCanalVendedor = obterSubcanal(usuario);
            if (!superior.getSubCanais().contains(subCanalVendedor)) {
                throw new ValidacaoException("Favor deve-se por este usuario no mesmo subcanal"
                    + " do superior ou trocar a hierarquia para um superior do mesmo subcanal");
            }
        }
    }

    private boolean validarCanalEOperador(Usuario usuario) {
        return usuario.getCanais().contains(ECanal.D2D_PROPRIO)
            && VENDEDOR_OPERACAO.equals(usuario.getCargoCodigo());
    }

    private Usuario validarSuperior(Usuario usuario) {
        return usuario.getUsuariosHierarquia().stream()
            .map(UsuarioHierarquia::getUsuarioSuperior)
            .filter(usuarioHierquia -> SUPERVISOR_OPERACAO.equals(usuarioHierquia.getCargoCodigo())
                || COORDENADOR_OPERACAO.equals(usuarioHierquia.getCargoCodigo()))
            .findFirst().orElseThrow(() -> new ValidacaoException("Superior do Vendedor não foi encontrado"));
    }

    private SubCanal obterSubcanal(Usuario usuario) {
        return usuario.getSubCanais().stream()
            .findFirst()
            .orElseThrow(() -> new ValidacaoException("Não foi encontrado o subcanal do " + usuario.getCargo().getCodigo()));
    }

    private boolean encontrouAgenteAutorizadoByUsuarioId(Integer usuarioId) {
        return agenteAutorizadoService.existeAaAtivoByUsuarioId(usuarioId);
    }

    private boolean encontrouAgenteAutorizadoBySocioEmail(String usuarioEmail) {
        return agenteAutorizadoService.existeAaAtivoBySocioEmail(usuarioEmail);
    }

    public void limparCpfUsuario(Integer id) {
        var usuario = limpaCpf(id);
        colaboradorVendasService.limparCpfColaboradorVendas(usuario.getEmail());
    }

    public void limparCpfAntigoSocioPrincipal(Integer id) {
        var socio = findOneById(id);
        socio.setCpf(null);
        repository.save(socio);
    }

    @Transactional
    public Usuario limpaCpf(Integer id) {
        var usuario = findComplete(id);
        usuario.setCpf(null);

        return repository.save(usuario);
    }

    public void inativar(Integer id) {
        repository.findComplete(id)
            .ifPresent(user -> {
                agenteAutorizadoService.inativarUsuario(id);
                user.setSituacao(ESituacao.I);
                repository.save(user);
                autenticacaoService.forcarLogoutGeradorLeadsEClienteLojaFuturo(user);
            });
    }

    @Transactional
    public void inativar(UsuarioInativacaoDto usuarioInativacao) {
        var usuario = findComplete(usuarioInativacao.getIdUsuario());
        validarUsuarioAtivoLocalEPossuiAgendamento(usuario);
        usuario.setSituacao(ESituacao.I);
        usuario.adicionarHistorico(gerarDadosDeHistoricoDeInativacao(usuarioInativacao, usuario));
        inativarUsuarioNaEquipeVendas(usuario, carregarMotivoInativacao(usuarioInativacao));
        removerHierarquiaDoUsuarioEquipe(usuario, carregarMotivoInativacao(usuarioInativacao));
        autenticacaoService.logout(usuario.getId());
        repository.save(usuario);
        inativarSocio(usuario);
    }

    private void ativarSocio(Usuario usuario) {
        if (isSocioPrincipal(usuario)) {
            agenteAutorizadoService.ativarUsuario(usuario.getId());
        }
    }

    private void inativarSocio(Usuario usuario) {
        if (isSocioPrincipal(usuario)) {
            agenteAutorizadoService.inativarUsuario(usuario.getId());
        }
    }

    private void validarUsuarioAtivoLocalEPossuiAgendamento(Usuario usuario) {
        if (usuario.isOperadorTelevendasAtivoLocal()
            && mailingService.countQuantidadeAgendamentosProprietariosDoUsuario(usuario.getId(), ECanal.ATIVO_PROPRIO) > 0) {
            throw USUARIO_ATIVO_LOCAL_POSSUI_AGENDAMENTOS_EX;
        }
    }

    private UsuarioHistorico gerarDadosDeHistoricoDeInativacao(UsuarioInativacaoDto usuarioInativacao,
                                                               Usuario usuario) {
        return UsuarioHistorico.builder()
            .dataCadastro(LocalDateTime.now())
            .motivoInativacao(carregarMotivoInativacao(usuarioInativacao))
            .usuario(usuario)
            .usuarioAlteracao(getUsuarioInativacaoTratado(usuarioInativacao))
            .observacao(usuarioInativacao.getObservacao())
            .situacao(usuario.getSituacao())
            .ferias(usuarioFeriasService
                .save(usuario, usuarioInativacao).orElse(null))
            .afastamento(usuarioAfastamentoService
                .save(usuario, usuarioInativacao).orElse(null))
            .build();
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
        var usuarioPredicate = new UsuarioPredicate();
        usuarioPredicate.filtraPermitidos(autenticacaoService.getUsuarioAutenticado(), this, true);
        usuarioPredicate.comNivel(Collections.singletonList(nivelId));

        return repository.findAllUsuariosHierarquia(usuarioPredicate.build());
    }

    public List<UsuarioHierarquiaResponse> getUsuariosCargoSuperior(Integer cargoId, List<Integer> cidadesId) {
        var usuarios = repository.getUsuariosFilter(
            new UsuarioPredicate()
                .filtraPermitidos(autenticacaoService.getUsuarioAutenticado(), this, true)
                .comCargos(cargoService.findById(cargoId).getCargosSuperioresId())
                .comCidade(cidadesId)
                .build());

        return UsuarioHierarquiaResponse.convertTo(usuarios);
    }

    public List<UsuarioHierarquiaResponse> getUsuariosCargoSuperiorByCanal(Integer cargoId,
                                                                           UsuarioCargoSuperiorPost post,
                                                                           Set<ECanal> canais) {
        var usuariosCargoSuperior = repository.getUsuariosFilter(
            new UsuarioPredicate()
                .filtraPermitidos(autenticacaoService.getUsuarioAutenticado(), this, false)
                .comCargos(cargoService.findById(cargoId).getCargosSuperioresId())
                .comCidade(post.getCidadeIds())
                .comCanais(canais)
                .comOrganizacaoEmpresaId(post.getOrganizacaoId())
                .build());

        return UsuarioHierarquiaResponse.convertTo(usuariosCargoSuperior);
    }

    public List<UsuarioHierarquiaResponse> getUsuariosCargoSuperiorByCanalAndSubCanal(Integer cargoId,
                                                                                      UsuarioCargoSuperiorPost post,
                                                                                      Set<ECanal> canais,
                                                                                      Set<Integer> subCanais) {
        var usuariosCargoSuperior = repository.getUsuariosFilter(
            new UsuarioPredicate()
                .filtraPermitidos(autenticacaoService.getUsuarioAutenticado(), this, false)
                .comCargos(cargoService.findById(cargoId).getCargosSuperioresId())
                .comCidade(post.getCidadeIds())
                .comCanais(canais)
                .comSubCanais(subCanais)
                .comOrganizacaoEmpresaId(post.getOrganizacaoId())
                .build());
        return UsuarioHierarquiaResponse.convertTo(usuariosCargoSuperior);
    }

    public List<UsuarioDto> getUsuariosFiltros(UsuarioFiltrosDto usuarioFiltrosDto) {
        var usuarioPredicate = new UsuarioPredicate()
            .comEmpresas(usuarioFiltrosDto.getEmpresasIds())
            .comUnidadesNegocio(usuarioFiltrosDto.getUnidadesNegocioIds())
            .comNivel(usuarioFiltrosDto.getNivelIds())
            .comCargo(usuarioFiltrosDto.getCargoIds())
            .comDepartamento(usuarioFiltrosDto.getDepartamentoIds())
            .comIds(usuarioFiltrosDto.getUsuariosIds())
            .isAtivo(usuarioFiltrosDto.getAtivo());

        montarPredicateComCidade(usuarioPredicate, usuarioFiltrosDto);

        var usuarioList = repository.getUsuariosFilter(usuarioPredicate.build());

        return usuarioList.stream()
            .map(UsuarioDto::of)
            .collect(toList());
    }

    private void montarPredicateComCidade(UsuarioPredicate predicate, UsuarioFiltrosDto filtro) {
        var listaPartes = ListUtil.divideListaEmListasMenores(filtro.getCidadesIds(), QTD_MAX_IN_NO_ORACLE);

        listaPartes.forEach(lista -> predicate.comCidade(lista));
    }

    public List<UsuarioResponse> getUsuariosByIds(List<Integer> idsUsuarios) {
        var usuarios = repository.findBySituacaoAndIdsIn(ESituacao.A,
            new UsuarioPredicate().comUsuariosIds(idsUsuarios).build());
        return usuarios.stream()
            .map(UsuarioResponse::of)
            .collect(toList());
    }

    public List<Integer> getUsuariosAtivosByIds(List<Integer> idsUsuarios) {
        return repository.findAllIdsBySituacaoAndIdsIn(ESituacao.A,
            new UsuarioPredicate().comUsuariosIds(idsUsuarios).build());
    }

    public List<UsuarioResponse> getUsuariosByIdsTodasSituacoes(Collection<Integer> idsUsuarios) {
        return partition(List.copyOf(idsUsuarios), QTD_MAX_IN_NO_ORACLE).stream()
            .map(repository::findByIdIn)
            .flatMap(List::stream)
            .map(UsuarioResponse::of)
            .collect(toList());
    }

    public List<UsuarioResponse> getUsuariosInativosByIds(List<Integer> usuariosInativosIds) {
        var usuarios = repository.findBySituacaoAndIdIn(ESituacao.I, usuariosInativosIds);

        return usuarios.stream()
            .map(UsuarioResponse::of)
            .collect(toList());
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
            .collect(toList());
    }

    public List<UsuarioSuperiorAutoComplete> getUsuariosSupervisoresDoAaAutoComplete(Integer executivoId) {
        return repository.getUsuariosSuperioresDoExecutivoDoAa(executivoId)
            .stream()
            .map(UsuarioSuperiorAutoComplete::of)
            .collect(toList());
    }

    private Integer objectToInteger(Object arg) {
        return NumberUtils.parseNumber(arg.toString(), Integer.class);
    }

    private String objectToString(Object arg) {
        return arg != null ? arg.toString() : "";
    }

    public UsuarioResponse getUsuarioSuperior(Integer idUsuario) {
        var usuarioHierarquia = repository.getUsuarioSuperior(idUsuario)
            .orElse(null);
        if (Objects.isNull(usuarioHierarquia)) {
            return new UsuarioResponse();
        }
        return UsuarioResponse.of(usuarioHierarquia.getUsuarioSuperior());
    }

    public List<UsuarioResponse> getUsuarioSuperiores(Integer idUsuario) {
        var usuariosHierarquia = repository.getUsuarioSuperiores(idUsuario);

        return usuariosHierarquia
            .stream()
            .map(uh -> UsuarioResponse.of(uh.getUsuarioSuperior()))
            .collect(toList());
    }

    public List<UsuarioResponse> getUsuarioByPermissaoEspecial(String funcionalidade) {
        return repository.getUsuariosByPermissaoEspecial(funcionalidade)
            .stream()
            .map(PermissaoEspecial::getUsuario)
            .map(UsuarioResponse::of)
            .collect(toList());
    }

    @Transactional
    public void alterarSenhaEReenviarPorEmail(Integer idUsuario) {
        var usuario = findComplete(idUsuario);
        updateSenha(usuario, Eboolean.V);
    }

    @Transactional
    public void alterarSenhaAa(UsuarioAlterarSenhaDto usuarioAlterarSenhaDto) {
        var usuario = findComplete(usuarioAlterarSenhaDto.getUsuarioId());
        usuario.setAlterarSenha(usuarioAlterarSenhaDto.getAlterarSenha());
        updateSenha(usuario, usuarioAlterarSenhaDto.getAlterarSenha());
        repository.save(usuario);
    }

    @Transactional
    public void alterarDadosAcessoEmail(UsuarioDadosAcessoRequest usuarioDadosAcessoRequest) {
        var usuario = findComplete(usuarioDadosAcessoRequest.getUsuarioId());
        validarEmail(usuario, usuarioDadosAcessoRequest.getEmailAtual(), usuarioDadosAcessoRequest.getEmailNovo());
        usuario.setEmail(usuarioDadosAcessoRequest.getEmailNovo());
        repository.updateEmail(usuarioDadosAcessoRequest.getEmailNovo(), usuario.getId());
        notificacaoService.enviarEmailAtualizacaoEmail(usuario, usuarioDadosAcessoRequest);
        processarUsuarioParaSocialHub(getUsuario(usuarioDadosAcessoRequest.getUsuarioId()));
        updateSenha(usuario, Eboolean.V);
        enviarParaFilaDeUsuariosSalvos(UsuarioDto.of(usuario));
    }

    public void atualizarEmailSocioInativo(Integer socioPrincipalId) {
        var socio = findOneById(socioPrincipalId);
        var emailAtual = socio.getEmail();
        var emailInativo = atualizarEmailInativo(emailAtual);

        socio.setEmail(emailInativo);
        repository.save(socio);

        agenteAutorizadoService.atualizarEmailSocioPrincipalInativo(emailAtual, emailInativo, socioPrincipalId);
    }

    private void updateSenha(Usuario usuario, Eboolean alterarSenha) {
        var senhaDescriptografada = getSenhaRandomica(MAX_CARACTERES_SENHA);
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
        autenticacaoService.forcarLogoutGeradorLeadsEClienteLojaFuturo(usuario);

        return usuario.getId();
    }

    private void validarSenhaAtual(Usuario usuario, final String senhaAtual) {
        if (!BCrypt.checkpw(senhaAtual, usuario.getSenha())) {
            throw SENHA_ATUAL_INCORRETA_EXCEPTION;
        }
    }

    public ConfiguracaoResponse getConfiguracaoByUsuario() {
        var usuario = repository.findComConfiguracao(autenticacaoService.getUsuarioId()).orElse(null);

        return usuario != null
            ? ConfiguracaoResponse.convertFrom(usuario.getConfiguracao())
            : new ConfiguracaoResponse();
    }

    public List<FuncionalidadeResponse> getFuncionalidadeByUsuario(Integer idUsuario) {
        var usuario = findComplete(idUsuario);
        var predicate = getFuncionalidadePredicate(usuario);
        var funcionalidades = cargoDepartamentoFuncionalidadeRepository
            .findFuncionalidadesPorCargoEDepartamento(predicate.build());

        return Stream.concat(
                funcionalidades
                    .stream()
                    .map(CargoDepartamentoFuncionalidade::getFuncionalidade),
                permissaoEspecialRepository
                    .findPorUsuario(usuario.getId()).stream())
            .distinct()
            .map(FuncionalidadeResponse::convertFrom)
            .collect(toList());
    }

    public UsuarioPermissaoResponse findPermissoesByUsuario(Integer idUsuario) {
        var usuario = findComplete(idUsuario);

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
            .map(UsuarioResponse::of).collect(toList());
    }

    public List<Integer> getUsuariosIdsByNivel(CodigoNivel nivel) {
        return repository.getUsuariosIdsByNivel(nivel);
    }

    public List<CidadeResponse> getCidadesByUsuarioId(Integer usuarioId) {
        var usuario = findComplete(usuarioId);

        if (usuario.getCidades().isEmpty()) {
            return List.of();
        }

        return getCidadesResponseByUsuarioCidades(usuario.getCidades());
    }

    @Transactional
    public ConfiguracaoResponse adicionarConfiguracao(UsuarioConfiguracaoDto dto) {
        var configuracao = configuracaoRepository
            .findByUsuario(new Usuario(dto.getUsuario()))
            .orElse(new Configuracao());
        configuracao.configurar(dto);
        configuracao = configuracaoRepository.save(configuracao);

        return ConfiguracaoResponse.convertFrom(configuracao);
    }

    @Transactional
    public void removerConfiguracao(UsuarioConfiguracaoDto dto) {
        var configuracao = configuracaoRepository.findByRamal(dto.getRamal());
        configuracao.forEach(c -> configuracaoRepository.delete(c));
    }

    @Transactional
    public void removerRamalConfiguracao(UsuarioConfiguracaoDto dto) {
        var configuracao = configuracaoRepository.findByRamal(dto.getRamal());
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
        var novasHierarquiasValidas = validaUsuarioHierarquiaExistente(novasHierarquias);

        novasHierarquiasValidas.forEach(u -> {
            var usuarioHierarquia
                = UsuarioHierarquia.criar(new Usuario(u.getUsuarioId()), u.getUsuarioSuperiorId(), u.getUsuarioCadastroId());
            usuarioHierarquiaRepository.save(usuarioHierarquia);
        });
    }

    private List<UsuarioHierarquiaCarteiraDto> validaUsuarioHierarquiaExistente(
        List<UsuarioHierarquiaCarteiraDto> novasHierarquias) {
        var usuarioHierarquiasExistentes = (List<UsuarioHierarquia>) usuarioHierarquiaRepository.findAll();

        return novasHierarquias
            .stream()
            .filter(c -> !validaUsuarioHierarquiaExistente(usuarioHierarquiasExistentes, c))
            .distinct()
            .collect(toList());
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
        var usuario = findByEmailAa(email, null);
        usuario.ifPresent(u -> {
            var usuarioCompleto = repository.findById(u.getId());
            usuarioCompleto.ifPresent(user -> {
                user.setSituacao(ESituacao.A);
                repository.save(user);
            });
        });
    }

    public void inativarSocioPrincipal(String email) {
        var usuario = findByEmailAa(email, null);
        usuario.ifPresent(u -> {
            var usuarioCompleto = repository.findById(usuario.get().getId());
            usuarioCompleto.ifPresent(user -> {
                user.setSituacao(ESituacao.I);
                repository.save(user);
            });
        });
    }

    public void inativarAntigoSocioPrincipal(String email) {
        var antigoSocioPrincipal = findOneByEmail(email);

        if (antigoSocioPrincipal.getSituacao() == ATIVO) {
            antigoSocioPrincipal.setSituacao(INATIVO);
            repository.save(antigoSocioPrincipal);
            autenticacaoService.logout(antigoSocioPrincipal.getId());
        }

        agenteAutorizadoService.inativarAntigoSocioPrincipal(email);
    }

    private Usuario findOneByEmail(String email) {
        return repository.findByEmail(email)
            .orElseThrow(() -> EX_NAO_ENCONTRADO);
    }

    public void inativarColaboradores(String cnpj) {
        var emailColaboradores = agenteAutorizadoService.recuperarColaboradoresDoAgenteAutorizado(cnpj);
        emailColaboradores.forEach(colaborador -> {
            var usuario = repository.findByEmail(colaborador)
                .orElseThrow(() -> EX_NAO_ENCONTRADO);
            usuario.setSituacao(ESituacao.I);
            usuario.removerCaracteresDoCpf();
            repository.save(usuario);
        });
    }

    public List<UsuarioHierarquiaResponse> getVendedoresOperacaoDaHierarquia(Integer usuarioId) {
        return repository.getSubordinadosPorCargo(usuarioId,
                Set.of(VENDEDOR_OPERACAO.name(),
                    CodigoCargo.OPERACAO_TELEVENDAS.name(),
                    CodigoCargo.OPERACAO_EXECUTIVO_VENDAS.name()))
            .stream()
            .map(this::criarUsuarioHierarquiaVendedoresResponse)
            .collect(toList());
    }

    public List<UsuarioHierarquiaResponse> getSupervisoresOperacaoDaHierarquia(Integer usuarioId) {
        return repository.getSubordinadosPorCargo(usuarioId, Set.of(SUPERVISOR_OPERACAO.name()))
            .stream()
            .map(this::criarUsuarioHierarquiaVendedoresResponse)
            .collect(toList());
    }

    public List<Integer> getIdsVendedoresOperacaoDaHierarquia(Integer usuarioId) {
        return getVendedoresOperacaoDaHierarquia(usuarioId).stream()
            .map(UsuarioHierarquiaResponse::getId)
            .collect(toList());
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
        var usuarioCsvResponses =
            repository.getUsuariosCsv(filtrarUsuariosPermitidos(filtros).build());
        preencherUsuarioCsvsDeOperacao(usuarioCsvResponses);
        preencherUsuarioCsvsDeAa(usuarioCsvResponses);

        return usuarioCsvResponses;
    }

    void preencherUsuarioCsvsDeOperacao(List<UsuarioCsvResponse> usuarioCsvResponses) {
        var usuarioIds = usuarioCsvResponses.parallelStream()
            .filter(usuarioCsvResponse -> OPERACAO.equals(usuarioCsvResponse.getNivel()))
            .map(UsuarioCsvResponse::getId)
            .collect(toList());

        if (!usuarioIds.isEmpty()) {

            var map = partition(usuarioIds, QTD_MAX_IN_NO_ORACLE).parallelStream()
                .map(parte -> repository.getCanaisByUsuarioIds(parte))
                .flatMap(Collection::parallelStream)
                .collect(Collectors.groupingBy(Canal::getUsuarioId));

            usuarioCsvResponses.forEach(
                usuarioCsvResponse -> usuarioCsvResponse.setCanais(
                    map.getOrDefault(usuarioCsvResponse.getId(), null))
            );
        }
    }

    void preencherUsuarioCsvsDeAa(List<UsuarioCsvResponse> usuarioCsvResponses) {
        var usuarioRequest = UsuarioRequest.of(usuarioCsvResponses.parallelStream().filter(
            usuarioCsvResponse -> AGENTE_AUTORIZADO.equals(usuarioCsvResponse.getNivel())
        ).map(UsuarioCsvResponse::getId).collect(toList()));

        if (!usuarioRequest.getUsuarioIds().isEmpty()) {
            var agenteAutorizadosUsuarioDtos = agenteAutorizadoService
                .getAgenteAutorizadosUsuarioDtosByUsuarioIds(usuarioRequest);

            usuarioCsvResponses.parallelStream().forEach(usuarioCsvResponse -> {
                findAasDeUsuarioId(
                    agenteAutorizadosUsuarioDtos, usuarioCsvResponse.getId()
                ).parallelStream().forEach(agenteAutorizadoUsuarioDto -> {
                    usuarioCsvResponse.setRazaoSocial(agenteAutorizadoUsuarioDto.getRazaoSocial());
                    usuarioCsvResponse.setCnpj(agenteAutorizadoUsuarioDto.getCnpj());
                });
            });
        }
    }

    private List<AgenteAutorizadoUsuarioDto> findAasDeUsuarioId(List<AgenteAutorizadoUsuarioDto> agenteAutorizadoUsuarioDtos,
                                                                Integer usuarioId) {
        return agenteAutorizadoUsuarioDtos.parallelStream().filter(agenteAutorizadoUsuarioDto ->
            agenteAutorizadoUsuarioDto.getUsuarioId().equals(usuarioId)).collect(toList());
    }

    public void exportUsuariosToCsv(UsuarioFiltros filtros, HttpServletResponse response) {
        var usuarios = getAllForCsv(filtros);
        if (!CsvUtils.setCsvNoHttpResponse(
            getCsv(usuarios),
            CsvUtils.createFileName(USUARIOS_CSV.name()),
            response)) {
            throw new ValidacaoException("Falha ao tentar baixar relatório de usuários!");
        }
    }

    private UsuarioPredicate filtrarUsuariosPermitidos(UsuarioFiltros filtros) {
        filtros.setNovasRegionaisIds(regionalService.getNovasRegionaisIds());
        var predicate = filtros.toPredicate();
        predicate.filtraPermitidos(autenticacaoService.getUsuarioAutenticado(), this, true);
        if (!StringUtils.isEmpty(filtros.getCnpjAa())) {
            obterUsuariosAa(filtros.getCnpjAa(), predicate, true);
        }
        return predicate;
    }

    public List<Integer> getUsuariosPermitidosPelaEquipeDeVenda() {
        var cargos = ECanal.ATIVO_PROPRIO == autenticacaoService.getUsuarioCanal()
            ? CARGOS_PARA_INTEGRACAO_ATIVO_LOCAL
            : CARGOS_PARA_INTEGRACAO_D2D;

        var usuariosIdsDaEquipe = equipeVendaD2dService.getUsuariosPermitidos(cargos)
            .stream()
            .mapToInt(EquipeVendaUsuarioResponse::getUsuarioId);
        return IntStream.concat(usuariosIdsDaEquipe, IntStream.of(autenticacaoService.getUsuarioId()))
            .boxed()
            .collect(toList());
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
        return funcionalidadeService.getFuncionalidadesPermitidasAoUsuarioComCanal(
                findCompleteById(autenticacaoService.getUsuarioId()))
            .stream()
            .map(UsuarioPermissaoCanal::of)
            .collect(toList());
    }

    public List<Integer> getIdsSubordinadosDaHierarquia(Integer usuarioId, Set<String> codigoCargo) {
        return repository.getSubordinadosPorCargo(usuarioId, codigoCargo)
            .stream()
            .map(row -> objectToInteger(row[POSICAO_ZERO]))
            .collect(toList());
    }

    public List<SelectResponse> getSubclusterUsuario(Integer usuarioId) {
        return repository
            .getSubclustersUsuario(usuarioId)
            .stream()
            .map(s -> SelectResponse.of(s.getId(), s.getNomeComMarca()))
            .collect(toList());
    }

    public List<SelectResponse> getUfUsuario(Integer usuarioId) {
        return repository
            .getUfsUsuario(usuarioId)
            .stream()
            .map(uf -> SelectResponse.of(uf.getId(), uf.getNome()))
            .collect(toList());
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
            .collect(toList());
    }

    private void adicionarFiltroEquipeVendas(PublicoAlvoComunicadoFiltros usuarioFiltros) {
        if (!CollectionUtils.isEmpty(usuarioFiltros.getEquipesVendasIds())) {
            var usuarios = equipeVendaD2dService.getUsuariosDaEquipe(usuarioFiltros.getEquipesVendasIds());
            usuarioFiltros.adicionarUsuariosId(usuarios);
        }
    }

    private void adicionarFiltroAgenteAutorizado(PublicoAlvoComunicadoFiltros usuarioFiltros) {
        if (!CollectionUtils.isEmpty(usuarioFiltros.getAgentesAutorizadosIds())) {
            var usuarios = new ArrayList<Integer>();
            usuarioFiltros.getAgentesAutorizadosIds()
                .forEach(aaId -> usuarios.addAll(getIdUsuariosAa(aaId)));
            if (usuarios.isEmpty()) {
                throw new ValidacaoException("Não foi encontrado nenhum usuário do agente autorizado");
            }
            usuarioFiltros.adicionarUsuariosId(usuarios);
        }
    }

    private List<Integer> getIdUsuariosAa(Integer aaId) {
        try {
            return agenteAutorizadoService.getUsuariosByAaId(aaId, true)
                .stream()
                .map(UsuarioAgenteAutorizadoResponse::getId)
                .collect(toList());
        } catch (Exception ex) {
            log.error("Erro ao recuperar usuarios do agente autorizado.", ex);
            return List.of();
        }
    }

    public List<Integer> getIdDosUsuariosAlvoDoComunicado(PublicoAlvoComunicadoFiltros usuarioFiltros) {
        montarPredicate(usuarioFiltros);
        usuarioFiltros.setComUsuariosLogadosHoje(true);
        return repository.findAllIds(usuarioFiltros, regionalService.getNovasRegionaisIds());
    }

    private void montarPredicate(PublicoAlvoComunicadoFiltros usuarioFiltros) {
        usuarioFiltros.tratarFiltrosLocalizacaoParaMelhorDesempenho();
        adicionarFiltroAgenteAutorizado(usuarioFiltros);
        adicionarFiltroEquipeVendas(usuarioFiltros);
        usuarioFiltros.setUsuarioService(this);
        var usuarioAutenticado = autenticacaoService.getUsuarioAutenticado();
        usuarioFiltros.setUsuarioAutenticado(usuarioAutenticado);
    }

    public List<UsuarioNomeResponse> getUsuariosAlvoDoComunicado(PublicoAlvoComunicadoFiltros usuarioFiltros) {
        montarPredicate(usuarioFiltros);
        return repository.findAllNomesIds(usuarioFiltros, regionalService.getNovasRegionaisIds());
    }

    public List<UsuarioCidadeDto> findCidadesDoUsuarioLogado() {
        var cidades = usuarioCidadeRepository
            .findUsuarioCidadesByUsuarioId(autenticacaoService.getUsuarioAutenticadoId()
                .orElseThrow(() -> EX_NAO_ENCONTRADO));

        if (cidades.isEmpty()) {
            return List.of();
        }

        var cidadesResponse = getCidadesResponseByUsuarioCidades(cidades);

        return UsuarioCidadeDto.ofCidadesResponse(cidadesResponse);
    }

    private List<CidadeResponse> getCidadesResponseByUsuarioCidades(Set<UsuarioCidade> usuarioCidades) {
        var cidadesResponse = usuarioCidades
            .stream()
            .map(usuarioCidade -> CidadeResponse.of(usuarioCidade.getCidade()))
            .sorted(Comparator.comparing(CidadeResponse::getNome))
            .collect(toList());

        if (cidadesResponse.stream().anyMatch(cidadeResponse ->
            hasFkCidadeSemNomeCidadePai(cidadeResponse.getFkCidade(), cidadeResponse.getCidadePai()))) {
            var distritos = cidadeService.getCidadesDistritos(Eboolean.V);

            cidadesResponse
                .forEach(cidadeResponse -> CidadeResponse.definirNomeCidadePaiPorDistritos(cidadeResponse, distritos));
        }

        return cidadesResponse;
    }

    public List<UsuarioResponse> getVendedoresByIds(List<Integer> idsUsuarios) {
        return partition(idsUsuarios, QTD_MAX_IN_NO_ORACLE)
            .stream()
            .map(ids -> repository.findByIdIn(ids))
            .flatMap(Collection::stream)
            .map(UsuarioResponse::of)
            .collect(toList());
    }

    public UsuarioResponse findById(Integer id) {
        return repository.findById(id)
            .map(UsuarioResponse::of)
            .orElseThrow(() -> EX_NAO_ENCONTRADO);
    }

    public List<UsuarioResponse> findUsuariosByCodigoCargo(CodigoCargo codigoCargo) {
        return repository.findUsuariosByCodigoCargo(codigoCargo).stream()
            .map(UsuarioResponse::of)
            .collect(toList());
    }

    public List<Integer> findIdUsuariosAtivosByCodigoCargos(List<CodigoCargo> codigoCargos) {
        return repository.findIdUsuariosAtivosByCodigoCargos(codigoCargos);
    }

    public UsuarioComLoginNetSalesResponse getUsuarioByIdComLoginNetSales(Integer usuarioId) {
        return Optional.of(Optional.of(repository.findById(usuarioId)
                    .orElseThrow(() -> EX_NAO_ENCONTRADO))
                .filter(Usuario::isAtivo)
                .orElseThrow(() -> COLABORADOR_NAO_ATIVO))
            .map(UsuarioComLoginNetSalesResponse::of)
            .filter(UsuarioComLoginNetSalesResponse::hasLoginNetSales)
            .orElseThrow(() -> USUARIO_NAO_POSSUI_LOGIN_NET_SALES_EX);
    }

    public List<Integer> buscarIdsUsuariosDeCargosInferiores(Integer nivelId) {
        return repository.buscarIdsUsuariosPorCargosIds(
            cargoService.getPermitidosPorNivel(new CargoPredicate().comNivel(nivelId))
                .stream()
                .map(Cargo::getId)
                .collect(toList())
        );
    }

    public List<SelectResponse> buscarUsuariosAtivosNivelOperacaoCanalAa() {
        return repository.findAllAtivosByNivelOperacaoCanalAa();
    }

    public List<Usuario> getUsuariosDaHierarquiaAtivoLocalDoUsuarioLogado() {
        return (List<Usuario>) repository.findAll(
            new UsuarioPredicate().filtraPermitidos(
                    autenticacaoService.getUsuarioAutenticado(), this, true)
                .build());
    }

    public Set<Integer> getAllUsuariosIdsSuperiores() {
        var usuarioAutenticado = autenticacaoService.getUsuarioAutenticado();
        var cargosAceitos = cargoSuperiorRepository.getCargosHierarquia(usuarioAutenticado.getCargoId());
        var usuarios = new HashSet<Integer>();
        if (usuarioAutenticado.haveCanalAgenteAutorizado()) {
            var usuariosPol = agenteAutorizadoService.getUsuariosIdsSuperioresPol();

            if (!CollectionUtils.isEmpty(usuariosPol)) {
                usuarios.addAll(repository.findAllIds(new UsuarioPredicate()
                    .comCargo(cargosAceitos)
                    .comUsuariosIds(usuariosPol)
                    .build()));
                usuarios.addAll(repository.getUsuariosSuperioresIds(usuariosPol));
            }
        }
        if (usuarioAutenticado.haveCanalDoorToDoor()
            && CARGOS_PARA_INTEGRACAO_D2D.contains(usuarioAutenticado.getCargoCodigo())) {
            usuarios.addAll(getUsuariosPermitidosPelaEquipeDeVenda());
        }
        usuarios.addAll(repository.getUsuariosSuperiores(usuarioAutenticado.getUsuario().getId()));
        return usuarios;
    }

    public List<SelectResponse> findUsuariosOperadoresBackofficeByOrganizacaoEmpresa(Integer organizacaoId,
                                                                                     boolean buscarInativos) {
        return repository.findByOrganizacaoEmpresaIdAndCargo_CodigoIn(organizacaoId, CARGOS_OPERADORES_BACKOFFICE)
            .stream()
            .filter(usuario -> buscarInativos || usuario.isAtivo())
            .map(usuario -> SelectResponse.of(usuario.getId(), usuario.getNome()))
            .collect(toList());
    }

    public List<UsuarioResponse> findOperadoresBkoCentralizadoByFornecedor(Integer fornecedorId,
                                                                           boolean buscarInativos) {
        var cargosOperadoresBkoCentralizado = List.of(
            BACKOFFICE_OPERADOR_TRATAMENTO_VENDAS,
            BACKOFFICE_ANALISTA_TRATAMENTO_VENDAS);

        return repository.findByOrganizacaoEmpresaIdAndCargo_CodigoIn(fornecedorId, cargosOperadoresBkoCentralizado)
            .stream()
            .filter(usuario -> buscarInativos || usuario.isAtivo())
            .map(usuario -> new UsuarioResponse(usuario.getId(), usuario.getNome(), usuario.getEmail()))
            .collect(toList());
    }

    public List<Integer> getAllUsuariosDaHierarquiaD2dDoUserLogado() {
        var predicate = new UsuarioPredicate();
        predicate.filtraPermitidos(autenticacaoService.getUsuarioAutenticado(), this, true);

        return StreamSupport.stream(repository.findAll(predicate.build()).spliterator(), false)
            .map(Usuario::getId)
            .collect(toList());
    }

    public List<SelectResponse> buscarUsuariosDaHierarquiaDoUsuarioLogado(CodigoCargo codigoCargo) {
        var predicate = new UsuarioPredicate();

        predicate.filtraPermitidos(autenticacaoService.getUsuarioAutenticado(), this, true)
            .comCodigoCargo(codigoCargo)
            .comSituacoes(List.of(ESituacao.A));

        return StreamSupport.stream(
                repository.findAll(predicate.build(), new Sort(ASC, "nome")).spliterator(), false)
            .map(usuario -> SelectResponse.of(usuario.getId(), usuario.getNome()))
            .collect(toList());
    }

    public UrlLojaOnlineResponse getUrlLojaOnline(Integer id) {
        return repository.findById(id)
            .map(UrlLojaOnlineResponse::of)
            .orElseThrow(() -> EX_NAO_ENCONTRADO);
    }

    public List<Integer> obterIdsPorUsuarioCadastroId(Integer usuarioCadastroId) {
        return repository.obterIdsPorUsuarioCadastroId(usuarioCadastroId);
    }

    public List<UsuarioAgenteAutorizadoResponse> buscarBackOfficesAndSociosAaPorAaIds(List<Integer> agentesAutorizadoId) {
        return agentesAutorizadoId
            .stream()
            .map(aaId -> buscarBackOfficesESociosAaPorUsuariosId(buscarUsuariosIdPorAaId(aaId), aaId))
            .flatMap(List::stream)
            .collect(toList());
    }

    private List<UsuarioAgenteAutorizadoResponse> buscarBackOfficesESociosAaPorUsuariosId(
        List<Integer> usuariosId, Integer aaId) {
        var predicate = new UsuarioPredicate();
        predicate.comCodigosCargos(CARGOS_BACKOFFICE_AND_SOCIO_PRINCIPAL_AA);
        predicate.comIds(usuariosId);
        return StreamSupport.stream(repository.findAll(predicate.build()).spliterator(), false)
            .map(usuario -> preencherAaId(usuario, aaId))
            .map(UsuarioAgenteAutorizadoResponse::of)
            .collect(toList());
    }

    private List<Integer> buscarUsuariosIdPorAaId(Integer aaId) {
        return agenteAutorizadoService.getUsuariosByAaId(aaId, false)
            .stream()
            .map(UsuarioAgenteAutorizadoResponse::getId)
            .collect(toList());
    }

    private Usuario preencherAaId(Usuario usuario, Integer aaId) {
        usuario.setAgenteAutorizadoId(aaId);
        return usuario;
    }

    public List<VendedoresFeederResponse> buscarVendedoresFeeder(VendedoresFeederFiltros filtros) {
        return Optional.ofNullable(buscarUsuariosIdsPorAasIds(filtros.getAasIds(), true))
            .filter(usuariosIds -> !CollectionUtils.isEmpty(usuariosIds))
            .map(filtros::toPredicate)
            .map(this::buscarTodosPorPredicate)
            .map(usuarios -> usuarios
                .stream()
                .map(VendedoresFeederResponse::of)
                .sorted(Comparator.comparing(VendedoresFeederResponse::getNome))
                .collect(toList()))
            .orElse(List.of());
    }

    private List<Integer> buscarUsuariosIdsPorAasIds(List<Integer> aasIds, Boolean buscarInativos) {
        return agenteAutorizadoService.buscarTodosUsuariosDosAas(aasIds, buscarInativos)
            .stream()
            .map(UsuarioDtoVendas::getId)
            .distinct()
            .collect(toList());
    }

    private List<Usuario> buscarTodosPorPredicate(Predicate predicate) {
        return (List<Usuario>) repository.findAll(predicate);
    }

    public String obterNomeUsuarioPorId(Integer id) {
        return findById(id)
            .getNome();
    }

    public List<SelectResponse> getTiposCanalOptions() {
        return Arrays.stream(ETipoCanal.values())
            .map(tipoCanal -> SelectResponse.of(
                tipoCanal.name(),
                tipoCanal.getDescricao().toUpperCase()))
            .collect(toList());
    }

    public List<UsuarioSituacaoResponse> buscarUsuarioSituacaoPorIds(UsuarioSituacaoFiltro filtro) {
        return repository.buscarUsuarioSituacao(filtro.toPredicate().build());
    }

    public List<UsuarioResponse> findAllResponsePorIds(UsuarioPorIdFiltro filtro) {
        var usuarios = repository.findAll(filtro.toPredicate().build());
        return StreamSupport.stream(usuarios.spliterator(), false).map(UsuarioResponse::of).collect(toList());
    }

    public List<UsuarioNomeResponse> buscarUsuariosPorCanalECargo(ECanal canal, CodigoCargo cargo) {
        return repository.buscarUsuariosPorCanalECargo(canal, cargo);
    }

    public List<UsuarioResponse> buscarSubordinadosAtivosPorSuperioresIdsECodigosCargos(List<Integer> superioresIds,
                                                                                        Set<String> codigosCargos) {
        return repository.buscarSubordinadosAtivosPorSuperioresIdsECodigosCargos(superioresIds, codigosCargos)
            .stream()
            .sorted(Comparator.comparing(UsuarioResponse::getNome))
            .collect(toList());
    }

    public List<UsuarioCargoResponse> getSuperioresPorId(Integer usuarioId) {
        return repository.findSuperioresDoUsuarioId(usuarioId);
    }

    public List<SelectResponse> buscarTodosVendedoresReceptivos() {
        return repository.findAllVendedoresReceptivos().stream()
            .map(usuario -> {
                usuario.setNome(verificarSituacao(usuario.getNome(), usuario.getSituacao()));
                return SelectResponse.builder()
                    .label(usuario.getNome())
                    .value(usuario.getId())
                    .build();
            })
            .collect(toList());
    }

    public List<UsuarioVendedorReceptivoResponse> buscarVendedoresReceptivosPorId(List<Integer> ids) {
        return repository.findAllVendedoresReceptivosByIds(ids).stream()
            .map(usuario -> {
                usuario.setNome(verificarSituacao(usuario.getNome(), usuario.getSituacao()));
                return UsuarioVendedorReceptivoResponse.of(usuario);
            })
            .collect(toList());
    }

    private static String verificarSituacao(String nome, ESituacao situacao) {
        return ESituacao.I == situacao
            ? nome.concat(" (INATIVO)")
            : ESituacao.R == situacao
            ? nome.concat(" (REALOCADO)")
            : nome;
    }

    public List<SelectResponse> buscarUsuariosDaHierarquiaDoUsuarioLogadoPorFiltros(UsuarioFiltros filtros) {
        var predicate = filtros.toPredicate();
        predicate.filtraPermitidos(autenticacaoService.getUsuarioAutenticado(), this, true);

        return StreamSupport.stream(
                repository.findAll(predicate.build(), new Sort(ASC, "situacao", "nome")).spliterator(), false)
            .map(usuario -> SelectResponse.of(usuario.getId(), obterNomeComSituacao(usuario.getNome(), usuario.getSituacao())))
            .collect(toList());
    }

    private String obterNomeComSituacao(String usuarioNome, ESituacao situacao) {
        if (situacao == ESituacao.I) {
            return usuarioNome.concat(" (INATIVO)");
        }
        if (situacao == ESituacao.R) {
            return usuarioNome.concat(" (REALOCADO)");
        }

        return usuarioNome;
    }

    public UsuarioSubCanalNivelResponse findByUsuarioId(Integer usuarioId) {
        return UsuarioSubCanalNivelResponse.of(
            repository.findById(usuarioId)
                .orElseThrow(() -> new NotFoundException("O usuário " + usuarioId + " não foi encontrado.")));
    }

    public UsuarioSubCanalNivelResponse findByCpf(String cpf) {
        return repository.findTop1UsuarioByCpf(cpf)
            .stream()
            .map(UsuarioSubCanalNivelResponse::of)
            .findFirst()
            .orElse(new UsuarioSubCanalNivelResponse());
    }

    @Transactional(readOnly = true)
    public UsuarioSubCanalResponse findUsuarioD2dByCpf(String cpf) {
        var predicate = new UsuarioPredicate();
        predicate.comCpf(cpf)
            .comCanalD2d(true);
        return findUsuarioByPredicate(predicate);
    }

    private UsuarioSubCanalResponse findUsuarioByPredicate(UsuarioPredicate predicate) {
        return repository.findByPredicate(predicate.build())
            .stream()
            .map(UsuarioSubCanalResponse::of)
            .findFirst()
            .orElse(null);
    }

    public List<PermissaoEspecial> getPermissoesEspeciaisDoUsuario(Integer usuarioId, Integer usuarioCadastroId,
                                                                   List<Integer> funcionalidadesIds) {
        return funcionalidadesIds.stream()
            .filter(funcionalidadeId -> permissaoEspecialRepository.findOneByUsuarioIdAndFuncionalidadeIdAndDataBaixaIsNull(
                usuarioId, funcionalidadeId).isEmpty())
            .map(funcionalidadeId -> criarPermissaoEspecial(usuarioId, funcionalidadeId, usuarioCadastroId))
            .collect(toList());
    }

    private PermissaoEspecial criarPermissaoEspecial(Integer usuarioId, Integer funcionalidadeId, Integer usuarioCadastroId) {
        return PermissaoEspecial.builder()
            .funcionalidade(new Funcionalidade(funcionalidadeId))
            .usuarioCadastro(new Usuario(usuarioCadastroId))
            .usuario(new Usuario(usuarioId))
            .dataCadastro(LocalDateTime.now())
            .build();
    }

    public void salvarPermissoesEspeciais(List<PermissaoEspecial> permissoesEspeciais) {
        if (!isEmpty(permissoesEspeciais)) {
            permissaoEspecialRepository.save(permissoesEspeciais);
        }
    }

    public void removerPermissoesEspeciais(List<Integer> funcionalidadesIds, List<Integer> usuariosIds) {
        permissaoEspecialRepository.deletarPermissaoEspecialBy(funcionalidadesIds, usuariosIds);
    }

    public void validarVinculoDoUsuarioNaEquipeVendasComSubCanal(UsuarioDto usuarioDto) {
        if (validarCondicoesUsuarioCanalD2d(usuarioDto)) {
            validarSeUsuarioEstaEmEquipeVendasComOutroSubCanalId(usuarioDto,
                new ValidacaoException("Não foi possível editar o usuário, "
                    + "pois ele possui vínculo com equipe(s) com outro subcanal."));
        }
        if (validarCondicoesSeUsuarioTrocarDeCanal(usuarioDto)) {
            validarSeUsuarioEstaEmEquipeVendasComOutroSubCanalId(usuarioDto,
                new ValidacaoException("Não foi possível editar o usuário, "
                    + "pois ele possui vínculo com equipe(s) do Canal D2D PRÓPRIO."));
        }
    }

    private boolean validarCondicoesUsuarioCanalD2d(UsuarioDto usuarioDto) {
        return usuarioDto.hasIdAndCargoCodigo()
            && usuarioDto.hasSubCanaisId()
            && usuarioDto.hasCanalD2dProprio()
            && LISTA_CARGOS_EQUIPE_VENDAS_D2D.contains(usuarioDto.getCargoCodigo());
    }

    private boolean validarCondicoesSeUsuarioTrocarDeCanal(UsuarioDto usuarioDto) {
        return usuarioDto.hasIdAndCargoCodigo()
            && !usuarioDto.hasSubCanaisId()
            && !usuarioDto.hasCanalD2dProprio();
    }

    private void validarSeUsuarioEstaEmEquipeVendasComOutroSubCanalId(UsuarioDto usuarioDto,
                                                                      ValidacaoException validacaoException) {
        var subCanaisDaEquipeVendas = equipeVendaD2dService.getSubCanaisDaEquipeVendaD2dByUsuarioId(usuarioDto.getId());

        if (!subCanaisDaEquipeVendas.isEmpty()) {
            var subCanaisExistentes = subCanaisDaEquipeVendas.stream()
                .filter(subCanalDaEquipeVenda -> usuarioDto.getSubCanaisId().contains(subCanalDaEquipeVenda))
                .collect(toList());

            if (subCanaisExistentes.size() < subCanaisDaEquipeVendas.size()) {
                throw validacaoException;
            }
        }
    }

    public List<UsuarioResponse> getUsuariosOperacaoCanalAa(CodigoNivel codigoNivel) {
        return repository.getUsuariosOperacaoCanalAa(codigoNivel).stream()
            .map(UsuarioResponse::of).collect(toList());
    }

    private void configurarDataReativacao(Usuario usuario, ESituacao situacaoAnterior) {
        if (usuario.getSituacao() == ESituacao.A && situacaoAnterior == ESituacao.I) {
            usuario.setDataReativacao(LocalDateTime.now());
        }
    }

    @Transactional
    public List<UsuarioResponse> findByEmails(List<String> emails, Boolean buscarAtivo) {
        if (Boolean.TRUE.equals(buscarAtivo)) {
            return repository.findByEmailsAndSituacao(
                    new UsuarioPredicate()
                        .comUsuariosEmail(emails)
                        .build(), ESituacao.A)
                .stream()
                .map(Usuario::forceLoadCanais)
                .map(UsuarioResponse::of)
                .collect(toList());
        }

        return repository.findByEmails(
                new UsuarioPredicate()
                    .comUsuariosEmail(emails)
                    .build()
            )
            .stream()
            .map(Usuario::forceLoadCanais)
            .map(UsuarioResponse::of)
            .collect(toList());
    }

    @Transactional
    public List<UsuarioResponse> findByCpfs(List<String> cpfs, Boolean buscarAtivo) {
        if (Boolean.TRUE.equals(buscarAtivo)) {
            return repository.findByCpfsAndSituacao(
                    new UsuarioPredicate()
                        .comUsuariosCpfs(cpfs)
                        .build(), ESituacao.A)
                .stream()
                .map(Usuario::forceLoadCanais)
                .map(UsuarioResponse::of)
                .collect(toList());
        }

        return repository.findByCpfs(
                new UsuarioPredicate()
                    .comUsuariosCpfs(cpfs)
                    .build()
            )
            .stream()
            .map(Usuario::forceLoadCanais)
            .map(UsuarioResponse::of)
            .collect(toList());
    }

    @Transactional
    public void atualizarPermissaoEquipeTecnica(PermissaoEquipeTecnicaDto dto) {
        var sociosIds = Stream.of(dto.getUsuarioProprietarioId(), dto.getSociosSecundariosIds())
            .flatMap(id -> id instanceof List ? ((List<?>) id).stream() : Stream.of(id))
            .filter(Objects::nonNull)
            .map(Integer.class::cast)
            .collect(toList());

        if (dto.hasEquipeTecnica()) {
            permissaoEspecialService.save(criarPermissaoEspecialEquipeTecnica(sociosIds, dto.getUsuarioCadastroId()));
        } else {
            permissaoEspecialService.deletarPermissoesEspeciaisBy(FUNCIONALIDADES_EQUIPE_TECNICA, sociosIds);
        }
        gerarHistoricoPermissaoEquipeTecnica(sociosIds, dto.hasEquipeTecnica());
    }

    @Transactional
    public void gerarHistoricoTentativasLoginSenhaIncorreta(String email) {
        var usuarioOptional = this.repository.findUsuarioHistoricoTentativaLoginSenhaIncorretaHoje(email);
        if (usuarioOptional.isPresent()) {
            var usuario = usuarioOptional.get();
            var registro = UsuarioSenhaIncorretaHistorico.builder()
                .usuario(usuario)
                .dataTentativa(LocalDate.now())
                .build();
            usuario.adicionar(registro);

            this.repository.save(usuario);

            var tentativas = usuario.getHistoricosSenhaIncorretas().stream()
                .filter(item -> item.getDataTentativa().equals(LocalDate.now()))
                .count();

            if (tentativas >= NUMERO_MAXIMO_TENTATIVAS_LOGIN_SENHA_INCORRETA) {
                var usuarioInativacaoDto = UsuarioInativacaoDto.builder()
                    .idUsuario(usuario.getId())
                    .idUsuarioInativacao(1)
                    .observacao(ECodigoObservacao.ITL.getObservacao())
                    .codigoMotivoInativacao(CodigoMotivoInativacao.TENTATIVAS_LOGIN_SENHA_INCORRETA)
                    .build();

                this.inativar(usuarioInativacaoDto);
                var colaboradorInativacao = ColaboradorInativacaoPolRequest.of(usuario.getEmail(), ECodigoObservacao.ITL);
                this.inativarColaboradorMqSender.sendSuccess(colaboradorInativacao);
            }
        }
    }

    public List<SelectResponse> getCanaisPermitidosParaOrganizacao() {
        return ECanal.getCanaisAtivos()
            .stream()
            .filter(canal -> canal == ECanal.INTERNET)
            .map(canal -> SelectResponse.of(canal.name(), canal.getDescricao()))
            .collect(toList());
    }

    private List<PermissaoEspecial> criarPermissoesEspeciaisPor(Integer usuarioId, Integer usuarioCadastroId,
                                                                List<Integer> funcionalidades) {
        return funcionalidades.stream()
            .filter(funcionalidadeId -> !permissaoEspecialService.hasPermissaoEspecialAtiva(usuarioId, funcionalidadeId))
            .map(funcionalidadeId -> PermissaoEspecial.of(usuarioId, funcionalidadeId, usuarioCadastroId))
            .collect(toList());
    }

    private List<PermissaoEspecial> criarPermissaoEspecialEquipeTecnica(List<Integer> sociosIds, Integer usuarioCadastroId) {
        return sociosIds.stream()
            .flatMap(socioId -> criarPermissoesEspeciaisPor(socioId, usuarioCadastroId, FUNCIONALIDADES_EQUIPE_TECNICA).stream())
            .collect(toList());
    }

    private void criarPermissaoEspecialEquipeTecnica(UsuarioDto usuarioDto, UsuarioMqRequest usuarioMqRequest) {
        if (usuarioMqRequest.isNovoCadastroSocioSecundario() && usuarioMqRequest.isEquipeTecnica()) {
            permissaoEspecialService.save(
                criarPermissoesEspeciaisPor(
                    usuarioDto.getId(),
                    usuarioDto.getUsuarioCadastroId(),
                    FUNCIONALIDADES_EQUIPE_TECNICA
                )
            );
            gerarHistoricoPermissaoEquipeTecnica(List.of(usuarioDto.getId()), true);
        }
    }

    private void gerarHistoricoPermissaoEquipeTecnica(List<Integer> usuariosIds, boolean hasEquipeTecnica) {
        if (!CollectionUtils.isEmpty(usuariosIds)) {
            usuarioHistoricoService.save(
                UsuarioHistorico.gerarHistorico(
                    usuariosIds,
                    hasEquipeTecnica
                        ? "Agente Autorizado com permissão de Equipe Técnica."
                        : "Agente Autorizado sem permissão de Equipe Técnica.",
                    ESituacao.A)
            );
        }
    }

    @Transactional
    public void moverAvatarMinio() {
        autenticacaoService.getUsuarioAutenticado().validarAdministrador();
        log.info("Inicia alteração caminho do banco das fotos do MinIO.");
        var fotoCaminho = repository.findByFotoDiretorioIsNotNull();
        alteraColunaFotoDiretorio(fotoCaminho);
        log.info("Finaliza alteração caminho do banco das fotos do MinIO.");
    }

    private void alteraColunaFotoDiretorio(List<Usuario> fotoCaminho) {
        if (!isEmpty(fotoCaminho)) {
            log.info("Inicia update coluna FotoDiretório");
            fotoCaminho.forEach(user -> {
                var fileName = gerarNovoCaminhoBanco(user.getFotoDiretorio());
                log.info("Update usuario Id: {}", user.getId());
                repository.updateFotoDiretorio(fileName, user.getId());
            });
        }
    }

    private String gerarNovoCaminhoBanco(String path) {
        if (isNotBlank(path) && path.length() > 1) {
            var pattern = Pattern.compile(".*/([^/]+)$");
            var matcher = pattern.matcher(path);
            if (matcher.find()) {
                return path.replace(path, concat(urlDir, matcher.group(1)));
            }
        }
        return path;
    }

    public void filtrarPermitidosCanalInternet(UsuarioAutenticado usuario, UsuarioPredicate predicate) {
        if (usuario.isGerenteInternetOperacao()) {
            predicate.comCanal(ECanal.INTERNET);
        } else if (usuario.isSupervisorInternetOperacao() || usuario.isCoordenadorInternetOperacao()
            || usuario.isBackofficeInternetOperacao()) {
            predicate.comOrganizacaoEmpresaId(usuario.getOrganizacaoId());
            predicate.comIds(
                Stream.concat(
                    getIdsUsuariosHierarquiaPorCargos(validarCargosPermitidosParaFiltroCanalInternet(usuario)).stream(),
                    Stream.of(usuario.getUsuario().getId())
                ).collect(toList())
            );
        } else if (usuario.isVendedorInternetOperacao()) {
            predicate.comIds(List.of(usuario.getId()));
        }
    }

    private Set<CodigoCargo> validarCargosPermitidosParaFiltroCanalInternet(UsuarioAutenticado usuario) {
        if (usuario.isBackofficeInternetOperacao()) {
            return Set.of(INTERNET_VENDEDOR);
        } else if (usuario.isCoordenadorInternetOperacao()) {
            return CARGOS_PERMITIDOS_INTERNET_COODERNADOR;
        }
        return CARGOS_PERMITIDOS_INTERNET_SUPERVISOR;
    }

    private List<Integer> getIdsUsuariosHierarquiaPorCargos(Set<CodigoCargo> codigoCargos) {
        return repository.getIdsUsuariosHierarquiaPorCargos(codigoCargos);
    }

    private void processarUsuarioParaSocialHub(Usuario usuario) {
        this.gerenciarPermissaoSocialHub(usuario);
        this.enviarDadosAtualizacaoParaSocialHub(usuario);
    }

    private void gerenciarPermissaoSocialHub(Usuario usuario) {
        var email = usuario.getEmail();
        var dominio = extractDominio(email);

        if (this.isDominioPermitido(dominio)) {
            this.adicionarPermissaoSocialHub(usuario);
        }
    }

    private boolean isDominioPermitido(String dominio) {
        return dominiosPermitidos.contains(dominio);
    }

    private void enviarDadosAtualizacaoParaSocialHub(Usuario usuario) {
        if (permissaoEspecialService.hasPermissaoEspecialAtiva(usuario.getId(), ROLE_SHB)) {
            this.enviarParaFilaDeAtualizarUsuariosSocialHub(usuario);
        }
    }

    private void enviarParaFilaDeAtualizarUsuariosSocialHub(Usuario usuario) {
        var regionais = regionalService.getRegionalIds(usuario.getId());
        usuarioMqSender.enviarDadosUsuarioParaSocialHub(UsuarioSocialHubRequestMq.from(usuario, regionais));
    }

    private String extractDominio(String email) {
        int atIndex = email.lastIndexOf("@");
        if (atIndex != -1) {
            return email.substring(atIndex + 1);
        }
        return "";
    }

    private void adicionarPermissaoSocialHub(Usuario usuario) {
        permissaoEspecialService.save(criarPermissaoEspecialSocialHub(usuario.getId(),
            usuario.getUsuarioCadastro().getId()));
    }

    private List<PermissaoEspecial> criarPermissaoEspecialSocialHub(Integer usuarioId, Integer usuarioCadastroId) {
        return criarPermissoesEspeciaisPor(usuarioId, usuarioCadastroId, FUNCIONALIDADES_SOCIAL_HUB);
    }
}
