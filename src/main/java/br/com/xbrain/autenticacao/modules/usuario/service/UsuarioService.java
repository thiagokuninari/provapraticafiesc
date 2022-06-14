package br.com.xbrain.autenticacao.modules.usuario.service;

import br.com.xbrain.autenticacao.modules.agenteautorizadonovo.dto.UsuarioDtoVendas;
import br.com.xbrain.autenticacao.modules.agenteautorizadonovo.service.AgenteAutorizadoNovoService;
import br.com.xbrain.autenticacao.modules.autenticacao.dto.UsuarioAutenticado;
import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.comum.dto.EmpresaResponse;
import br.com.xbrain.autenticacao.modules.comum.dto.PageRequest;
import br.com.xbrain.autenticacao.modules.comum.dto.SelectResponse;
import br.com.xbrain.autenticacao.modules.comum.enums.*;
import br.com.xbrain.autenticacao.modules.comum.exception.NotFoundException;
import br.com.xbrain.autenticacao.modules.comum.exception.PermissaoException;
import br.com.xbrain.autenticacao.modules.comum.exception.ValidacaoException;
import br.com.xbrain.autenticacao.modules.comum.model.Empresa;
import br.com.xbrain.autenticacao.modules.comum.model.Organizacao;
import br.com.xbrain.autenticacao.modules.comum.model.UnidadeNegocio;
import br.com.xbrain.autenticacao.modules.comum.repository.EmpresaRepository;
import br.com.xbrain.autenticacao.modules.comum.repository.UnidadeNegocioRepository;
import br.com.xbrain.autenticacao.modules.comum.service.FileService;
import br.com.xbrain.autenticacao.modules.comum.util.ListUtil;
import br.com.xbrain.autenticacao.modules.comum.util.StringUtil;
import br.com.xbrain.autenticacao.modules.equipevenda.dto.EquipeVendaUsuarioResponse;
import br.com.xbrain.autenticacao.modules.equipevenda.service.EquipeVendaD2dService;
import br.com.xbrain.autenticacao.modules.equipevenda.service.EquipeVendasUsuarioService;
import br.com.xbrain.autenticacao.modules.feeder.dto.VendedoresFeederFiltros;
import br.com.xbrain.autenticacao.modules.feeder.dto.VendedoresFeederResponse;
import br.com.xbrain.autenticacao.modules.feeder.service.FeederService;
import br.com.xbrain.autenticacao.modules.feeder.service.FeederUtil;
import br.com.xbrain.autenticacao.modules.mailing.service.MailingService;
import br.com.xbrain.autenticacao.modules.notificacao.service.NotificacaoService;
import br.com.xbrain.autenticacao.modules.parceirosonline.dto.AgenteAutorizadoResponse;
import br.com.xbrain.autenticacao.modules.parceirosonline.dto.UsuarioAgenteAutorizadoResponse;
import br.com.xbrain.autenticacao.modules.parceirosonline.service.AgenteAutorizadoClient;
import br.com.xbrain.autenticacao.modules.parceirosonline.service.AgenteAutorizadoService;
import br.com.xbrain.autenticacao.modules.permissao.dto.FuncionalidadeResponse;
import br.com.xbrain.autenticacao.modules.permissao.filtros.FuncionalidadePredicate;
import br.com.xbrain.autenticacao.modules.permissao.model.CargoDepartamentoFuncionalidade;
import br.com.xbrain.autenticacao.modules.permissao.model.PermissaoEspecial;
import br.com.xbrain.autenticacao.modules.permissao.repository.CargoDepartamentoFuncionalidadeRepository;
import br.com.xbrain.autenticacao.modules.permissao.repository.PermissaoEspecialRepository;
import br.com.xbrain.autenticacao.modules.permissao.service.FuncionalidadeService;
import br.com.xbrain.autenticacao.modules.site.model.Site;
import br.com.xbrain.autenticacao.modules.site.service.SiteService;
import br.com.xbrain.autenticacao.modules.usuario.dto.*;
import br.com.xbrain.autenticacao.modules.usuario.enums.*;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static br.com.xbrain.autenticacao.modules.comum.enums.RelatorioNome.USUARIOS_CSV;
import static br.com.xbrain.autenticacao.modules.comum.util.Constantes.QTD_MAX_IN_NO_ORACLE;
import static br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo.*;
import static br.com.xbrain.autenticacao.modules.usuario.enums.CodigoFuncionalidade.AUT_VISUALIZAR_GERAL;
import static br.com.xbrain.autenticacao.modules.usuario.enums.CodigoMotivoInativacao.DEMISSAO;
import static br.com.xbrain.autenticacao.modules.usuario.enums.EObservacaoHistorico.*;
import static br.com.xbrain.xbrainutils.NumberUtils.getOnlyNumbers;
import static com.google.common.collect.Lists.partition;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static org.springframework.data.domain.Sort.Direction.ASC;
import static org.springframework.util.CollectionUtils.isEmpty;
import static org.springframework.util.ObjectUtils.isEmpty;

@Service
@Slf4j
@SuppressWarnings({"PMD.TooManyStaticImports", "VariableDeclarationUsageDistance"})
public class UsuarioService {

    private static final int POSICAO_ZERO = 0;
    private static final int MAX_CARACTERES_SENHA = 6;
    private static final ValidacaoException EX_NAO_ENCONTRADO = new ValidacaoException("Usuário não encontrado.");
    private static final ESituacao ATIVO = ESituacao.A;
    private static final ESituacao INATIVO = ESituacao.I;
    private static final String MSG_ERRO_AO_ATIVAR_USUARIO =
        "Erro ao ativar, o agente autorizado está inativo ou descredenciado.";
    private static final String MSG_ERRO_AO_REMOVER_CANAL_ATIVO_LOCAL =
        "Não é possível remover o canal Ativo Local, pois o usuário possui vínculo com o(s) Site(s): %s.";
    private static final String MSG_ERRO_AO_REMOVER_CANAL_AGENTE_AUTORIZADO =
        "Não é possível remover o canal Agente Autorizado, pois o usuário possui vínculo com o(s) AA(s): %s.";
    private static final String MSG_ERRO_AO_ALTERAR_CARGO_SITE =
        "Não é possível alterar o cargo, pois o usuário possui vínculo com o(s) Site(s): %s.";
    private static final String EX_USUARIO_POSSUI_OUTRA_EQUIPE =
        "Usuário já está cadastrado em outra equipe";
    private static final List<CodigoCargo> cargosOperadoresBackoffice
        = List.of(BACKOFFICE_OPERADOR_TRATAMENTO, BACKOFFICE_ANALISTA_TRATAMENTO);
    private static final ValidacaoException USUARIO_NAO_POSSUI_LOGIN_NET_SALES_EX = new ValidacaoException(
        "Usuário não possui login NetSales válido."
    );

    private static final ValidacaoException COLABORADOR_NAO_ATIVO = new ValidacaoException(
        "O colaborador não se encontra mais com a situação Ativo. Favor verificar seu cadastro."
    );
    public static final String OPERACAO = "Operação";
    public static final String AGENTE_AUTORIZADO = "Agente Autorizado";
    public static final String MSG_ERRO_ATIVAR_USUARIO_INATIVADO_POR_MUITAS_SIMULACOES = "Não foi possível ativar usuário. "
        + "O usuário foi inativado por realizar muitas simulações, por favor entre em contato com algum usuário XBrain "
        + "para que ele possa reativar o usuário.";
    private static ValidacaoException EMAIL_CADASTRADO_EXCEPTION = new ValidacaoException("Email já cadastrado.");
    private static ValidacaoException EMAIL_ATUAL_INCORRETO_EXCEPTION
        = new ValidacaoException("Email atual está incorreto.");
    private static ValidacaoException SENHA_ATUAL_INCORRETA_EXCEPTION
        = new ValidacaoException("Senha atual está incorreta.");
    private static ValidacaoException USUARIO_NOT_FOUND_EXCEPTION
        = new ValidacaoException("O usuário não foi encontrado.");
    private static List<CodigoCargo> CARGOS_PARA_INTEGRACAO_D2D = List.of(SUPERVISOR_OPERACAO, ASSISTENTE_OPERACAO,
        VENDEDOR_OPERACAO);
    private static ValidacaoException USUARIO_ATIVO_LOCAL_POSSUI_AGENDAMENTOS_EX = new ValidacaoException(
        "Não foi possível inativar usuario Ativo Local com agendamentos"
    );
    private static ValidacaoException MSG_ERRO_USUARIO_NAO_POSSUI_SUBCANAIS = new ValidacaoException(
        "Usuário não possui sub-canais, deve ser cadastrado no mínimo um."
    );
    private static ValidacaoException MSG_ERRO_USUARIO_CARGO_SOMENTE_UM_SUBCANAL = new ValidacaoException(
        "Não é permitido cadastrar mais de um sub-canal para este cargo."
    );
    private static ValidacaoException MSG_ERRO_USUARIO_SEM_SUBCANAL_DA_HIERARQUIA = new ValidacaoException(
        "Usuário não possui sub-canal em comum com usuários da hierarquia."
    );
    private static List<CodigoCargo> CARGOS_PARA_INTEGRACAO_ATIVO_LOCAL = List.of(
        SUPERVISOR_OPERACAO, ASSISTENTE_OPERACAO, OPERACAO_TELEVENDAS);
    private static final List<CodigoCargo> LISTA_CARGOS_VALIDACAO_PROMOCAO = List.of(
        SUPERVISOR_OPERACAO, VENDEDOR_OPERACAO, ASSISTENTE_OPERACAO, OPERACAO_EXECUTIVO_VENDAS, COORDENADOR_OPERACAO);
    private static final List<CodigoCargo> LISTA_CARGOS_LIDERES_EQUIPE = List.of(
        SUPERVISOR_OPERACAO, COORDENADOR_OPERACAO);
    private static List<CodigoCargo> CARGOS_COM_MAIS_SUBCANAIS = List.of(
        COORDENADOR_OPERACAO, DIRETOR_OPERACAO, GERENTE_OPERACAO);

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
    private AgenteAutorizadoNovoService agenteAutorizadoNovoService;
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
    private UsuarioClientService usuarioClientService;
    @Autowired
    private EquipeVendasUsuarioService equipeVendasUsuarioService;

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
            .map(CidadeResponse::of)
            .collect(toList());
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
        Usuario usuario = findComplete(idUsuario);
        return usuario.getEmpresas().stream().map(EmpresaResponse::convertFrom).collect(toList());
    }

    public Page<Usuario> getAll(PageRequest pageRequest, UsuarioFiltros filtros) {
        UsuarioPredicate predicate = filtrarUsuariosPermitidos(filtros);
        Page<Usuario> pages = repository.findAll(predicate.build(), pageRequest);
        if (!isEmpty(pages.getContent())) {
            popularUsuarios(pages.getContent());
        }
        return pages;
    }

    public List<Usuario> getAllByPredicate(UsuarioFiltros filtros) {
        var predicate = filtrarUsuariosPermitidos(filtros);
        return (List<Usuario>) repository.findAll(predicate.build());
    }

    private void popularUsuarios(List<Usuario> usuarios) {
        usuarios.forEach(c -> {
            c.setEmpresas(repository.findEmpresasById(c.getId()));
            c.setUnidadesNegocios(repository.findUnidadesNegociosById(c.getId()));
            c.setCpf(repository.findCpfById(c.getId()));
        });
    }

    private void obterUsuariosAa(String cnpjAa, UsuarioPredicate predicate, Boolean buscarInativos) {
        List<Integer> lista = agenteAutorizadoNovoService.getIdUsuariosPorAa(cnpjAa, buscarInativos);
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
        usuario.removerCaracteresDoCpf();
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

    public List<Integer> getIdDosUsuariosSubordinadosDoPol(UsuarioAutenticado usuario) {
        if (!usuario.haveCanalAgenteAutorizado() || usuario.hasPermissao(AUT_VISUALIZAR_GERAL)) {
            return List.of();
        }

        return Stream.of(
                agenteAutorizadoNovoService.getIdsUsuariosSubordinados(false),
                repository.getUsuariosSubordinados(usuario.getId())
            ).flatMap(Collection::stream)
            .distinct()
            .collect(toList());
    }

    public List<Integer> getIdDosUsuariosSubordinadosDoPol(UsuarioAutenticado usuario, PublicoAlvoComunicadoFiltros filtros) {
        if (!usuario.haveCanalAgenteAutorizado() || usuario.hasPermissao(AUT_VISUALIZAR_GERAL)) {
            return List.of();
        }
        var usuariosPol = isEmpty(filtros.getUsuariosFiltradosPorCidadePol())
            ? agenteAutorizadoService.getIdsUsuariosPermitidosDoUsuario(filtros)
            : filtros.getUsuariosFiltradosPorCidadePol();
        var usuariosSubordinados = Sets.newHashSet(repository.getUsuariosSubordinados(usuario.getId()));
        usuariosSubordinados.addAll(usuariosPol);

        return List.copyOf(usuariosSubordinados);
    }

    public List<Integer> getIdDosUsuariosParceiros(PublicoAlvoComunicadoFiltros filtros) {
        return agenteAutorizadoService.getIdsUsuariosPermitidosDoUsuario(filtros);
    }

    public List<UsuarioSubordinadoDto> getSubordinadosDoUsuario(Integer usuarioId) {
        return repository.getUsuariosCompletoSubordinados(usuarioId);
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
    public UsuarioDto save(Usuario request, MultipartFile foto) {
        if (!isEmpty(foto)) {
            fileService.uploadFotoUsuario(request, foto);
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
            repository.saveAndFlush(usuario);
            configurarCadastro(usuario);
            gerarHistoricoAlteracaoCadastro(usuario, situacaoAnterior);
            enviarEmailDadosAcesso(usuario, enviarEmail);

            return UsuarioDto.of(usuario);
        } catch (PersistenceException ex) {
            log.error("Erro de persistência ao salvar o Usuario.", ex.getMessage());
            throw new ValidacaoException("Erro ao cadastrar usuário.");
        } catch (Exception ex) {
            log.error("Erro ao salvar Usuário.", ex);
            throw ex;
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

    private void validarVinculoComAa(Usuario usuarioOriginal, Usuario usuarioAlterado) {
        if (usuarioOriginal.isNivelOperacao() && usuarioOriginal.isCanalAgenteAutorizadoRemovido(usuarioAlterado.getCanais())) {
            var aas = agenteAutorizadoNovoService.findAgenteAutorizadoByUsuarioId(usuarioOriginal.getId());
            if (!isEmpty(aas)) {
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

    private boolean verificarSubCanalValidacao(Usuario usuario) {
        return repository.getSubCanaisByUsuarioIds(usuario.getHierarquiasId()).stream()
            .map(SubCanal::getId)
            .filter(usuario.getSubCanaisId()::contains)
            .collect(Collectors.toSet())
            .isEmpty();
    }

    public Set<SubCanal> verificarSubCanalValidacao(Integer usuarioId) {
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

        if (!isEmpty(sitesVinculados)) {
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
        tratarCadastroUsuario(usuario);
        var enviarEmail = usuario.isNovoCadastro();
        repository.save(usuario);

        enviarEmailDadosAcesso(usuario, enviarEmail);
        return usuario;
    }

    private void configurarCadastro(Usuario usuario) {
        tratarHierarquiaUsuario(usuario, usuario.getHierarquiasId());
        tratarCidadesUsuario(usuario);
    }

    private void tratarUsuarioBackoffice(Usuario usuario) {
        usuario.setOrganizacao(Optional.ofNullable(usuario.getOrganizacao())
            .orElse(new Organizacao(autenticacaoService.getUsuarioAutenticado().getOrganizacaoId())));
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
        Usuario usuarioSuperior = repository.findById(idUsuarioSuperior)
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
        return AGENTE_AUTORIZADO_SOCIO.equals(cargoCodigo);
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
        validarCanalD2dProprioESubCanais(usuario);
        usuario.verificarPermissaoCargoSobreCanais();
        usuario.removerCaracteresDoCpf();
        usuario.tratarEmails();
    }

    private void validarCanalD2dProprioESubCanais(Usuario usuario) {
        if (usuario.hasCanal(ECanal.D2D_PROPRIO)) {
            Optional.ofNullable(cargoService.findById(usuario.getCargoId()))
                .ifPresent(cargo -> {
                    validarSubCanaisUsuario(usuario, cargo);
                    validarSubCanaisHierarquia(usuario, cargo);
                });
        }
    }

    private void validarSubCanaisUsuario(Usuario usuario, Cargo cargo) {
        if (!isEmpty(usuario.getSubCanais())) {
            if (usuario.getSubCanais().size() > 1
                && !CARGOS_COM_MAIS_SUBCANAIS.contains(cargo.getCodigo())) {
                throw MSG_ERRO_USUARIO_CARGO_SOMENTE_UM_SUBCANAL;
            }
        } else {
            throw MSG_ERRO_USUARIO_NAO_POSSUI_SUBCANAIS;
        }
    }

    private void validarSubCanaisHierarquia(Usuario usuario, Cargo cargo) {
        var isCargoDiretor = cargo.getCodigo().equals(DIRETOR_OPERACAO);
        var hasHierarquia = !isEmpty(usuario.getHierarquiasId());

        if (!isCargoDiretor && hasHierarquia) {
            var naoPossuiSubCanalHierarquia = verificarSubCanalValidacao(usuario);            
            if (naoPossuiSubCanalHierarquia) {
                throw MSG_ERRO_USUARIO_SEM_SUBCANAL_DA_HIERARQUIA;
            }
        }
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
        List<Usuario> valores = usuarios.stream().distinct().collect(toList());
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
        try {
            UsuarioDto usuarioDto = UsuarioDto.parse(usuarioMqRequest);
            configurarUsuario(usuarioMqRequest, usuarioDto);
            usuarioDto = save(UsuarioDto.convertFrom(usuarioDto));

            if (usuarioMqRequest.isNovoCadastroSocioPrincipal()) {
                enviarParaFilaDeSocioPrincipalSalvo(usuarioDto);
            } else {
                enviarParaFilaDeUsuariosSalvos(usuarioDto);
            }
            feederService.adicionarPermissaoFeederParaUsuarioNovo(usuarioDto, usuarioMqRequest);
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
                save(UsuarioDto.convertFrom(usuarioDto));
                removerPermissoesFeeder(usuarioMqRequest);
                feederService.adicionarPermissaoFeederParaUsuarioNovo(usuarioDto, usuarioMqRequest);
                enviarParaFilaDeUsuariosSalvos(usuarioDto);
            } else {
                saveUsuarioAlteracaoCpf(UsuarioDto.convertFrom(usuarioDto));
            }
        } catch (Exception ex) {
            usuarioMqRequest.setException(ex.getMessage());
            enviarParaFilaDeErroAtualizacaoUsuarios(usuarioMqRequest);
            log.error("erro ao atualizar usuário da fila.", ex);
        }
    }

    private void removerPermissoesFeeder(UsuarioMqRequest usuarioMqRequest) {
        if (usuarioMqRequest.getAgenteAutorizadoFeeder() == ETipoFeeder.RESIDENCIAL
            || usuarioMqRequest.getAgenteAutorizadoFeeder() == ETipoFeeder.EMPRESARIAL) {
            feederService.removerPermissoesEspeciais(List.of(usuarioMqRequest.getId()));
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

    public void remanejarUsuario(UsuarioMqRequest usuarioMqRequest) {
        try {
            var usuarioDto = UsuarioDto.parse(usuarioMqRequest);
            configurarUsuario(usuarioMqRequest, usuarioDto);
            duplicarUsuarioERemanejarAntigo(UsuarioDto.convertFrom(usuarioDto), usuarioMqRequest);
        } catch (Exception ex) {
            enviarParaFilaDeErroUsuariosRemanejadosAut(UsuarioRemanejamentoRequest.of(usuarioMqRequest));
            log.error("Erro ao processar usuário da fila: ", ex);
        }
    }

    @Transactional
    private void duplicarUsuarioERemanejarAntigo(Usuario usuario, UsuarioMqRequest usuarioMqRequest) {
        usuario.removerCaracteresDoCpf();
        salvarUsuarioRemanejado(usuario);
        var usuarioNovo = criaNovoUsuarioAPartirDoRemanejado(usuario);
        gerarHistoricoAtivoAposRemanejamento(usuario);
        repository.save(usuarioNovo);
        enviarParaFilaDeUsuariosRemanejadosAut(UsuarioRemanejamentoRequest.of(usuarioNovo, usuarioMqRequest));
        feederService.adicionarPermissaoFeederParaUsuarioNovo(UsuarioDto.of(usuarioNovo), usuarioMqRequest);
    }

    private void salvarUsuarioRemanejado(Usuario usuarioRemanejado) {
        usuarioRemanejado.setAlterarSenha(Eboolean.F);
        usuarioRemanejado.setSituacao(ESituacao.R);
        usuarioRemanejado.setSenha(repository.findById(usuarioRemanejado.getId())
            .orElseThrow(() -> EX_NAO_ENCONTRADO).getSenha());
        usuarioRemanejado.adicionarHistorico(UsuarioHistorico.gerarHistorico(usuarioRemanejado, REMANEJAMENTO));
        repository.save(usuarioRemanejado);
    }

    private Usuario criaNovoUsuarioAPartirDoRemanejado(Usuario usuario) {
        validarUsuarioComCpfDiferenteRemanejado(usuario);
        usuario.setDataCadastro(LocalDateTime.now());
        usuario.setSituacao(ESituacao.A);
        usuario.setId(null);
        return usuario;
    }

    public void validarUsuarioComCpfDiferenteRemanejado(Usuario usuario) {
        if (repository.existsByCpfAndSituacaoNot(usuario.getCpf(), ESituacao.R)) {
            throw new ValidacaoException("Não é possível remanejar o usuário pois já existe outro usuário "
                + "para este CPF.");
        }
    }

    private void gerarHistoricoAtivoAposRemanejamento(Usuario usuario) {
        usuario.getHistoricos().clear();
        usuario.adicionarHistorico(UsuarioHistorico.gerarHistorico(usuario, REMANEJAMENTO));
    }

    public boolean isAlteracaoCpf(Usuario usuario) {
        Usuario usuarioCpfAntigo = repository.findById(usuario.getId())
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
            Usuario usuario = repository.findOne(usuarioMqRequest.getId());
            usuario = usuario.parse(usuarioMqRequest);
            usuario.setEmpresas(empresaRepository.findByCodigoIn(usuarioMqRequest.getEmpresa()));
            usuario.setUnidadesNegocios(unidadeNegocioRepository.findByCodigoIn(usuarioMqRequest.getUnidadesNegocio()));
            usuario.setCargo(cargoRepository.findByCodigo(usuarioMqRequest.getCargo()));
            usuario.setDepartamento(departamentoRepository.findByCodigo(usuarioMqRequest.getDepartamento()));
            usuario.setAlterarSenha(Eboolean.V);
            usuario.removerCaracteresDoCpf();

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

    private void enviarParaFilaDeUsuariosSalvos(UsuarioDto usuarioDto) {
        usuarioMqSender.sendSuccess(usuarioDto);
    }

    private void enviarParaFilaDeSocioPrincipalSalvo(UsuarioDto usuarioDto) {
        usuarioMqSender.sendSuccessSocioPrincipal(usuarioDto);
    }

    private void enviarParaFilaDeAtualizarUsuariosPol(UsuarioDto usuarioDto) {
        atualizarUsuarioMqSender.sendSuccess(usuarioDto);
    }

    private void enviarParaFilaDeUsuariosRemanejadosAut(UsuarioRemanejamentoRequest request) {
        atualizarUsuarioMqSender.sendUsuarioRemanejadoAut(request);
    }

    private void enviarParaFilaDeErroUsuariosRemanejadosAut(UsuarioRemanejamentoRequest request) {
        atualizarUsuarioMqSender.sendErrorUsuarioRemanejadoAut(request);
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
            .map(UnidadeNegocio::getId).collect(toList()));
    }

    private void configurarEmpresas(UsuarioMqRequest usuarioMqRequest, UsuarioDto usuarioDto) {
        List<Empresa> empresas = empresaRepository.findByCodigoIn(usuarioMqRequest.getEmpresa());
        usuarioDto.setEmpresasId(empresas.stream().map(Empresa::getId).collect(toList()));
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
        alterarSituacaoSocioPrincipal(usuario);
    }

    public void ativar(Integer id) {
        repository.findById(id)
            .ifPresent(user -> {
                usuarioClientService.alterarSituacao(id);
                user.setSituacao(ATIVO);
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

        if (isEmpty(usuario.getCpf())) {
            throw new ValidacaoException("O usuário não pode ser ativado por não possuir CPF.");
        } else if (usuario.isSocioPrincipal() && !encontrouAgenteAutorizadoBySocioEmail(usuario.getEmail())) {
            throw new ValidacaoException(MSG_ERRO_AO_ATIVAR_USUARIO
                + " Ou email do sócio está divergente do que está inserido no agente autorizado.");
        } else if (!usuario.isSocioPrincipal() && usuario.isAgenteAutorizado()
            && !encontrouAgenteAutorizadoByUsuarioId(usuario.getId())) {
            throw new ValidacaoException(MSG_ERRO_AO_ATIVAR_USUARIO);
        } else if (!isUsuarioAdmin && usuarioInativoPorMuitasSimulacoes) {
            throw new ValidacaoException(MSG_ERRO_ATIVAR_USUARIO_INATIVADO_POR_MUITAS_SIMULACOES);
        }

        repository.save(usuario);
    }

    private boolean encontrouAgenteAutorizadoByUsuarioId(Integer usuarioId) {
        return agenteAutorizadoNovoService.existeAaAtivoByUsuarioId(usuarioId);
    }

    private boolean encontrouAgenteAutorizadoBySocioEmail(String usuarioEmail) {
        return agenteAutorizadoNovoService.existeAaAtivoBySocioEmail(usuarioEmail);
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

    public void inativar(Integer id) {
        repository.findById(id)
            .ifPresent(user -> {
                usuarioClientService.alterarSituacao(id);
                user.setSituacao(INATIVO);
                repository.save(user);
            });
    }

    @Transactional
    public void inativar(UsuarioInativacaoDto usuarioInativacao) {
        Usuario usuario = findComplete(usuarioInativacao.getIdUsuario());
        validarUsuarioAtivoLocalEPossuiAgendamento(usuario);
        usuario.setSituacao(ESituacao.I);
        usuario.adicionarHistorico(gerarDadosDeHistoricoDeInativacao(usuarioInativacao, usuario));
        inativarUsuarioNaEquipeVendas(usuario, carregarMotivoInativacao(usuarioInativacao));
        removerHierarquiaDoUsuarioEquipe(usuario, carregarMotivoInativacao(usuarioInativacao));
        autenticacaoService.logout(usuario.getId());
        repository.save(usuario);
        alterarSituacaoSocioPrincipal(usuario);
    }

    private void alterarSituacaoSocioPrincipal(Usuario usuario) {
        if (usuario.isSocioPrincipal() && usuario.isAgenteAutorizado()) {
            usuarioClientService.alterarSituacao(usuario.getId());
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
        UsuarioPredicate usuarioPredicate = new UsuarioPredicate();
        usuarioPredicate.filtraPermitidos(autenticacaoService.getUsuarioAutenticado(), this, true);
        usuarioPredicate.comNivel(Collections.singletonList(nivelId));
        return repository.findAllUsuariosHierarquia(usuarioPredicate.build());
    }

    public List<Usuario> getUsuariosCargoSuperior(Integer cargoId, List<Integer> cidadesId) {
        return repository.getUsuariosFilter(
            new UsuarioPredicate()
                .filtraPermitidos(autenticacaoService.getUsuarioAutenticado(), this, true)
                .comCargos(cargoService.findById(cargoId).getCargosSuperioresId())
                .comCidade(cidadesId)
                .build());
    }

    public List<UsuarioHierarquiaResponse> getUsuariosCargoSuperiorByCanal(Integer cargoId, List<Integer> cidadesId,
                                                                           Set<ECanal> canais) {
        var usuariosCargoSuperior = repository.getUsuariosFilter(
            new UsuarioPredicate()
                .filtraPermitidos(autenticacaoService.getUsuarioAutenticado(), this, false)
                .comCargos(cargoService.findById(cargoId).getCargosSuperioresId())
                .comCidade(cidadesId)
                .comCanais(canais)
                .build());
        return UsuarioHierarquiaResponse.convertTo(usuariosCargoSuperior);
    }

    public List<UsuarioHierarquiaResponse> getUsuariosCargoSuperiorByCanalAndSubCanal(Integer cargoId, List<Integer> cidadesId,
                                                                                    Set<ECanal> canais, Set<Integer> subCanais) {
        var usuariosCargoSuperior = repository.getUsuariosFilter(
            new UsuarioPredicate()
                .filtraPermitidos(autenticacaoService.getUsuarioAutenticado(), this, false)
                .comCargos(cargoService.findById(cargoId).getCargosSuperioresId())
                .comCidade(cidadesId)
                .comCanais(canais)
                .comSubCanais(subCanais)
                .build());
        return UsuarioHierarquiaResponse.convertTo(usuariosCargoSuperior);
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
            .collect(toList());
    }

    private void montarPredicateComCidade(UsuarioPredicate predicate, UsuarioFiltrosDto filtro) {

        List<List<Integer>> listaPartes = ListUtil.divideListaEmListasMenores(filtro.getCidadesIds(), QTD_MAX_IN_NO_ORACLE);

        listaPartes.forEach(lista -> predicate.comCidade(lista));
    }

    public List<UsuarioResponse> getUsuariosByIds(List<Integer> idsUsuarios) {
        List<Usuario> usuarios = repository.findBySituacaoAndIdIn(ESituacao.A, idsUsuarios);
        return usuarios.stream()
            .map(UsuarioResponse::of)
            .collect(toList());
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
        List<UsuarioHierarquia> usuariosHierarquia = repository.getUsuarioSuperiores(idUsuario);
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
        enviarParaFilaDeUsuariosSalvos(UsuarioDto.of(usuario));
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
            .collect(toList());
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
            .map(UsuarioResponse::of).collect(toList());
    }

    public List<Integer> getUsuariosIdsByNivel(CodigoNivel nivel) {
        return repository.getUsuariosIdsByNivel(nivel);
    }

    public List<UsuarioCidadeDto> getCidadeByUsuario(Integer usuarioId) {
        Usuario usuario = findComplete(usuarioId);
        return usuario.getCidades().stream()
            .map(c -> UsuarioCidadeDto.of(c.getCidade()))
            .collect(toList());
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
        Optional<UsuarioResponse> usuario = findByEmailAa(email, null);
        usuario.ifPresent(u -> {
            Optional<Usuario> usuarioCompleto = repository.findById(u.getId());
            usuarioCompleto.ifPresent(user -> {
                user.setSituacao(ATIVO);
                repository.save(user);
            });
        });
    }

    public void inativarSocioPrincipal(String email) {
        Optional<UsuarioResponse> usuario = findByEmailAa(email, null);
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
                .orElseThrow(() -> EX_NAO_ENCONTRADO);
            usuario.setSituacao(INATIVO);
            usuario.removerCaracteresDoCpf();
            repository.save(usuario);
        });
    }

    public List<UsuarioHierarquiaResponse> getVendedoresOperacaoDaHierarquia(Integer usuarioId) {
        return repository.getSubordinadosPorCargo(usuarioId,
                Set.of(VENDEDOR_OPERACAO.name(), OPERACAO_TELEVENDAS.name(), OPERACAO_EXECUTIVO_VENDAS.name()))
            .stream()
            .map(this::criarUsuarioHierarquiaVendedoresResponse)
            .collect(toList());
    }

    public List<UsuarioHierarquiaResponse> getSupervisoresOperacaoDaHierarquia(Integer usuarioId) {
        return repository.getSubordinadosPorCargo(usuarioId, Set.of(CodigoCargo.SUPERVISOR_OPERACAO.name()))
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
        List<Integer> usuarioIds = usuarioCsvResponses.parallelStream()
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
        UsuarioRequest usuarioRequest = UsuarioRequest.of(usuarioCsvResponses.parallelStream().filter(
            usuarioCsvResponse -> AGENTE_AUTORIZADO.equals(usuarioCsvResponse.getNivel())
        ).map(UsuarioCsvResponse::getId).collect(toList()));

        if (!usuarioRequest.getUsuarioIds().isEmpty()) {
            var agenteAutorizadosUsuarioDtos = agenteAutorizadoNovoService
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

        return IntStream.concat(
                equipeVendaD2dService
                    .getUsuariosPermitidos(cargos)
                    .stream()
                    .mapToInt(EquipeVendaUsuarioResponse::getUsuarioId),
                IntStream.of(autenticacaoService.getUsuarioId()))
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
        if (!isEmpty(usuarioFiltros.getEquipesVendasIds())) {
            var usuarios = equipeVendaD2dService.getUsuariosDaEquipe(usuarioFiltros.getEquipesVendasIds());
            usuarioFiltros.adicionarUsuariosId(usuarios);
        }
    }

    private void adicionarFiltroAgenteAutorizado(PublicoAlvoComunicadoFiltros usuarioFiltros) {

        if (!isEmpty(usuarioFiltros.getAgentesAutorizadosIds())) {
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
            return agenteAutorizadoNovoService.getUsuariosByAaId(aaId, true)
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
        return repository.findAllIds(usuarioFiltros);
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
        return repository.findAllNomesIds(usuarioFiltros);
    }

    public List<UsuarioCidadeDto> findCidadesDoUsuarioLogado() {

        return usuarioCidadeRepository.findCidadesDtoByUsuarioId(autenticacaoService.getUsuarioAutenticadoId()
            .orElseThrow(PermissaoException::new));
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

            if (!isEmpty(usuariosPol)) {
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

    public List<SelectResponse> findUsuariosOperadoresBackofficeByOrganizacao(Integer organizacaoId,
                                                                              boolean buscarInativos) {

        return repository.findByOrganizacaoIdAndCargo_CodigoIn(organizacaoId, cargosOperadoresBackoffice)
            .stream()
            .filter(usuario -> buscarInativos || usuario.isAtivo())
            .map(usuario -> SelectResponse.of(usuario.getId(), usuario.getNome()))
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
        predicate.comCodigosCargos(FeederUtil.CARGOS_BACKOFFICE_AND_SOCIO_PRINCIPAL_AA);
        predicate.comIds(usuariosId);
        return StreamSupport.stream(repository.findAll(predicate.build()).spliterator(), false)
            .map(usuario -> preencherAaId(usuario, aaId))
            .map(UsuarioAgenteAutorizadoResponse::of)
            .collect(toList());
    }

    private List<Integer> buscarUsuariosIdPorAaId(Integer aaId) {
        return agenteAutorizadoNovoService.getUsuariosByAaId(aaId, false)
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
            .filter(usuariosIds -> !isEmpty(usuariosIds))
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
        return agenteAutorizadoNovoService.buscarTodosUsuariosDosAas(aasIds, buscarInativos)
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
                tipoCanal.getDescricao().toUpperCase()
            )).collect(toList());
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
            }).collect(toList());
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
}
