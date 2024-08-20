package br.com.xbrain.autenticacao.modules.usuario.service;

import br.com.xbrain.autenticacao.modules.agenteautorizado.dto.UsuarioDtoVendas;
import br.com.xbrain.autenticacao.modules.agenteautorizado.service.AgenteAutorizadoService;
import br.com.xbrain.autenticacao.modules.agenteautorizado.service.PermissaoTecnicoIndicadorService;
import br.com.xbrain.autenticacao.modules.autenticacao.dto.UsuarioAutenticado;
import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.comum.dto.PageRequest;
import br.com.xbrain.autenticacao.modules.comum.dto.SelectResponse;
import br.com.xbrain.autenticacao.modules.comum.enums.*;
import br.com.xbrain.autenticacao.modules.comum.exception.IntegracaoException;
import br.com.xbrain.autenticacao.modules.comum.exception.NotFoundException;
import br.com.xbrain.autenticacao.modules.comum.exception.PermissaoException;
import br.com.xbrain.autenticacao.modules.comum.exception.ValidacaoException;
import br.com.xbrain.autenticacao.modules.comum.model.Empresa;
import br.com.xbrain.autenticacao.modules.comum.model.SubCluster;
import br.com.xbrain.autenticacao.modules.comum.model.Uf;
import br.com.xbrain.autenticacao.modules.comum.model.UnidadeNegocio;
import br.com.xbrain.autenticacao.modules.comum.repository.EmpresaRepository;
import br.com.xbrain.autenticacao.modules.comum.repository.UnidadeNegocioRepository;
import br.com.xbrain.autenticacao.modules.comum.service.FileService;
import br.com.xbrain.autenticacao.modules.comum.service.MinioFileService;
import br.com.xbrain.autenticacao.modules.comum.service.RegionalService;
import br.com.xbrain.autenticacao.modules.equipevenda.service.EquipeVendaD2dService;
import br.com.xbrain.autenticacao.modules.equipevenda.service.EquipeVendasUsuarioService;
import br.com.xbrain.autenticacao.modules.feeder.service.FeederService;
import br.com.xbrain.autenticacao.modules.feeder.service.FeederUtil;
import br.com.xbrain.autenticacao.modules.gestaocolaboradorespol.service.ColaboradorTecnicoService;
import br.com.xbrain.autenticacao.modules.gestaocolaboradorespol.service.ColaboradorVendasService;
import br.com.xbrain.autenticacao.modules.mailing.service.MailingService;
import br.com.xbrain.autenticacao.modules.notificacao.service.NotificacaoService;
import br.com.xbrain.autenticacao.modules.organizacaoempresa.enums.ESituacaoOrganizacaoEmpresa;
import br.com.xbrain.autenticacao.modules.organizacaoempresa.model.OrganizacaoEmpresa;
import br.com.xbrain.autenticacao.modules.organizacaoempresa.service.OrganizacaoEmpresaService;
import br.com.xbrain.autenticacao.modules.parceirosonline.dto.UsuarioAgenteAutorizadoResponse;
import br.com.xbrain.autenticacao.modules.parceirosonline.service.ParceirosOnlineService;
import br.com.xbrain.autenticacao.modules.permissao.model.Funcionalidade;
import br.com.xbrain.autenticacao.modules.permissao.model.PermissaoEspecial;
import br.com.xbrain.autenticacao.modules.permissao.repository.PermissaoEspecialRepository;
import br.com.xbrain.autenticacao.modules.permissao.service.PermissaoEspecialService;
import br.com.xbrain.autenticacao.modules.site.service.SiteService;
import br.com.xbrain.autenticacao.modules.suportevendas.service.SuporteVendasService;
import br.com.xbrain.autenticacao.modules.usuario.dto.*;
import br.com.xbrain.autenticacao.modules.usuario.enums.*;
import br.com.xbrain.autenticacao.modules.usuario.event.UsuarioSubCanalEvent;
import br.com.xbrain.autenticacao.modules.usuario.exceptions.ValidacaoSubCanalException;
import br.com.xbrain.autenticacao.modules.usuario.helpers.UsuarioAutenticadoHelper;
import br.com.xbrain.autenticacao.modules.usuario.helpers.UsuarioHelper;
import br.com.xbrain.autenticacao.modules.usuario.model.*;
import br.com.xbrain.autenticacao.modules.usuario.predicate.UsuarioPredicate;
import br.com.xbrain.autenticacao.modules.usuario.rabbitmq.AtualizarUsuarioMqSender;
import br.com.xbrain.autenticacao.modules.usuario.rabbitmq.InativarColaboradorMqSender;
import br.com.xbrain.autenticacao.modules.usuario.rabbitmq.UsuarioCadastroMqSender;
import br.com.xbrain.autenticacao.modules.usuario.rabbitmq.UsuarioEquipeVendaMqSender;
import br.com.xbrain.autenticacao.modules.usuario.repository.*;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import helpers.TestBuilders;
import io.minio.MinioClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.test.util.ReflectionTestUtils;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static br.com.xbrain.autenticacao.modules.comum.enums.EErrors.ERRO_BUSCAR_TODOS_AAS_DO_USUARIO;
import static br.com.xbrain.autenticacao.modules.comum.enums.EErrors.ERRO_VALIDAR_EMAIL_CADASTRADO;
import static br.com.xbrain.autenticacao.modules.comum.enums.ESituacao.A;
import static br.com.xbrain.autenticacao.modules.comum.util.Constantes.ROLE_SHB;
import static br.com.xbrain.autenticacao.modules.feeder.helper.VendedoresFeederFiltrosHelper.umVendedoresFeederFiltros;
import static br.com.xbrain.autenticacao.modules.organizacaoempresa.helper.OrganizacaoEmpresaHelper.umaOrganizacaoEmpresa;
import static br.com.xbrain.autenticacao.modules.site.helper.SiteHelper.umSite;
import static br.com.xbrain.autenticacao.modules.usuario.controller.UsuarioGerenciaControllerTest.*;
import static br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo.*;
import static br.com.xbrain.autenticacao.modules.usuario.enums.CodigoFuncionalidade.AUT_VISUALIZAR_GERAL;
import static br.com.xbrain.autenticacao.modules.usuario.enums.CodigoFuncionalidade.CTR_VISUALIZAR_CARTEIRA_HIERARQUIA;
import static br.com.xbrain.autenticacao.modules.usuario.enums.CodigoNivel.*;
import static br.com.xbrain.autenticacao.modules.usuario.enums.ETipoCanal.*;
import static br.com.xbrain.autenticacao.modules.usuario.helpers.CargoHelper.*;
import static br.com.xbrain.autenticacao.modules.usuario.helpers.CidadeHelper.listaDistritosDeLondrinaECampinaDaLagoaECidadeCampinaDaLagoa;
import static br.com.xbrain.autenticacao.modules.usuario.helpers.CidadeHelper.umMapApenasDistritosComCidadePai;
import static br.com.xbrain.autenticacao.modules.usuario.helpers.DepartamentoHelper.umDepartamentoAa;
import static br.com.xbrain.autenticacao.modules.usuario.helpers.NivelHelper.umNivelAa;
import static br.com.xbrain.autenticacao.modules.usuario.helpers.PermissaoEquipeTecnicaHelper.permissaoEquipeTecnicaDto;
import static br.com.xbrain.autenticacao.modules.usuario.helpers.SubCanalHelper.*;
import static br.com.xbrain.autenticacao.modules.usuario.helpers.UsuarioAutenticadoHelper.*;
import static br.com.xbrain.autenticacao.modules.usuario.helpers.UsuarioCidadeHelper.listaUsuarioCidadeDeDistritosDeLondrina;
import static br.com.xbrain.autenticacao.modules.usuario.helpers.UsuarioCidadeHelper.listaUsuarioCidadesDoParana;
import static br.com.xbrain.autenticacao.modules.usuario.helpers.UsuarioHelper.*;
import static br.com.xbrain.autenticacao.modules.usuario.helpers.UsuarioPredicateHelper.umVendedoresFeederPredicateComSocioPrincipalESituacaoAtiva;
import static br.com.xbrain.autenticacao.modules.usuario.helpers.UsuarioPredicateHelper.umVendedoresFeederPredicateComSocioPrincipalETodasSituacaoes;
import static br.com.xbrain.autenticacao.modules.usuario.helpers.UsuarioResponseHelper.umUsuarioResponse;
import static br.com.xbrain.autenticacao.modules.usuario.helpers.UsuarioServiceHelper.*;
import static br.com.xbrain.autenticacao.modules.usuarioacesso.helper.UsuarioAcessoHelper.umUsuarioXBrain;
import static helpers.TestBuilders.umUsuarioAutenticadoAdmin;
import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.groups.Tuple.tuple;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.*;
import static org.springframework.data.domain.Sort.Direction.ASC;

@RunWith(MockitoJUnitRunner.class)
public class UsuarioServiceTest {

    private static final String INATIVADO_POR_REALIZAR_MUITAS_SIMULACOES = "INATIVADO POR REALIZAR MUITAS SIMULAÇÕES";
    private static final String MSG_ERRO_ATIVAR_USUARIO_INATIVADO_POR_MUITAS_SIMULACOES =
        "Usuário inativo por excesso de consultas, não foi possível reativá-lo. Para reativação deste usuário é"
            + " necessário a abertura de um incidente no CA, anexando a liberação do diretor comercial.";
    private static final String MSG_ERRO_ATIVAR_USUARIO_COM_AA_ESTRUTURA_NAO_LOJA_FUTURO =
        "O usuário não pode ser ativado pois a estrutura do agente autorizado não é Loja do Futuro.";

    @InjectMocks
    private UsuarioService service;
    @Mock
    private UsuarioRepository repository;
    @Mock
    private UsuarioHistoricoService usuarioHistoricoService;
    @Mock
    private NotificacaoService notificacaoService;
    @Mock
    private EmpresaRepository empresaRepository;
    @Mock
    private UnidadeNegocioRepository unidadeNegocioRepository;
    @Mock
    private CargoRepository cargoRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private AutenticacaoService autenticacaoService;
    @Mock
    private EntityManager entityManager;
    @Mock
    private UsuarioHierarquiaRepository usuarioHierarquiaRepository;
    @Mock
    private UsuarioCidadeRepository usuarioCidadeRepository;
    @Mock
    private EquipeVendaD2dService equipeVendaD2dService;
    @Mock
    private ParceirosOnlineService parceirosOnlineService;
    @Mock
    private AgenteAutorizadoService agenteAutorizadoService;
    @Mock
    private SiteService siteService;
    @Mock
    private MailingService mailingService;
    @Mock
    private MotivoInativacaoService motivoInativacaoService;
    @Mock
    private UsuarioFeriasService usuarioFeriasService;
    @Mock
    private UsuarioAfastamentoService usuarioAfastamentoService;
    @Mock
    private UsuarioEquipeVendaMqSender equipeVendaMqSender;
    @Mock
    private RegionalService regionalService;
    @Mock
    private EquipeVendasUsuarioService equipeVendasUsuarioService;
    @Mock
    private ColaboradorVendasService colaboradorVendasService;
    @Mock
    private ColaboradorTecnicoService colaboradorTecnicoService;
    @Mock
    private CargoService cargoService;
    @Mock
    private SubCanalService subCanalService;
    @Mock
    private FeederService feederService;
    @Mock
    private InativarColaboradorMqSender inativarColaboradorMqSender;
    @Mock
    private UsuarioCadastroMqSender usuarioMqSender;
    @Mock
    private DepartamentoRepository departamentoRepository;
    @Mock
    private NivelRepository nivelRepository;
    @Captor
    private ArgumentCaptor<Usuario> usuarioCaptor;
    @Captor
    private ArgumentCaptor<List<PermissaoEspecial>> permissaoEspecialCaptor;
    @Captor
    private ArgumentCaptor<UsuarioSocialHubRequestMq> socialHubRequestCaptor;
    @Captor
    private ArgumentCaptor<List<UsuarioHistorico>> usuarioHistoricoCaptor;
    @Mock
    private PermissaoEspecialService permissaoEspecialService;
    @Mock
    private TokenStore tokenStore;
    @Mock
    private MinioFileService minioFileService;
    @Mock
    private SuporteVendasService suporteVendasService;
    @Mock
    private MinioClient minioClient;
    @Mock
    private FileService fileService;
    @Mock
    private PermissaoTecnicoIndicadorService permissaoTecnicoIndicadorService;
    @Mock
    private CidadeService cidadeService;
    @Mock
    private PermissaoEspecialRepository permissaoEspecialRepository;
    @Mock
    private AtualizarUsuarioMqSender atualizarUsuarioMqSender;
    @Mock
    private OrganizacaoEmpresaService organizacaoEmpresaService;
    @Mock
    private SubNivelService subNivelService;

    private static UsuarioAgenteAutorizadoResponse umUsuarioAgenteAutorizadoResponse(Integer id, Integer aaId) {
        return UsuarioAgenteAutorizadoResponse.builder()
            .id(id)
            .nome("FULANO DE TESTE")
            .email("TESTE@TESTE.COM")
            .agenteAutorizadoId(aaId)
            .build();
    }

    @Test
    public void findCidadesByUsuario_deveLancarValidacaoException_quandoNaoEncontrarUsuario() {
        assertThatExceptionOfType(ValidacaoException.class)
            .isThrownBy(() -> service.findCidadesByUsuario(101214))
            .withMessage("Usuário não encontrado.");

        verify(repository).findComCidade(101214);
    }

    @Test
    public void findCidadesByUsuario_deveRetornarListaVazia_quandoUsuarioNaoPossuirCidadesAtreladas() {
        when(repository.findComCidade(101214)).thenReturn(Optional.of(List.of()));

        assertThat(service.findCidadesByUsuario(101214)).isEmpty();

        verify(repository).findComCidade(101214);
    }

    @Test
    public void findCidadesByUsuario_deveRetornarListaCidadeResponse_quandoUsuarioPossuirCidadesAtreladas() {
        when(repository.findComCidade(101214))
            .thenReturn(Optional.of(listaDistritosDeLondrinaECampinaDaLagoaECidadeCampinaDaLagoa()));
        when(cidadeService.getCidadesDistritos(Eboolean.V))
            .thenReturn(umMapApenasDistritosComCidadePai());

        assertThat(service.findCidadesByUsuario(101214))
            .extracting("id", "nome", "uf.id", "uf.nome", "regional.id", "regional.nome", "fkCidade", "cidadePai")
            .hasSize(11)
            .containsExactly(
                tuple(30650, "BELA VISTA DO PIQUIRI", 1, "PARANA", 1027, "RPS", 3272, "CAMPINA DA LAGOA"),
                tuple(3272, "CAMPINA DA LAGOA", 1, "PARANA", 1027, "RPS", null, null),
                tuple(30858, "GUARAVERA", 1, "PARANA", 1027, "RPS", 5578, "LONDRINA"),
                tuple(30574, "HERVEIRA", 1, "PARANA", 1027, "RPS", 3272, "CAMPINA DA LAGOA"),
                tuple(30813, "IRERE", 1, "PARANA", 1027, "RPS", 5578, "LONDRINA"),
                tuple(30732, "LERROVILLE", 1, "PARANA", 1027, "RPS", 5578, "LONDRINA"),
                tuple(30757, "MARAVILHA", 1, "PARANA", 1027, "RPS", 5578, "LONDRINA"),
                tuple(30676, "PAIQUERE", 1, "PARANA", 1027, "RPS", 5578, "LONDRINA"),
                tuple(30780, "SALLES DE OLIVEIRA", 1, "PARANA", 1027, "RPS", 3272, "CAMPINA DA LAGOA"),
                tuple(30848, "SAO LUIZ", 1, "PARANA", 1027, "RPS", 5578, "LONDRINA"),
                tuple(30910, "WARTA", 1, "PARANA", 1027, "RPS", 5578, "LONDRINA")
            );

        verify(repository).findComCidade(101214);
    }

    @Test
    public void buscarNaoRealocadoByCpf_deveRetornarUsuarioNaoRealocado_quandoCpfForValido() {
        when(repository
            .findTop1UsuarioByCpfAndSituacaoNotOrderByDataCadastroDesc(eq("09723864592"), eq(ESituacao.R)))
            .thenReturn(Optional.of(umUsuario()));

        assertThat(service.buscarNaoRealocadoByCpf("097.238.645-92"))
            .extracting(UsuarioResponse::getId, UsuarioResponse::getCpf)
            .containsExactly(1, "097.238.645-92");
    }

    @Test
    public void buscarNaoRealocadoByCpf_deveRetornarNull_quandoCpfNaoExistir() {
        when(repository
            .findTop1UsuarioByCpfAndSituacaoNotOrderByDataCadastroDesc(anyString(), eq(ESituacao.R)))
            .thenReturn(Optional.empty());
        assertThat(service.buscarNaoRealocadoByCpf("86271666418"))
            .extracting(UsuarioResponse::getId, UsuarioResponse::getNome, UsuarioResponse::getCpf,
                UsuarioResponse::getEmail, UsuarioResponse::getSituacao, UsuarioResponse::getCodigoCargo)
            .containsExactly(null, null, null, null, null, null);
    }

    @Test
    public void ativar_deveAlterarSituacaoUsuario_quandoOMesmoForSocioPrincialEAa() {
        doReturn(umUsuarioAutenticadoAdmin(1))
            .when(autenticacaoService)
            .getUsuarioAutenticado();

        doReturn(Optional.of("INATIVADO POR REALIZAR MUITAS SIMULAÇÕES"))
            .when(usuarioHistoricoService)
            .findMotivoInativacaoByUsuarioId(1);

        doReturn(Optional.of(umUsuarioSocioPrincipalEAa()))
            .when(repository)
            .findComplete(anyInt());

        doReturn(true)
            .when(agenteAutorizadoService)
            .existeAaAtivoBySocioEmail(anyString());

        service.ativar(umUsuarioAtivacaoDto());
        verify(agenteAutorizadoService).ativarUsuario(1);
    }

    @Test
    public void ativar_NaoDeveAlterarSituacaoUsuario_quandoOMesmoForSocioPrincialEAa() {
        doReturn(umUsuarioAutenticadoAdmin(1))
            .when(autenticacaoService)
            .getUsuarioAutenticado();

        doReturn(Optional.of(outroUsuarioCompleto()))
            .when(repository)
            .findComplete(anyInt());

        service.ativar(umUsuarioAtivacaoDto());
        verify(agenteAutorizadoService, never()).ativarUsuario(2);
    }

    @Test
    public void ativarUsuarioOperadorTelevendas_deveAlterarSituacaoUsuario_quandoUsuarioAtivacaoForUsuarioXBrainOuMSO() {
        doReturn(Optional.of(umUsuarioCompleto(ESituacao.I, OPERACAO_TELEVENDAS, 120,
            OPERACAO, CodigoDepartamento.COMERCIAL, ECanal.ATIVO_PROPRIO)))
            .when(repository)
            .findComplete(anyInt());

        doReturn(umUsuarioAutenticadoAdmin(1))
            .when(autenticacaoService)
            .getUsuarioAutenticado();

        doReturn(Optional.of("INATIVADO POR REALIZAR MUITAS SIMULAÇÕES"))
            .when(usuarioHistoricoService)
            .findMotivoInativacaoByUsuarioId(anyInt());

        service.ativar(umUsuarioAtivacaoDto());

        assertThat(umUsuarioCompleto(OPERACAO_TELEVENDAS, 120,
            OPERACAO, CodigoDepartamento.COMERCIAL, ECanal.ATIVO_PROPRIO))
            .extracting("situacao", "cargo.id", "cargo.codigo")
            .containsExactly(A, 120, OPERACAO_TELEVENDAS);
    }

    @Test
    public void ativar_deveLancarException_quandoUsuarioAtivacaoNaoForUsuarioXBrainOuMSO() {
        doReturn(Optional.of(umUsuarioCompleto(ESituacao.I, OPERACAO_TELEVENDAS, 120,
            OPERACAO, CodigoDepartamento.COMERCIAL, ECanal.ATIVO_PROPRIO)))
            .when(repository)
            .findComplete(anyInt());

        doReturn(TestBuilders.umUsuarioAutenticado(1, VENDEDOR_OPERACAO, "OPERACAO"))
            .when(autenticacaoService)
            .getUsuarioAutenticado();

        doReturn(Optional.of(INATIVADO_POR_REALIZAR_MUITAS_SIMULACOES))
            .when(usuarioHistoricoService)
            .findMotivoInativacaoByUsuarioId(anyInt());

        assertThatExceptionOfType(ValidacaoException.class)
            .isThrownBy(() -> service.ativar(umUsuarioAtivacaoDto()))
            .withMessage(MSG_ERRO_ATIVAR_USUARIO_INATIVADO_POR_MUITAS_SIMULACOES);
    }

    @Test
    public void ativar_deveLancarException_quandoAaDoUsuarioLojaFuturoNaoPossuiEstruturaLojaFuturo() {
        doReturn(Optional.of(umUsuarioCompleto(ESituacao.I, CLIENTE_LOJA_FUTURO, 120,
            OPERACAO, CodigoDepartamento.COMERCIAL, ECanal.AGENTE_AUTORIZADO)))
            .when(repository)
            .findComplete(anyInt());

        doReturn(TestBuilders.umUsuarioAutenticado(1, CLIENTE_LOJA_FUTURO, "OPERACAO"))
            .when(autenticacaoService)
            .getUsuarioAutenticado();

        doReturn("AGENTE_AUTORIZADO")
            .when(agenteAutorizadoService)
            .getEstruturaByUsuarioId(anyInt());

        assertThatExceptionOfType(ValidacaoException.class)
            .isThrownBy(() -> service.ativar(umUsuarioAtivacaoDto()))
            .withMessage(MSG_ERRO_ATIVAR_USUARIO_COM_AA_ESTRUTURA_NAO_LOJA_FUTURO);
    }

    @Test
    public void ativar_deveAtivarVendedorD2d_quandoSubcanalSupervisorIgual() {
        doReturn(Optional.of(umUsuarioD2D(PAP_PREMIUM)))
            .when(repository)
            .findComplete(anyInt());

        doReturn(umUsuarioAutenticadoAdmin(1))
            .when(autenticacaoService)
            .getUsuarioAutenticado();

        service.ativar(umUsuarioAtivacaoDtoD2d());
    }

    @Test
    public void ativar_deveRetornarExcecao_quandoSubcanalSupervisorDiferente() {
        doReturn(Optional.of(umUsuarioD2D(PAP)))
            .when(repository)
            .findComplete(anyInt());

        doReturn(umUsuarioAutenticadoAdmin(1))
            .when(autenticacaoService)
            .getUsuarioAutenticado();

        assertThatExceptionOfType(ValidacaoException.class)
            .isThrownBy(() -> service.ativar(umUsuarioAtivacaoDtoD2d()))
            .withMessage("Favor deve-se por este usuario no mesmo subcanal"
                + " do superior ou trocar a hierarquia para um superior do mesmo subcanal");
    }

    @Test
    public void ativar_deveAtivarVendedorD2d_quandoSubcanalCoordenadorIgual() {
        doReturn(Optional.of(umUsuarioD2DComCoordenador(PAP_PREMIUM)))
            .when(repository)
            .findComplete(anyInt());

        doReturn(umUsuarioAutenticadoAdmin(1))
            .when(autenticacaoService)
            .getUsuarioAutenticado();

        service.ativar(umUsuarioAtivacaoDtoD2d());
    }

    @Test
    public void ativar_deveAlterarUsuario_quandoOrganizacaoEmpresaAtiva() {
        var usuario = umUsuarioCompleto(ESituacao.I, BACKOFFICE_GERENTE, 120,
            BACKOFFICE, CodigoDepartamento.COMERCIAL, ECanal.D2D_PROPRIO);
        usuario.setOrganizacaoEmpresa(OrganizacaoEmpresa.builder().id(100).situacao(ESituacaoOrganizacaoEmpresa.A).build());

        doReturn(Optional.of(usuario))
            .when(repository)
            .findComplete(anyInt());

        doReturn(umUsuarioAutenticadoAdmin(1))
            .when(autenticacaoService)
            .getUsuarioAutenticado();

        service.ativar(umUsuarioAtivacaoDto());
    }

    @Test
    public void ativar_deveRetornarExcecao_quandoSubcanalCoordenadorDiferente() {
        doReturn(Optional.of(umUsuarioD2DComCoordenador(PAP)))
            .when(repository)
            .findComplete(anyInt());

        doReturn(umUsuarioAutenticadoAdmin(1))
            .when(autenticacaoService)
            .getUsuarioAutenticado();

        assertThatExceptionOfType(ValidacaoException.class)
            .isThrownBy(() -> service.ativar(umUsuarioAtivacaoDtoD2d()))
            .withMessage("Favor deve-se por este usuario no mesmo subcanal"
                + " do superior ou trocar a hierarquia para um superior do mesmo subcanal");
    }

    @Test
    public void ativar_deveRetornarExcecao_quandoSuperiorNaoEncontrado() {
        doReturn(Optional.of(umUsuarioD2DSemCoordenador(PAP)))
            .when(repository)
            .findComplete(anyInt());

        doReturn(umUsuarioAutenticadoAdmin(1))
            .when(autenticacaoService)
            .getUsuarioAutenticado();

        assertThatExceptionOfType(ValidacaoException.class)
            .isThrownBy(() -> service.ativar(umUsuarioAtivacaoDtoD2d()))
            .withMessage("Superior do Vendedor não foi encontrado");
    }

    @Test
    public void ativar_deveRetornarExcecao_quandoSubcanalNaoEncontrado() {
        doReturn(Optional.of(umUsuarioD2DSemSubcanal(null)))
            .when(repository)
            .findComplete(anyInt());

        doReturn(umUsuarioAutenticadoAdmin(1))
            .when(autenticacaoService)
            .getUsuarioAutenticado();

        assertThatExceptionOfType(ValidacaoException.class)
            .isThrownBy(() -> service.ativar(umUsuarioAtivacaoDtoD2d()))
            .withMessage("Não foi encontrado o subcanal do " + umUsuarioD2DSemSubcanal(null).getCargo().getCodigo());
    }

    @Test
    public void ativar_deveRetornarExcecao_quandoOrganizacaoEmpresaInativa() {
        var usuario = umUsuarioCompleto(ESituacao.I, BACKOFFICE_GERENTE, 120,
            BACKOFFICE, CodigoDepartamento.COMERCIAL, ECanal.D2D_PROPRIO);
        usuario.setOrganizacaoEmpresa(OrganizacaoEmpresa.builder().id(100).situacao(ESituacaoOrganizacaoEmpresa.I).build());

        doReturn(Optional.of(usuario))
            .when(repository)
            .findComplete(anyInt());

        doReturn(umUsuarioAutenticadoAdmin(1))
            .when(autenticacaoService)
            .getUsuarioAutenticado();

        assertThatExceptionOfType(ValidacaoException.class)
            .isThrownBy(() -> service.ativar(umUsuarioAtivacaoDto()))
            .withMessage("O usuário não pode ser ativado pois o fornecedor está inativo.");
    }

    @Test
    public void inativar_deveRetornarExcecao_quandoUsuarioAtivoLocalEPossuiAgendamento() {
        when(mailingService.countQuantidadeAgendamentosProprietariosDoUsuario(eq(umUsuario().getId()), eq(ECanal.ATIVO_PROPRIO)))
            .thenReturn(Long.valueOf(1));
        when(repository.findComplete(eq(1))).thenReturn(Optional.of(umUsuarioCompleto()));

        assertThatExceptionOfType(ValidacaoException.class)
            .isThrownBy(() -> service.inativar(umUsuarioInativoDto()))
            .withMessage("Não foi possível inativar usuario Ativo Local com agendamentos");

        verify(mailingService, times(1))
            .countQuantidadeAgendamentosProprietariosDoUsuario(eq(umUsuario().getId()), eq(ECanal.ATIVO_PROPRIO));
    }

    @Test
    public void inativar_deveInativarUsuario_seUsuarioNaoAtivoLocal() {
        var usuario = umUsuarioCompleto();
        usuario.setCargo(Cargo
            .builder()
            .codigo(AGENTE_AUTORIZADO_SOCIO)
            .nivel(Nivel
                .builder()
                .codigo(CodigoNivel.AGENTE_AUTORIZADO)
                .nome("AGENTE AUTORIZADO")
                .build())
            .build());
        when(repository.findComplete(eq(1))).thenReturn(Optional.of(usuario));

        assertThatCode(() -> service.inativar(umUsuarioInativoDto()))
            .doesNotThrowAnyException();

        verify(mailingService, never()).countQuantidadeAgendamentosProprietariosDoUsuario(any(), any());
    }

    @Test
    public void inativar_deveInativarUsuario_quandoUsuarioAtivoLocalESemAgendamento() {
        when(mailingService.countQuantidadeAgendamentosProprietariosDoUsuario(eq(umUsuario().getId()), eq(ECanal.ATIVO_PROPRIO)))
            .thenReturn(Long.valueOf(0));
        when(repository.findComplete(eq(1))).thenReturn(Optional.of(umUsuarioCompleto()));
        when(motivoInativacaoService.findByCodigoMotivoInativacao(eq(CodigoMotivoInativacao.DEMISSAO)))
            .thenReturn(MotivoInativacao.builder().codigo(CodigoMotivoInativacao.DEMISSAO).build());

        assertThatCode(() -> service.inativar(umUsuarioInativoDto()))
            .doesNotThrowAnyException();

        verify(mailingService, times(1))
            .countQuantidadeAgendamentosProprietariosDoUsuario(eq(umUsuario().getId()), eq(ECanal.ATIVO_PROPRIO));
    }

    private UsuarioInativacaoDto umUsuarioInativoDto() {
        return UsuarioInativacaoDto.builder()
            .idUsuario(1)
            .codigoMotivoInativacao(CodigoMotivoInativacao.DEMISSAO)
            .build();
    }

    @Test
    public void save_validacaoException_quandoUsuarioNaoTiverPermissaoSobreOCanalParaOCargo() {
        var usuario = Usuario.builder()
            .cargo(Cargo.builder()
                .id(6)
                .codigo(DIRETOR_OPERACAO)
                .canais(Set.of(ECanal.ATIVO_PROPRIO, ECanal.AGENTE_AUTORIZADO))
                .build())
            .canais(Set.of(ECanal.D2D_PROPRIO))
            .subCanais(Set.of(new SubCanal(1)))
            .build();

        when(cargoService.findById(anyInt())).thenReturn(usuario.getCargo());

        doReturn(umUsuarioAutenticadoNivelAa())
            .when(autenticacaoService)
            .getUsuarioAutenticado();

        assertThatExceptionOfType(ValidacaoException.class)
            .isThrownBy(() -> service.save(usuario))
            .withMessage("Usuário sem permissão para o cargo com os canais.");
    }

    @Test
    public void save_naoDeveLancarException_seUsuarioPossuirSubCanal() {
        var usuario = umUsuarioCompleto(SUPERVISOR_OPERACAO,
            10,
            OPERACAO,
            CodigoDepartamento.COMERCIAL,
            ECanal.D2D_PROPRIO);
        usuario.setSubCanais(Set.of(new SubCanal(1)));

        when(repository.findById(eq(1))).thenReturn(Optional.of(usuario));
        doReturn(umUsuarioAutenticadoAdmin(1))
            .when(autenticacaoService)
            .getUsuarioAutenticado();

        usuario.setNome("Usuario Teste");

        assertThatCode(() -> service.save(usuario)).doesNotThrowAnyException();
    }

    @Test
    public void save_naoDeveLancarException_seUsuarioPossuirSubCanaisECargoDiretor() {
        var usuario = umUsuarioCompleto(DIRETOR_OPERACAO,
            5,
            OPERACAO,
            CodigoDepartamento.COMERCIAL,
            ECanal.D2D_PROPRIO);
        usuario.setSubCanais(Set.of(
            new SubCanal(1),
            new SubCanal(2)
        ));

        when(repository.findById(eq(1))).thenReturn(Optional.of(usuario));
        when(cargoService.findById(anyInt()))
            .thenReturn(Cargo.builder().codigo(DIRETOR_OPERACAO).build());
        doReturn(umUsuarioAutenticadoAdmin(1))
            .when(autenticacaoService)
            .getUsuarioAutenticado();

        assertThatCode(() -> service.save(usuario)).doesNotThrowAnyException();
    }

    @Test
    public void save_deveLancarException_seUsuarioPossuirSubCanaisECargoSupervisor() {
        var usuario = umUsuarioCompleto(SUPERVISOR_OPERACAO,
            1,
            OPERACAO,
            CodigoDepartamento.COMERCIAL,
            ECanal.D2D_PROPRIO);
        usuario.setSubCanais(Set.of(
            new SubCanal(1),
            new SubCanal(2)
        ));

        when(cargoService.findById(anyInt())).thenReturn(Cargo.builder().codigo(SUPERVISOR_OPERACAO).build());

        doReturn(umUsuarioAutenticadoNivelAa())
            .when(autenticacaoService)
            .getUsuarioAutenticado();

        assertThatExceptionOfType(ValidacaoException.class)
            .isThrownBy(() -> service.save(usuario))
            .withMessage("Não é permitido cadastrar mais de um sub-canal para este cargo.");
    }

    @Test
    public void save_deveLancarException_seUsuarioNaoPossuirSubCanais() {
        var usuario = umUsuarioCompleto(SUPERVISOR_OPERACAO,
            1,
            OPERACAO,
            CodigoDepartamento.COMERCIAL,
            ECanal.D2D_PROPRIO);

        when(cargoService.findById(anyInt()))
            .thenReturn(Cargo.builder().codigo(SUPERVISOR_OPERACAO).build());

        doReturn(umUsuarioAutenticadoNivelAa())
            .when(autenticacaoService)
            .getUsuarioAutenticado();

        assertThatExceptionOfType(ValidacaoException.class)
            .isThrownBy(() -> service.save(usuario))
            .withMessage("Usuário não possui sub-canais, deve ser cadastrado no mínimo um.");
    }

    @Test
    public void save_naoDeveLancarException_seUsuarioPossuirSubCanaisDaHierarquia() {
        var usuario = umUsuarioCompleto(GERENTE_OPERACAO,
            5,
            OPERACAO,
            CodigoDepartamento.COMERCIAL,
            ECanal.D2D_PROPRIO);
        usuario.setSubCanais(Set.of(
            new SubCanal(1),
            new SubCanal(2)
        ));
        usuario.setHierarquiasId(List.of(10));

        when(repository.findById(eq(1))).thenReturn(Optional.of(usuario));
        when(repository.findById(eq(10))).thenReturn(Optional.of(usuario));
        when(cargoService.findById(5))
            .thenReturn(Cargo.builder().codigo(GERENTE_OPERACAO).build());
        when(repository.getSubCanaisByUsuarioIds(usuario.getHierarquiasId()))
            .thenReturn(Set.of(
                new SubCanal(1),
                new SubCanal(2),
                new SubCanal(3)
            ));
        when(repository.getCanaisByUsuarioIds(anyList()))
            .thenReturn(List.of(new Canal(1, ECanal.D2D_PROPRIO)));
        doReturn(UsuarioAutenticadoHelper.umUsuarioSuperiorD2d())
            .when(autenticacaoService)
            .getUsuarioAutenticado();

        assertThatCode(() -> service.save(usuario)).doesNotThrowAnyException();
    }

    @Test
    public void save_deveLancarException_seUsuarioNaoPossuirSubCanaisDaHierarquia() {
        var usuario = umUsuarioCompleto(GERENTE_OPERACAO,
            5,
            OPERACAO,
            CodigoDepartamento.COMERCIAL,
            ECanal.D2D_PROPRIO);
        usuario.setSubCanais(Set.of(
            new SubCanal(3),
            new SubCanal(4)
        ));
        usuario.setHierarquiasId(List.of(10));

        when(cargoService.findById(5)).thenReturn(Cargo.builder().codigo(GERENTE_OPERACAO).build());
        when(repository.getSubCanaisByUsuarioIds(usuario.getHierarquiasId()))
            .thenReturn(Set.of(
                new SubCanal(1),
                new SubCanal(2)
            ));
        doReturn(umUsuarioAutenticadoNivelAa())
            .when(autenticacaoService)
            .getUsuarioAutenticado();

        assertThatExceptionOfType(ValidacaoException.class)
            .isThrownBy(() -> service.save(usuario))
            .withMessage("Usuário não possui sub-canal em comum com usuários da hierarquia.");
    }

    @Test
    public void save_naoDeveLancarException_seSuperiorPossuirTodosSubCanaisDosSubordinados() {
        var usuario = umUsuarioCompleto(GERENTE_OPERACAO,
            5,
            OPERACAO,
            CodigoDepartamento.COMERCIAL,
            ECanal.D2D_PROPRIO);
        usuario.setSubCanais(Set.of(
            new SubCanal(1),
            new SubCanal(2),
            new SubCanal(3)
        ));
        usuario.setHierarquiasId(List.of(10));

        when(repository.findById(eq(1))).thenReturn(Optional.of(usuario));
        when(repository.findById(eq(10))).thenReturn(Optional.of(usuario));
        when(cargoService.findById(5)).thenReturn(Cargo.builder().codigo(GERENTE_OPERACAO).build());
        when(repository.getSubCanaisByUsuarioIds(usuario.getHierarquiasId()))
            .thenReturn(Set.of(
                new SubCanal(1),
                new SubCanal(2),
                new SubCanal(3),
                new SubCanal(4)
            ));
        when(repository.getAllSubordinadosComSubCanalId(usuario.getId()))
            .thenReturn(List.of());
        when(repository.getCanaisByUsuarioIds(anyList()))
            .thenReturn(List.of(new Canal(1, ECanal.D2D_PROPRIO)));
        doReturn(UsuarioAutenticadoHelper.umUsuarioSuperiorD2d())
            .when(autenticacaoService)
            .getUsuarioAutenticado();

        assertThatCode(() -> service.save(usuario)).doesNotThrowAnyException();
    }

    @Test
    public void save_deveLancarException_seSuperiorNaoPossuirTodosSubCanaisDosSubordinados() {
        var applicationEventPublisher = MockitoPublisherConfiguration.publisher();
        mockApplicationEventPublisher(applicationEventPublisher);

        var usuario = umUsuarioCompleto(GERENTE_OPERACAO,
            5, OPERACAO,
            CodigoDepartamento.COMERCIAL,
            ECanal.D2D_PROPRIO);
        usuario.setSubCanais(Set.of(
            new SubCanal(1),
            new SubCanal(2)
        ));
        usuario.setHierarquiasId(List.of(10));

        when(cargoService.findById(5)).thenReturn(Cargo.builder().codigo(GERENTE_OPERACAO).build());
        when(repository.getSubCanaisByUsuarioIds(usuario.getHierarquiasId()))
            .thenReturn(Set.of(
                new SubCanal(1),
                new SubCanal(2),
                new SubCanal(3),
                new SubCanal(4),
                new SubCanal(5)
            ));
        when(repository.getAllSubordinadosComSubCanalId(usuario.getId()))
            .thenReturn(umaListaDeUsuarioSubCanalIds());

        doReturn(umUsuarioAutenticadoNivelAa())
            .when(autenticacaoService)
            .getUsuarioAutenticado();

        assertThatExceptionOfType(ValidacaoSubCanalException.class)
            .isThrownBy(() -> service.save(usuario))
            .withMessage("Usuário não possui sub-canal em comum com usuários subordinados.");

        verify(applicationEventPublisher, times(1)).publishEvent(any(UsuarioSubCanalEvent.class));
    }

    @Test
    public void save_deveLancarException_seUsuarioNaoPossuirCanaisDaHierarquia() {
        var usuario = umUsuarioCompleto(GERENTE_OPERACAO,
            5,
            OPERACAO,
            CodigoDepartamento.COMERCIAL,
            ECanal.D2D_PROPRIO);
        usuario.setSubCanais(Set.of(
            new SubCanal(1),
            new SubCanal(2),
            new SubCanal(3)
        ));
        usuario.setHierarquiasId(List.of(10));

        when(repository.findById(eq(1))).thenReturn(Optional.of(usuario));
        when(cargoService.findById(5)).thenReturn(Cargo.builder().codigo(GERENTE_OPERACAO).build());
        when(repository.getSubCanaisByUsuarioIds(usuario.getHierarquiasId()))
            .thenReturn(Set.of(
                new SubCanal(1),
                new SubCanal(2),
                new SubCanal(3),
                new SubCanal(4)
            ));

        when(repository.getCanaisByUsuarioIds(anyList()))
            .thenReturn(List.of(new Canal(1, ECanal.AGENTE_AUTORIZADO)));
        doReturn(UsuarioAutenticadoHelper.umUsuarioSuperiorAtivoLocal())
            .when(autenticacaoService)
            .getUsuarioAutenticado();

        assertThatExceptionOfType(ValidacaoException.class)
            .isThrownBy(() -> service.save(usuario))
            .withMessage("Usuário não possui canal em comum com usuários da hierarquia.");
    }

    @Test
    public void save_naoDeveLancarException_seUsuarioPossuirCanaisDaHierarquia() {
        var usuario = umUsuarioCompleto(GERENTE_OPERACAO,
            5,
            OPERACAO,
            CodigoDepartamento.COMERCIAL,
            ECanal.D2D_PROPRIO);
        usuario.setSubCanais(Set.of(
            new SubCanal(1),
            new SubCanal(2),
            new SubCanal(3)
        ));
        usuario.setHierarquiasId(List.of(10));

        when(repository.findById(eq(1))).thenReturn(Optional.of(usuario));
        when(repository.findById(eq(10))).thenReturn(Optional.of(usuario));
        when(cargoService.findById(5)).thenReturn(Cargo.builder().codigo(GERENTE_OPERACAO).build());
        when(repository.getSubCanaisByUsuarioIds(usuario.getHierarquiasId()))
            .thenReturn(Set.of(
                new SubCanal(1),
                new SubCanal(2),
                new SubCanal(3),
                new SubCanal(4)
            ));

        when(repository.getCanaisByUsuarioIds(anyList()))
            .thenReturn(List.of(new Canal(1, ECanal.D2D_PROPRIO)));
        doReturn(UsuarioAutenticadoHelper.umUsuarioSuperiorD2d())
            .when(autenticacaoService)
            .getUsuarioAutenticado();

        assertThatCode(() -> service.save(usuario)).doesNotThrowAnyException();

        verify(repository, times(2)).save(eq(usuario));
    }

    @Test
    public void save_deveLancarExcecao_quandoEmailConterCedilha() {
        var usuario = Usuario.builder().email("emailç@gmail.com").build();

        doReturn(umUsuarioAutenticadoNivelAa())
            .when(autenticacaoService)
            .getUsuarioAutenticado();

        assertThatThrownBy(() -> service.save(usuario))
            .isInstanceOf(ValidacaoException.class)
            .hasMessage("Email inválido.");
    }

    @Test
    public void save_deveLancarExcecao_quandoEmailConterAcento() {
        var usuario = Usuario.builder().email("émail@gmail.com").build();

        doReturn(umUsuarioAutenticadoNivelAa())
            .when(autenticacaoService)
            .getUsuarioAutenticado();

        assertThatThrownBy(() -> service.save(usuario))
            .isInstanceOf(ValidacaoException.class)
            .hasMessage("Email inválido.");
    }

    @Test
    public void save_deveLancarExcecao_quandoEmailInvertidoAsOrdens() {
        var usuario = Usuario.builder().email("email.com@gmail").build();

        doReturn(umUsuarioAutenticadoNivelAa())
            .when(autenticacaoService)
            .getUsuarioAutenticado();

        assertThatThrownBy(() -> service.save(usuario))
            .isInstanceOf(ValidacaoException.class)
            .hasMessage("Email inválido.");
    }

    @Test
    public void save_deveLancarExcecao_quandoEmailSemArroba() {
        var usuario = Usuario.builder().email("emailgmail.com").build();

        doReturn(umUsuarioAutenticadoNivelAa())
            .when(autenticacaoService)
            .getUsuarioAutenticado();

        assertThatThrownBy(() -> service.save(usuario))
            .isInstanceOf(ValidacaoException.class)
            .hasMessage("Email inválido.");
    }

    @Test
    public void save_deveLancarExcecao_quandoEmailComDoisArrobas() {
        var usuario = Usuario.builder().email("email@@gmail.com").build();

        doReturn(umUsuarioAutenticadoNivelAa())
            .when(autenticacaoService)
            .getUsuarioAutenticado();

        assertThatThrownBy(() -> service.save(usuario))
            .isInstanceOf(ValidacaoException.class)
            .hasMessage("Email inválido.");
    }

    @Test
    public void save_deveLancarExcecao_quandoEmailSemPonto() {
        var usuario = Usuario.builder().email("email@gmailcom").build();

        doReturn(umUsuarioAutenticadoNivelAa())
            .when(autenticacaoService)
            .getUsuarioAutenticado();

        assertThatThrownBy(() -> service.save(usuario))
            .isInstanceOf(ValidacaoException.class)
            .hasMessage("Email inválido.");
    }

    @Test
    public void save_deveLancarExcecao_quandoEmailSemCaracterDepoisPonto() {
        var usuario = Usuario.builder().email("email@gmail.").build();

        doReturn(umUsuarioAutenticadoNivelAa())
            .when(autenticacaoService)
            .getUsuarioAutenticado();

        assertThatThrownBy(() -> service.save(usuario))
            .isInstanceOf(ValidacaoException.class)
            .hasMessage("Email inválido.");
    }

    @Test
    public void save_deveLancarExcecao_quandoEmailSemCaracterEntreArrobaEPonto() {
        var usuario = Usuario.builder().email("email@.com").build();

        doReturn(umUsuarioAutenticadoNivelAa())
            .when(autenticacaoService)
            .getUsuarioAutenticado();

        assertThatThrownBy(() -> service.save(usuario))
            .isInstanceOf(ValidacaoException.class)
            .hasMessage("Email inválido.");
    }

    @Test
    public void save_deveLancarExcecao_quandoEmailSemCaracterAntesPonto() {
        var usuario = Usuario.builder().email(".com").build();

        doReturn(umUsuarioAutenticadoNivelAa())
            .when(autenticacaoService)
            .getUsuarioAutenticado();

        assertThatThrownBy(() -> service.save(usuario))
            .isInstanceOf(ValidacaoException.class)
            .hasMessage("Email inválido.");
    }

    @Test
    public void save_deveLancarExcecao_quandoEmailSemCaracterAntesArroba() {
        var usuario = Usuario.builder().email("@gmail.com").build();

        doReturn(umUsuarioAutenticadoNivelAa())
            .when(autenticacaoService)
            .getUsuarioAutenticado();

        assertThatThrownBy(() -> service.save(usuario))
            .isInstanceOf(ValidacaoException.class)
            .hasMessage("Email inválido.");
    }

    @Test
    public void save_deveLancarExcecao_quandoEmailSemCaracterDepoisArroba() {
        var usuario = Usuario.builder().email("email@").build();

        doReturn(umUsuarioAutenticadoNivelAa())
            .when(autenticacaoService)
            .getUsuarioAutenticado();

        assertThatThrownBy(() -> service.save(usuario))
            .isInstanceOf(ValidacaoException.class)
            .hasMessage("Email inválido.");
    }

    @Test
    public void save_deveLancarExcecao_quandoEmailComCaracteresEspeciais() {
        doReturn(umUsuarioAutenticadoNivelAa())
            .when(autenticacaoService)
            .getUsuarioAutenticado();

        var emailsComCaracteresEspeciais = List.of("asteristico*@test.com", "!exclamacao@gmail.com",
            "#hashtag@gmail.com", "&ecomercial@gmail.com", "(parentese@gmail.com", ")parentese@gmail.com",
            "=igual@gmail.com", "/barra@gmail.com", "{chave@gmail.com", "}chave@gmail.com", "[colchete@gmail.com",
            "]colchete@gmail.com", "?interrogacao@gmail.com");

        emailsComCaracteresEspeciais.forEach(email -> {
                var usuario = Usuario.builder().email(email).build();
                assertThatThrownBy(() -> service.save(usuario)).hasMessage("Email inválido.");
            }
        );
    }

    @Test
    public void save_deveLancarExcecao_quandoUsuarioReceptivoPossuirOrganizacaoEmpresaInativa() {
        doReturn(umUsuarioAutenticadoAdmin(1))
            .when(autenticacaoService)
            .getUsuarioAutenticado();
        var usuario = umUsuarioCompleto(SUPERVISOR_RECEPTIVO,
            5, RECEPTIVO,
            CodigoDepartamento.COMERCIAL,
            ECanal.INTERNET);
        usuario.setCanais(null);
        var organizacao = OrganizacaoEmpresa.builder()
            .id(1)
            .situacao(ESituacaoOrganizacaoEmpresa.I)
            .build();
        usuario.setOrganizacaoEmpresa(organizacao);

        doReturn(organizacao)
            .when(organizacaoEmpresaService)
            .findById(anyInt());

        assertThatExceptionOfType(ValidacaoException.class)
            .isThrownBy(() -> service.save(usuario))
            .withMessage("O usuário não pode ser salvo pois o fornecedor está inativo.");
    }

    @Test
    public void save_deveLancarExcecao_quandoUsuarioOperacaoCanalInternetPossuirOrganizacaoEmpresaInativa() {
        doReturn(umUsuarioAutenticadoAdmin(1))
            .when(autenticacaoService)
            .getUsuarioAutenticado();
        var usuario = umUsuarioCompleto(GERENTE_OPERACAO,
            5, OPERACAO,
            CodigoDepartamento.COMERCIAL,
            ECanal.INTERNET);
        var organizacao = OrganizacaoEmpresa.builder()
            .id(1)
            .situacao(ESituacaoOrganizacaoEmpresa.I)
            .build();
        usuario.setOrganizacaoEmpresa(organizacao);

        doReturn(organizacao)
            .when(organizacaoEmpresaService)
            .findById(anyInt());

        assertThatExceptionOfType(ValidacaoException.class)
            .isThrownBy(() -> service.save(usuario))
            .withMessage("O usuário não pode ser salvo pois o fornecedor está inativo.");
    }

    @Test
    public void save_deveRemoverPermissaoInsideSalesPme_quandoUsuarioJaCadastradoENivelOperacao() {
        doReturn(umUsuarioAutenticadoNivelMso())
            .when(autenticacaoService)
            .getUsuarioAutenticado();

        doReturn(Optional.of(umUsuarioOperacaoComSubCanal(101112, 3, PAP_PREMIUM)))
            .when(repository)
            .findById(101112);

        assertThatCode(() -> service.save(umUsuarioOperacaoComSubCanal(101112, 3, PAP_PREMIUM)))
            .doesNotThrowAnyException();

        verify(autenticacaoService).getUsuarioAutenticado();
        verify(repository, times(6)).findById(101112);
        verify(subCanalService).removerPermissaoIndicacaoInsideSalesPme(any());
    }

    @Test
    public void save_deveAdicionarPermissaoInsideSalesPme_quandoUsuarioNivelOperacaoComSubCanalInsideSalesPme() {
        doReturn(umUsuarioAutenticadoNivelMso())
            .when(autenticacaoService)
            .getUsuarioAutenticado();

        doReturn(Optional.of(umUsuarioOperacaoComSubCanal(101112, 4, INSIDE_SALES_PME)))
            .when(repository)
            .findById(101112);

        assertThatCode(() -> service.save(umUsuarioOperacaoComSubCanal(101112, 4, INSIDE_SALES_PME)))
            .doesNotThrowAnyException();

        verify(autenticacaoService).getUsuarioAutenticado();
        verify(repository, times(6)).findById(101112);
        verify(subCanalService).adicionarPermissaoIndicacaoInsideSalesPme(any());
    }

    @Test
    public void save_deveEnviarNovosDadosParaEquipeVendas_quandoNomeDoUsuarioForAlterado() {
        doReturn(umUsuarioAutenticadoNivelAa())
            .when(autenticacaoService).getUsuarioAutenticado();

        var novoUsuario = umUsuarioOperacaoComSubCanal(1, 1, PAP);

        doReturn(Optional.of(umUsuario()))
            .when(repository).findById(1);

        doReturn(umSetSubCanal(1, PAP, PAP.getDescricao()))
            .when(repository).getSubCanaisByUsuarioIds(List.of(1));

        assertThatCode(() -> service.save(novoUsuario))
            .doesNotThrowAnyException();

        verify(autenticacaoService).getUsuarioAutenticado();
        verify(equipeVendasUsuarioService).updateEquipeVendasUsuario(umaEquipeVendaUsuarioRequest());
        verify(repository).getSubCanaisByUsuarioIds(List.of(1));
    }

    @Test
    public void save_deveEnviarNovosDadosParaEquipeVendas_quandoSubcanalDoUsuarioForAlterador() {
        doReturn(umUsuarioAutenticadoNivelAa())
            .when(autenticacaoService).getUsuarioAutenticado();

        var novoUsuario = umUsuarioOperacaoComSubCanal(1, 1, PAP);
        novoUsuario.setNome("NAKANO");

        doReturn(Optional.of(novoUsuario))
            .when(repository).findById(1);

        doReturn(umSetSubCanal(2, PAP_PME, PAP_PME.getDescricao()))
            .when(repository).getSubCanaisByUsuarioIds(List.of(1));

        assertThatCode(() -> service.save(novoUsuario))
            .doesNotThrowAnyException();

        verify(autenticacaoService).getUsuarioAutenticado();
        verify(equipeVendasUsuarioService).updateEquipeVendasUsuario(umaEquipeVendaUsuarioRequestComTrocaDeSubcanal());
        verify(repository).getSubCanaisByUsuarioIds(List.of(1));
    }

    @Test
    public void save_naoDeveEnviarNovosDadosParaEquipeVendas_quandoNivelDoUsuarioNaoForOperacao() {
        doReturn(umUsuarioAutenticadoNivelAa())
            .when(autenticacaoService).getUsuarioAutenticado();

        var novoUsuario = umUsuarioOperacaoComSubCanal(1, 1, PAP);
        novoUsuario.setNome("NAKANO");
        novoUsuario.getCargo().setCodigo(MSO_CONSULTOR);
        novoUsuario.getCargo().getNivel().setCodigo(MSO);

        doReturn(Optional.of(novoUsuario))
            .when(repository).findById(1);

        assertThatCode(() -> service.save(novoUsuario))
            .doesNotThrowAnyException();

        verify(autenticacaoService).getUsuarioAutenticado();
        verifyZeroInteractions(equipeVendasUsuarioService);
    }

    @Test
    public void save_naoDeveEnviarNovosDadosParaEquipeVendas_quandoNaoPossuirNenhumaAlteracaoNoNomeOuSubCanal() {
        doReturn(umUsuarioAutenticadoNivelAa())
            .when(autenticacaoService).getUsuarioAutenticado();

        var novoUsuario = umUsuarioOperacaoComSubCanal(1, 1, PAP);
        novoUsuario.setNome("NAKANO");

        doReturn(Optional.of(novoUsuario))
            .when(repository).findById(1);

        doReturn(umSetSubCanal(1, PAP, PAP.getDescricao()))
            .when(repository).getSubCanaisByUsuarioIds(List.of(1));

        assertThatCode(() -> service.save(novoUsuario))
            .doesNotThrowAnyException();

        verify(autenticacaoService).getUsuarioAutenticado();
        verifyZeroInteractions(equipeVendasUsuarioService);
    }

    @Test
    public void findCompleteByIdComLoginNetSales_deveDispararExcecao_seUsuarioNaoEncontrado() {
        when(repository.findComplete(eq(1))).thenReturn(Optional.empty());

        assertThatExceptionOfType(ValidacaoException.class)
            .isThrownBy(() -> service.findCompleteByIdComLoginNetSales(1))
            .withMessage("Usuário não encontrado.");
    }

    @Test
    public void findCompleteByIdComLoginNetSales_deveDispararExcecao_seUsuarioNaoPossuirLoginNetsales() {
        var usuario = umUsuarioCompleto();
        usuario.setLoginNetSales(null);

        when(repository.findComplete(eq(1))).thenReturn(Optional.of(usuario));

        assertThatExceptionOfType(ValidacaoException.class)
            .isThrownBy(() -> service.findCompleteByIdComLoginNetSales(1))
            .withMessage("Usuário não possui login NetSales válido.");
    }

    @Test
    public void findCompleteByIdComLoginNetSales_deveRetornarUsuarioCompleto_seUsuarioPossuirLoginNetsales() {
        when(repository.findComplete(eq(1))).thenReturn(Optional.of(umUsuarioCompleto()));
        assertThat(service.findCompleteByIdComLoginNetSales(1)).isEqualTo(umUsuarioCompleto());
    }

    @Test
    public void getSubclustersUsuario_deveConverterORetornoEmSelectResponse_conformeListaDeSubclusters() {
        when(repository.getSubclustersUsuario(anyInt()))
            .thenReturn(List.of(
                SubCluster.of(1, "TESTE1"),
                SubCluster.of(2, "TESTE2")));

        assertThat(service.getSubclusterUsuario(1))
            .extracting("value", "label")
            .containsExactly(
                tuple(1, "TESTE1"),
                tuple(2, "TESTE2"));
    }

    @Test
    public void getUfsUsuario_deveConverterORetornoEmSelectResponse_conformeListaDeEstados() {
        when(repository.getUfsUsuario(anyInt()))
            .thenReturn(List.of(
                Uf.builder().id(1).nome("PARANA").build(),
                Uf.builder().id(22).nome("SANTA CATARINA").build()));

        assertThat(service.getUfUsuario(1))
            .extracting("value", "label")
            .containsExactly(
                tuple(1, "PARANA"),
                tuple(22, "SANTA CATARINA"));
    }

    @Test
    public void buscarExecutivosPorSituacao_deveRetornarOsExecutivos() {
        when(repository.findAllExecutivosBySituacao(eq(A)))
            .thenReturn(List.of(umUsuarioExecutivo()));

        assertThat(service.buscarExecutivosPorSituacao(A))
            .hasSize(1)
            .extracting("id", "nome")
            .containsExactly(
                tuple(1, "BAKUGO"));
    }

    @Test
    public void findById_deveRetornarUsuarioResponse_quandoSolicitado() {
        when(repository.findById(1))
            .thenReturn(Optional.of(Usuario.builder()
                .id(1)
                .nome("RENATO")
                .build()));

        assertThat(service.findById(1))
            .extracting("id", "nome")
            .containsExactly(1, "RENATO");
    }

    @Test
    public void findById_deveRetornarException_quandoNaoEncontrarUsuarioById() {
        when(repository.findById(1))
            .thenReturn(Optional.empty());

        assertThatCode(() -> service.findById(1))
            .hasMessage("Usuário não encontrado.")
            .isInstanceOf(ValidacaoException.class);
    }

    @Test
    public void findUsuariosByCodigoCargo_deveRetornarUsuariosAtivos_peloCodigoDoCargo() {
        when(repository.findUsuariosByCodigoCargo(CodigoCargo.EXECUTIVO))
            .thenReturn(umaListaUsuariosExecutivosAtivo());

        assertThat(service.findUsuariosByCodigoCargo(CodigoCargo.EXECUTIVO))
            .extracting("id", "nome", "email", "codigoNivel", "codigoCargo", "codigoDepartamento", "situacao")
            .containsExactly(
                tuple(1, "JOSÉ", "JOSE@HOTMAIL.COM", CodigoNivel.AGENTE_AUTORIZADO,
                    CodigoCargo.EXECUTIVO, CodigoDepartamento.AGENTE_AUTORIZADO, A),
                tuple(2, "HIGOR", "HIGOR@HOTMAIL.COM", CodigoNivel.AGENTE_AUTORIZADO,
                    CodigoCargo.EXECUTIVO, CodigoDepartamento.AGENTE_AUTORIZADO, A));

        verify(repository, times(1)).findUsuariosByCodigoCargo(CodigoCargo.EXECUTIVO);
    }

    @Test
    public void findIdUsuariosAtivosByCodigoCargos_deveRetornarListaIdUsuariosAtivos_pelosCodigosDosCargos() {
        var listaCargos = List.of(MSO_CONSULTOR, ADMINISTRADOR);
        when(repository.findIdUsuariosAtivosByCodigoCargos(eq(listaCargos)))
            .thenReturn(List.of(24, 34));

        assertThat(service.findIdUsuariosAtivosByCodigoCargos(listaCargos))
            .isEqualTo(List.of(24, 34));
    }

    @Test
    public void salvarUsuarioBackoffice_deveAdicionarPermissaoEEnviarDadosParaFilaSocialHub_quandoMercadoDesenvolvimento() {
        var usuario = umUsuarioBackoffice();
        usuario.setId(1);
        usuario.setUsuarioCadastro(new Usuario(1));
        usuario.setCargo(Cargo.builder()
            .codigo(BACKOFFICE_GERENTE)
            .nivel(Nivel.builder()
                .codigo(BACKOFFICE)
                .build())
            .build());

        var organizacao = OrganizacaoEmpresa.builder()
            .id(5)
            .situacao(ESituacaoOrganizacaoEmpresa.A)
            .build();
        lenient().when(organizacaoEmpresaService.findById(anyInt()))
            .thenReturn(organizacao);

        doReturn(Optional.of(usuario)).when(repository).findById(1);
        doReturn(Optional.of(usuario)).when(repository).findComplete(1);

        when(autenticacaoService.getUsuarioAutenticado())
            .thenReturn(umUsuarioAutenticadoNivelBackoffice());
        when(cargoService.findByUsuarioId(1))
            .thenReturn(umCargo(1, SUPERVISOR_OPERACAO));
        when(permissaoEspecialService.hasPermissaoEspecialAtiva(usuario.getId(), ROLE_SHB))
            .thenReturn(true);

        assertThatCode(() -> service.salvarUsuarioBackoffice(usuario))
            .doesNotThrowAnyException();

        verify(permissaoEspecialService, times(1)).save(anyList());
        verify(usuarioMqSender, times(1)).enviarDadosUsuarioParaSocialHub(any());
    }

    @Test
    public void salvarUsuarioBackoffice_naoDeveAdicionarPermissaoEEnviarDadosParaFilaSocialHub_quandoNaoMercadoDesenvolvimento() {
        var usuario = umUsuarioBackoffice();
        usuario.setTerritorioMercadoDesenvolvimentoId(null);
        usuario.setId(1);
        usuario.setUsuarioCadastro(new Usuario(1));
        usuario.setCargo(Cargo.builder()
            .codigo(BACKOFFICE_GERENTE)
            .nivel(Nivel.builder()
                .codigo(BACKOFFICE)
                .build())
            .build());

        var organizacao = OrganizacaoEmpresa.builder()
            .id(5)
            .situacao(ESituacaoOrganizacaoEmpresa.A)
            .build();
        lenient().when(organizacaoEmpresaService.findById(anyInt()))
            .thenReturn(organizacao);

        doReturn(Optional.of(usuario)).when(repository).findById(1);
        doReturn(Optional.of(usuario)).when(repository).findComplete(1);

        when(autenticacaoService.getUsuarioAutenticado())
            .thenReturn(umUsuarioAutenticadoNivelBackoffice());

        assertThatCode(() -> service.salvarUsuarioBackoffice(usuario))
            .doesNotThrowAnyException();

        verify(permissaoEspecialService, never()).save(anyList());
        verify(usuarioMqSender, never())
            .enviarDadosUsuarioParaSocialHub(UsuarioSocialHubRequestMq.from(usuario, List.of(1022), "Diretor"));
    }

    @Test
    public void salvarUsuarioBackoffice_deveSalvar() {
        when(autenticacaoService.getUsuarioAutenticado())
            .thenReturn(umUsuarioAutenticadoNivelBackoffice());

        var organizacao = OrganizacaoEmpresa.builder()
            .id(5)
            .situacao(ESituacaoOrganizacaoEmpresa.A)
            .build();
        when(organizacaoEmpresaService.findById(anyInt()))
            .thenReturn(organizacao);

        service.salvarUsuarioBackoffice(umUsuarioBackoffice());

        verify(empresaRepository, atLeastOnce()).findAllAtivo();
        verify(unidadeNegocioRepository, atLeastOnce()).findAllAtivo();
        verify(notificacaoService, atLeastOnce())
            .enviarEmailDadosDeAcesso(argThat(arg -> arg.getNome().equals("Backoffice")), anyString());
    }

    @Test
    public void salvarUsuarioBackoffice_deveDesvincularUsuarioDoGrupo_quandoUsuarioAlterarCargo() {
        var usuarioAntigoMock = umUsuarioComCargoEOrganizacao(100, 100);
        var usuarioNovoMock = umUsuarioComCargoEOrganizacao(200, 100);
        when(autenticacaoService.getUsuarioAutenticado())
            .thenReturn(umUsuarioAutenticadoNivelBackoffice());
        when(repository.findComplete(anyInt()))
            .thenReturn(Optional.of(usuarioAntigoMock));
        when(repository.findById(anyInt()))
            .thenReturn(Optional.of(usuarioAntigoMock));
        when(organizacaoEmpresaService.findById(anyInt()))
            .thenReturn(umaOrganizacaoEmpresa());

        assertThatCode(() -> service.salvarUsuarioBackoffice(usuarioNovoMock))
            .doesNotThrowAnyException();

        verify(suporteVendasService).desvincularGruposByUsuarioId(100);
    }

    @Test
    public void salvarUsuarioBackoffice_deveDesvincularUsuarioDoGrupo_quandoUsuarioAlterarOrganizacao() {
        var usuarioAntigoMock = umUsuarioComCargoEOrganizacao(100, 100);
        var usuarioNovoMock = umUsuarioComCargoEOrganizacao(100, 200);
        when(autenticacaoService.getUsuarioAutenticado())
            .thenReturn(umUsuarioAutenticadoNivelBackoffice());
        when(repository.findComplete(anyInt()))
            .thenReturn(Optional.of(usuarioAntigoMock));
        when(repository.findById(anyInt()))
            .thenReturn(Optional.of(usuarioAntigoMock));
        when(organizacaoEmpresaService.findById(anyInt()))
            .thenReturn(umaOrganizacaoEmpresa());

        assertThatCode(() -> service.salvarUsuarioBackoffice(usuarioNovoMock))
            .doesNotThrowAnyException();

        verify(suporteVendasService).desvincularGruposByUsuarioId(100);
    }

    @Test
    public void salvarUsuarioBackoffice_naoDeveDesvincularUsuarioDoGrupo_quandoDadosIdenticos() {
        var usuarioMock = umUsuarioComCargoEOrganizacao(100, 100);
        when(autenticacaoService.getUsuarioAutenticado())
            .thenReturn(umUsuarioAutenticadoNivelBackoffice());
        when(repository.findComplete(anyInt()))
            .thenReturn(Optional.of(usuarioMock));
        when(repository.findById(anyInt()))
            .thenReturn(Optional.of(usuarioMock));
        when(organizacaoEmpresaService.findById(anyInt()))
            .thenReturn(umaOrganizacaoEmpresa());

        assertThatCode(() -> service.salvarUsuarioBackoffice(usuarioMock))
            .doesNotThrowAnyException();

        verifyZeroInteractions(suporteVendasService);
    }

    @Test
    public void salvarUsuarioBackoffice_deveRemoverCaracteresEspeciais() {
        var usuario = umUsuarioBackoffice();
        when(autenticacaoService.getUsuarioAutenticado())
            .thenReturn(umUsuarioAutenticadoNivelBackoffice());

        var organizacao = OrganizacaoEmpresa.builder()
            .id(5)
            .situacao(ESituacaoOrganizacaoEmpresa.A)
            .build();
        when(organizacaoEmpresaService.findById(anyInt()))
            .thenReturn(organizacao);

        assertThat(usuario)
            .extracting("cpf")
            .containsExactly("097.238.645-92");

        service.salvarUsuarioBackoffice(usuario);

        verify(repository, times(1)).save(usuarioCaptor.capture());
        assertThat(usuarioCaptor.getValue())
            .extracting("cpf")
            .containsExactly("09723864592");
    }

    @Test
    public void salvarUsuarioBackoffice_validacaoException_quandoCpfExistente() {
        when(autenticacaoService.getUsuarioAutenticado())
            .thenReturn(umUsuarioAutenticadoNivelBackoffice());
        when(repository.findTop1UsuarioByCpfAndSituacaoNot(any(), any()))
            .thenReturn(Optional.of(umUsuario()));
        when(organizacaoEmpresaService.findById(anyInt()))
            .thenReturn(umaOrganizacaoEmpresa());

        assertThatExceptionOfType(ValidacaoException.class)
            .isThrownBy(() -> service.salvarUsuarioBackoffice(umUsuarioBackoffice()))
            .withMessage("CPF já cadastrado.");

        verify(empresaRepository, atLeastOnce()).findAllAtivo();
        verify(unidadeNegocioRepository, atLeastOnce()).findAllAtivo();
        verify(repository, atLeastOnce()).findTop1UsuarioByCpfAndSituacaoNot(eq("09723864592"), any());
        verify(repository, never()).findTop1UsuarioByEmailIgnoreCaseAndSituacaoNot(any(), any());
    }

    @Test
    public void salvarUsuarioBackoffice_validacaoException_quandoEmailExistente() {
        when(autenticacaoService.getUsuarioAutenticado())
            .thenReturn(umUsuarioAutenticadoNivelBackoffice());
        when(repository.findTop1UsuarioByEmailIgnoreCaseAndSituacaoNot(any(), any()))
            .thenReturn(Optional.of(umUsuario()));
        when(organizacaoEmpresaService.findById(anyInt()))
            .thenReturn(umaOrganizacaoEmpresa());

        assertThatExceptionOfType(ValidacaoException.class)
            .isThrownBy(() -> service.salvarUsuarioBackoffice(umUsuarioBackoffice()))
            .withMessage("Email já cadastrado.");

        verify(empresaRepository, atLeastOnce()).findAllAtivo();
        verify(unidadeNegocioRepository, atLeastOnce()).findAllAtivo();
        verify(repository, atLeastOnce()).findTop1UsuarioByCpfAndSituacaoNot(eq("09723864592"), any());
        verify(repository, atLeastOnce()).findTop1UsuarioByEmailIgnoreCaseAndSituacaoNot(any(), any());
    }

    @Test
    public void salvarUsuarioBackoffice_validacaoException_quandoUsuarioNaoTiverPermissaoSobreOCanalParaOCargo() {
        when(autenticacaoService.getUsuarioAutenticado())
            .thenReturn(umUsuarioAutenticadoNivelBackoffice());
        when(organizacaoEmpresaService.findById(anyInt()))
            .thenReturn(umaOrganizacaoEmpresa());

        var usuario = Usuario.builder()
            .cargo(Cargo.builder()
                .id(22)
                .canais(Set.of(ECanal.ATIVO_PROPRIO, ECanal.AGENTE_AUTORIZADO))
                .build())
            .canais(Set.of(ECanal.D2D_PROPRIO))
            .subCanais(Set.of(new SubCanal(1)))
            .build();

        assertThatExceptionOfType(ValidacaoException.class)
            .isThrownBy(() -> service.salvarUsuarioBackoffice(usuario))
            .withMessage("Usuário sem permissão para o cargo com os canais.");
    }

    @Test
    public void salvarUsuarioBackoffice_validacaoException_quandoFornecedorInativo() {
        when(autenticacaoService.getUsuarioAutenticado())
            .thenReturn(umUsuarioAutenticadoNivelBackoffice());

        var organizacao = OrganizacaoEmpresa.builder()
            .id(5)
            .situacao(ESituacaoOrganizacaoEmpresa.I)
            .build();
        when(organizacaoEmpresaService.findById(anyInt()))
            .thenReturn(organizacao);

        assertThatExceptionOfType(ValidacaoException.class)
            .isThrownBy(() -> service.salvarUsuarioBackoffice(umUsuarioBackoffice()))
            .withMessage("O usuário não pode ser salvo pois o fornecedor está inativo.");
    }

    @Test
    public void salvarUsuarioBriefing_deveSalvar() {
        when(autenticacaoService.getUsuarioAutenticado())
            .thenReturn(umUsuarioAutenticadoNivelMso());

        service.salvarUsuarioBriefing(umUsuarioBriefing());
        verify(notificacaoService, atLeastOnce())
            .enviarEmailDadosDeAcesso(argThat(arg -> arg.getNome().equals("Briefing")), anyString());
    }

    @Test
    public void salvarUsuarioBriefing_deveRemoverCaracteresEspeciais() {
        var usaurio = umUsuarioBriefing();
        when(autenticacaoService.getUsuarioAutenticado())
            .thenReturn(umUsuarioAutenticadoNivelMso());

        assertThat(usaurio)
            .extracting("cpf")
            .containsExactly("097.238.645-92");

        service.salvarUsuarioBriefing(usaurio);

        verify(repository, times(1)).save(usuarioCaptor.capture());
        assertThat(usuarioCaptor.getValue())
            .extracting("cpf")
            .containsExactly("09723864592");
    }

    @Test
    public void salvarUsuarioBriefing_validacaoException_quandoCpfExistente() {
        when(autenticacaoService.getUsuarioAutenticado())
            .thenReturn(umUsuarioAutenticadoNivelMso());
        when(repository.findTop1UsuarioByCpfAndSituacaoNot(any(), any()))
            .thenReturn(Optional.of(umUsuario()));

        assertThatExceptionOfType(ValidacaoException.class)
            .isThrownBy(() -> service.salvarUsuarioBriefing(umUsuarioBriefing()))
            .withMessage("CPF já cadastrado.");

        verify(repository, atLeastOnce()).findTop1UsuarioByCpfAndSituacaoNot(eq("09723864592"), any());
        verify(repository, never()).findTop1UsuarioByEmailIgnoreCaseAndSituacaoNot(any(), any());
        verify(repository, never()).save(anyIterable());
    }

    @Test
    public void salvarUsuarioBriefing_validacaoException_quandoEmailExistente() {
        when(autenticacaoService.getUsuarioAutenticado())
            .thenReturn(umUsuarioAutenticadoNivelMso());
        when(repository.findTop1UsuarioByEmailIgnoreCaseAndSituacaoNot(any(), any()))
            .thenReturn(Optional.of(umUsuario()));

        assertThatExceptionOfType(ValidacaoException.class)
            .isThrownBy(() -> service.salvarUsuarioBriefing(umUsuarioBriefing()))
            .withMessage("Email já cadastrado.");

        verify(repository, atLeastOnce()).findTop1UsuarioByCpfAndSituacaoNot(eq("09723864592"), any());
        verify(repository, atLeastOnce()).findTop1UsuarioByEmailIgnoreCaseAndSituacaoNot(any(), any());
        verify(repository, never()).save(anyIterable());
    }

    private List<Usuario> umaListaUsuariosExecutivosAtivo() {
        return List.of(
            Usuario.builder()
                .id(1)
                .nome("JOSÉ")
                .email("JOSE@HOTMAIL.COM")
                .situacao(A)
                .departamento(Departamento.builder()
                    .id(1)
                    .codigo(CodigoDepartamento.AGENTE_AUTORIZADO)
                    .build())
                .cargo(Cargo.builder()
                    .id(1)
                    .codigo(CodigoCargo.EXECUTIVO)
                    .nivel(Nivel.builder()
                        .id(1)
                        .codigo(CodigoNivel.AGENTE_AUTORIZADO)
                        .build())
                    .build())
                .build(),
            Usuario.builder()
                .id(2)
                .nome("HIGOR")
                .email("HIGOR@HOTMAIL.COM")
                .situacao(A)
                .departamento(Departamento.builder()
                    .id(1)
                    .codigo(CodigoDepartamento.AGENTE_AUTORIZADO)
                    .build())
                .cargo(Cargo.builder()
                    .id(1)
                    .codigo(CodigoCargo.EXECUTIVO)
                    .nivel(Nivel.builder()
                        .id(1)
                        .codigo(CodigoNivel.AGENTE_AUTORIZADO)
                        .build())
                    .build())
                .build()
        );
    }

    @Test
    public void findUsuariosByIds_deveRetonarUsuarios_quandoForPassadoIdsDosUsuarios() {
        when(repository.findUsuariosByIds(any()))
            .thenReturn(List.of(
                umUsuarioSituacaoResponse(1, "JONATHAN", A),
                umUsuarioSituacaoResponse(2, "FLAVIA", ESituacao.I)));

        assertThat(service.findUsuariosByIds(List.of(1, 2)))
            .extracting("id", "nome", "situacao")
            .containsExactlyInAnyOrder(
                tuple(1, "JONATHAN", A),
                tuple(2, "FLAVIA", ESituacao.I));
    }

    @Test
    public void buscarUsuariosAtivosNivelOperacaoCanalAa_listaComDoisUsuarios_quandoSituacaoAtivoECanalAa() {
        when(repository.findAllAtivosByNivelOperacaoCanalAa())
            .thenReturn(List.of(
                SelectResponse.of(100, "JOSÉ"),
                SelectResponse.of(101, "JOÃO")
            ));

        assertThat(service.buscarUsuariosAtivosNivelOperacaoCanalAa())
            .extracting("value", "label")
            .containsExactly(
                tuple(100, "JOSÉ"),
                tuple(101, "JOÃO")
            );
    }

    @Test
    public void getVendedoresByIds_deveRetornarUsuarios() {
        when(repository.findByIdIn(List.of(1, 2, 3)))
            .thenReturn(umaUsuariosList());
        assertThat(service.getVendedoresByIds(List.of(1, 2, 3)))
            .extracting("id", "nome", "loginNetSales", "email")
            .containsExactly(
                tuple(1, "Caio", "H", "caio@teste.com"),
                tuple(2, "Mario", "QQ", "mario@teste.com"),
                tuple(3, "Maria", "LOG", "maria@teste.com")
            );
    }

    @Test
    public void getVendedoresByIds_deveDividirListaIds_seListaMaiorQueMil() {
        when(repository.findByIdIn(IntStream.rangeClosed(3000, 3999).boxed().collect(Collectors.toList())))
            .thenReturn(umaUsuariosList());

        assertThat(service.getVendedoresByIds(IntStream.rangeClosed(0, 4000).boxed().collect(Collectors.toList())))
            .extracting("id", "nome", "loginNetSales", "email")
            .containsExactly(
                tuple(1, "Caio", "H", "caio@teste.com"),
                tuple(2, "Mario", "QQ", "mario@teste.com"),
                tuple(3, "Maria", "LOG", "maria@teste.com")
            );

        verify(repository, times(5))
            .findByIdIn(any());
        verify(repository, times(1))
            .findByIdIn(eq(IntStream.rangeClosed(0, 999).boxed().collect(Collectors.toList())));
        verify(repository, times(1))
            .findByIdIn(eq(IntStream.rangeClosed(1000, 1999).boxed().collect(Collectors.toList())));
        verify(repository, times(1))
            .findByIdIn(eq(IntStream.rangeClosed(2000, 2999).boxed().collect(Collectors.toList())));
        verify(repository, times(1))
            .findByIdIn(eq(IntStream.rangeClosed(3000, 3999).boxed().collect(Collectors.toList())));
        verify(repository, times(1))
            .findByIdIn(eq(List.of(4000)));
    }

    @Test
    public void getVendedoresByIds_naoDeveDividirListaIds_seListaMenorQueMil() {
        when(repository.findByIdIn(IntStream.rangeClosed(0, 800).boxed().collect(Collectors.toList())))
            .thenReturn(umaUsuariosList());

        assertThat(service.getVendedoresByIds(IntStream.rangeClosed(0, 800).boxed().collect(Collectors.toList())))
            .extracting("id", "nome", "loginNetSales", "email")
            .containsExactly(
                tuple(1, "Caio", "H", "caio@teste.com"),
                tuple(2, "Mario", "QQ", "mario@teste.com"),
                tuple(3, "Maria", "LOG", "maria@teste.com")
            );

        verify(repository, times(1))
            .findByIdIn(any());
        verify(repository, times(1))
            .findByIdIn(eq(IntStream.rangeClosed(0, 800).boxed().collect(Collectors.toList())));
    }

    @Test
    public void getAllUsuariosDaHierarquiaD2dDoUserLogado_usuarios_quandoUsuarioDiferenteDeAaExbrain() {
        var usuarioComPermissaoDeVisualizarAa = umUsuarioAutenticado(1, "AGENTE_AUTORIZADO",
            CodigoCargo.AGENTE_AUTORIZADO_SOCIO, AUT_VISUALIZAR_GERAL);
        when(autenticacaoService.getUsuarioAutenticado()).thenReturn(usuarioComPermissaoDeVisualizarAa);

        service.getAllUsuariosDaHierarquiaD2dDoUserLogado();

        verify(repository, times(1))
            .findAll(eq(new UsuarioPredicate().ignorarAa(true).ignorarXbrain(true).build()));
    }

    @Test
    public void getAllUsuariosDaHierarquiaD2dDoUserLogado_usuariosDaEquipe_quandoUsuarioEquipeVendas() {
        var usuarioEquipeVendas = umUsuarioAutenticado(1, "OPERACAO",
            CodigoCargo.VENDEDOR_OPERACAO, AUT_VISUALIZAR_GERAL);
        when(autenticacaoService.getUsuarioAutenticado()).thenReturn(usuarioEquipeVendas);

        when(equipeVendaD2dService.getUsuariosPermitidos(any())).thenReturn(List.of());
        when(autenticacaoService.getUsuarioId()).thenReturn(3);
        when(repository.getUsuariosSubordinados(any())).thenReturn(new ArrayList<>(List.of(2, 4, 5)));

        service.getAllUsuariosDaHierarquiaD2dDoUserLogado();

        verify(repository, times(1))
            .findAll(eq(new UsuarioPredicate().ignorarAa(true).ignorarXbrain(true)
                .comIds(List.of(3, 2, 4, 5, 1, 1)).build()));
    }

    @Test
    public void buscarUsuariosDaHierarquiaDoUsuarioLogado_usuariosSubordinados_quandoUsuarioEquipeVendas() {
        var usuarioEquipeVendas = umUsuarioAutenticado(1, "OPERACAO",
            CodigoCargo.SUPERVISOR_OPERACAO, AUT_VISUALIZAR_GERAL);
        when(autenticacaoService.getUsuarioAutenticado()).thenReturn(usuarioEquipeVendas);

        when(equipeVendaD2dService.getUsuariosPermitidos(any())).thenReturn(List.of());
        when(autenticacaoService.getUsuarioId()).thenReturn(3);
        when(repository.getUsuariosSubordinados(any())).thenReturn(Lists.newArrayList(List.of(2, 4, 5)));

        service.buscarUsuariosDaHierarquiaDoUsuarioLogado(null);

        verify(equipeVendaD2dService, times(1))
            .getUsuariosPermitidos(argThat(arg -> arg.size() == 3));
        verify(repository, times(1))
            .findAll(any(Predicate.class), eq(new Sort(ASC, "nome")));
    }

    @Test
    public void buscarUsuariosDaHierarquiaDoUsuarioLogado_usuarios_quandoUsuarioCoordenador() {
        var usuarioEquipeVendas = umUsuarioAutenticado(1, "OPERACAO",
            CodigoCargo.COORDENADOR_OPERACAO, CTR_VISUALIZAR_CARTEIRA_HIERARQUIA);
        when(autenticacaoService.getUsuarioAutenticado()).thenReturn(usuarioEquipeVendas);

        when(repository.getUsuariosSubordinados(any())).thenReturn(Lists.newArrayList(List.of(2, 4, 5)));

        service.buscarUsuariosDaHierarquiaDoUsuarioLogado(null);

        verify(repository, times(1))
            .findAll(any(Predicate.class), eq(new Sort(ASC, "nome")));
    }

    @Test
    public void findByCpfAa_deveRetornarUsuario_quandoBuscarPorCpfNaoInformandoFiltro() {
        when(repository.findTop1UsuarioByCpf(anyString())).thenReturn(Optional.of(umUsuarioAtivo()));

        var usuario = service.findByCpfAa("31114231827", null);

        assertThat(usuario).isPresent().get().extracting("id", "cpf", "nome", "situacao", "email")
            .containsExactly(10, "98471883007", "Usuario Ativo", A, "usuarioativo@email.com");
    }

    @Test
    public void findByCpfAa_deveRetornarUsuario_quandoBuscarPorCpfIgnorandoBuscaPorSomenteUsuarioAtivo() {
        when(repository.findTop1UsuarioByCpf(anyString())).thenReturn(Optional.of(umUsuarioInativo()));

        var usuario = service.findByCpfAa("31114231827", false);

        assertThat(usuario).isPresent().get().extracting("id", "cpf", "nome", "situacao", "email")
            .containsExactly(11, "31114231827", "Usuario Inativo", ESituacao.I, "usuarioinativo@email.com");
    }

    @Test
    public void findByCpfAa_deveRetornarUsuario_quandoBuscarPorCpfBuscandoSomenteUsuarioAtivo() {
        when(repository.findTop1UsuarioByCpfAndSituacao(anyString(), any())).thenReturn(Optional.of(umUsuarioAtivo()));

        var usuario = service.findByCpfAa("98471883007", true);

        assertThat(usuario).isPresent().get().extracting("id", "cpf", "nome", "situacao", "email")
            .containsExactly(10, "98471883007", "Usuario Ativo", A, "usuarioativo@email.com");
    }

    @Test
    public void findByCpfAa_deveRetornarVazio_quandoBuscarPorCpfENaoEncontrarUsuarioCorrespondente() {
        when(repository.findTop1UsuarioByCpf(anyString())).thenReturn(Optional.empty());

        var usuario = service.findByCpfAa("12345678901", null);

        assertThat(usuario).isEmpty();
    }

    @Test
    public void findByCpfAa_deveRetornarVazio_quandoBuscarPorCpfSomenteSituacaoAtivoEUsuarioEstiverInativoOuRealocado() {
        when(repository.findTop1UsuarioByCpfAndSituacao(anyString(), any())).thenReturn(Optional.empty());

        var usuario = service.findByCpfAa("31114231827", true);

        assertThat(usuario).isEmpty();
    }

    @Test
    public void findByEmailAa_deveRetornarUsuario_quandoBuscarPorEmailNaoInformandoFiltro() {
        when(repository.findByEmail(anyString())).thenReturn(Optional.of(umUsuarioAtivo()));

        var usuario = service.findByEmailAa("usuarioativo@email.com", null);

        assertThat(usuario).isPresent().get().extracting("id", "cpf", "nome", "situacao", "email")
            .containsExactly(10, "98471883007", "Usuario Ativo", A, "usuarioativo@email.com");
    }

    @Test
    public void findByEmailAa_deveRetornarUsuario_quandoBuscarPorEmailIgnorandoBuscaPorSomenteUsuarioAtivo() {
        when(repository.findByEmail(anyString())).thenReturn(Optional.of(umUsuarioInativo()));

        var usuario = service.findByEmailAa("usuarioinativo@email.com", false);

        assertThat(usuario).isPresent().get().extracting("id", "cpf", "nome", "situacao", "email")
            .containsExactly(11, "31114231827", "Usuario Inativo", ESituacao.I, "usuarioinativo@email.com");
    }

    @Test
    public void findByEmailAa_deveRetornarUsuario_quandoBuscarPorEmailBuscandoSomenteUsuarioAtivo() {
        when(repository.findByEmailAndSituacao(anyString(), any())).thenReturn(Optional.of(umUsuarioAtivo()));

        var usuario = service.findByEmailAa("usuarioativo@email.com", true);

        assertThat(usuario).isPresent().get().extracting("id", "cpf", "nome", "situacao", "email")
            .containsExactly(10, "98471883007", "Usuario Ativo", A, "usuarioativo@email.com");
    }

    @Test
    public void findByEmailAa_deveRetornarVazio_quandoBuscarPorEmailENaoEncontrarUsuarioCorrespondente() {
        when(repository.findByEmail(anyString())).thenReturn(Optional.empty());

        var usuario = service.findByEmailAa("teste@teste.com", null);

        assertThat(usuario).isEmpty();
    }

    @Test
    public void findByEmailAa_deveRetornarVazio_quandoBuscarPorEmailSomenteSituacaoAtivoEUsuarioEstiverInativoOuRealocado() {
        when(repository.findByEmailAndSituacao(anyString(), any())).thenReturn(Optional.empty());

        var usuario = service.findByEmailAa("usuarioinativo@email.com", true);

        assertThat(usuario).isEmpty();
    }

    @Test
    public void getUsuariosAlvoDoComunicado_deveRetornarUsuarios_quandoBuscarPorNovaRegionalId() {
        when(repository.findAllNomesIds(any(PublicoAlvoComunicadoFiltros.class)))
            .thenReturn(List.of(UsuarioNomeResponse.of(1, "TESTE", ESituacao.A)));
        var filtros = PublicoAlvoComunicadoFiltros.builder()
            .regionalId(1027)
            .build();
        assertThat(service.getUsuariosAlvoDoComunicado(filtros))
            .extracting("id", "nome", "situacao")
            .containsExactly(tuple(1, "TESTE", ESituacao.A));
        verify(repository, times(1)).findAllNomesIds(eq(
            PublicoAlvoComunicadoFiltros.builder()
                .todoCanalAa(false)
                .todoCanalD2d(false)
                .comUsuariosLogadosHoje(false)
                .regionalId(1027)
                .usuarioService(service)
                .build()));
    }

    @Test
    public void getUsuariosAlvoDoComunicado_deveRetornarUsuarios_quandoBuscarPorUfId() {
        when(repository.findAllNomesIds(any(PublicoAlvoComunicadoFiltros.class)))
            .thenReturn(List.of(UsuarioNomeResponse.of(1, "TESTE", ESituacao.A)));
        var filtros = PublicoAlvoComunicadoFiltros.builder()
            .regionalId(1027)
            .ufId(1)
            .build();
        assertThat(service.getUsuariosAlvoDoComunicado(filtros))
            .extracting("id", "nome", "situacao")
            .containsExactly(tuple(1, "TESTE", ESituacao.A));
        verify(repository, times(1)).findAllNomesIds(eq(
            PublicoAlvoComunicadoFiltros.builder()
                .todoCanalAa(false)
                .todoCanalD2d(false)
                .comUsuariosLogadosHoje(false)
                .ufId(1)
                .usuarioService(service)
                .build()));
    }

    @Test
    public void getUsuariosAlvoDoComunicado_deveRetornarUsuarios_quandoBuscarCidadesIds() {
        when(repository.findAllNomesIds(any(PublicoAlvoComunicadoFiltros.class)))
            .thenReturn(List.of(UsuarioNomeResponse.of(1, "TESTE", ESituacao.A)));
        var filtros = PublicoAlvoComunicadoFiltros.builder()
            .regionalId(1027)
            .ufId(1)
            .cidadesIds(List.of(5578))
            .build();
        assertThat(service.getUsuariosAlvoDoComunicado(filtros))
            .extracting("id", "nome", "situacao")
            .containsExactly(tuple(1, "TESTE", ESituacao.A));
        verify(repository, times(1)).findAllNomesIds(eq(
            PublicoAlvoComunicadoFiltros.builder()
                .todoCanalAa(false)
                .todoCanalD2d(false)
                .comUsuariosLogadosHoje(false)
                .cidadesIds(List.of(5578))
                .usuarioService(service)
                .build()));
    }

    private Usuario umUsuarioAtivo() {
        return Usuario.builder()
            .id(10)
            .cpf("98471883007")
            .nome("Usuario Ativo")
            .situacao(A)
            .email("usuarioativo@email.com")
            .build();
    }

    private Usuario umUsuarioInativo() {
        return Usuario.builder()
            .id(11)
            .cpf("31114231827")
            .nome("Usuario Inativo")
            .situacao(ESituacao.I)
            .email("usuarioinativo@email.com")
            .build();
    }

    private Usuario umUsuarioBackoffice() {
        return Usuario.builder()
            .nome("Backoffice")
            .cargo(new Cargo(110))
            .departamento(new Departamento(69))
            .organizacaoEmpresa(new OrganizacaoEmpresa(5))
            .cpf("097.238.645-92")
            .email("usuario@teste.com")
            .telefone("43995565661")
            .hierarquiasId(List.of())
            .territorioMercadoDesenvolvimentoId(1)
            .usuariosHierarquia(new HashSet<>())
            .build();
    }

    private Usuario umUsuarioBriefing() {
        return Usuario.builder()
            .nome("Briefing")
            .cpf("097.238.645-92")
            .email("briefing@teste.com")
            .telefone("14999999999")
            .hierarquiasId(List.of())
            .usuariosHierarquia(new HashSet<>())
            .cargo(new Cargo(1234))
            .departamento(new Departamento(12345))
            .organizacaoEmpresa(new OrganizacaoEmpresa(1))
            .build();
    }

    private Usuario umUsuarioVendedorInternet() {
        return Usuario.builder()
            .id(5436278)
            .nome("VENDEDOR")
            .loginNetSales("VENDEDOR_LOGIN")
            .email("VENDEDOR@TESTE.COM")
            .situacao(ESituacao.A)
            .cargo(umCargoVendedorInternet())
            .departamento(umDepartamentoComercial())
            .build();
    }

    private Usuario umUsuario() {
        return Usuario.builder()
            .id(1)
            .cpf("097.238.645-92")
            .nome("Seiya")
            .situacao(ESituacao.A)
            .departamento(Departamento.builder().id(1).nome("teste").build())
            .unidadesNegocios(List.of(
                umaUnidadeNegocio(CodigoUnidadeNegocio.CLARO_RESIDENCIAL),
                umaUnidadeNegocio(CodigoUnidadeNegocio.RESIDENCIAL_COMBOS)))
            .empresas(List.of(umaEmpresa()))
            .cargo(Cargo.builder()
                .id(1)
                .nivel(Nivel.builder()
                    .id(1)
                    .codigo(CodigoNivel.XBRAIN)
                    .build())
                .build())
            .build();
    }

    private List<Usuario> umaUsuariosList() {
        return List.of(
            Usuario.builder()
                .id(1)
                .nome("Caio")
                .loginNetSales("H")
                .email("caio@teste.com")
                .situacao(A)
                .build(),
            Usuario.builder()
                .id(2)
                .nome("Mario")
                .loginNetSales("QQ")
                .email("mario@teste.com")
                .situacao(ESituacao.I)
                .build(),
            Usuario.builder()
                .id(3)
                .nome("Maria")
                .loginNetSales("LOG")
                .email("maria@teste.com")
                .situacao(ESituacao.R)
                .build()
        );
    }

    private UsuarioAutenticado umUsuarioAutenticado(int usuarioId, String nivelCodigo, CodigoCargo cargo,
                                                    CodigoFuncionalidade... permissoes) {
        return UsuarioAutenticado.builder()
            .usuario(getUser(usuarioId, getCargo(cargo)))
            .nivelCodigo(nivelCodigo)
            .cargoCodigo(cargo)
            .id(usuarioId)
            .canais(Set.of(ECanal.D2D_PROPRIO))
            .permissoes(getPermissoes(permissoes))
            .build();
    }

    private Usuario getUser(int usuarioId, Cargo cargo) {
        return Usuario.builder()
            .id(usuarioId)
            .cpf("097.238.645-92")
            .cargo(cargo)
            .build();
    }

    private Cargo getCargo(CodigoCargo cargo) {
        return Cargo.builder()
            .codigo(cargo)
            .build();
    }

    private List<SimpleGrantedAuthority> getPermissoes(CodigoFuncionalidade... permissoes) {
        return Objects.nonNull(permissoes)
            ? Arrays.stream(permissoes)
            .map(permissao -> new SimpleGrantedAuthority(permissao.getRole()))
            .collect(Collectors.toList())
            : null;
    }

    @Test
    public void getUsuarioSuperior_usuarioResponseVazio_quandoNaoEncontrarSuperiorDoUsuario() {
        when(repository.getUsuarioSuperior(100)).thenReturn(Optional.empty());

        assertThat(service.getUsuarioSuperior(100))
            .isEqualTo(new UsuarioResponse());
    }

    @Test
    public void getUsuarioSuperior_usuarioResponse_quandoBuscarSuperiorDoUsuario() {
        when(repository.getUsuarioSuperior(100))
            .thenReturn(Optional.of(umUsuarioHierarquia()));

        assertThat(service.getUsuarioSuperior(100))
            .isEqualTo(UsuarioResponse.builder()
                .id(100)
                .nome("RENATO")
                .telefone("43 3322-0000")
                .email("RENATO@GMAIL.COM")
                .situacao(A)
                .codigoUnidadesNegocio(List.of(
                    CodigoUnidadeNegocio.CLARO_RESIDENCIAL,
                    CodigoUnidadeNegocio.RESIDENCIAL_COMBOS))
                .codigoCargo(CodigoCargo.SUPERVISOR_OPERACAO)
                .codigoDepartamento(CodigoDepartamento.COMERCIAL)
                .codigoEmpresas(List.of(CodigoEmpresa.CLARO_RESIDENCIAL))
                .codigoNivel(OPERACAO)
                .nomeNivel("OPERACAO")
                .cpf("097.238.645-92")
                .canais(Collections.emptySet())
                .build());
    }

    @Test
    public void obterNomeUsuarioPorId_deveRetornarNome_quandoSolicitado() {
        when(repository.findById(eq(1))).thenReturn(Optional.of(Usuario.builder().nome("NOME UM").build()));

        assertThat(service.obterNomeUsuarioPorId(1)).isEqualTo("NOME UM");
    }

    @Test
    public void buscarSubordinadosAtivosPorSuperioresIdsECodigosCargos_listUsuarioResponse_seSolicitado() {
        when(repository
            .buscarSubordinadosAtivosPorSuperioresIdsECodigosCargos(eq(List.of(1)), eq(Set.of(ASSISTENTE_OPERACAO.name()))))
            .thenReturn(List.of(
                umUsuarioResponse(1, "NOME 1", A, ASSISTENTE_OPERACAO),
                umUsuarioResponse(2, "NOME 2", A, ASSISTENTE_OPERACAO),
                umUsuarioResponse(3, "NOME 3", A, ASSISTENTE_OPERACAO)));

        assertThat(service
            .buscarSubordinadosAtivosPorSuperioresIdsECodigosCargos(List.of(1), Set.of(ASSISTENTE_OPERACAO.name())))
            .extracting("id", "nome", "situacao", "codigoCargo")
            .containsExactlyInAnyOrder(
                tuple(1, "NOME 1", A, ASSISTENTE_OPERACAO),
                tuple(2, "NOME 2", A, ASSISTENTE_OPERACAO),
                tuple(3, "NOME 3", A, ASSISTENTE_OPERACAO));
    }

    private UsuarioHierarquia umUsuarioHierarquia() {
        return UsuarioHierarquia.builder()
            .usuarioSuperior(umUsuarioSuperior())
            .usuario(new Usuario(100))
            .usuarioCadastro(new Usuario(103))
            .dataCadastro(LocalDateTime.now())
            .build();
    }

    private Usuario umUsuarioSuperior() {
        return Usuario.builder()
            .id(100)
            .telefone("43 3322-0000")
            .cpf("097.238.645-92")
            .situacao(A)
            .cargo(umCargoSupervisorOperacao())
            .departamento(umDepartamentoComercial())
            .nome("RENATO")
            .email("RENATO@GMAIL.COM")
            .unidadesNegocios(List.of(
                umaUnidadeNegocio(CodigoUnidadeNegocio.CLARO_RESIDENCIAL),
                umaUnidadeNegocio(CodigoUnidadeNegocio.RESIDENCIAL_COMBOS)))
            .empresas(List.of(umaEmpresa()))
            .build();
    }

    private Cargo umCargoSupervisorOperacao() {
        return Cargo.builder()
            .id(1)
            .codigo(CodigoCargo.SUPERVISOR_OPERACAO)
            .nivel(umNivelOperacao())
            .nome(CodigoCargo.SUPERVISOR_OPERACAO.name())
            .situacao(A)
            .build();
    }

    private Nivel umNivelOperacao() {
        return Nivel.builder()
            .id(1)
            .codigo(OPERACAO)
            .nome(OPERACAO.name())
            .build();
    }

    private Departamento umDepartamentoComercial() {
        return Departamento.builder()
            .id(1)
            .codigo(CodigoDepartamento.COMERCIAL)
            .nome(CodigoDepartamento.COMERCIAL.name())
            .build();
    }

    private UnidadeNegocio umaUnidadeNegocio(CodigoUnidadeNegocio codigoUnidadeNegocio) {
        return UnidadeNegocio.builder()
            .id(1)
            .codigo(codigoUnidadeNegocio)
            .nome(codigoUnidadeNegocio.name())
            .situacao(A)
            .build();
    }

    private Empresa umaEmpresa() {
        return Empresa.builder()
            .id(1)
            .codigo(CodigoEmpresa.CLARO_RESIDENCIAL)
            .nome(CodigoEmpresa.CLARO_RESIDENCIAL.name())
            .build();
    }

    @Test
    public void getUsuarioByIdComLoginNetSales_deveRetornarUsuario_sePossuirLoginNetSales() {
        var umUsuarioComLogin = 1000;

        when(repository.findById(umUsuarioComLogin))
            .thenReturn(Optional.of(umUsuarioComLoginNetSales(umUsuarioComLogin)));

        var response = service.getUsuarioByIdComLoginNetSales(umUsuarioComLogin);

        assertThat(response)
            .extracting(UsuarioComLoginNetSalesResponse::getId,
                UsuarioComLoginNetSalesResponse::getNome,
                UsuarioComLoginNetSalesResponse::getLoginNetSales,
                UsuarioComLoginNetSalesResponse::getNivelCodigo,
                UsuarioComLoginNetSalesResponse::getCpfNetSales,
                UsuarioComLoginNetSalesResponse::getNomeEquipeVendasNetSales,
                UsuarioComLoginNetSalesResponse::getCanalNetSales)
            .containsExactly(
                umUsuarioComLogin,
                "UM USUARIO COM LOGIN",
                "UM LOGIN NETSALES",
                "ATIVO_LOCAL_PROPRIO",
                "123.456.887-91",
                "NOME EQUIPE VENDAS",
                "CANAL VENDAS NETSALES");
    }

    @Test
    public void getUsuarioByIdComLoginNetSales_deveRetornarUsuario_sePossuirLoginNetSalesEForOperador() {
        var umUsuarioComLogin = 1000;
        var user = umUsuarioComLoginNetSales(umUsuarioComLogin);
        user.setCanais(Set.of(ECanal.AGENTE_AUTORIZADO));
        user.getCargo().setNivel(Nivel.builder().codigo(OPERACAO).build());
        when(repository.findById(umUsuarioComLogin))
            .thenReturn(Optional.of(user));

        var response = service.getUsuarioByIdComLoginNetSales(umUsuarioComLogin);

        assertThat(response)
            .extracting(UsuarioComLoginNetSalesResponse::getId,
                UsuarioComLoginNetSalesResponse::getNome,
                UsuarioComLoginNetSalesResponse::getLoginNetSales,
                UsuarioComLoginNetSalesResponse::getNivelCodigo,
                UsuarioComLoginNetSalesResponse::getCpfNetSales,
                UsuarioComLoginNetSalesResponse::getNomeEquipeVendasNetSales,
                UsuarioComLoginNetSalesResponse::getCanalNetSales)
            .containsExactly(
                umUsuarioComLogin,
                "UM USUARIO COM LOGIN",
                "UM LOGIN NETSALES",
                "OPERACAO_AGENTE_AUTORIZADO",
                "123.456.887-91",
                "NOME EQUIPE VENDAS",
                "CANAL VENDAS NETSALES");
    }

    @Test
    public void getUsuarioByIdComLoginNetSales_deveRetornarUsuario_sePossuirLoginNetSalesEForReceptivo() {
        var umUsuarioComLogin = 1000;
        var user = umUsuarioComLoginNetSales(umUsuarioComLogin);
        user.setOrganizacaoEmpresa(OrganizacaoEmpresa.builder().nome("ATENTO").build());
        user.getCargo().setNivel(Nivel.builder().codigo(CodigoNivel.RECEPTIVO).build());
        when(repository.findById(umUsuarioComLogin))
            .thenReturn(Optional.of(user));

        var response = service.getUsuarioByIdComLoginNetSales(umUsuarioComLogin);

        assertThat(response)
            .extracting(UsuarioComLoginNetSalesResponse::getId,
                UsuarioComLoginNetSalesResponse::getNome,
                UsuarioComLoginNetSalesResponse::getLoginNetSales,
                UsuarioComLoginNetSalesResponse::getNivelCodigo,
                UsuarioComLoginNetSalesResponse::getCpfNetSales,
                UsuarioComLoginNetSalesResponse::getNomeEquipeVendasNetSales,
                UsuarioComLoginNetSalesResponse::getCanalNetSales)
            .containsExactly(
                umUsuarioComLogin,
                "UM USUARIO COM LOGIN",
                "UM LOGIN NETSALES",
                "RECEPTIVO_ATENTO",
                "123.456.887-91",
                "NOME EQUIPE VENDAS",
                "CANAL VENDAS NETSALES");
    }

    @Test
    public void getUsuarioByIdComLoginNetSales_deveLancarException_seUsuarioNaoPossuirLoginNetSales() {
        var umUsuarioSemLogin = 1001;

        when(repository.findById(umUsuarioSemLogin))
            .thenReturn(Optional.of(umUsuarioSemLoginNetSales(umUsuarioSemLogin)));

        assertThatExceptionOfType(ValidacaoException.class)
            .isThrownBy(() -> service.getUsuarioByIdComLoginNetSales(umUsuarioSemLogin))
            .withMessage("Usuário não possui login NetSales válido.");
    }

    @Test
    public void getUsuarioByIdComLoginNetSales_deveLancarException_seUsuarioNaoEncontrado() {
        var umUsuarioInexistente = 1002;

        when(repository.findById(umUsuarioInexistente))
            .thenReturn(Optional.empty());

        assertThatExceptionOfType(ValidacaoException.class)
            .isThrownBy(() -> service.getUsuarioByIdComLoginNetSales(umUsuarioInexistente))
            .withMessage("Usuário não encontrado.");
    }

    @Test
    public void obterIdsPorUsuarioCadastroId_deveRetornarListaVazia_quandoNaoEncontrarUsuarios() {
        when(repository.obterIdsPorUsuarioCadastroId(eq(1000))).thenReturn(List.of());

        assertThat(service.obterIdsPorUsuarioCadastroId(1000))
            .isEmpty();
    }

    @Test
    public void obterIdsPorUsuarioCadastroId_deveRetornarListaIds_quandoEncontrarUsuarios() {
        when(repository.obterIdsPorUsuarioCadastroId(eq(400))).thenReturn(List.of(100, 200, 300));

        assertThat(service.obterIdsPorUsuarioCadastroId(400))
            .hasSize(3)
            .containsExactly(100, 200, 300);
    }

    @Test
    public void getAll_deveRetornarUsuarioPage_quandoPossuirMaisQueMilUsuariosSubordinados() {
        var idsUsuariosSubordinados = IntStream.rangeClosed(0, 2000).boxed().collect(Collectors.toList());
        var idsUsuariosSubordinadosComIdDoUsuario = Stream.of(idsUsuariosSubordinados, List.of(3000))
            .flatMap(Collection::stream)
            .collect(Collectors.toList());
        var predicate = new UsuarioPredicate().ignorarAa(true).ignorarXbrain(true).comIds(idsUsuariosSubordinadosComIdDoUsuario);

        when(autenticacaoService.getUsuarioAutenticado())
            .thenReturn(umUsuarioAutenticado(3000, OPERACAO.toString(), DIRETOR_OPERACAO, CTR_VISUALIZAR_CARTEIRA_HIERARQUIA));
        when(repository.obterIdsPorUsuarioCadastroId(eq(3000))).thenReturn(List.of());
        when(repository.getUsuariosSubordinados(eq(3000))).thenReturn(idsUsuariosSubordinados);
        when(repository.findAll(eq(predicate.build()), eq(new PageRequest())))
            .thenReturn(umaPageUsuario(new PageRequest(), List.of(umUsuario())));

        assertThat(service.getAll(new PageRequest(), new UsuarioFiltros()))
            .isNotEmpty();
    }

    @Test
    public void getAll_deveRetornarUsuarioPage_quandoColaboradorCanalInternet() {
        var predicate = new UsuarioPredicate().ignorarAa(true).ignorarXbrain(true).comCanal(ECanal.INTERNET);

        var usuarioAutenticado = umUsuarioAutenticado(3000, OPERACAO.toString(), INTERNET_GERENTE);
        usuarioAutenticado.setCanais(Set.of(ECanal.INTERNET));

        when(autenticacaoService.getUsuarioAutenticado())
            .thenReturn(usuarioAutenticado);
        when(repository.findAll(predicate.build(), new PageRequest()))
            .thenReturn(umaPageUsuario(new PageRequest(), List.of(umUsuario())));

        assertThat(service.getAll(new PageRequest(), new UsuarioFiltros()))
            .isNotEmpty();

        verify(autenticacaoService, times(2)).getUsuarioAutenticado();
        verify(repository).findAll(predicate.build(), new PageRequest());
        verify(repository, never()).getIdsUsuariosHierarquiaPorCargos(anySet());
    }

    @Test
    public void getAll_deveRetornarUsuarioPage_quandoSupervisorCanalInternet() {
        var usuarioAutenticado = umUsuarioAutenticado(3000, OPERACAO.toString(), INTERNET_SUPERVISOR);
        usuarioAutenticado.setCanais(Set.of(ECanal.INTERNET));
        usuarioAutenticado.setOrganizacaoId(1);

        doReturn(usuarioAutenticado)
            .when(autenticacaoService)
            .getUsuarioAutenticado();
        doReturn(List.of(5436278))
            .when(repository)
            .getIdsUsuariosHierarquiaPorCargos(anySet());
        doReturn(umaPageUsuario(new PageRequest(), List.of(umUsuarioVendedorInternet())))
            .when(repository)
            .findAll(any(Predicate.class), any(PageRequest.class));

        assertThat(service.getAll(new PageRequest(), new UsuarioFiltros()))
            .isNotEmpty();

        verify(autenticacaoService, times(2))
            .getUsuarioAutenticado();
        verify(repository)
            .findAll(any(Predicate.class), any(PageRequest.class));
        verify(repository)
            .getIdsUsuariosHierarquiaPorCargos(Set.of(INTERNET_BACKOFFICE, INTERNET_VENDEDOR, INTERNET_COORDENADOR));
    }

    @Test
    public void buscarBackOfficesAndSociosAaPorAaIds_deveRetornarResponse_seEncontradoUsuariosIdPorUmAaId() {
        when(agenteAutorizadoService.getUsuariosByAaId(100, false)).thenReturn(List.of(
            umUsuarioAgenteAutorizadoResponse(100, 100),
            umUsuarioAgenteAutorizadoResponse(101, 100)));
        when(repository.findAll(umUsuarioPredicateComCargoCodigoBackOfficeESocioAaDosIds(List.of(100, 101)).build()))
            .thenReturn(List.of(
                umUsuarioDoIdECodigoCargo(100, CodigoCargo.BACKOFFICE_ANALISTA_TRATAMENTO),
                umUsuarioDoIdECodigoCargo(101, CodigoCargo.AGENTE_AUTORIZADO_SOCIO)));

        when(agenteAutorizadoService.getUsuariosByAaId(200, false)).thenReturn(Collections.emptyList());

        assertThat(service.buscarBackOfficesAndSociosAaPorAaIds(List.of(100, 200)))
            .extracting("id", "nome", "email", "agenteAutorizadoId")
            .containsExactly(
                tuple(100, "FULANO DE TESTE", "TESTE@TESTE.COM", 100),
                tuple(101, "FULANO DE TESTE", "TESTE@TESTE.COM", 100));
    }

    @Test
    public void buscarBackOfficesAndSociosAaPorAaIds_deveRetornarResponse_seEncontradoUsuariosPorUmAaId() {
        when(agenteAutorizadoService.getUsuariosByAaId(100, false)).thenReturn(List.of(
            umUsuarioAgenteAutorizadoResponse(100, 100),
            umUsuarioAgenteAutorizadoResponse(101, 100)));
        when(repository.findAll(umUsuarioPredicateComCargoCodigoBackOfficeESocioAaDosIds(List.of(100, 101)).build()))
            .thenReturn(Collections.emptyList());

        when(agenteAutorizadoService.getUsuariosByAaId(200, false)).thenReturn(List.of(
            umUsuarioAgenteAutorizadoResponse(200, 200),
            umUsuarioAgenteAutorizadoResponse(201, 200)));
        when(repository.findAll(umUsuarioPredicateComCargoCodigoBackOfficeESocioAaDosIds(List.of(200, 201)).build()))
            .thenReturn(List.of(
                umUsuarioDoIdECodigoCargo(200, CodigoCargo.BACKOFFICE_ANALISTA_TRATAMENTO),
                umUsuarioDoIdECodigoCargo(201, CodigoCargo.AGENTE_AUTORIZADO_SOCIO)));

        assertThat(service.buscarBackOfficesAndSociosAaPorAaIds(List.of(100, 200)))
            .extracting("id", "nome", "email", "agenteAutorizadoId")
            .containsExactly(
                tuple(200, "FULANO DE TESTE", "TESTE@TESTE.COM", 200),
                tuple(201, "FULANO DE TESTE", "TESTE@TESTE.COM", 200));
    }

    @Test
    public void buscarBackOfficesAndSociosAaPorAaIds_deveRetornarUsuarioAgenteAutorizadoResponse_seEncontradoPorTodosAaId() {
        when(agenteAutorizadoService.getUsuariosByAaId(100, false)).thenReturn(List.of(
            umUsuarioAgenteAutorizadoResponse(100, 100),
            umUsuarioAgenteAutorizadoResponse(101, 100)));
        when(repository.findAll(umUsuarioPredicateComCargoCodigoBackOfficeESocioAaDosIds(List.of(100, 101)).build()))
            .thenReturn(List.of(
                umUsuarioDoIdECodigoCargo(100, CodigoCargo.BACKOFFICE_ANALISTA_TRATAMENTO),
                umUsuarioDoIdECodigoCargo(101, CodigoCargo.AGENTE_AUTORIZADO_SOCIO)));

        when(agenteAutorizadoService.getUsuariosByAaId(200, false)).thenReturn(List.of(
            umUsuarioAgenteAutorizadoResponse(200, 200),
            umUsuarioAgenteAutorizadoResponse(201, 200)));
        when(repository.findAll(umUsuarioPredicateComCargoCodigoBackOfficeESocioAaDosIds(List.of(200, 201)).build()))
            .thenReturn(List.of(
                umUsuarioDoIdECodigoCargo(200, CodigoCargo.BACKOFFICE_ANALISTA_TRATAMENTO),
                umUsuarioDoIdECodigoCargo(201, CodigoCargo.AGENTE_AUTORIZADO_SOCIO)));

        assertThat(service.buscarBackOfficesAndSociosAaPorAaIds(List.of(100, 200)))
            .extracting("id", "nome", "email", "agenteAutorizadoId")
            .containsExactly(
                tuple(100, "FULANO DE TESTE", "TESTE@TESTE.COM", 100),
                tuple(101, "FULANO DE TESTE", "TESTE@TESTE.COM", 100),
                tuple(200, "FULANO DE TESTE", "TESTE@TESTE.COM", 200),
                tuple(201, "FULANO DE TESTE", "TESTE@TESTE.COM", 200));
    }

    @Test
    public void buscarBackOfficesAndSociosAaPorAaIds_naoDeveRetornarUsuarioAgenteAutorizadoResponse_seNaoEncontrarUsuariosId() {
        when(agenteAutorizadoService.getUsuariosByAaId(100, false)).thenReturn(Collections.emptyList());
        assertThat(service.buscarBackOfficesAndSociosAaPorAaIds(List.of(100, 200))).isEqualTo(Collections.emptyList());
    }

    @Test
    public void buscarBackOfficesAndSociosAaPorAaIds_naoDeveRetornarUsuarioAgenteAutorizadoResponse_seNaoEncontrarUsuarios() {
        when(agenteAutorizadoService.getUsuariosByAaId(100, false)).thenReturn(List.of(
            umUsuarioAgenteAutorizadoResponse(100, 100),
            umUsuarioAgenteAutorizadoResponse(101, 100)));
        when(repository.findAll(umUsuarioPredicateComCargoCodigoBackOfficeESocioAaDosIds(List.of(100, 101)).build()))
            .thenReturn(Collections.emptyList());

        assertThat(service.buscarBackOfficesAndSociosAaPorAaIds(List.of(100, 200))).isEqualTo(Collections.emptyList());
    }

    @Test
    public void buscarVendedoresFeeder_deveRetornarListaVazia_quandoNaoHouverUsuariosDosAgentesAutorizados() {
        when(agenteAutorizadoService.buscarTodosUsuariosDosAas(eq(List.of(1)), eq(true)))
            .thenReturn(List.of());

        assertThat(service.buscarVendedoresFeeder(umVendedoresFeederFiltros(List.of(1), null, false)))
            .isEmpty();

        verify(repository, never()).findAll(any(Predicate.class));
    }

    @Test
    public void buscarVendedoresFeeder_deveRetornarListaUsuarioConsultaDto_quandoBuscarInativosNull() {
        when(agenteAutorizadoService.buscarTodosUsuariosDosAas(eq(List.of(1)), eq(true)))
            .thenReturn(List.of(umUsuarioDtoVendas(1)));
        when(repository.findAll(eq(umVendedoresFeederPredicateComSocioPrincipalESituacaoAtiva(List.of(1)).build())))
            .thenReturn(List.of(umUsuarioCompleto()));

        assertThat(service.buscarVendedoresFeeder(umVendedoresFeederFiltros(List.of(1), true, false)))
            .hasSize(1)
            .extracting("id", "nome", "situacao", "nivelCodigo")
            .containsExactly(tuple(1, "NOME UM", "A", "AGENTE_AUTORIZADO"));
    }

    @Test
    public void buscarVendedoresFeeder_deveRetornarListaUsuarioConsultaDto_quandoBuscarInativosFalse() {
        when(agenteAutorizadoService.buscarTodosUsuariosDosAas(eq(List.of(1)), eq(true)))
            .thenReturn(List.of(umUsuarioDtoVendas(1)));
        when(repository.findAll(eq(umVendedoresFeederPredicateComSocioPrincipalESituacaoAtiva(List.of(1)).build())))
            .thenReturn(List.of(umUsuarioCompleto()));

        assertThat(service.buscarVendedoresFeeder(umVendedoresFeederFiltros(List.of(1), true, false)))
            .hasSize(1)
            .extracting("id", "nome", "situacao", "nivelCodigo")
            .containsExactly(tuple(1, "NOME UM", "A", "AGENTE_AUTORIZADO"));
    }

    @Test
    public void buscarVendedoresFeeder_deveRetornarListaUsuarioConsultaDto_quandoBuscarInativosTrue() {
        when(agenteAutorizadoService.buscarTodosUsuariosDosAas(eq(List.of(1)), eq(true)))
            .thenReturn(List.of(umUsuarioDtoVendas(1)));
        when(repository.findAll(eq(umVendedoresFeederPredicateComSocioPrincipalETodasSituacaoes(List.of(1)).build())))
            .thenReturn(List.of(umUsuarioCompleto()));

        assertThat(service.buscarVendedoresFeeder(umVendedoresFeederFiltros(List.of(1), true, true)))
            .hasSize(1)
            .extracting("id", "nome", "situacao", "nivelCodigo")
            .containsExactly(tuple(1, "NOME UM", "A", "AGENTE_AUTORIZADO"));
    }

    @Test
    public void buscarUsuarioSituacaoPorIds_listaDeUsuarioSituacao_seSolicitado() {
        var usuariosSituacao = List.of(
            UsuarioSituacaoResponse.builder().id(1).nome("NOME 1").situacao(A).build(),
            UsuarioSituacaoResponse.builder().id(2).nome("NOME 2").situacao(ESituacao.I).build(),
            UsuarioSituacaoResponse.builder().id(3).nome("NOME 3").situacao(ESituacao.R).build()
        );

        when(repository.buscarUsuarioSituacao(eq(new BooleanBuilder(QUsuario.usuario.id.in(List.of(1, 2, 3))))))
            .thenReturn(usuariosSituacao);

        assertThat(service.buscarUsuarioSituacaoPorIds(new UsuarioSituacaoFiltro(List.of(1, 2, 3))))
            .isEqualTo(usuariosSituacao);
    }

    @Test
    public void findOperadoresBkoCentralizadoByFornecedor_deveBuscarCargosBkoCentralizado_quandoSolicitado() {
        service.findOperadoresBkoCentralizadoByFornecedor(10, false);

        verify(repository).findByOrganizacaoEmpresaIdAndCargo_CodigoIn(10, List.of(
            BACKOFFICE_OPERADOR_TRATAMENTO_VENDAS, BACKOFFICE_ANALISTA_TRATAMENTO_VENDAS));
    }

    @Test
    public void findOperadoresBkoCentralizadoByFornecedor_deveRetornarSelectResponse_quandoSolicitado() {
        when(repository.findByOrganizacaoEmpresaIdAndCargo_CodigoIn(5, List.of(
            BACKOFFICE_OPERADOR_TRATAMENTO_VENDAS,
            BACKOFFICE_ANALISTA_TRATAMENTO_VENDAS)))
            .thenReturn(List.of(umUsuarioAtivo(), umUsuarioInativo(), umUsuarioCompleto()));

        assertThat(service.findOperadoresBkoCentralizadoByFornecedor(5, true))
            .extracting("id", "nome", "email")
            .containsExactly(
                tuple(10, "Usuario Ativo", "usuarioativo@email.com"),
                tuple(11, "Usuario Inativo", "usuarioinativo@email.com"),
                tuple(1, "NOME UM", "email@email.com"));
    }

    @Test
    public void findUsuariosOperadoresBackofficeByOrganizacaoEmpresa_deveRetornarResponseSemFiltrarAtivos_seBuscarInativosTrue() {
        when(repository.findByOrganizacaoEmpresaIdAndCargo_CodigoIn(
            eq(5), eq(List.of(BACKOFFICE_OPERADOR_TRATAMENTO, BACKOFFICE_ANALISTA_TRATAMENTO))))
            .thenReturn(List.of(umUsuarioAtivo(), umUsuarioInativo(), umUsuarioCompleto()));

        assertThat(service.findUsuariosOperadoresBackofficeByOrganizacaoEmpresa(5, true, List.of()))
            .extracting("value", "label")
            .containsExactly(
                tuple(10, "Usuario Ativo"),
                tuple(11, "Usuario Inativo"),
                tuple(1, "NOME UM"));
    }

    @Test
    public void findUsuariosOperadoresBackofficeByOrganizacaoEmpresa_deveRetornarResponseEFiltrarAtivos_seBuscarInativosFalse() {
        when(repository.findByOrganizacaoEmpresaIdAndCargo_CodigoIn(
            eq(5), eq(List.of(BACKOFFICE_OPERADOR_TRATAMENTO, BACKOFFICE_ANALISTA_TRATAMENTO))))
            .thenReturn(List.of(umUsuarioAtivo(), umUsuarioInativo(), umUsuarioCompleto()));

        assertThat(service.findUsuariosOperadoresBackofficeByOrganizacaoEmpresa(5, false, List.of()))
            .extracting("value", "label")
            .containsExactly(
                tuple(10, "Usuario Ativo"),
                tuple(1, "NOME UM"));
    }

    @Test
    public void findUsuariosOperadoresBackofficeByOrganizacaoEmpresa_deveRetornarResponseEFiltrarPorCargo_quandoInformarCargos() {
        doReturn(List.of(umUsuarioAtivo()))
            .when(repository)
            .findByOrganizacaoEmpresaIdAndCargo_CodigoIn(5, List.of(BACKOFFICE_ANALISTA_DE_TRATAMENTO_DE_ANTI_FRAUDE));

        assertThat(service.findUsuariosOperadoresBackofficeByOrganizacaoEmpresa(5, false,
            List.of(BACKOFFICE_ANALISTA_DE_TRATAMENTO_DE_ANTI_FRAUDE)))
            .extracting("value", "label")
            .containsExactly(
                tuple(10, "Usuario Ativo"));
    }

    private Usuario umUsuarioComLoginNetSales(int id) {
        return Usuario.builder()
            .id(id)
            .nome("UM USUARIO COM LOGIN")
            .loginNetSales("UM LOGIN NETSALES")
            .cargo(Cargo.builder()
                .codigo(CodigoCargo.VENDEDOR_ATIVO_LOCAL_PROPRIO)
                .nivel(Nivel.builder().codigo(CodigoNivel.ATIVO_LOCAL_PROPRIO).build())
                .build())
            .cpf("123.456.887-91")
            .situacao(A)
            .nomeEquipeVendaNetSales("NOME EQUIPE VENDAS")
            .canalNetSales("CANAL VENDAS NETSALES")
            .build();
    }

    private Usuario umUsuarioSemLoginNetSales(int id) {
        var usuario = umUsuarioComLoginNetSales(id);
        usuario.setLoginNetSales(null);

        return usuario;
    }

    private Usuario umUsuarioDoIdECodigoCargo(int id, CodigoCargo codigoCargo) {
        return Usuario.builder()
            .id(id)
            .nome("FULANO DE TESTE")
            .email("TESTE@TESTE.COM")
            .cargo(Cargo.builder()
                .codigo(codigoCargo)
                .nivel(Nivel.builder().codigo(CodigoNivel.XBRAIN).build())
                .build())
            .cpf("123.456.887-91")
            .situacao(A)
            .build();
    }

    private Page<Usuario> umaPageUsuario(PageRequest pageRequest, List<Usuario> usuariosList) {
        return new PageImpl<>(
            usuariosList,
            pageRequest,
            usuariosList.size());
    }

    private UsuarioPredicate umUsuarioPredicateComCargoCodigoBackOfficeESocioAaDosIds(List<Integer> ids) {
        var predicate = new UsuarioPredicate();
        predicate.comCodigosCargos(FeederUtil.CARGOS_BACKOFFICE_AND_SOCIO_PRINCIPAL_AA);
        predicate.comIds(ids);

        return predicate;
    }

    @Test
    public void ativar_deveAtivarUsuario_quandoDesejarAlterarSituacaoDoUsuarioComId100ParaAtivo() {
        var usuarioInativo = Usuario.builder()
            .id(100)
            .nome("RENATO")
            .situacao(ESituacao.I)
            .build();

        when(repository.findById(100))
            .thenReturn(Optional.of(usuarioInativo));

        service.ativar(100);

        assertThat(usuarioInativo.getSituacao()).isEqualTo(A);

        verify(repository).save(usuarioInativo);
        verify(agenteAutorizadoService).ativarUsuario(eq(100));
    }

    @Test
    public void getUsuariosByIdsTodasSituacoes_deveEfetuarABuscaParticionada_quandoQtdeIdsMaiorQueMaximoOracle() {
        when(repository.findByIdIn(IntStream.rangeClosed(1, 1000).boxed().collect(Collectors.toList())))
            .thenReturn(
                List.of(
                    Usuario.builder()
                        .id(133)
                        .nome("Márcio Oliveira")
                        .build(),
                    Usuario.builder()
                        .id(988)
                        .nome("Any Gabrielly")
                        .build()
                ));

        var emptyUsersIdsPart = IntStream.rangeClosed(1001, 2000).boxed().collect(Collectors.toList());
        when(repository.findByIdIn(emptyUsersIdsPart)).thenReturn(List.of());

        when(repository.findByIdIn(IntStream.rangeClosed(2001, 2700).boxed().collect(Collectors.toList())))
            .thenReturn(List.of(
                Usuario.builder()
                    .id(2029)
                    .nome("Lee Ji Eun")
                    .build()
            ));

        var idsUsuarios = IntStream.rangeClosed(1, 2700).boxed().collect(Collectors.toCollection(LinkedHashSet::new));
        var usuarios = service.getUsuariosByIdsTodasSituacoes(idsUsuarios);

        assertThat(usuarios)
            .extracting("id", "nome")
            .containsExactlyInAnyOrder(
                tuple(133, "Márcio Oliveira"),
                tuple(988, "Any Gabrielly"),
                tuple(2029, "Lee Ji Eun")
            );

        verify(repository, times(1)).findByIdIn(eq(emptyUsersIdsPart));
    }

    @Test
    public void getUsuariosByIdsTodasSituacoes_deveRetornarListaVazia_quandoRegistrosNaoEncontrados() {
        var emptyUsersIdsPart = IntStream
            .rangeClosed(1, 500)
            .boxed()
            .collect(Collectors.toList());

        var idsUsuarios = IntStream
            .rangeClosed(1, 500)
            .boxed()
            .collect(Collectors.toCollection(LinkedHashSet::new));

        when(repository.findByIdIn(emptyUsersIdsPart)).thenReturn(List.of());

        var listaDeUsuarios = service.getUsuariosByIdsTodasSituacoes(idsUsuarios);

        assertThat(listaDeUsuarios).isEmpty();

        verify(repository, times(1)).findByIdIn(emptyUsersIdsPart);
    }

    @Test
    public void getTiposCanalOptions_opcoesDeSelectParaOsTiposCanal_quandoBuscarOpcoesParaOSelect() {
        assertThat(service.getTiposCanalOptions())
            .extracting("value", "label")
            .containsExactly(
                tuple("PAP", "PAP"),
                tuple("PAP_PME", "PAP PME"),
                tuple("PAP_PREMIUM", "PAP PREMIUM"),
                tuple("INSIDE_SALES_PME", "INSIDE SALES PME"),
                tuple("PAP_CONDOMINIO", "PAP CONDOMINIO")
            );
    }

    @Test
    public void getAllForCsv_deveRetornarCsv_quandoEncontrarUsuarios() {
        var usuarioFiltros = new UsuarioFiltros();
        var usuarioPredicate = usuarioFiltros.toPredicate();

        when(autenticacaoService.getUsuarioAutenticado())
            .thenReturn(UsuarioAutenticadoHelper.umUsuarioAutenticadoNivelAa());

        usuarioPredicate.filtraPermitidos(autenticacaoService.getUsuarioAutenticado(), service, true);

        when(repository.getUsuariosCsv(usuarioPredicate.build()))
            .thenReturn(List.of(umUsuarioOperacaoCsv(), umUsuarioAaCsv()));

        var usuarioCsvs = service.getAllForCsv(usuarioFiltros);

        assertThat(usuarioCsvs)
            .isEqualTo(List.of(umUsuarioOperacaoCsv(), umUsuarioAaCsv()));
    }

    @Test
    public void preencheUsuarioCsvsDeAa_devePreencherColunasDeAa_seUsuarioForAa() {
        when(agenteAutorizadoService.getAgenteAutorizadosUsuarioDtosByUsuarioIds(UsuarioRequest.of(List.of(2))))
            .thenReturn(Collections.singletonList(umAgenteAutorizadoUsuarioDto()));

        List<UsuarioCsvResponse> usuarioCsvResponses = new ArrayList<>();
        usuarioCsvResponses.add(umUsuarioAaCsv());
        usuarioCsvResponses.add(umUsuarioOperacaoCsv());
        service.preencherUsuarioCsvsDeAa(usuarioCsvResponses);

        var usuarioAaCsvCompletado = umUsuarioAaCsv();
        usuarioAaCsvCompletado.setCnpj("78300110000166");
        usuarioAaCsvCompletado.setRazaoSocial("Razao Social");

        assertThat(usuarioCsvResponses)
            .isEqualTo(List.of(usuarioAaCsvCompletado, umUsuarioOperacaoCsv()));
    }

    @Test
    public void preencheUsuarioCsvsDeOperacao_devePreencherColunasDeCanal_seUsuarioForOperacao() {
        when(repository.getCanaisByUsuarioIds(Collections.singletonList(1)))
            .thenReturn(List.of(umCanal(), umOutroCanal()));

        List<UsuarioCsvResponse> usuarioCsvResponses = new ArrayList<>();
        usuarioCsvResponses.add(umUsuarioAaCsv());
        usuarioCsvResponses.add(umUsuarioOperacaoCsv());

        service.preencherUsuarioCsvsDeOperacao(usuarioCsvResponses);

        var usuarioOperacaoCsvCompletado = umUsuarioOperacaoCsv();
        usuarioOperacaoCsvCompletado.setCanais(List.of(umCanal(), umOutroCanal()));

        assertThat(usuarioCsvResponses)
            .isEqualTo(List.of(umUsuarioAaCsv(), usuarioOperacaoCsvCompletado));
    }

    @Test
    @SuppressWarnings("LineLength")
    public void save_deveDispararValidacaoException_seUsuarioOperacaoEstiverNaCarteiraDeAlgumAgenteAutorizado() {
        when(repository.findById(eq(1)))
            .thenReturn(Optional.of(umUsuarioCompleto(SUPERVISOR_OPERACAO, 1, OPERACAO,
                CodigoDepartamento.COMERCIAL, ECanal.AGENTE_AUTORIZADO)));
        when(agenteAutorizadoService.findAgenteAutorizadoByUsuarioId(eq(1)))
            .thenReturn(List.of(umAgenteAutorizadoAtivoResponse()));

        doReturn(umUsuarioAutenticadoNivelAa())
            .when(autenticacaoService)
            .getUsuarioAutenticado();

        assertThatExceptionOfType(ValidacaoException.class)
            .isThrownBy(() -> service.save(umUsuarioCompleto(SUPERVISOR_OPERACAO, 1, OPERACAO,
                CodigoDepartamento.COMERCIAL, ECanal.ATIVO_PROPRIO)))
            .withMessage("Não é possível remover o canal Agente Autorizado, "
                + "pois o usuário possui vínculo com o(s) AA(s): TESTE AA 00.000.0000/0001-00.");
    }

    @Test
    public void save_naoDeveDispararValidacaoException_seUsuarioDadosAlteradosNaoForCanalAgenteAutorizado() {
        var usuarioCompleto = umUsuarioCompleto(SUPERVISOR_OPERACAO, 1, OPERACAO,
            CodigoDepartamento.COMERCIAL, ECanal.AGENTE_AUTORIZADO);

        when(repository.findById(eq(1))).thenReturn(Optional.of(usuarioCompleto));

        doReturn(umUsuarioAutenticadoNivelBackoffice())
            .when(autenticacaoService)
            .getUsuarioAutenticado();

        usuarioCompleto.setNome("AA Teste Dois");

        assertThatCode(() -> service.save(usuarioCompleto)).doesNotThrowAnyException();

        verifyNoMoreInteractions(agenteAutorizadoService);
    }

    @Test
    @SuppressWarnings("LineLength")
    public void save_deveDispararValidacaoException_seUsuarioOriginalPossuirSitesVinculadosEUsuarioOriginalForCoordenadorOuSupervisorOperacaoECanalAtivoProprioRemovido() {
        when(repository.findById(eq(1)))
            .thenReturn(Optional.of(UsuarioHelper.umUsuario(1, umCargo(1, CodigoCargo.COORDENADOR_OPERACAO), Set.of(ECanal.ATIVO_PROPRIO), 1)));
        when(siteService.buscarSitesAtivosPorCoordenadorOuSupervisor(eq(1)))
            .thenReturn(List.of(umSite(1, "SITE UM"), umSite(2, "SITE DOIS")));

        doReturn(umUsuarioAutenticadoNivelAa())
            .when(autenticacaoService)
            .getUsuarioAutenticado();

        assertThatExceptionOfType(ValidacaoException.class)
            .isThrownBy(() -> service.save(UsuarioHelper.umUsuario(1, umCargo(1, CodigoCargo.COORDENADOR_OPERACAO), Set.of(), 1)))
            .withMessage("Não é possível remover o canal Ativo Local, "
                + "pois o usuário possui vínculo com o(s) Site(s): SITE UM, SITE DOIS.");
    }

    @Test
    @SuppressWarnings("LineLength")
    public void save_deveDispararValidacaoException_seUsuarioOriginalPossuirSitesVinculadosECargoCoordenadorOuSupervisorOperacaoDoUsuarioOriginalAlterado() {
        when(repository.findById(eq(1)))
            .thenReturn(Optional.of(UsuarioHelper.umUsuario(1, umCargo(2, CodigoCargo.SUPERVISOR_OPERACAO), Set.of(ECanal.ATIVO_PROPRIO), 1)));
        when(siteService.buscarSitesAtivosPorCoordenadorOuSupervisor(eq(1)))
            .thenReturn(List.of(umSite(1, "SITE UM"), umSite(2, "SITE DOIS")));

        doReturn(umUsuarioAutenticadoNivelAa())
            .when(autenticacaoService)
            .getUsuarioAutenticado();

        assertThatExceptionOfType(ValidacaoException.class)
            .isThrownBy(() -> service.save(UsuarioHelper.umUsuario(1, umCargo(1, CodigoCargo.COORDENADOR_OPERACAO), Set.of(ECanal.ATIVO_PROPRIO), 1)))
            .withMessage("Não é possível alterar o cargo, "
                + "pois o usuário possui vínculo com o(s) Site(s): SITE UM, SITE DOIS.");
    }

    @Test
    @SuppressWarnings("LineLength")
    public void save_naoDeveDispararValidacaoException_seUsuarioOriginalNaoPossuirSitesVinculados() {
        when(repository.findById(eq(1)))
            .thenReturn(Optional.of(UsuarioHelper.umUsuario(1, umCargo(2, CodigoCargo.SUPERVISOR_OPERACAO), Set.of(ECanal.ATIVO_PROPRIO), 1)));
        when(siteService.buscarSitesAtivosPorCoordenadorOuSupervisor(eq(1)))
            .thenReturn(List.of());

        doReturn(umUsuarioAutenticadoNivelBackoffice())
            .when(autenticacaoService)
            .getUsuarioAutenticado();

        assertThatCode(() -> service.save(UsuarioHelper.umUsuario(1, umCargo(2, CodigoCargo.SUPERVISOR_OPERACAO), Set.of(), 1)))
            .doesNotThrowAnyException();
    }

    @Test
    @SuppressWarnings("LineLength")
    public void save_naoDeveDispararValidacaoException_seUsuarioOriginalPossuirSitesVinculadosENaoForCoordenadorOuSupervisorOperacao() {
        when(repository.findById(eq(1)))
            .thenReturn(Optional.of(UsuarioHelper.umUsuario(1, umCargo(1, CodigoCargo.COORDENADOR_OPERACAO), Set.of(ECanal.ATIVO_PROPRIO), 1)));

        when(siteService.buscarSitesAtivosPorCoordenadorOuSupervisor(eq(1)))
            .thenReturn(List.of(umSite(1, "SITE UM"), umSite(2, "SITE DOIS")));

        doReturn(umUsuarioAutenticadoNivelBackoffice())
            .when(autenticacaoService)
            .getUsuarioAutenticado();

        assertThatCode(() -> service
            .save(UsuarioHelper.umUsuario(1, umCargo(1, CodigoCargo.COORDENADOR_OPERACAO), Set.of(ECanal.ATIVO_PROPRIO), 1)))
            .doesNotThrowAnyException();
    }

    @Test
    @SuppressWarnings("LineLength")
    public void save_naoDeveDispararValidacaoException_seUsuarioOriginalPossuirSitesVinculadosECargoCoordenadorOuSupervisorECanalAtivoLocalMantidos() {
        when(repository.findById(eq(1)))
            .thenReturn(Optional.of(UsuarioHelper.umUsuario(1, umCargo(1, CodigoCargo.COORDENADOR_OPERACAO), Set.of(ECanal.ATIVO_PROPRIO), 1)));

        when(siteService.buscarSitesAtivosPorCoordenadorOuSupervisor(eq(1)))
            .thenReturn(List.of(umSite(1, "SITE UM"), umSite(2, "SITE DOIS")));

        doReturn(umUsuarioAutenticadoNivelBackoffice())
            .when(autenticacaoService)
            .getUsuarioAutenticado();

        assertThatCode(() -> service
            .save(UsuarioHelper.umUsuario(1, umCargo(1, CodigoCargo.COORDENADOR_OPERACAO), Set.of(ECanal.ATIVO_PROPRIO), 1)))
            .doesNotThrowAnyException();
    }

    @Test
    public void save_retornaValidacaoException_quandoUsuarioAtivoOutraEquipe() {
        var usuario = umUsuarioCompleto(ASSISTENTE_OPERACAO, 2,
            OPERACAO, CodigoDepartamento.COMERCIAL, ECanal.D2D_PROPRIO);
        usuario.setSubCanais(Set.of(new SubCanal(1)));
        when(repository.findById(any())).thenReturn(Optional.of(usuario));
        when(repository.getCanaisByUsuarioIds(any())).thenReturn(List.of(new Canal(1, ECanal.D2D_PROPRIO)));
        when(equipeVendasUsuarioService.buscarUsuarioEquipeVendasPorId(anyInt()))
            .thenReturn(List.of(1));

        doReturn(umUsuarioAutenticadoNivelAa())
            .when(autenticacaoService)
            .getUsuarioAutenticado();

        var usuarioCadastro = umUsuarioCompleto(VENDEDOR_OPERACAO, 8, CodigoNivel.OPERACAO,
            CodigoDepartamento.COMERCIAL, ECanal.D2D_PROPRIO);
        usuarioCadastro.setSubCanais(Set.of(new SubCanal(1)));

        assertThatExceptionOfType(ValidacaoException.class)
            .isThrownBy(() -> service.save(usuarioCadastro))
            .withMessage("Usuário já está cadastrado em outra equipe");
    }

    @Test
    public void save_naoDeveLancarException_quandoUsuarioNaoPossuiOutraEquipe() {
        var usuarioSalvo = umUsuarioCompleto(ASSISTENTE_OPERACAO, 2,
            OPERACAO, CodigoDepartamento.COMERCIAL,
            ECanal.D2D_PROPRIO);
        usuarioSalvo.setSubCanais(Set.of(new SubCanal(1)));
        when(repository.findById(any()))
            .thenReturn(Optional.of(usuarioSalvo));
        when(repository.getCanaisByUsuarioIds(any()))
            .thenReturn(List.of(new Canal(1, ECanal.D2D_PROPRIO)));
        when(equipeVendasUsuarioService.buscarUsuarioEquipeVendasPorId(anyInt()))
            .thenReturn(List.of());

        doReturn(umUsuarioAutenticadoNivelBackoffice())
            .when(autenticacaoService)
            .getUsuarioAutenticado();

        var usuario = umUsuarioCompleto(VENDEDOR_OPERACAO, 8,
            CodigoNivel.OPERACAO, CodigoDepartamento.COMERCIAL,
            ECanal.D2D_PROPRIO);
        usuario.setSubCanais(Set.of(new SubCanal(1)));

        assertThatCode(() -> service.save(usuario))
            .doesNotThrowAnyException();
    }

    @Test
    public void save_retornaValidacaoException_quandoLiderEquipe() {
        var usuarioSalvo = umUsuarioCompleto(SUPERVISOR_OPERACAO, 10,
            OPERACAO, CodigoDepartamento.COMERCIAL,
            ECanal.D2D_PROPRIO);
        usuarioSalvo.setSubCanais(Set.of(new SubCanal(1)));
        when(repository.findById(any()))
            .thenReturn(Optional.of(usuarioSalvo));
        when(repository.getCanaisByUsuarioIds(any()))
            .thenReturn(List.of(new Canal(1, ECanal.D2D_PROPRIO)));
        when(equipeVendaD2dService.getEquipeVendasBySupervisorId(any()))
            .thenReturn(List.of(1));

        doReturn(umUsuarioAutenticadoNivelAa())
            .when(autenticacaoService)
            .getUsuarioAutenticado();

        var usuario = umUsuarioCompleto(COORDENADOR_OPERACAO, 4, CodigoNivel.OPERACAO,
            CodigoDepartamento.COMERCIAL, ECanal.D2D_PROPRIO);
        usuario.setSubCanais(Set.of(new SubCanal(1)));

        assertThatExceptionOfType(ValidacaoException.class)
            .isThrownBy(() -> service.save(usuario))
            .withMessage("Usuário já está cadastrado em outra equipe");
    }

    @Test
    public void save_retornaValidacaoException_quandoCoordenadorLiderEquipe() {
        var usuarioSalvo = umUsuarioCompleto(COORDENADOR_OPERACAO, 10,
            OPERACAO, CodigoDepartamento.COMERCIAL,
            ECanal.D2D_PROPRIO);
        usuarioSalvo.setSubCanais(Set.of(new SubCanal(1)));
        when(repository.findById(any()))
            .thenReturn(Optional.of(usuarioSalvo));
        when(repository.getCanaisByUsuarioIds(any()))
            .thenReturn(List.of(new Canal(1, ECanal.D2D_PROPRIO)));
        when(equipeVendaD2dService.getEquipeVendasBySupervisorId(any()))
            .thenReturn(List.of(1));

        doReturn(umUsuarioAutenticadoNivelAa())
            .when(autenticacaoService)
            .getUsuarioAutenticado();

        var usuario = umUsuarioCompleto(GERENTE_OPERACAO, 9, CodigoNivel.OPERACAO,
            CodigoDepartamento.COMERCIAL, ECanal.D2D_PROPRIO);
        usuario.setSubCanais(Set.of(new SubCanal(1)));
        assertThatExceptionOfType(ValidacaoException.class)
            .isThrownBy(() -> service.save(usuario))
            .withMessage("Usuário já está cadastrado em outra equipe");

        verify(repository, never()).saveAndFlush(any());
    }

    @Test
    public void save_naoDeveLancarException_quandoCoordenadorNaoPossuirOutraEquipe() {
        var usuarioSalvo = umUsuarioCompleto(COORDENADOR_OPERACAO, 10,
            OPERACAO, CodigoDepartamento.COMERCIAL,
            ECanal.D2D_PROPRIO);
        usuarioSalvo.setSubCanais(Set.of(new SubCanal(1)));

        when(repository.findById(any()))
            .thenReturn(Optional.of(usuarioSalvo));
        when(repository.getCanaisByUsuarioIds(any()))
            .thenReturn(List.of(new Canal(1, ECanal.D2D_PROPRIO)));
        when(equipeVendaD2dService.getEquipeVendasBySupervisorId(any()))
            .thenReturn(List.of());

        doReturn(umUsuarioAutenticadoNivelBackoffice())
            .when(autenticacaoService)
            .getUsuarioAutenticado();

        var usuario = umUsuarioCompleto(GERENTE_OPERACAO, 7, CodigoNivel.OPERACAO,
            CodigoDepartamento.COMERCIAL, ECanal.D2D_PROPRIO);
        usuario.setSubCanais(Set.of(new SubCanal(1)));
        assertThatCode(() -> service.save(usuario))
            .doesNotThrowAnyException();

        verify(repository, times(1)).saveAndFlush(any());
    }

    @Test
    public void save_naoDeveLancarException_quandoUsuarioPossuiCargoForaVerificacao() {
        var usuarioSalvo = umUsuarioCompleto(OPERACAO_CONSULTOR, 3,
            OPERACAO, CodigoDepartamento.COMERCIAL,
            ECanal.D2D_PROPRIO);
        usuarioSalvo.setSubCanais(Set.of(new SubCanal(1)));
        when(repository.findById(any()))
            .thenReturn(Optional.of(usuarioSalvo));

        doReturn(umUsuarioAutenticadoNivelBackoffice())
            .when(autenticacaoService)
            .getUsuarioAutenticado();

        var usuario = umUsuarioCompleto(GERENTE_OPERACAO, 7,
            CodigoNivel.OPERACAO, CodigoDepartamento.COMERCIAL,
            ECanal.D2D_PROPRIO);
        usuario.setSubCanais(Set.of(new SubCanal(1)));
        assertThatCode(() -> service.save(usuario))
            .doesNotThrowAnyException();

        verify(equipeVendaD2dService, never()).getEquipeVendasBySupervisorId(any());
        verify(equipeVendasUsuarioService, never()).buscarUsuarioEquipeVendasPorId(any());
        verify(repository, times(1)).saveAndFlush(any());
    }

    @Test
    public void save_naoDeveLancarException_quandoUsuarioPossuiDepartamentoForaVerificacao() {
        var usuarioSalvo = umUsuarioCompleto(ASSISTENTE_OPERACAO, 2, OPERACAO,
            CodigoDepartamento.AGENTE_AUTORIZADO,
            ECanal.D2D_PROPRIO);
        usuarioSalvo.setSubCanais(Set.of(new SubCanal(1)));
        when(repository.findById(any()))
            .thenReturn(Optional.of(usuarioSalvo));

        doReturn(umUsuarioAutenticadoNivelBackoffice())
            .when(autenticacaoService)
            .getUsuarioAutenticado();

        var usuario = umUsuarioCompleto(COORDENADOR_OPERACAO, 8,
            CodigoNivel.OPERACAO, CodigoDepartamento.AGENTE_AUTORIZADO,
            ECanal.D2D_PROPRIO);
        usuario.setSubCanais(Set.of(new SubCanal(1)));

        assertThatCode(() -> service.save(usuario))
            .doesNotThrowAnyException();

        verify(equipeVendasUsuarioService, never()).buscarUsuarioEquipeVendasPorId(any());
        verify(repository, times(1)).saveAndFlush(any());
    }

    @Test
    public void save_naoDeveLancarException_quandoUsuarioPossuiCanalForaVerificacao() {
        when(repository.findById(any()))
            .thenReturn(Optional.of(umUsuarioCompleto(ASSISTENTE_OPERACAO, 2, OPERACAO,
                CodigoDepartamento.COMERCIAL, ECanal.ATIVO_PROPRIO)));

        doReturn(umUsuarioAutenticadoNivelBackoffice())
            .when(autenticacaoService)
            .getUsuarioAutenticado();

        assertThatCode(() -> service.save(
            umUsuarioCompleto(COORDENADOR_OPERACAO, 8,
                CodigoNivel.OPERACAO, CodigoDepartamento.AGENTE_AUTORIZADO, ECanal.ATIVO_PROPRIO)))
            .doesNotThrowAnyException();

        verify(equipeVendasUsuarioService, never()).buscarUsuarioEquipeVendasPorId(any());
        verify(repository, times(1)).saveAndFlush(any());
    }

    @Test
    public void save_naoDeveLancarException_quandoUsuarioComSubCanalPapPremiumNivelOperacao() {
        var usuarioSalvo = umUsuarioCompleto(VENDEDOR_OPERACAO, 8, OPERACAO,
            CodigoDepartamento.COMERCIAL, ECanal.D2D_PROPRIO);
        usuarioSalvo.setSubCanais(Set.of(new SubCanal(2)));
        when(repository.findById(any()))
            .thenReturn(Optional.of(usuarioSalvo));
        doReturn(umUsuarioAutenticadoAdmin(1))
            .when(autenticacaoService)
            .getUsuarioAutenticado();

        var usuario = umUsuarioCompleto(VENDEDOR_OPERACAO, 8, CodigoNivel.OPERACAO,
            CodigoDepartamento.COMERCIAL, ECanal.D2D_PROPRIO);
        usuario.setSubCanais(Set.of(new SubCanal(3)));
        assertThatCode(() -> service.save(usuario))
            .doesNotThrowAnyException();

        verify(equipeVendasUsuarioService, never()).buscarUsuarioEquipeVendasPorId(any());
        verify(subCanalService, times(1)).removerPermissaoIndicacaoPremium(any());
        verify(repository, times(1)).saveAndFlush(any());
        verify(subCanalService, times(1)).adicionarPermissaoIndicacaoPremium(any());
    }

    @Test
    public void save_deveAtualizarUsuarioCadastroId_quandoUsuarioPossuiUsuarioCadastroNulo() {
        var usuarioComUsuarioCadastroNulo = umUsuarioMso();
        usuarioComUsuarioCadastroNulo.setUsuarioCadastro(null);

        when(repository.findById(eq(150016)))
            .thenReturn(Optional.of(usuarioComUsuarioCadastroNulo));
        when(autenticacaoService.getUsuarioAutenticadoId())
            .thenReturn(Optional.of(101112));

        doReturn(umUsuarioAutenticadoNivelBackoffice())
            .when(autenticacaoService)
            .getUsuarioAutenticado();

        assertThatCode(() -> service.save(usuarioComUsuarioCadastroNulo))
            .doesNotThrowAnyException();

        verify(autenticacaoService, times(1)).getUsuarioAutenticadoId();
        verify(repository, times(1)).saveAndFlush(eq(umUsuarioMso()));
    }

    @Test
    public void save_naoDeveAtualizarUsuarioCadastroId_quandoUsuarioJaPossuirUsuarioCadastro() {
        when(repository.findById(eq(150016)))
            .thenReturn(Optional.of(umUsuarioMso()));

        doReturn(umUsuarioAutenticadoNivelBackoffice())
            .when(autenticacaoService)
            .getUsuarioAutenticado();

        assertThatCode(() -> service.save(umUsuarioMso()))
            .doesNotThrowAnyException();

        verify(repository, times(1)).saveAndFlush(eq(umUsuarioMso()));
    }

    @Test
    public void save_deveSetarIdDoUsuarioAutenticado_quandoUsuarioAutenticadoForSupervisor() {
        var vendedor = umUsuario();
        vendedor.setSituacao(ESituacao.A);
        vendedor.setUsuariosHierarquia(new HashSet<>());
        vendedor.setEmail("vendedortest@xbrain.com.br");
        vendedor.setCargo(umCargo(1, VENDEDOR_OPERACAO));
        vendedor.setCanais(Set.of(ECanal.D2D_PROPRIO));
        vendedor.setHierarquiasId(List.of(100));

        doReturn(Optional.of(vendedor))
            .when(repository)
            .findById(1);

        when(repository.findById(eq(100))).thenReturn(Optional.of(vendedor));
        when(repository.getCanaisByUsuarioIds(anyList()))
            .thenReturn(List.of(new Canal(100, ECanal.D2D_PROPRIO)));
        doReturn(umUsuarioAutenticado(100, "OPERACAO", SUPERVISOR_OPERACAO, AUT_VISUALIZAR_GERAL))
            .when(autenticacaoService)
            .getUsuarioAutenticado();

        var usuarioDto = (UsuarioDto) service.save(vendedor);
        assertThat(usuarioDto.getHierarquiasId()).isEqualTo(List.of(100));
    }

    @Test
    public void save_deveSetarIdDoUsuarioAutenticado_quandoUsuarioAutenticadoForAssistente() {
        var vendedor = umUsuario();
        vendedor.setSituacao(ESituacao.A);
        vendedor.setUsuariosHierarquia(new HashSet<>());
        vendedor.setEmail("vendedortest@xbrain.com.br");
        vendedor.setCargo(umCargo(1, VENDEDOR_OPERACAO));
        vendedor.setCanais(Set.of(ECanal.D2D_PROPRIO));
        vendedor.setHierarquiasId(List.of(100));

        doReturn(Optional.of(vendedor))
            .when(repository)
            .findById(1);

        when(repository.findById(eq(100))).thenReturn(Optional.of(vendedor));
        when(repository.getCanaisByUsuarioIds(anyList()))
            .thenReturn(List.of(new Canal(100, ECanal.D2D_PROPRIO)));
        doReturn(umUsuarioAutenticado(100, "OPERACAO", ASSISTENTE_OPERACAO, AUT_VISUALIZAR_GERAL))
            .when(autenticacaoService)
            .getUsuarioAutenticado();

        var usuarioDto = (UsuarioDto) service.save(vendedor);
        assertThat(usuarioDto.getHierarquiasId()).isEqualTo(List.of(100));
    }

    @Test
    public void save_deveAdicionarSubNiveisESalvarPermissoesEspeciais_quandoRequestConterSubNiveisIdsECadastroMso() {
        var usuarioDto = umUsuarioMsoBackofficeDto(null);
        usuarioDto.setCargoId(22);
        var subniveis = umSetDeSubNiveisComUmSubNivel();

        when(subNivelService.findByIdIn(Set.of(1))).thenReturn(subniveis);
        when(autenticacaoService.getUsuarioAutenticado()).thenReturn(umUsuarioXBrain());
        when(subNivelService.getSubNivelFuncionalidadesIdsByCargo(anySet(), anyInt())).thenReturn(List.of(1));
        when(repository.saveAndFlush(any(Usuario.class))).thenAnswer(invocation -> {
            Usuario usuarioArgumento = invocation.getArgument(0);
            usuarioArgumento.setId(1);
            return usuarioArgumento;
        });
        assertThatCode(() -> service.save(usuarioDto, null))
            .doesNotThrowAnyException();

        verify(repository).saveAndFlush(any(Usuario.class));
        verify(subNivelService).findByIdIn(Set.of(1));
        verify(permissaoEspecialRepository).save(permissaoEspecialCaptor.capture());
        verify(repository, times(2)).save(usuarioCaptor.capture());
        verify(permissaoEspecialService).hasPermissaoEspecialAtiva(1, 30000);
        verifyNoMoreInteractions(permissaoEspecialService);

        var usuarioSalvo = usuarioCaptor.getValue();
        assertThat(usuarioSalvo)
            .extracting(Usuario::getId, Usuario::getSubNiveis)
            .containsExactly(1, subniveis);

        var permissoesSalvas = permissaoEspecialCaptor.getValue();
        assertThat(permissoesSalvas)
            .extracting(permissaoEspecial -> permissaoEspecial.getFuncionalidade().getId(),
                permissaoEspecial -> permissaoEspecial.getUsuario().getId(),
                permissaoEspecial -> permissaoEspecial.getUsuarioCadastro().getId())
            .containsExactly(
                tuple(1, 1, 1));
    }

    @Test
    public void save_naoDeveAdicionarSubNiveisENaoDeveSalvarPermissoesEspeciais_quandoRequestNaoConterSubNiveisIdsECadastroMso() {
        var usuarioDto = umUsuarioMsoBackofficeDto(null);
        usuarioDto.setSubNiveisIds(Set.of());

        when(autenticacaoService.getUsuarioAutenticado()).thenReturn(umUsuarioXBrain());
        when(repository.saveAndFlush(any(Usuario.class))).thenAnswer(invocation -> {
            Usuario usuarioArgumento = invocation.getArgument(0);
            usuarioArgumento.setId(1);
            return usuarioArgumento;
        });
        assertThatCode(() -> service.save(usuarioDto, null))
            .doesNotThrowAnyException();

        verify(repository).saveAndFlush(any(Usuario.class));
        verify(repository, times(2)).save(usuarioCaptor.capture());
        verify(permissaoEspecialService).hasPermissaoEspecialAtiva(1, 30000);
        verifyZeroInteractions(subNivelService);
        verifyNoMoreInteractions(permissaoEspecialService);

        var usuarioSalvo = usuarioCaptor.getValue();
        assertThat(usuarioSalvo)
            .extracting(Usuario::getId, Usuario::getSubNiveis)
            .containsExactly(1, null);
    }

    @Test
    @SuppressWarnings("LineLength")
    public void save_deveAdicionarOsSubNiveisEAsPermissoesEspeciais_quandoForEditarMsoEAdicionarAlgumSubNivelSemOUsuarioPossuirAlgumAnteriormente() {
        var usuarioDto = umUsuarioMsoBackofficeDto(23);
        usuarioDto.setSubNiveisIds(Set.of(2, 3));
        usuarioDto.setSituacao(A);
        usuarioDto.setCargoId(20);
        var usuarioAntigo = umUsuarioMsoConsultor(1, PAP);
        usuarioAntigo.setSituacao(A);
        var subniveis = umSetDeSubNiveis();

        when(subNivelService.getFuncionalidadesIdsByNivel(2)).thenReturn(List.of(1, 2, 3));
        when(repository.findById(23)).thenReturn(Optional.of(usuarioAntigo));
        when(subNivelService.findByIdIn(Set.of(2, 3))).thenReturn(subniveis);
        when(autenticacaoService.getUsuarioAutenticado()).thenReturn(umUsuarioXBrain());
        when(subNivelService.getSubNivelFuncionalidadesIdsByCargo(anySet(), anyInt())).thenReturn(List.of(2, 3));

        assertThatCode(() -> service.save(usuarioDto, null))
            .doesNotThrowAnyException();

        verify(repository).saveAndFlush(any(Usuario.class));
        verify(subNivelService).findByIdIn(Set.of(2, 3));
        verify(subNivelService).getFuncionalidadesIdsByNivel(2);
        verify(permissaoEspecialService).deletarPermissoesEspeciaisBy(List.of(1, 2, 3), List.of(23));
        verify(permissaoEspecialRepository).save(permissaoEspecialCaptor.capture());
        verify(repository, times(2)).save(usuarioCaptor.capture());

        var usuarioSalvo = usuarioCaptor.getValue();
        assertThat(usuarioSalvo)
            .extracting(Usuario::getId, Usuario::getSubNiveis)
            .containsExactly(23, subniveis);

        var permissoesSalvas = permissaoEspecialCaptor.getValue();
        assertThat(permissoesSalvas)
            .extracting(permissaoEspecial -> permissaoEspecial.getFuncionalidade().getId(),
                permissaoEspecial -> permissaoEspecial.getUsuario().getId(),
                permissaoEspecial -> permissaoEspecial.getUsuarioCadastro().getId())
            .containsExactly(
                tuple(2, 23, 1),
                tuple(3, 23, 1));
    }

    @Test
    @SuppressWarnings("LineLength")
    public void save_deveRemoverPermissoesEspeciaisENaoDeveAdicionarNovasPermissoesEspeciais_quandoForEdicaoDeUsuarioMsoParaRemoverSubNivel() {
        var usuarioDto = umUsuarioMsoBackofficeDto(23);
        usuarioDto.setSubNiveisIds(null);
        usuarioDto.setSituacao(A);
        var usuarioAntigo = umUsuarioMsoConsultor(1, PAP);
        usuarioAntigo.setSituacao(A);

        when(subNivelService.getFuncionalidadesIdsByNivel(2)).thenReturn(List.of(1, 2, 3));
        when(autenticacaoService.getUsuarioAutenticado()).thenReturn(umUsuarioXBrain());
        when(repository.findById(23)).thenReturn(Optional.of(usuarioAntigo));

        assertThatCode(() -> service.save(usuarioDto, null))
            .doesNotThrowAnyException();

        verify(repository, times(5)).findById(23);
        verify(subNivelService).getFuncionalidadesIdsByNivel(2);
        verify(autenticacaoService).getUsuarioAutenticado();
        verify(repository).saveAndFlush(any(Usuario.class));
        verify(repository, times(2)).save(usuarioCaptor.capture());
        verify(permissaoEspecialService).deletarPermissoesEspeciaisBy(List.of(1, 2, 3), List.of(23));
        verifyZeroInteractions(permissaoEspecialRepository);
        verifyNoMoreInteractions(subNivelService);

        var usuarioSalvo = usuarioCaptor.getValue();
        assertThat(usuarioSalvo)
            .extracting(Usuario::getId, Usuario::getSubNiveis)
            .containsExactly(23, null);
    }

    @Test
    @SuppressWarnings("LineLength")
    public void save_deveAlterarOsSubNiveisEAlterarAsPermissoesEspeciais_quandoForEditarMsoERequestConterSubNiveisIdsDiferentesDosSubNiveisAnterioresDoUsuario() {
        var usuarioDto = umUsuarioMsoBackofficeDto(23);
        usuarioDto.setSubNiveisIds(Set.of(2, 3));
        usuarioDto.setSituacao(A);
        usuarioDto.setCargoId(23);
        var usuarioAntigo = umUsuarioMsoConsultor(1, PAP);
        usuarioAntigo.setSituacao(A);
        usuarioAntigo.setSubNiveis(umSetDeSubNiveisComUmSubNivel());
        var subniveis = umSetDeSubNiveis();

        when(subNivelService.getFuncionalidadesIdsByNivel(2)).thenReturn(List.of(1, 2, 3));
        when(repository.findById(23)).thenReturn(Optional.of(usuarioAntigo));
        when(subNivelService.findByIdIn(Set.of(2, 3))).thenReturn(subniveis);
        when(autenticacaoService.getUsuarioAutenticado()).thenReturn(umUsuarioXBrain());
        when(subNivelService.getSubNivelFuncionalidadesIdsByCargo(anySet(), anyInt())).thenReturn(List.of(2, 3));

        assertThatCode(() -> service.save(usuarioDto, null))
            .doesNotThrowAnyException();

        verify(repository).saveAndFlush(any(Usuario.class));
        verify(subNivelService).findByIdIn(Set.of(2, 3));
        verify(subNivelService).getFuncionalidadesIdsByNivel(2);
        verify(permissaoEspecialService).deletarPermissoesEspeciaisBy(List.of(1, 2, 3), List.of(23));
        verify(permissaoEspecialRepository).save(permissaoEspecialCaptor.capture());
        verify(repository, times(2)).save(usuarioCaptor.capture());

        var usuarioSalvo = usuarioCaptor.getValue();
        assertThat(usuarioSalvo)
            .extracting(Usuario::getId, Usuario::getSubNiveis)
            .containsExactly(23, subniveis);

        var permissoesSalvas = permissaoEspecialCaptor.getValue();
        assertThat(permissoesSalvas)
            .extracting(permissaoEspecial -> permissaoEspecial.getFuncionalidade().getId(),
                permissaoEspecial -> permissaoEspecial.getUsuario().getId(),
                permissaoEspecial -> permissaoEspecial.getUsuarioCadastro().getId())
            .containsExactly(
                tuple(2, 23, 1),
                tuple(3, 23, 1));
    }

    @Test
    public void buscarTodosVendedoresReceptivos_deveRetornarVendedoresReceptivoComoSelectResponse_quandoValido() {
        when(repository.findAllVendedoresReceptivos())
            .thenReturn(List.of(umVendedorReceptivo()));

        var vendedores = service.buscarTodosVendedoresReceptivos();
        verify(repository, times(1)).findAllVendedoresReceptivos();
        assertThat(vendedores.stream().allMatch(this::isSelectResponse)).isTrue();
    }

    @Test
    public void buscarTodosVendedoresReceptivos_retornarVendedorReceptivoNomeComInativo_quandoTerUsuarioInativo() {
        var vendedorReceptivoInativo = umVendedorReceptivo();
        vendedorReceptivoInativo.setSituacao(ESituacao.I);
        when(repository.findAllVendedoresReceptivos())
            .thenReturn(List.of(vendedorReceptivoInativo));

        var vendedores = service.buscarTodosVendedoresReceptivos();
        verify(repository, times(1)).findAllVendedoresReceptivos();
        assertThat(vendedores.stream().allMatch(this::isSelectResponse)).isTrue();
        assertThat(vendedores).contains(umSelectResponseDeVendedorReceptivoInativo());
    }

    @Test
    public void buscarTodosVendedoresReceptivos_retornarVendedorReceptivoNomeComRealocado_quandoTerUsuarioRealocado() {
        var vendededorReceptivoRealocado = umVendedorReceptivo();
        vendededorReceptivoRealocado.setSituacao(ESituacao.R);

        when(repository.findAllVendedoresReceptivos())
            .thenReturn(List.of(vendededorReceptivoRealocado));

        var vendedores = service.buscarTodosVendedoresReceptivos();
        verify(repository, times(1)).findAllVendedoresReceptivos();
        assertThat(vendedores.stream().allMatch(this::isSelectResponse)).isTrue();
        assertThat(vendedores).contains(umSelectResponseDeVendedorReceptivoRealocado());
    }

    private boolean isSelectResponse(Object obj) {
        return obj instanceof SelectResponse;
    }

    @Test
    public void buscarVendedoresReceptivosPorId_deverRetornarUsuarioVendedorReceptivoResponse_quandoValido() {
        when(repository.findAllVendedoresReceptivosByIds(anyList())).thenReturn(List.of(umVendedorReceptivo()));
        var vendedores = service.buscarVendedoresReceptivosPorId(List.of(1));
        assertThat(vendedores).extracting("nome", "email", "loginNetSales", "nivel", "organizacao")
            .containsExactly(
                tuple(
                    umVendedorReceptivo().getNome(),
                    umVendedorReceptivo().getEmail(),
                    umVendedorReceptivo().getLoginNetSales(),
                    umVendedorReceptivo().getNivelNome(),
                    umVendedorReceptivo().getOrganizacaoEmpresa().getDescricao()));
    }

    @Test
    public void buscarVendedoresReceptivosPorId_deverRetornarUsuarioVendedorReceptivo_quandoTiverVendedorInativo() {
        var vendedorReceptivo = umVendedorReceptivo();
        vendedorReceptivo.setSituacao(ESituacao.I);
        when(repository.findAllVendedoresReceptivosByIds(anyList())).thenReturn(List.of((vendedorReceptivo)));
        var vendedores = service.buscarVendedoresReceptivosPorId(List.of(1));
        assertThat(vendedores).extracting("nome", "email", "loginNetSales", "nivel", "organizacao")
            .containsExactly(
                tuple(
                    umVendedorReceptivo().getNome() + " (INATIVO)",
                    umVendedorReceptivo().getEmail(),
                    umVendedorReceptivo().getLoginNetSales(),
                    umVendedorReceptivo().getNivelNome(),
                    umVendedorReceptivo().getOrganizacaoEmpresa().getDescricao()));
    }

    @Test
    public void buscarVendedoresReceptivosPorId_deverRetornarUsuarioVendedorReceptivo_quandoTiverVendedorRealocado() {
        var vendedorReceptivo = umVendedorReceptivo();
        vendedorReceptivo.setSituacao(ESituacao.R);
        when(repository.findAllVendedoresReceptivosByIds(anyList())).thenReturn(List.of((vendedorReceptivo)));
        var vendedores = service.buscarVendedoresReceptivosPorId(List.of(1));
        assertThat(vendedores).extracting("nome", "email", "loginNetSales", "nivel", "organizacao")
            .containsExactly(
                tuple(
                    umVendedorReceptivo().getNome() + " (REALOCADO)",
                    umVendedorReceptivo().getEmail(),
                    umVendedorReceptivo().getLoginNetSales(),
                    umVendedorReceptivo().getNivelNome(),
                    umVendedorReceptivo().getOrganizacaoEmpresa().getDescricao()));
    }

    @Test
    public void buscarUsuariosReceptivosIdsPorOrganizacaoId_deverRetornarListaVazia_quandoNaoEncontrarUsuarios() {
        when(repository.findAllUsuariosReceptivosIdsByOrganizacaoId(1))
            .thenReturn(Collections.emptyList());

        assertThat(service.buscarUsuariosReceptivosIdsPorOrganizacaoId(1)).isEmpty();

        verify(repository).findAllUsuariosReceptivosIdsByOrganizacaoId(1);
    }

    @Test
    public void buscarUsuariosReceptivosIdsPorOrganizacaoId_deverRetornarUsuariosIdsDeUmaOrganizacao_quandoSolicitado() {
        when(repository.findAllUsuariosReceptivosIdsByOrganizacaoId(1))
            .thenReturn(List.of(1, 2, 3));

        assertThat(service.buscarUsuariosReceptivosIdsPorOrganizacaoId(1)).isEqualTo(List.of(1, 2, 3));

        verify(repository).findAllUsuariosReceptivosIdsByOrganizacaoId(1);
    }

    @Test
    public void buscarUsuariosDaHierarquiaDoUsuarioLogadoPorFiltros_usuarios_quandoUsuarioDiferenteDeAaEXbrain() {
        var usuarioComPermissaoDeVisualizarAa = umUsuarioAutenticado(1, "AGENTE_AUTORIZADO",
            CodigoCargo.AGENTE_AUTORIZADO_SOCIO, AUT_VISUALIZAR_GERAL);
        when(autenticacaoService.getUsuarioAutenticado()).thenReturn(usuarioComPermissaoDeVisualizarAa);
        when(repository.findAll(any(Predicate.class), any(Sort.class))).thenReturn(umaListaUsuariosExecutivosAtivo());

        assertThat(service.buscarUsuariosDaHierarquiaDoUsuarioLogadoPorFiltros(umUsuarioFiltro()))
            .extracting("label", "value")
            .containsExactly(tuple("JOSÉ", 1),
                tuple("HIGOR", 2));

        verify(repository, times(1))
            .findAll(eq(new UsuarioPredicate()
                .comCanal(ECanal.D2D_PROPRIO)
                .comCodigosCargos(List.of(SUPERVISOR_OPERACAO, ASSISTENTE_OPERACAO))
                .ignorarAa(true)
                .ignorarXbrain(true)
                .build()), eq(new Sort(ASC, "situacao", "nome")));
    }

    @Test
    public void buscarUsuariosDaHierarquiaDoUsuarioLogadoPorFiltros_usuariosDaEquipeComNomeAlterado_quandoUsuarioEquipeVendas() {
        var usuarioEquipeVendas = umUsuarioAutenticado(1, "OPERACAO",
            CodigoCargo.VENDEDOR_OPERACAO, AUT_VISUALIZAR_GERAL);
        when(autenticacaoService.getUsuarioAutenticado()).thenReturn(usuarioEquipeVendas);
        when(equipeVendaD2dService.getUsuariosPermitidos(any())).thenReturn(List.of());
        when(autenticacaoService.getUsuarioId()).thenReturn(3);
        when(repository.getUsuariosSubordinados(any())).thenReturn(new ArrayList<>(List.of(2, 4, 5)));
        when(repository.findAll(any(Predicate.class), any(Sort.class))).thenReturn(umaUsuariosList());

        assertThat(service.buscarUsuariosDaHierarquiaDoUsuarioLogadoPorFiltros(umUsuarioFiltro()))
            .extracting("label", "value")
            .containsExactly(tuple("Caio", 1),
                tuple("Mario (INATIVO)", 2),
                tuple("Maria (REALOCADO)", 3));

        verify(repository, times(1))
            .findAll(eq(new UsuarioPredicate()
                .comCanal(ECanal.D2D_PROPRIO)
                .comCodigosCargos(List.of(SUPERVISOR_OPERACAO, ASSISTENTE_OPERACAO))
                .ignorarAa(true).ignorarXbrain(true)
                .comIds(List.of(3, 2, 4, 5, 1, 1))
                .build()), eq(new Sort(ASC, "situacao", "nome")));
    }

    @Test
    public void buscarUsuariosDaHierarquiaDoUsuarioLogadoPorFiltros_deveRetornarUsuario_quandoUsuarioVendedorInternet() {
        var usuario = umUsuarioAutenticado(1, "OPERACAO",
            INTERNET_VENDEDOR);
        usuario.setCanais(Set.of(ECanal.INTERNET));

        var predicate = new UsuarioPredicate()
            .comCanal(ECanal.INTERNET)
            .comCodigosCargos(List.of(INTERNET_VENDEDOR))
            .ignorarAa(true).ignorarXbrain(true)
            .comIds(List.of(1))
            .build();

        var sort = new Sort(ASC, "situacao", "nome");

        doReturn(usuario)
            .when(autenticacaoService)
            .getUsuarioAutenticado();
        doReturn(List.of(umUsuarioVendedorInternet()))
            .when(repository)
            .findAll(predicate, sort);

        assertThat(service.buscarUsuariosDaHierarquiaDoUsuarioLogadoPorFiltros(umUsuarioFiltroInternet()))
            .extracting("label", "value")
            .containsExactly(tuple("VENDEDOR", 5436278));

        verify(repository).findAll(predicate, sort);
        verify(autenticacaoService).getUsuarioAutenticado();
        verify(repository, never()).getIdsUsuariosHierarquiaPorCargos(anySet());
    }

    @Test
    public void buscarUsuariosDaHierarquiaDoUsuarioLogadoPorFiltros_deveRetornarUsuario_quandoUsuarioBackofficeInternet() {
        var usuario = umUsuarioAutenticado(1, "OPERACAO",
            INTERNET_BACKOFFICE);
        usuario.setOrganizacaoId(1);
        usuario.setCanais(Set.of(ECanal.INTERNET));

        var predicate = new UsuarioPredicate()
            .comCanal(ECanal.INTERNET)
            .comCodigosCargos(List.of(INTERNET_VENDEDOR))
            .ignorarAa(true).ignorarXbrain(true)
            .comOrganizacaoEmpresaId(1)
            .comIds(List.of(5436278, 1))
            .build();

        var sort = new Sort(ASC, "situacao", "nome");

        doReturn(usuario)
            .when(autenticacaoService)
            .getUsuarioAutenticado();
        doReturn(List.of(umUsuarioVendedorInternet()))
            .when(repository)
            .findAll(predicate, sort);
        doReturn(List.of(5436278))
            .when(repository)
            .getIdsUsuariosHierarquiaPorCargos(anySet());

        assertThat(service.buscarUsuariosDaHierarquiaDoUsuarioLogadoPorFiltros(umUsuarioFiltroInternet()))
            .extracting("label", "value")
            .containsExactly(tuple("VENDEDOR", 5436278));

        verify(repository).findAll(predicate, sort);
        verify(autenticacaoService).getUsuarioAutenticado();
        verify(repository).getIdsUsuariosHierarquiaPorCargos(anySet());
    }

    @Test
    public void getUsuariosDaHierarquiaAtivoLocalDoUsuarioLogado_deveRetornarUsuarios_seEncontrado() {
        when(autenticacaoService.getUsuarioAutenticado())
            .thenReturn(umUsuarioAutenticadoNivelMso());
        when(repository.findAll(eq(
            new UsuarioPredicate().filtraPermitidos(
                    autenticacaoService.getUsuarioAutenticado(), service, true)
                .build())))
            .thenReturn(umaUsuariosList());

        assertThat(service.getUsuariosDaHierarquiaAtivoLocalDoUsuarioLogado())
            .isEqualTo(umaUsuariosList());
    }

    @Test
    public void getUsuariosDaHierarquiaAtivoLocalDoUsuarioLogado_naoDeveRetornarUsuarios_seNaoEncontrado() {
        when(autenticacaoService.getUsuarioAutenticado())
            .thenReturn(umUsuarioAutenticadoNivelMso());
        when(repository.findAll(eq(
            new UsuarioPredicate().filtraPermitidos(
                    autenticacaoService.getUsuarioAutenticado(), service, true)
                .build())))
            .thenReturn(Collections.emptyList());

        assertThat(service.getUsuariosDaHierarquiaAtivoLocalDoUsuarioLogado())
            .isEmpty();
    }

    @Test
    public void getUsuariosPermitidosPelaEquipeDeVenda_deveBuscarPorCargosDoAtivo_seCanalForAtivoLocal() {
        when(autenticacaoService.getUsuarioCanal()).thenReturn(ECanal.ATIVO_PROPRIO);

        service.getUsuariosPermitidosPelaEquipeDeVenda();

        verify(equipeVendaD2dService, times(1))
            .getUsuariosPermitidos(eq(List.of(SUPERVISOR_OPERACAO, ASSISTENTE_OPERACAO, OPERACAO_TELEVENDAS)));
    }

    @Test
    public void getUsuariosPermitidosPelaEquipeDeVenda_deveBuscarPorCargosDoD2d_seCanalForD2d() {
        when(autenticacaoService.getUsuarioCanal()).thenReturn(ECanal.D2D_PROPRIO);

        service.getUsuariosPermitidosPelaEquipeDeVenda();

        verify(equipeVendaD2dService, times(1))
            .getUsuariosPermitidos(eq(List.of(SUPERVISOR_OPERACAO, ASSISTENTE_OPERACAO, VENDEDOR_OPERACAO)));
    }

    @Test
    public void findByUsuarioId_deveRetornarUsuarioSubCanalNivelResponse_seUsuarioExistir() {
        var usuario = umUsuarioCompleto(OPERACAO_TELEVENDAS, 120,
            OPERACAO, CodigoDepartamento.COMERCIAL, ECanal.D2D_PROPRIO);
        usuario.setSubCanais(Set.of(umSubCanal()));

        when(repository.findById(1)).thenReturn(Optional.of(usuario));

        assertThat(service.findByUsuarioId(1))
            .extracting("id", "nome", "nivel", "subCanais")
            .containsExactly(1, "NOME UM", OPERACAO, Set.of(umSubCanalDto(1, PAP, "PAP")));

        verify(repository, times(1)).findById(eq(1));
    }

    @Test
    public void findByUsuarioId_deveLancarNotFoundException_seUsuarioNaoExistir() {
        assertThatExceptionOfType(NotFoundException.class)
            .isThrownBy(() -> service.findByUsuarioId(1))
            .withMessage("O usuário 1 não foi encontrado.");

        verify(repository, times(1)).findById(eq(1));
    }

    @Test
    public void validarVinculoDoUsuarioNaEquipeVendasComSubCanal_naoDeveLancarValidacaoException_quandoUsuarioDtoIdForNull() {
        var usuarioDto = UsuarioDto.builder()
            .id(null)
            .build();

        assertThatCode(() -> service.validarVinculoDoUsuarioNaEquipeVendasComSubCanal(usuarioDto))
            .doesNotThrowAnyException();

        verify(equipeVendaD2dService, never()).getSubCanaisDaEquipeVendaD2dByUsuarioId(anyInt());
    }

    @Test
    @SuppressWarnings("LineLength")
    public void validarVinculoDoUsuarioNaEquipeVendasComSubCanal_naoDeveLancarValidacaoException_quandoCargoCodigoUsuarioDtoForNull() {
        var usuarioDto = UsuarioDto.builder()
            .id(1660123)
            .cargoCodigo(null)
            .build();

        assertThatCode(() -> service.validarVinculoDoUsuarioNaEquipeVendasComSubCanal(usuarioDto))
            .doesNotThrowAnyException();

        verify(equipeVendaD2dService, never()).getSubCanaisDaEquipeVendaD2dByUsuarioId(anyInt());
    }

    @Test
    @SuppressWarnings("LineLength")
    public void validarVinculoDoUsuarioNaEquipeVendasComSubCanal_naoDeveLancarValidacaoException_quandoUsuarioDtoComCanalD2dEListaSubCanaisIdVazia() {
        var usuarioDto = UsuarioDto.builder()
            .id(1660123)
            .cargoCodigo(VENDEDOR_OPERACAO)
            .canais(Set.of(ECanal.D2D_PROPRIO))
            .subCanaisId(Set.of())
            .build();

        assertThatCode(() -> service.validarVinculoDoUsuarioNaEquipeVendasComSubCanal(usuarioDto))
            .doesNotThrowAnyException();

        verify(equipeVendaD2dService, never()).getSubCanaisDaEquipeVendaD2dByUsuarioId(anyInt());
    }

    @Test
    @SuppressWarnings("LineLength")
    public void validarVinculoDoUsuarioNaEquipeVendasComSubCanal_naoDeveLancarValidacaoException_quandoUsuarioDtoSemCanalD2dComListaSubCanaisId() {
        var usuarioDto = UsuarioDto.builder()
            .id(1660123)
            .cargoCodigo(VENDEDOR_OPERACAO)
            .canais(Set.of(ECanal.ATIVO_PROPRIO))
            .subCanaisId(Set.of(1))
            .build();

        assertThatCode(() -> service.validarVinculoDoUsuarioNaEquipeVendasComSubCanal(usuarioDto))
            .doesNotThrowAnyException();

        verify(equipeVendaD2dService, never()).getSubCanaisDaEquipeVendaD2dByUsuarioId(anyInt());
    }

    @Test
    @SuppressWarnings("LineLength")
    public void validarVinculoDoUsuarioNaEquipeVendasComSubCanal_naoDeveLancarValidacaoException_quandoCargoCodigoUsuarioDtoNaoEstiverNaListaDeCargosEquipeD2d() {
        var usuarioDto = UsuarioDto.builder()
            .id(1660123)
            .cargoCodigo(DIRETOR_OPERACAO)
            .canais(Set.of(ECanal.D2D_PROPRIO))
            .subCanaisId(Set.of(1, 2, 3, 4))
            .build();

        assertThatCode(() -> service.validarVinculoDoUsuarioNaEquipeVendasComSubCanal(usuarioDto))
            .doesNotThrowAnyException();

        verify(equipeVendaD2dService, never()).getSubCanaisDaEquipeVendaD2dByUsuarioId(anyInt());
    }

    @Test
    @SuppressWarnings("LineLength")
    public void validarVinculoDoUsuarioNaEquipeVendasComSubCanal_naoDeveLancarValidacaoException_quandoUsuarioDtoComCanalD2dEClientEquipeVendasRetornarListaVazia() {
        var usuarioDto = UsuarioDto.builder()
            .id(1660123)
            .cargoCodigo(SUPERVISOR_OPERACAO)
            .canais(Set.of(ECanal.D2D_PROPRIO))
            .subCanaisId(Set.of(3))
            .build();

        when(equipeVendaD2dService.getSubCanaisDaEquipeVendaD2dByUsuarioId(usuarioDto.getId())).thenReturn(List.of());

        assertThatCode(() -> service.validarVinculoDoUsuarioNaEquipeVendasComSubCanal(usuarioDto))
            .doesNotThrowAnyException();

        verify(equipeVendaD2dService, times(1)).getSubCanaisDaEquipeVendaD2dByUsuarioId(eq(usuarioDto.getId()));
    }

    @Test
    @SuppressWarnings("LineLength")
    public void validarVinculoDoUsuarioNaEquipeVendasComSubCanal_naoDeveLancarValidacaoException_quandoUsuarioDtoSemCanalD2dClientEquipeVendasRetornarListaVazia() {
        var usuarioDto = UsuarioDto.builder()
            .id(1660123)
            .cargoCodigo(SUPERVISOR_OPERACAO)
            .canais(Set.of(ECanal.ATIVO_PROPRIO))
            .subCanaisId(Set.of())
            .build();

        when(equipeVendaD2dService.getSubCanaisDaEquipeVendaD2dByUsuarioId(usuarioDto.getId())).thenReturn(List.of());

        assertThatCode(() -> service.validarVinculoDoUsuarioNaEquipeVendasComSubCanal(usuarioDto))
            .doesNotThrowAnyException();

        verify(equipeVendaD2dService, times(1)).getSubCanaisDaEquipeVendaD2dByUsuarioId(eq(usuarioDto.getId()));
    }

    @Test
    @SuppressWarnings("LineLength")
    public void validarVinculoDoUsuarioNaEquipeVendasComSubCanal_naoDeveLancarValidacaoException_quandoUsuarioEstiverEmEquipeVendasComMesmoSubCanal() {
        var usuarioDto = UsuarioDto.builder()
            .id(1660123)
            .cargoCodigo(SUPERVISOR_OPERACAO)
            .canais(Set.of(ECanal.D2D_PROPRIO))
            .subCanaisId(Set.of(3))
            .build();

        when(equipeVendaD2dService.getSubCanaisDaEquipeVendaD2dByUsuarioId(usuarioDto.getId())).thenReturn(List.of(3));

        assertThatCode(() -> service.validarVinculoDoUsuarioNaEquipeVendasComSubCanal(usuarioDto))
            .doesNotThrowAnyException();

        verify(equipeVendaD2dService, times(1)).getSubCanaisDaEquipeVendaD2dByUsuarioId(eq(usuarioDto.getId()));
    }

    @Test
    @SuppressWarnings("LineLength")
    public void validarVinculoDoUsuarioNaEquipeVendasComSubCanal_deveLancarValidacaoException_quandoUsuarioDtoComCanalD2dEComApenasUmSubCanalEstiverEmUmaEquipeVendasComOutroSubCanal() {
        var usuarioDto = UsuarioDto.builder()
            .id(1660123)
            .cargoCodigo(SUPERVISOR_OPERACAO)
            .canais(Set.of(ECanal.D2D_PROPRIO))
            .subCanaisId(Set.of(2))
            .build();

        when(equipeVendaD2dService.getSubCanaisDaEquipeVendaD2dByUsuarioId(usuarioDto.getId())).thenReturn(List.of(3));

        assertThatExceptionOfType(ValidacaoException.class)
            .isThrownBy(() -> service.validarVinculoDoUsuarioNaEquipeVendasComSubCanal(usuarioDto))
            .withMessage("Não foi possível editar o usuário, pois ele possui vínculo com equipe(s) com outro subcanal.");

        verify(equipeVendaD2dService, times(1)).getSubCanaisDaEquipeVendaD2dByUsuarioId(eq(usuarioDto.getId()));
    }

    @Test
    @SuppressWarnings("LineLength")
    public void validarVinculoDoUsuarioNaEquipeVendasComSubCanal_deveLancarValidacaoException_quandoUsuarioDtoComCanalD2dEComMaisDeUmSubCanalEstiverEmUmaEquipeVendasComOutroSubCanal() {
        var usuarioDto = UsuarioDto.builder()
            .id(1660123)
            .cargoCodigo(COORDENADOR_OPERACAO)
            .canais(Set.of(ECanal.D2D_PROPRIO))
            .subCanaisId(Set.of(2, 3, 4))
            .build();

        when(equipeVendaD2dService.getSubCanaisDaEquipeVendaD2dByUsuarioId(usuarioDto.getId())).thenReturn(List.of(1, 3));

        assertThatExceptionOfType(ValidacaoException.class)
            .isThrownBy(() -> service.validarVinculoDoUsuarioNaEquipeVendasComSubCanal(usuarioDto))
            .withMessage("Não foi possível editar o usuário, pois ele possui vínculo com equipe(s) com outro subcanal.");

        verify(equipeVendaD2dService, times(1)).getSubCanaisDaEquipeVendaD2dByUsuarioId(eq(usuarioDto.getId()));
    }

    @Test
    @SuppressWarnings("LineLength")
    public void validarVinculoDoUsuarioNaEquipeVendasComSubCanal_deveLancarValidacaoException_quandoUsuarioDtoSemCanalD2dEstiverEmUmaEquipeVendasDoCanalD2d() {
        var usuarioDto = UsuarioDto.builder()
            .id(1660123)
            .cargoCodigo(SUPERVISOR_OPERACAO)
            .canais(Set.of(ECanal.ATIVO_PROPRIO))
            .subCanaisId(Set.of())
            .build();

        when(equipeVendaD2dService.getSubCanaisDaEquipeVendaD2dByUsuarioId(usuarioDto.getId())).thenReturn(List.of(1));

        assertThatExceptionOfType(ValidacaoException.class)
            .isThrownBy(() -> service.validarVinculoDoUsuarioNaEquipeVendasComSubCanal(usuarioDto))
            .withMessage("Não foi possível editar o usuário, pois ele possui vínculo com equipe(s) do Canal D2D PRÓPRIO.");

        verify(equipeVendaD2dService, times(1)).getSubCanaisDaEquipeVendaD2dByUsuarioId(eq(usuarioDto.getId()));
    }

    @Test
    public void getUsuariosOperacaoCanalAa_deveRetornarListaUsuariosCanalOpEnivelAa() {
        var codigoNivel = OPERACAO;
        when(repository.getUsuariosOperacaoCanalAa(eq(codigoNivel)))
            .thenReturn(List.of(outroUsuarioNivelOpCanalAa()));

        assertThat(service.getUsuariosOperacaoCanalAa(codigoNivel))
            .containsExactly(outroUsuarioNivelOpCanalAaResponse());
    }

    @Test
    public void findByCpf_deveRetornarUsuarioSubCanalNivelResponse_seUsuarioExistir() {
        var usuario = umUsuarioCompleto(OPERACAO_TELEVENDAS, 120,
            OPERACAO, CodigoDepartamento.COMERCIAL, ECanal.D2D_PROPRIO);
        usuario.setSubCanais(Set.of(umSubCanal()));

        when(repository.findTop1UsuarioByCpf(any())).thenReturn(Optional.of(usuario));

        assertThat(service.findByCpf("11122233344"))
            .extracting("id", "nome", "nivel", "subCanais")
            .containsExactly(1, "NOME UM", OPERACAO, Set.of(umSubCanalDto(1, PAP, "PAP")));

        verify(repository, times(1)).findTop1UsuarioByCpf(anyString());
    }

    @Test
    public void findByCpf_deveRetornarNovoObjeto_seUsuarioNaoExistir() {
        assertThat(service.findByCpf("00000000000")).isEqualTo(new UsuarioSubCanalNivelResponse());

        verify(repository, times(1)).findTop1UsuarioByCpf(anyString());
    }

    @Test
    public void getSubordinadosAndAasDoUsuario_deveRetornarValidacaoException_quandoUsuarioNaoEncontrado() {
        when(autenticacaoService.getUsuarioId()).thenReturn(1);
        when(repository.findById(1)).thenReturn(Optional.empty());

        assertThatExceptionOfType(ValidacaoException.class)
            .isThrownBy(() -> service.getSubordinadosAndAasDoUsuario(true))
            .withMessage("O usuário não foi encontrado.");

        verify(autenticacaoService, times(1)).getUsuarioId();
        verify(repository, times(1)).findById(eq(1));
        verify(repository, never()).getUsuariosCompletoSubordinados(any());
        verify(agenteAutorizadoService, never()).findAgentesAutorizadosByUsuariosIds(anyList(), anyBoolean());
    }

    @Test
    public void getSubordinadosAndAasDoUsuario_deveRetornarIntegracaoException_quandoNaoPuderRecuperarAas() {
        when(autenticacaoService.getUsuarioId()).thenReturn(1);
        when(repository.findById(1)).thenReturn(Optional.of(umUsuarioTopHierarquia()));
        when(agenteAutorizadoService.findAgentesAutorizadosByUsuariosIds(List.of(1), true))
            .thenThrow(new IntegracaoException(ERRO_BUSCAR_TODOS_AAS_DO_USUARIO.getDescricao()));

        assertThatExceptionOfType(IntegracaoException.class)
            .isThrownBy(() -> service.getSubordinadosAndAasDoUsuario(true))
            .withMessage(ERRO_BUSCAR_TODOS_AAS_DO_USUARIO.getDescricao());

        verify(autenticacaoService, times(1)).getUsuarioId();
        verify(repository, times(1)).findById(eq(1));
        verify(repository, times(1)).getUsuariosCompletoSubordinados(eq(1));
        verify(agenteAutorizadoService, times(1))
            .findAgentesAutorizadosByUsuariosIds(eq(List.of(1)), eq(true));
    }

    @Test
    @SuppressWarnings("LineLength")
    public void getSubordinadosAndAasDoUsuario_deveRetornarSubordinadosAtivosAndInativos_quandoIncluirInativosTrue() {
        when(autenticacaoService.getUsuarioId()).thenReturn(1);
        when(repository.findById(1)).thenReturn(Optional.of(umUsuarioTopHierarquia()));
        when(repository.getUsuariosCompletoSubordinados(1))
            .thenReturn(List.of(usuarioSubordinadoDtoDtoResponse(22),
                umOutroUsuarioSubordinadoDtoDtoResponse(33)));
        when(agenteAutorizadoService.findAgentesAutorizadosByUsuariosIds(List.of(22, 33, 1), true))
            .thenReturn(umaListaDeAgenteAutorizadoResponse());

        assertThat(service.getSubordinadosAndAasDoUsuario(true))
            .extracting("id", "cpf", "cnpj", "razaoSocialNome", "situacao")
            .containsExactlyInAnyOrder(
                tuple(1, "097.238.645-92", null, "Seiya", "Ativo"),
                tuple(22, "12345678911", null, "Uma nome", "Ativo"),
                tuple(33, "98765432111", null, "Uma outro nome", "Inativo"),
                tuple(1, null, "00.000.0000/0001-00", "TESTE AA", "CONTRATO ATIVO"),
                tuple(3, null, "00.000.0000/0001-30", "TESTE AA INATIVO", "INATIVO"),
                tuple(4, null, "00.000.0000/0001-40", "TESTE AA REJEITADO", "REJEITADO"),
                tuple(2, null, "00.000.0000/0001-20", "OUTRO TESTE AA", "CONTRATO ATIVO")
            );

        verify(autenticacaoService, times(1)).getUsuarioId();
        verify(repository, times(1)).findById(eq(1));
        verify(repository, times(1))
            .getUsuariosCompletoSubordinados(eq(1));
        verify(agenteAutorizadoService, times(1))
            .findAgentesAutorizadosByUsuariosIds(eq(List.of(22, 33, 1)), eq(true));
    }

    @Test
    @SuppressWarnings("LineLength")
    public void getSubordinadosAndAasDoUsuario_deveRetornarSubordinadosAtivos_quandoIncluirInativosTrue() {
        when(autenticacaoService.getUsuarioId()).thenReturn(1);
        when(repository.findById(1)).thenReturn(Optional.of(umUsuarioTopHierarquia()));
        when(repository.getUsuariosCompletoSubordinados(1))
            .thenReturn(List.of(usuarioSubordinadoDtoDtoResponse(22),
                umOutroUsuarioSubordinadoDtoDtoResponse(33)));
        when(agenteAutorizadoService.findAgentesAutorizadosByUsuariosIds(List.of(22, 33, 1), true))
            .thenReturn(umaListaDeAgenteAutorizadoResponse());

        assertThat(service.getSubordinadosAndAasDoUsuario(false))
            .extracting("id", "cpf", "cnpj", "razaoSocialNome", "situacao")
            .containsExactlyInAnyOrder(
                tuple(1, "097.238.645-92", null, "Seiya", "Ativo"),
                tuple(22, "12345678911", null, "Uma nome", "Ativo"),
                tuple(1, null, "00.000.0000/0001-00", "TESTE AA", "CONTRATO ATIVO"),
                tuple(2, null, "00.000.0000/0001-20", "OUTRO TESTE AA", "CONTRATO ATIVO")
            );

        verify(autenticacaoService, times(1)).getUsuarioId();
        verify(repository, times(1)).findById(eq(1));
        verify(repository, times(1))
            .getUsuariosCompletoSubordinados(eq(1));
        verify(agenteAutorizadoService, times(1))
            .findAgentesAutorizadosByUsuariosIds(eq(List.of(22, 33, 1)), eq(true));
    }

    @Test
    public void getIdDosUsuariosParceiros_deveRetornarIds_quandoExisteremParaRegionalIdInformada() {
        when(agenteAutorizadoService.getIdsUsuariosSubordinadosByFiltros(any(PublicoAlvoComunicadoFiltros.class)))
            .thenReturn(List.of(1, 2));
        var filtros = PublicoAlvoComunicadoFiltros.builder().regionalId(1027).build();
        assertThat(service.getIdDosUsuariosParceiros(filtros)).isEqualTo(List.of(1, 2));

        verify(agenteAutorizadoService, times(1)).getIdsUsuariosSubordinadosByFiltros(eq(filtros));
    }

    @Test
    public void getIdDosUsuariosParceiros_deveRetornarIds_quandoExisteremParaUfIdInformada() {
        when(agenteAutorizadoService.getIdsUsuariosSubordinadosByFiltros(any(PublicoAlvoComunicadoFiltros.class)))
            .thenReturn(List.of(1, 2));
        var filtros = PublicoAlvoComunicadoFiltros.builder().ufId(1).build();
        assertThat(service.getIdDosUsuariosParceiros(filtros)).isEqualTo(List.of(1, 2));

        verify(agenteAutorizadoService, times(1)).getIdsUsuariosSubordinadosByFiltros(eq(filtros));
    }

    @Test
    public void getIdDosUsuariosParceiros_deveRetornarIds_quandoExisteremParaCidadeIdsInformada() {
        when(agenteAutorizadoService.getIdsUsuariosSubordinadosByFiltros(any(PublicoAlvoComunicadoFiltros.class)))
            .thenReturn(List.of(1, 2));
        var filtros = PublicoAlvoComunicadoFiltros.builder().cidadesIds(List.of(5578)).build();
        assertThat(service.getIdDosUsuariosParceiros(filtros)).isEqualTo(List.of(1, 2));

        verify(agenteAutorizadoService, times(1)).getIdsUsuariosSubordinadosByFiltros(eq(filtros));
    }

    @Test
    public void gerarHistoricoTentativasLoginSenhaIncorreta_deveGerarHistorico_quandoSenhaIncorreta() {
        when(repository.findUsuarioHistoricoTentativaLoginSenhaIncorretaHoje(any(String.class)))
            .thenReturn(Optional.of(umUsuarioCompleto()));

        service.gerarHistoricoTentativasLoginSenhaIncorreta("EMAIL@EMAIL.COM");

        verify(repository, times(1)).save(any(Usuario.class));
    }

    @Test
    public void gerarHistoricoTentativasLoginSenhaIncorreta_deveInativarUsuario_quandoSenhaIncorreta() {
        when(repository.findUsuarioHistoricoTentativaLoginSenhaIncorretaHoje(any(String.class)))
            .thenReturn(Optional.of(umUsuarioCompletoSenhaErrada()));

        when(repository.findComplete(any(Integer.class)))
            .thenReturn(Optional.of(umUsuarioCompletoSenhaErrada()));

        when(motivoInativacaoService.findByCodigoMotivoInativacao(any(CodigoMotivoInativacao.class)))
            .thenReturn(umMotivoInativacaoSenhaIncorreta());

        service.gerarHistoricoTentativasLoginSenhaIncorreta("EMAIL@EMAIL.COM");

        verify(repository, times(2)).save(any(Usuario.class));
        verify(autenticacaoService, times(1)).logout(any(Integer.class));
        verify(inativarColaboradorMqSender, times(1)).sendSuccess(any(ColaboradorInativacaoPolRequest.class));
    }

    @Test
    public void gerarHistoricoTentativasLoginSenhaIncorreta_naoDeveGerarHistorico_quandoUsuarioInativo() {
        var usuarioInativo = umUsuarioInativo();
        when(repository.findUsuarioHistoricoTentativaLoginSenhaIncorretaHoje(usuarioInativo.getEmail()))
            .thenReturn(Optional.empty());

        service.gerarHistoricoTentativasLoginSenhaIncorreta(usuarioInativo.getEmail());

        verify(repository).findUsuarioHistoricoTentativaLoginSenhaIncorretaHoje(usuarioInativo.getEmail());
        verify(repository, never()).save(usuarioInativo);
    }

    private MotivoInativacao umMotivoInativacaoSenhaIncorreta() {
        return MotivoInativacao.builder()
            .id(1)
            .descricao("TESTE")
            .codigo(CodigoMotivoInativacao.TENTATIVAS_LOGIN_SENHA_INCORRETA)
            .situacao(ESituacao.A)
            .build();
    }

    private Usuario umUsuarioCompletoSenhaErrada() {
        var usuario = umUsuarioCompleto();
        usuario.adicionar(UsuarioSenhaIncorretaHistorico.builder().id(1).dataTentativa(LocalDate.now()).usuario(usuario).build());
        usuario.adicionar(UsuarioSenhaIncorretaHistorico.builder().id(1).dataTentativa(LocalDate.now()).usuario(usuario).build());
        return usuario;
    }

    @Test
    public void atualizarPermissaoEquipeTecnica_deveCriarPermissoes_quandoDtoDeEquipeTecnicaTrue() {
        service.atualizarPermissaoEquipeTecnica(permissaoEquipeTecnicaDto(true, null));

        verify(permissaoEspecialService).save(permissaoEspecialCaptor.capture());
        verify(usuarioHistoricoService).save(usuarioHistoricoCaptor.capture());

        assertThat(permissaoEspecialCaptor.getValue())
            .hasSize(1)
            .flatExtracting("usuario", "funcionalidade", "usuarioCadastro")
            .containsExactly(
                Usuario.builder()
                    .id(100)
                    .build(),
                Funcionalidade.builder()
                    .id(16101)
                    .build(),
                Usuario.builder()
                    .id(105)
                    .build()
            );
        assertThat(permissaoEspecialCaptor.getValue().get(0).getDataCadastro())
            .isCloseTo(LocalDateTime.now(), within(1, ChronoUnit.MINUTES));

        assertThat(usuarioHistoricoCaptor.getValue())
            .flatExtracting("usuario", "observacao", "situacao")
            .containsExactly(
                Usuario.builder()
                    .id(100)
                    .build(),
                "Agente Autorizado com permissão de Equipe Técnica.",
                ESituacao.A
            );
        assertThat(usuarioHistoricoCaptor.getValue().get(0).getDataCadastro())
            .isCloseTo(LocalDateTime.now(), within(1, ChronoUnit.MINUTES));
    }

    @Test
    public void atualizarPermissaoEquipeTecnica_deveRemoverPermissoes_quandoDtoDeEquipeTecnicaFalse() {
        service.atualizarPermissaoEquipeTecnica(permissaoEquipeTecnicaDto(false, List.of(2023)));

        verify(permissaoEspecialService).deletarPermissoesEspeciaisBy(List.of(16101), List.of(100, 2023));
        verify(usuarioHistoricoService).save(usuarioHistoricoCaptor.capture());

        assertThat(usuarioHistoricoCaptor.getValue())
            .flatExtracting("usuario", "observacao", "situacao")
            .containsExactlyInAnyOrder(
                Usuario.builder()
                    .id(2023)
                    .build(),
                "Agente Autorizado sem permissão de Equipe Técnica.",
                ESituacao.A,
                Usuario.builder()
                    .id(100)
                    .build(),
                "Agente Autorizado sem permissão de Equipe Técnica.",
                ESituacao.A
            );
        assertThat(usuarioHistoricoCaptor.getValue().get(0).getDataCadastro())
            .isCloseTo(LocalDateTime.now(), within(1, ChronoUnit.MINUTES));
    }

    @Test
    public void saveFromQueue_deveEnviarParaFilaDeUsuaruiosSalvosComCargoCodigo_quandoSolicitado() {
        var umCargo = Cargo.builder()
            .id(1)
            .codigo(AGENTE_AUTORIZADO_TECNICO_VENDEDOR)
            .build();

        when(cargoRepository.findByCodigo(AGENTE_AUTORIZADO_TECNICO_VENDEDOR))
            .thenReturn(umCargo);
        when(departamentoRepository.findByCodigo(any())).thenReturn(new Departamento(1));
        when(nivelRepository.findByCodigo(any())).thenReturn(new Nivel(1));
        when(unidadeNegocioRepository.findByCodigoIn(any())).thenReturn(List.of(new UnidadeNegocio(1)));
        when(empresaRepository.findByCodigoIn(any())).thenReturn(List.of(new Empresa(1)));
        when(repository.findById(1)).thenReturn(Optional.of(umUsuario()));

        var usuarioMqRequest = UsuarioMqRequest.builder()
            .id(1)
            .email("EMAIL@TEST.COM")
            .cargo(AGENTE_AUTORIZADO_TECNICO_VENDEDOR)
            .situacao(ESituacao.A)
            .tecnicoIndicador(true)
            .build();
        var expectedDto = umUsuarioDtoSender();

        service.saveFromQueue(usuarioMqRequest);

        verify(feederService, times(1))
            .adicionarPermissaoFeederParaUsuarioNovo(eq(expectedDto), eq(usuarioMqRequest));
        verify(permissaoTecnicoIndicadorService, times(1))
            .adicionarPermissaoTecnicoIndicadorParaUsuarioNovo(eq(expectedDto), eq(usuarioMqRequest), eq(false));
        verify(usuarioMqSender, times(1)).sendSuccess(eq(expectedDto));
    }

    @Test
    public void updateFromQueue_deveEnviarParaFilaDeUsuaruiosSalvosComCargoCodigo_quandoSolicitado() {
        var umCargo = Cargo.builder()
            .id(1)
            .codigo(AGENTE_AUTORIZADO_TECNICO_VENDEDOR)
            .build();

        when(cargoRepository.findByCodigo(AGENTE_AUTORIZADO_TECNICO_VENDEDOR))
            .thenReturn(umCargo);
        when(departamentoRepository.findByCodigo(any())).thenReturn(new Departamento(1));
        when(nivelRepository.findByCodigo(any())).thenReturn(new Nivel(1));
        when(unidadeNegocioRepository.findByCodigoIn(any())).thenReturn(List.of(new UnidadeNegocio(1)));
        when(empresaRepository.findByCodigoIn(any())).thenReturn(List.of(new Empresa(1)));
        when(repository.findById(1)).thenReturn(Optional.of(umUsuario()));

        var expectedDto = umUsuarioDtoSender();
        expectedDto.setUnidadeNegocioId(null);
        expectedDto.setAlterarSenha(null);
        expectedDto.setHierarquiasId(null);
        expectedDto.setTiposFeeder(null);
        expectedDto.setSubCanaisId(null);
        expectedDto.setSubNiveisIds(null);
        var usuarioMqRequest = UsuarioMqRequest.builder()
            .id(1)
            .email("EMAIL@TEST.COM")
            .cargo(AGENTE_AUTORIZADO_TECNICO_VENDEDOR)
            .agenteAutorizadoFeeder(ETipoFeeder.RESIDENCIAL)
            .situacao(ESituacao.A)
            .tecnicoIndicador(true)
            .build();

        service.updateFromQueue(usuarioMqRequest);

        verify(feederService, times(1)).removerPermissoesEspeciais(any(), any());
        verify(feederService, times(1))
            .adicionarPermissaoFeederParaUsuarioNovo(eq(expectedDto), eq(usuarioMqRequest));
        verify(permissaoTecnicoIndicadorService, times(1))
            .adicionarPermissaoTecnicoIndicadorParaUsuarioNovo(eq(expectedDto), eq(usuarioMqRequest), eq(false));
        verify(usuarioMqSender, times(1)).sendSuccess(eq(expectedDto));
    }

    @Test
    public void getIdDosUsuariosSubordinados_deveRetornarIds_quandoSolicitado() {
        when(repository.getUsuariosSubordinados(1))
            .thenReturn(List.of(2));

        assertThat(service.getIdDosUsuariosSubordinados(1, false))
            .isEqualTo(List.of(2));
    }

    @Test
    public void getIdDosUsuariosSubordinados_deveRetornarIdsInclusiveDoUsuario_quandoIncluirProprioForTrue() {
        when(repository.getUsuariosSubordinados(1))
            .thenReturn(new ArrayList<>(List.of(2)));

        assertThat(service.getIdDosUsuariosSubordinados(1, true))
            .isEqualTo(List.of(2, 1));
    }

    @Test
    public void findUsuarioByCpfComSituacaoAtivoOuInativo_deveRetornarUsuarioAtivo_quandoBuscarPorCpf() {
        doReturn(Optional.of(umUsuarioAtivo()))
            .when(repository)
            .findTop1UsuarioByCpfAndSituacaoIn(anyString(), anyList());

        assertThat(service.findUsuarioByCpfComSituacaoAtivoOuInativo("31114231827"))
            .isNotNull()
            .extracting(UsuarioResponse::getId, UsuarioResponse::getCpf, UsuarioResponse::getNome,
                UsuarioResponse::getSituacao, UsuarioResponse::getEmail)
            .containsExactly(10, "98471883007", "Usuario Ativo", A, "usuarioativo@email.com");

        verify(repository).findTop1UsuarioByCpfAndSituacaoIn(anyString(), anyList());
    }

    @Test
    public void findUsuarioByCpfComSituacaoAtivoOuInativo_deveRetornarUsuarioInativo_quandoBuscarPorCpf() {
        doReturn(Optional.of(umUsuarioInativo()))
            .when(repository)
            .findTop1UsuarioByCpfAndSituacaoIn(anyString(), anyList());

        assertThat(service.findUsuarioByCpfComSituacaoAtivoOuInativo("31114231827"))
            .isNotNull()
            .extracting(UsuarioResponse::getId, UsuarioResponse::getCpf, UsuarioResponse::getNome,
                UsuarioResponse::getSituacao, UsuarioResponse::getEmail)
            .containsExactly(11, "31114231827", "Usuario Inativo", ESituacao.I, "usuarioinativo@email.com");

        verify(repository).findTop1UsuarioByCpfAndSituacaoIn(anyString(), anyList());
    }

    @Test
    public void findUsuarioByCpfComSituacaoAtivoOuInativo_deveRetornarVazio_quandoNaoEncontrarUsuarioCorrespondente() {
        assertThat(service.findUsuarioByCpfComSituacaoAtivoOuInativo("12345678901")).isNull();

        verify(repository).findTop1UsuarioByCpfAndSituacaoIn(anyString(), anyList());
    }

    @Test
    public void findUsuarioByCpfComSituacaoAtivoOuInativo_deveRetornarVazio_quandoBuscarPorCpfEUsuarioEstiverRealocado() {
        assertThat(service.findUsuarioByCpfComSituacaoAtivoOuInativo("31114231827")).isNull();

        verify(repository).findTop1UsuarioByCpfAndSituacaoIn(anyString(), anyList());
    }

    @Test
    public void findUsuarioByEmailComSituacaoAtivoOuInativo_deveRetornarUsuarioAtivo_quandoBuscarPorEmail() {
        doReturn(Optional.of(umUsuarioAtivo()))
            .when(repository)
            .findTop1UsuarioByEmailAndSituacaoIn(anyString(), anyList());

        assertThat(service.findUsuarioByEmailComSituacaoAtivoOuInativo("usuarioativo@email.com"))
            .isNotNull()
            .extracting(UsuarioResponse::getId, UsuarioResponse::getCpf, UsuarioResponse::getNome,
                UsuarioResponse::getSituacao, UsuarioResponse::getEmail)
            .containsExactly(10, "98471883007", "Usuario Ativo", A, "usuarioativo@email.com");

        verify(repository).findTop1UsuarioByEmailAndSituacaoIn(anyString(), anyList());
    }

    @Test
    public void findUsuarioByEmailComSituacaoAtivoOuInativo_deveRetornarUsuarioInativo_quandoBuscarPorEmail() {
        doReturn(Optional.of(umUsuarioInativo()))
            .when(repository)
            .findTop1UsuarioByEmailAndSituacaoIn(anyString(), anyList());

        assertThat(service.findUsuarioByEmailComSituacaoAtivoOuInativo("usuarioinativo@email.com"))
            .isNotNull()
            .extracting(UsuarioResponse::getId, UsuarioResponse::getCpf, UsuarioResponse::getNome,
                UsuarioResponse::getSituacao, UsuarioResponse::getEmail)
            .containsExactly(11, "31114231827", "Usuario Inativo", ESituacao.I, "usuarioinativo@email.com");

        verify(repository).findTop1UsuarioByEmailAndSituacaoIn(anyString(), anyList());
    }

    @Test
    public void findUsuarioByEmailComSituacaoAtivoOuInativo_deveRetornarVazio_quandoNaoEncontrarUsuarioCorrespondente() {
        assertThat(service.findUsuarioByEmailComSituacaoAtivoOuInativo("usuarioteste@email.com")).isNull();

        verify(repository).findTop1UsuarioByEmailAndSituacaoIn(anyString(), anyList());
    }

    @Test
    public void findUsuarioByEmailComSituacaoAtivoOuInativo_deveRetornarVazio_quandoBuscarPorEmailEUsuarioEstiverRealocado() {
        assertThat(service.findUsuarioByEmailComSituacaoAtivoOuInativo("usuarioteste@email.com")).isNull();

        verify(repository).findTop1UsuarioByEmailAndSituacaoIn(anyString(), anyList());
    }

    @Test
    public void moverAvatarMinio_deveFazerUpdate_seUsuarioAdmin() {
        when(autenticacaoService.getUsuarioAutenticado()).thenReturn(umUsuarioAutenticadoAdmin(1));

        when(repository.findByFotoDiretorioIsNotNull()).thenReturn(umaUsuariosList());

        service.moverAvatarMinio();

        verify(autenticacaoService).getUsuarioAutenticado();
        verify(repository).findByFotoDiretorioIsNotNull();
    }

    @Test
    public void moverAvatarMinio_deveNaoFazerUpdate_seUsuarioNaoAdmin() {
        when(autenticacaoService.getUsuarioAutenticado()).thenReturn(umUsuarioAutenticado(1, "OPERACAO",
            CodigoCargo.VENDEDOR_OPERACAO, AUT_VISUALIZAR_GERAL));

        assertThatExceptionOfType(PermissaoException.class)
            .isThrownBy(() -> service.moverAvatarMinio())
            .withMessage("Usuário não autorizado!");

        verify(autenticacaoService).getUsuarioAutenticado();
        verify(repository, never()).findByFotoDiretorioIsNotNull();
    }

    @Test
    public void updateFromQueue_deveFazerUpdate_quandoTodosDadosValidos() {
        var usuarioMqRequest = umUsuarioMqRequestCompleto();
        var usuarioDto = umUsuarioDtoParse();

        when(repository.findById(usuarioDto.getId())).thenReturn(Optional.of(umUsuarioConvertFrom()));
        when(cargoRepository.findByCodigo(usuarioMqRequest.getCargo())).thenReturn(umCargoGerente());
        when(departamentoRepository.findByCodigo(usuarioMqRequest.getDepartamento())).thenReturn(umDepartamentoAa());
        when(nivelRepository.findByCodigo(usuarioMqRequest.getNivel())).thenReturn(umNivelAa());
        when(unidadeNegocioRepository.findByCodigoIn(usuarioMqRequest.getUnidadesNegocio()))
            .thenReturn(List.of(umaUnidadeNegocio(CodigoUnidadeNegocio.CLARO_RESIDENCIAL)));
        when(empresaRepository.findByCodigoIn(usuarioMqRequest.getEmpresa())).thenReturn(List.of(umaEmpresa()));
        when(autenticacaoService.getUsuarioAutenticado()).thenReturn(umUsuarioAutenticadoAa());

        service.updateFromQueue(usuarioMqRequest);

        verify(repository, times(6)).findById(usuarioDto.getId());
        verify(cargoRepository).findByCodigo(usuarioMqRequest.getCargo());
        verify(departamentoRepository).findByCodigo(usuarioMqRequest.getDepartamento());
        verify(nivelRepository).findByCodigo(usuarioMqRequest.getNivel());
        verify(unidadeNegocioRepository).findByCodigoIn(usuarioMqRequest.getUnidadesNegocio());
        verify(empresaRepository).findByCodigoIn(usuarioMqRequest.getEmpresa());
        verify(autenticacaoService).getUsuarioAutenticado();
    }

    @Test
    public void updateFromQueue_deveSalvarUsuarioAlteracaoCpf_quandoCpfDiferenteDoCadastrado() {
        var usuarioDto = umUsuarioDtoParse();
        var usuario = umUsuarioConvertFrom();
        usuario.setCpf("963852741");
        var usuarioMqRequest = umUsuarioMqRequestCompleto();

        when(repository.findById(usuarioDto.getId())).thenReturn(Optional.of(usuario));
        when(repository.findComplete(usuarioDto.getId())).thenReturn(Optional.of(usuario));

        service.updateFromQueue(usuarioMqRequest);

        verify(repository).findById(usuarioDto.getId());
        verify(repository).findComplete(usuarioDto.getId());
    }

    @Test
    public void updateFromQueue_deveLancarException_quandoOcorrerErro() {
        var usuarioDto = umUsuarioDtoParse();
        var usuario = umUsuarioConvertFrom();
        usuario.setCpf("963852741");

        when(repository.findById(usuarioDto.getId())).thenReturn(Optional.of(usuario));
        when(repository.findComplete(usuarioDto.getId())).thenReturn(Optional.of(usuario));

        service.updateFromQueue(umUsuarioMqRequestCompleto());

        verify(repository).findById(usuarioDto.getId());
        verify(repository).findComplete(usuarioDto.getId());
    }

    @Test
    public void remanejarUsuario_deveRemanejarColaboradorVendasAntigoEDuplicarCriandoUmNovo_quandoDadosEstiveremCorretos() {
        var usuarioMqRequest = umUsuarioMqRequestCompleto();
        var usuarioDto = umUsuarioDtoParse();
        var usuarioAntigo = umUsuarioConvertFrom();
        usuarioAntigo.setId(null);

        when(repository.findById(usuarioDto.getId())).thenReturn(Optional.of(umUsuarioConvertFrom()));
        when(cargoRepository.findByCodigo(usuarioMqRequest.getCargo())).thenReturn(umCargoGerente());
        when(departamentoRepository.findByCodigo(usuarioMqRequest.getDepartamento())).thenReturn(umDepartamentoAa());
        when(nivelRepository.findByCodigo(usuarioMqRequest.getNivel())).thenReturn(umNivelAa());
        when(unidadeNegocioRepository.findByCodigoIn(usuarioMqRequest.getUnidadesNegocio()))
            .thenReturn(List.of(umaUnidadeNegocio(CodigoUnidadeNegocio.CLARO_RESIDENCIAL)));
        when(empresaRepository.findByCodigoIn(usuarioMqRequest.getEmpresa())).thenReturn(List.of(umaEmpresa()));
        var novoUsuario = umUsuarioConvertFrom();
        novoUsuario.setCargo(umCargo(1, COORDENADOR_OPERACAO));
        novoUsuario.setUsuariosHierarquia(Set.of());
        when(repository.save(usuarioAntigo)).thenReturn(novoUsuario);

        service.remanejarUsuario(usuarioMqRequest);

        verify(repository).findById(usuarioDto.getId());
        verify(cargoRepository).findByCodigo(usuarioMqRequest.getCargo());
        verify(departamentoRepository).findByCodigo(usuarioMqRequest.getDepartamento());
        verify(nivelRepository).findByCodigo(usuarioMqRequest.getNivel());
        verify(unidadeNegocioRepository).findByCodigoIn(usuarioMqRequest.getUnidadesNegocio());
        verify(empresaRepository).findByCodigoIn(usuarioMqRequest.getEmpresa());
        verify(colaboradorVendasService).atualizarUsuarioRemanejado(any(UsuarioRemanejamentoRequest.class));
        verifyNoMoreInteractions(colaboradorTecnicoService);
    }

    @Test
    public void remanejarUsuario_deveRemanejarColaboradorTecnicoAntigoEDuplicarCriandoUmNovo_quandoDadosEstiveremCorretos() {
        var usuarioMqRequest = umUsuarioMqRequestCompleto();
        var usuarioDto = umUsuarioDtoParse();
        var usuarioAntigo = umUsuarioConvertFrom();
        usuarioAntigo.setId(null);

        when(repository.findById(usuarioDto.getId())).thenReturn(Optional.of(umUsuarioConvertFrom()));
        when(cargoRepository.findByCodigo(usuarioMqRequest.getCargo())).thenReturn(umCargoGerente());
        when(departamentoRepository.findByCodigo(usuarioMqRequest.getDepartamento())).thenReturn(umDepartamentoAa());
        when(nivelRepository.findByCodigo(usuarioMqRequest.getNivel())).thenReturn(umNivelAa());
        when(unidadeNegocioRepository.findByCodigoIn(usuarioMqRequest.getUnidadesNegocio()))
            .thenReturn(List.of(umaUnidadeNegocio(CodigoUnidadeNegocio.CLARO_RESIDENCIAL)));
        when(empresaRepository.findByCodigoIn(usuarioMqRequest.getEmpresa())).thenReturn(List.of(umaEmpresa()));
        var novoUsuario = umUsuarioConvertFrom();
        novoUsuario.setCargo(umCargo(1, AGENTE_AUTORIZADO_TECNICO_VENDEDOR));
        novoUsuario.setUsuariosHierarquia(Set.of());
        when(repository.save(usuarioAntigo)).thenReturn(novoUsuario);

        service.remanejarUsuario(usuarioMqRequest);

        verify(repository).findById(usuarioDto.getId());
        verify(cargoRepository).findByCodigo(usuarioMqRequest.getCargo());
        verify(departamentoRepository).findByCodigo(usuarioMqRequest.getDepartamento());
        verify(nivelRepository).findByCodigo(usuarioMqRequest.getNivel());
        verify(unidadeNegocioRepository).findByCodigoIn(usuarioMqRequest.getUnidadesNegocio());
        verify(empresaRepository).findByCodigoIn(usuarioMqRequest.getEmpresa());
        verify(colaboradorTecnicoService).atualizarUsuarioRemanejado(any(UsuarioRemanejamentoRequest.class));
        verifyNoMoreInteractions(colaboradorVendasService);
    }

    private void mockApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        ReflectionTestUtils.setField(service, "applicationEventPublisher", applicationEventPublisher);
    }

    @Test
    public void getCanaisPermitidosParaOrganizacao_deveRetornarCanaisPermitidos_quandoSolicitado() {
        assertThat(service.getCanaisPermitidosParaOrganizacao())
            .extracting("value", "label")
            .containsExactly(
                tuple("INTERNET", "Internet")
            );
    }

    @Test
    public void getUsuariosCargoSuperiorByCanal_deveRetornarUsuarioSuperior_quandoSolicitadoComOrganizacaoId() {
        doReturn(TestBuilders.umUsuarioAutenticadoAdmin(1))
            .when(autenticacaoService)
            .getUsuarioAutenticado();

        var usuarioSuperior = umUsuarioCompleto(ESituacao.A, INTERNET_GERENTE, 500,
            OPERACAO, CodigoDepartamento.COMERCIAL, ECanal.INTERNET);
        usuarioSuperior.getCargo().setNome("INTERNET_GERENTE");

        doReturn(List.of(usuarioSuperior))
            .when(repository)
            .getUsuariosFilter(any(Predicate.class));

        var cargo = umCargo(98436, INTERNET_SUPERVISOR);
        cargo.setSuperiores(Set.of(umCargo(98436, INTERNET_GERENTE)));

        doReturn(cargo)
            .when(cargoService)
            .findById(501);

        assertThat(service.getUsuariosCargoSuperiorByCanal(501,
            UsuarioCargoSuperiorPost.builder().organizacaoId(1).build(), Set.of(ECanal.INTERNET)))
            .extracting("nome", "cargoNome")
            .containsExactly(
                tuple("NOME UM", "INTERNET_GERENTE")
            );
    }

    @Test
    public void getCidadesByUsuarioId_deveLancarValidacaoException_quandoNaoEncontrarPorUsuarioId() {
        assertThatExceptionOfType(ValidacaoException.class)
            .isThrownBy(() -> service.getCidadesByUsuarioId(101112))
            .withMessage("Usuário não encontrado.");

        verify(repository).findComplete(101112);
        verifyZeroInteractions(cidadeService);
    }

    @Test
    public void getCidadesByUsuarioId_deveRetornarListaVazia_quandoEncontrarUsuarioQueNaoTenhaCidadesAtreladas() {
        when(repository.findComplete(181920)).thenReturn(Optional.of(umUsuarioSemCidades()));

        assertThat(service.getCidadesByUsuarioId(181920)).isEmpty();

        verify(repository).findComplete(181920);
        verifyZeroInteractions(cidadeService);
    }

    @Test
    public void getCidadesByUsuarioId_deveRetornarListaCidadeResponseComCidadesSemDistritos_quandoEncontrarCidadesDoUsuario() {
        when(repository.findComplete(121314)).thenReturn(Optional.of(umUsuarioComCidades()));

        assertThat(service.getCidadesByUsuarioId(121314))
            .extracting("id", "nome", "uf.id", "uf.nome", "regional.id", "regional.nome", "fkCidade", "cidadePai")
            .containsExactly(
                tuple(3248, "BANDEIRANTES", 1, "PARANA", 1027, "RPS", null, null),
                tuple(3270, "CAMBE", 1, "PARANA", 1027, "RPS", null, null),
                tuple(3272, "CAMPINA DA LAGOA", 1, "PARANA", 1027, "RPS", null, null),
                tuple(3287, "CASCAVEL", 1, "PARANA", 1027, "RPS", null, null),
                tuple(3312, "CURITIBA", 1, "PARANA", 1027, "RPS", null, null),
                tuple(5578, "LONDRINA", 1, "PARANA", 1027, "RPS", null, null),
                tuple(3426, "MARINGA", 1, "PARANA", 1027, "RPS", null, null)
            );

        verify(repository).findComplete(121314);
        verifyZeroInteractions(cidadeService);
    }

    @Test
    @SuppressWarnings("LineLength")
    public void getCidadesByUsuarioId_deveRetornarListaCidadeResponseComDistritosENomeCidadePai_quandoEncontrarCidadesDoUsuario() {
        when(repository.findComplete(151617)).thenReturn(Optional.of(umUsuarioComDistritos()));
        when(cidadeService.getCidadesDistritos(Eboolean.V)).thenReturn(umMapApenasDistritosComCidadePai());

        assertThat(service.getCidadesByUsuarioId(151617))
            .extracting("id", "nome", "uf.id", "uf.nome", "regional.id", "regional.nome", "fkCidade", "cidadePai")
            .containsExactly(
                tuple(30858, "GUARAVERA", 1, "PARANA", 1027, "RPS", 5578, "LONDRINA"),
                tuple(30813, "IRERE", 1, "PARANA", 1027, "RPS", 5578, "LONDRINA"),
                tuple(30732, "LERROVILLE", 1, "PARANA", 1027, "RPS", 5578, "LONDRINA"),
                tuple(30757, "MARAVILHA", 1, "PARANA", 1027, "RPS", 5578, "LONDRINA"),
                tuple(30676, "PAIQUERE", 1, "PARANA", 1027, "RPS", 5578, "LONDRINA"),
                tuple(30848, "SAO LUIZ", 1, "PARANA", 1027, "RPS", 5578, "LONDRINA"),
                tuple(30910, "WARTA", 1, "PARANA", 1027, "RPS", 5578, "LONDRINA")
            );

        verify(repository).findComplete(151617);
        verify(cidadeService).getCidadesDistritos(Eboolean.V);
    }

    @Test
    public void findCidadesDoUsuarioLogado_deveLancarValidacaoException_quandoNaoEncontrarPorIdDoUsuarioAutenticado() {
        assertThatExceptionOfType(ValidacaoException.class)
            .isThrownBy(() -> service.findCidadesDoUsuarioLogado())
            .withMessage("Usuário não encontrado.");

        verify(autenticacaoService).getUsuarioAutenticadoId();
        verifyZeroInteractions(usuarioCidadeRepository);
        verifyZeroInteractions(cidadeService);
    }

    @Test
    public void findCidadesDoUsuarioLogado_deveRetornarListaVazia_quandoUsuarioNaoPossuirCidadesAtreladas() {
        when(autenticacaoService.getUsuarioAutenticadoId()).thenReturn(Optional.of(101112));
        when(usuarioCidadeRepository.findUsuarioCidadesByUsuarioId(101112)).thenReturn(Set.of());

        assertThat(service.findCidadesDoUsuarioLogado()).isEmpty();

        verify(autenticacaoService).getUsuarioAutenticadoId();
        verify(usuarioCidadeRepository).findUsuarioCidadesByUsuarioId(101112);
        verifyZeroInteractions(cidadeService);
    }

    @Test
    public void findCidadesDoUsuarioLogado_deveRetornarListaUsuarioCidadeDtoComCidades_quandoEncontrarCidadesDoUsuario() {
        when(autenticacaoService.getUsuarioAutenticadoId()).thenReturn(Optional.of(101112));
        when(usuarioCidadeRepository.findUsuarioCidadesByUsuarioId(101112)).thenReturn(listaUsuarioCidadesDoParana());

        assertThat(service.findCidadesDoUsuarioLogado())
            .extracting("idCidade", "nomeCidade", "idUf", "nomeUf", "idRegional", "nomeRegional", "fkCidade", "cidadePai")
            .containsExactly(
                tuple(3248, "BANDEIRANTES", 1, "PARANA", 1027, "RPS", null, null),
                tuple(3270, "CAMBE", 1, "PARANA", 1027, "RPS", null, null),
                tuple(3272, "CAMPINA DA LAGOA", 1, "PARANA", 1027, "RPS", null, null),
                tuple(3287, "CASCAVEL", 1, "PARANA", 1027, "RPS", null, null),
                tuple(3312, "CURITIBA", 1, "PARANA", 1027, "RPS", null, null),
                tuple(5578, "LONDRINA", 1, "PARANA", 1027, "RPS", null, null),
                tuple(3426, "MARINGA", 1, "PARANA", 1027, "RPS", null, null)
            );

        verify(autenticacaoService).getUsuarioAutenticadoId();
        verify(usuarioCidadeRepository).findUsuarioCidadesByUsuarioId(101112);
        verifyZeroInteractions(cidadeService);
    }

    @Test
    @SuppressWarnings("LineLength")
    public void findCidadesDoUsuarioLogado_deveRetornarListaUsuarioCidadeDtoComDistritosENomeCidadePai_quandoEncontrarCidadesDoUsuario() {
        when(autenticacaoService.getUsuarioAutenticadoId()).thenReturn(Optional.of(101112));
        when(usuarioCidadeRepository.findUsuarioCidadesByUsuarioId(101112)).thenReturn(listaUsuarioCidadeDeDistritosDeLondrina());
        when(cidadeService.getCidadesDistritos(Eboolean.V)).thenReturn(umMapApenasDistritosComCidadePai());

        assertThat(service.findCidadesDoUsuarioLogado())
            .extracting("idCidade", "nomeCidade", "idUf", "nomeUf", "idRegional", "nomeRegional", "fkCidade", "cidadePai")
            .containsExactly(
                tuple(30858, "GUARAVERA", 1, "PARANA", 1027, "RPS", 5578, "LONDRINA"),
                tuple(30813, "IRERE", 1, "PARANA", 1027, "RPS", 5578, "LONDRINA"),
                tuple(30732, "LERROVILLE", 1, "PARANA", 1027, "RPS", 5578, "LONDRINA"),
                tuple(30757, "MARAVILHA", 1, "PARANA", 1027, "RPS", 5578, "LONDRINA"),
                tuple(30676, "PAIQUERE", 1, "PARANA", 1027, "RPS", 5578, "LONDRINA"),
                tuple(30848, "SAO LUIZ", 1, "PARANA", 1027, "RPS", 5578, "LONDRINA"),
                tuple(30910, "WARTA", 1, "PARANA", 1027, "RPS", 5578, "LONDRINA")
            );

        verify(autenticacaoService).getUsuarioAutenticadoId();
        verify(usuarioCidadeRepository).findUsuarioCidadesByUsuarioId(101112);
        verify(cidadeService).getCidadesDistritos(Eboolean.V);
    }

    @Test
    public void validarSeUsuarioCpfEmailNaoCadastrados_deveRetornarValidacaoException_quandoCpfJaCadastrado() {
        doThrow(new ValidacaoException(CPF_JA_CADASTRADO))
            .when(repository)
            .findTop1UsuarioByCpfAndSituacaoNot(eq("07981056233"), eq(ESituacao.R));

        assertThatCode(() -> service
            .validarSeUsuarioCpfEmailNaoCadastrados("07981056233", "NOVOSOCIO@EMPRESA.COM.BR"))
            .isInstanceOf(ValidacaoException.class)
            .hasMessage(CPF_JA_CADASTRADO);

        verify(repository, times(1))
            .findTop1UsuarioByCpfAndSituacaoNot(eq("07981056233"), eq(ESituacao.R));
        verify(repository, never())
            .findTop1UsuarioByEmailIgnoreCaseAndSituacaoNot(eq("NOVOSOCIO@EMPRESA.COM.BR"), eq(ESituacao.R));
    }

    @Test
    public void validarSeUsuarioCpfEmailNaoCadastrados_deveRetornarValidacaoException_quandoEmailJaCadastrado() {
        when(repository.findTop1UsuarioByCpfAndSituacaoNot(eq("07981056233"), eq(ESituacao.R)))
            .thenReturn(Optional.empty());

        doThrow(new ValidacaoException(EMAIL_JA_CADASTRADO))
            .when(repository)
            .findTop1UsuarioByEmailIgnoreCaseAndSituacaoNot(eq("NOVOSOCIO@EMPRESA.COM.BR"), eq(ESituacao.R));

        assertThatCode(() -> service
            .validarSeUsuarioCpfEmailNaoCadastrados("07981056233", "NOVOSOCIO@EMPRESA.COM.BR"))
            .isInstanceOf(ValidacaoException.class)
            .hasMessage(EMAIL_JA_CADASTRADO);

        verify(repository, times(1))
            .findTop1UsuarioByCpfAndSituacaoNot(eq("07981056233"), eq(ESituacao.R));
        verify(repository, times(1))
            .findTop1UsuarioByEmailIgnoreCaseAndSituacaoNot(eq("NOVOSOCIO@EMPRESA.COM.BR"), eq(ESituacao.R));
    }

    @Test
    public void validarSeUsuarioCpfEmailNaoCadastrados_naoDeveRetornarValidacaoException_quandoCpfEEmailNaoCadastrados() {
        when(repository.findTop1UsuarioByCpfAndSituacaoNot(eq("07981056233"), eq(ESituacao.R)))
            .thenReturn(Optional.empty());

        when(repository.findTop1UsuarioByEmailIgnoreCaseAndSituacaoNot(eq("NOVOSOCIO@EMPRESA.COM.BR"), eq(ESituacao.R)))
            .thenReturn(Optional.empty());

        assertThatCode(() -> service
            .validarSeUsuarioCpfEmailNaoCadastrados("07981056233", "NOVOSOCIO@EMPRESA.COM.BR"))
            .doesNotThrowAnyException();

        verify(repository, times(1))
            .findTop1UsuarioByCpfAndSituacaoNot(eq("07981056233"), eq(ESituacao.R));
        verify(repository, times(1))
            .findTop1UsuarioByEmailIgnoreCaseAndSituacaoNot(eq("NOVOSOCIO@EMPRESA.COM.BR"), eq(ESituacao.R));
    }

    @Test
    public void inativarAntigoSocioPrincipal_deveLancarException_quandoUsuarioNaoLocalizado() {
        doReturn(Optional.empty())
            .when(repository)
            .findByEmail("ANTIGOSOCIO@EMPRESA.COM.BR");

        assertThatCode(() -> service
            .inativarAntigoSocioPrincipal("ANTIGOSOCIO@EMPRESA.COM.BR"))
            .isInstanceOf(ValidacaoException.class)
            .hasMessage("Usuário não encontrado.");

        verify(repository).findByEmail("ANTIGOSOCIO@EMPRESA.COM.BR");
        verify(repository, never()).save(any(Usuario.class));
        verify(autenticacaoService, never()).logout(anyInt());
        verify(agenteAutorizadoService, never()).inativarAntigoSocioPrincipal("ANTIGOSOCIO@EMPRESA.COM.BR");
    }

    @Test
    public void inativarAntigoSocioPrincipal_naoDeveInativarAntigoSocioPrincipal_seSituacaoDoUsuarioForAtivo() {
        doReturn(Optional.of(umAntigoSocioPrincipal(ESituacao.I)))
            .when(repository)
            .findByEmail("ANTIGOSOCIO@EMPRESA.COM.BR");

        assertThatCode(() -> service
            .inativarAntigoSocioPrincipal("ANTIGOSOCIO@EMPRESA.COM.BR"))
            .doesNotThrowAnyException();

        verify(repository).findByEmail("ANTIGOSOCIO@EMPRESA.COM.BR");
        verify(repository, never()).save(any(Usuario.class));
        verify(autenticacaoService, never()).logout(anyInt());
        verify(agenteAutorizadoService).inativarAntigoSocioPrincipal("ANTIGOSOCIO@EMPRESA.COM.BR");
    }

    @Test
    public void inativarAntigoSocioPrincipal_deveLancarException_quandoSocioNaoInativadoNoPol() {
        doReturn(Optional.of(umAntigoSocioPrincipal(ESituacao.A)))
            .when(repository)
            .findByEmail("ANTIGOSOCIO@EMPRESA.COM.BR");

        doThrow(new IntegracaoException(EErrors.ERRO_SOCIO_NAO_INATIVADO_NO_POL.getDescricao()))
            .when(agenteAutorizadoService)
            .inativarAntigoSocioPrincipal("ANTIGOSOCIO@EMPRESA.COM.BR");

        assertThatCode(() -> service
            .inativarAntigoSocioPrincipal("ANTIGOSOCIO@EMPRESA.COM.BR"))
            .isInstanceOf(IntegracaoException.class)
            .hasMessage(EErrors.ERRO_SOCIO_NAO_INATIVADO_NO_POL.getDescricao());

        verify(repository).findByEmail("ANTIGOSOCIO@EMPRESA.COM.BR");
        verify(repository).save(umAntigoSocioPrincipal(ESituacao.I));
        verify(autenticacaoService).logout(22);
        verify(agenteAutorizadoService).inativarAntigoSocioPrincipal("ANTIGOSOCIO@EMPRESA.COM.BR");
    }

    @Test
    public void inativarAntigoSocioPrincipal_deveInativarSocioPrincipal_quandoLocalizadoESituacaoAtivo() {
        doReturn(Optional.of(umAntigoSocioPrincipal(ESituacao.A)))
            .when(repository)
            .findByEmail("ANTIGOSOCIO@EMPRESA.COM.BR");

        assertThatCode(() -> service
            .inativarAntigoSocioPrincipal("ANTIGOSOCIO@EMPRESA.COM.BR"))
            .doesNotThrowAnyException();

        verify(repository).findByEmail("ANTIGOSOCIO@EMPRESA.COM.BR");
        verify(repository).save(umAntigoSocioPrincipal(ESituacao.I));
        verify(autenticacaoService).logout(22);
        verify(agenteAutorizadoService).inativarAntigoSocioPrincipal("ANTIGOSOCIO@EMPRESA.COM.BR");
    }

    @Test
    public void limparCpfAntigoSocioPrincipal_deveRetornarValidacaoException_quandoUsuarioNaoCadastrado() {
        doThrow(new ValidacaoException(USUARIO_NAO_ENCONTRADO)).when(repository).findById(eq(21));

        assertThatCode(() -> service.limparCpfAntigoSocioPrincipal(21))
            .isInstanceOf(ValidacaoException.class)
            .hasMessage(USUARIO_NAO_ENCONTRADO);

        verify(repository, times(1)).findById(eq(21));
        verify(repository, never()).save(any(Usuario.class));
    }

    @Test
    public void limparCpfAntigoSocioPrincipal_deveRetornarOk_quandoTudoOk() {
        var umSocioPrincipalCpfLimpo = umSocioPrincipal();
        umSocioPrincipalCpfLimpo.setCpf(null);

        when(repository.findById(eq(23))).thenReturn(Optional.of(umSocioPrincipal()));
        when(repository.save(umSocioPrincipalCpfLimpo)).thenReturn(umSocioPrincipalCpfLimpo);

        service.limparCpfAntigoSocioPrincipal(23);

        verify(repository, times(1)).findById(eq(23));
        verify(repository, times(1)).save(umSocioPrincipalCpfLimpo);
    }

    @Test
    public void atualizarEmailSocioInativo_deveRetornarValidacaoException_quandoUsuarioNaoCadastrado() {
        doThrow(new ValidacaoException(USUARIO_NAO_ENCONTRADO)).when(repository).findById(eq(21));

        assertThatCode(() -> service.atualizarEmailSocioInativo(21))
            .isInstanceOf(ValidacaoException.class)
            .hasMessage(USUARIO_NAO_ENCONTRADO);

        verify(repository, times(1)).findById(eq(21));
        verify(repository, never()).save(any(Usuario.class));
        verify(agenteAutorizadoService, never()).atualizarEmailSocioPrincipalInativo(anyString(), anyString(), anyInt());
    }

    @Test
    public void atualizarEmailSocioInativo_deveRetornarValidacaoException_quandoEmailDoUsuarioForVazio() {
        var umSocioPrincipalComEmailVazio = umSocioPrincipal();
        umSocioPrincipalComEmailVazio.setEmail("");

        when(repository.findById(eq(23))).thenReturn(Optional.of(umSocioPrincipalComEmailVazio));

        assertThatCode(() -> service.atualizarEmailSocioInativo(23))
            .isInstanceOf(ValidacaoException.class)
            .hasMessage(ERRO_VALIDAR_EMAIL_CADASTRADO.getDescricao());

        verify(repository, times(1)).findById(eq(23));
        verify(repository, never()).save(any(Usuario.class));
        verify(agenteAutorizadoService, never()).atualizarEmailSocioPrincipalInativo(anyString(), anyString(), anyInt());
    }

    @Test
    public void atualizarEmailSocioInativo_deveRetornarValidacaoException_quandoEmailDoUsuarioForNulo() {
        var umSocioPrincipalComEmailNulo = umSocioPrincipal();
        umSocioPrincipalComEmailNulo.setEmail(null);

        when(repository.findById(eq(23))).thenReturn(Optional.of(umSocioPrincipalComEmailNulo));

        assertThatCode(() -> service.atualizarEmailSocioInativo(23))
            .isInstanceOf(ValidacaoException.class)
            .hasMessage(ERRO_VALIDAR_EMAIL_CADASTRADO.getDescricao());

        verify(repository, times(1)).findById(eq(23));
        verify(repository, never()).save(any(Usuario.class));
        verify(agenteAutorizadoService, never()).atualizarEmailSocioPrincipalInativo(anyString(), anyString(), anyInt());
    }

    @Test
    public void atualizarEmailSocioInativo_deveRetornarValidacaoException_quandoEmailDoUsuarioForSemDominio() {
        var umSocioPrincipalComEmailSemDominio = umSocioPrincipal();
        umSocioPrincipalComEmailSemDominio.setEmail("NOVOSOCIO@EMPRESA");

        when(repository.findById(eq(23))).thenReturn(Optional.of(umSocioPrincipalComEmailSemDominio));

        assertThatCode(() -> service.atualizarEmailSocioInativo(23))
            .isInstanceOf(ValidacaoException.class)
            .hasMessage(ERRO_VALIDAR_EMAIL_CADASTRADO.getDescricao());

        verify(repository, times(1)).findById(eq(23));
        verify(repository, never()).save(any(Usuario.class));
        verify(agenteAutorizadoService, never()).atualizarEmailSocioPrincipalInativo(anyString(), anyString(), anyInt());
    }

    @Test
    public void atualizarEmailSocioInativo_deveRetornarValidacaoException_quandoEmailDoUsuarioForComMaisDeUmArroba() {
        var umSocioPrincipalComEmailComMaisDeUmArroba = umSocioPrincipal();
        umSocioPrincipalComEmailComMaisDeUmArroba.setEmail("NOVO@SOCIO@EMPRESA.COM.BR");

        when(repository.findById(eq(23))).thenReturn(Optional.of(umSocioPrincipalComEmailComMaisDeUmArroba));

        assertThatCode(() -> service.atualizarEmailSocioInativo(23))
            .isInstanceOf(ValidacaoException.class)
            .hasMessage(ERRO_VALIDAR_EMAIL_CADASTRADO.getDescricao());

        verify(repository, times(1)).findById(eq(23));
        verify(repository, never()).save(any(Usuario.class));
        verify(agenteAutorizadoService, never()).atualizarEmailSocioPrincipalInativo(anyString(), anyString(), anyInt());
    }

    @Test
    public void atualizarEmailSocioInativo_deveRetornarIntegracaoException_quandoEmailSocioPrincipalNaoAtualizadoNoPol() {
        var umSocioPrincipalComEmailAtualizado = umSocioPrincipal();
        umSocioPrincipalComEmailAtualizado.setEmail("NOVOSOCIO.INATIVO@EMPRESA.COM.BR");

        when(repository.findById(eq(23))).thenReturn(Optional.of(umSocioPrincipal()));
        when(repository.save(eq(umSocioPrincipalComEmailAtualizado))).thenReturn(umSocioPrincipalComEmailAtualizado);

        doThrow(new IntegracaoException(EErrors.ERRO_EMAIL_SOCIO_NAO_ATUALIZADO_NO_POL.getDescricao()))
            .when(agenteAutorizadoService)
            .atualizarEmailSocioPrincipalInativo(eq("NOVOSOCIO@EMPRESA.COM.BR"), eq("NOVOSOCIO.INATIVO@EMPRESA.COM.BR"), eq(23));

        assertThatCode(() -> service
            .atualizarEmailSocioInativo(23))
            .isInstanceOf(IntegracaoException.class)
            .hasMessage(EErrors.ERRO_EMAIL_SOCIO_NAO_ATUALIZADO_NO_POL.getDescricao());

        verify(repository, times(1))
            .findById(eq(23));
        verify(repository, times(1))
            .save(eq(umSocioPrincipalComEmailAtualizado));
        verify(agenteAutorizadoService, times(1))
            .atualizarEmailSocioPrincipalInativo(eq("NOVOSOCIO@EMPRESA.COM.BR"), eq("NOVOSOCIO.INATIVO@EMPRESA.COM.BR"), eq(23));
    }

    @Test
    public void atualizarEmailSocioInativo_deveRetornarOk_quandoTudoOk() {
        var umSocioPrincipalComEmailAtualizado = umSocioPrincipal();
        umSocioPrincipalComEmailAtualizado.setEmail("NOVOSOCIO.INATIVO@EMPRESA.COM.BR");

        when(repository.findById(eq(23))).thenReturn(Optional.of(umSocioPrincipal()));
        when(repository.save(eq(umSocioPrincipalComEmailAtualizado))).thenReturn(umSocioPrincipalComEmailAtualizado);

        service.atualizarEmailSocioInativo(23);

        verify(repository, times(1))
            .findById(eq(23));
        verify(repository, times(1))
            .save(eq(umSocioPrincipalComEmailAtualizado));
        verify(agenteAutorizadoService, times(1))
            .atualizarEmailSocioPrincipalInativo(eq("NOVOSOCIO@EMPRESA.COM.BR"), eq("NOVOSOCIO.INATIVO@EMPRESA.COM.BR"), eq(23));
    }

    private Usuario umSocioPrincipal() {
        return Usuario.builder()
            .id(23)
            .email("NOVOSOCIO@EMPRESA.COM.BR")
            .cpf("183.381.665-02")
            .situacao(ESituacao.A)
            .build();
    }

    private Usuario umAntigoSocioPrincipal(ESituacao situacao) {
        return Usuario.builder()
            .id(22)
            .email("ANTIGOSOCIO@EMPRESA.COM.BR")
            .cpf("93275298631")
            .situacao(situacao)
            .build();
    }

    @Test
    public void findByEmail_deveRetornarUmUsuario_quandoUsuarioAtivo() {
        var usuario = umUsuarioCompleto();
        when(repository.findByEmail(usuario.getEmail())).thenReturn(Optional.of(usuario));

        assertThat(service.findByEmail(usuario.getEmail())).isNotNull();
        verify(repository).findByEmail(usuario.getEmail());
    }

    @Test
    public void findByEmail_deveLancarException_quandoUsuarioNaoEncontrado() {
        var usuarioInativo = umUsuarioInativo();

        assertThatExceptionOfType(ValidacaoException.class)
            .isThrownBy(() -> service.findByEmail(usuarioInativo.getEmail()))
            .withMessage("Usuário não encontrado.");

        verify(repository).findByEmail(usuarioInativo.getEmail());
    }

    @Test
    public void findUsuarioD2dByCpf_deveRetornarUsuarioSubCanalResponse_seUsuarioExistir() {
        var usuario = umUsuarioCompleto(OPERACAO_TELEVENDAS, 120,
            OPERACAO, CodigoDepartamento.COMERCIAL, ECanal.D2D_PROPRIO);
        usuario.setSubCanais(Set.of(umSubCanalInsideSales()));

        var cpf = "11122233344";
        var predicate = new UsuarioPredicate();
        predicate.comCpf(cpf)
            .comCanalD2d(true);

        when(repository.findByPredicate(predicate.build())).thenReturn(Optional.of(usuario));

        assertThat(service.findUsuarioD2dByCpf(cpf))
            .extracting("id", "nome", "codigoNivel", "subCanais")
            .containsExactly(1, "NOME UM", OPERACAO, Set.of(umSubCanalDto(4, INSIDE_SALES_PME, "Inside Sales PME")));
    }

    @Test
    public void findUsuarioD2dByCpf_deveRetornarNovoObjeto_seUsuarioNaoExistir() {
        assertThat(service.findUsuarioD2dByCpf("00000000000")).isEqualTo(null);

        verify(repository, times(1)).findByPredicate(any());
    }

    @Test
    public void save_deveAdicionarPermissaoSocialHubEEnviarDadosParaFilaSocialHub_quandoMercadoDesenvolvimentoPresente() {
        var usuario = umUsuarioSocialHub("emailteste@xbrain.com.br", 1, XBRAIN);

        doReturn(Optional.of(usuario))
            .when(repository)
            .findById(1);
        when(repository.findById(2))
            .thenReturn(Optional.of(usuario));
        when(repository.getCanaisByUsuarioIds(anyList()))
            .thenReturn(List.of(new Canal(2, ECanal.INTERNET)));
        doReturn(umUsuarioAutenticadoCanalInternet(SUPERVISOR_OPERACAO))
            .when(autenticacaoService)
            .getUsuarioAutenticado();
        when(cargoService.findByUsuarioId(1))
            .thenReturn(umCargo(1, SUPERVISOR_OPERACAO));
        when(permissaoEspecialService.hasPermissaoEspecialAtiva(usuario.getId(), ROLE_SHB))
            .thenReturn(true);

        assertThatCode(() -> service.save(usuario))
            .doesNotThrowAnyException();

        verify(permissaoEspecialService).save(anyList());
        verify(usuarioMqSender).enviarDadosUsuarioParaSocialHub(any());
    }

    @Test
    public void save_naoDeveAdicionarPermissaoSocialHubENaoEnviarDadosParaFila_quandoMercadoDesenvolvimentoNaoPresente() {
        var usuario = umUsuarioSocialHub("emailteste@xbrain.com.br", null, XBRAIN);

        doReturn(Optional.of(usuario))
            .when(repository)
            .findById(1);

        when(repository.findById(2)).thenReturn(Optional.of(usuario));
        when(repository.getCanaisByUsuarioIds(anyList()))
            .thenReturn(List.of(new Canal(2, ECanal.INTERNET)));
        doReturn(umUsuarioAutenticadoCanalInternet(SUPERVISOR_OPERACAO))
            .when(autenticacaoService)
            .getUsuarioAutenticado();

        assertThatCode(() -> service.save(usuario))
            .doesNotThrowAnyException();

        verify(permissaoEspecialService, never()).save(anyList());
        verify(usuarioMqSender, never())
            .enviarDadosUsuarioParaSocialHub(UsuarioSocialHubRequestMq.from(usuario, List.of(1022), "Diretor"));
    }

    @Test
    public void save_deveRemoverPermissaoSocialHub_quandoMsoOuOperacaoSemTerritorioDesenvolvimento() {
        var usuario = umUsuarioSocialHub("emailteste@xbrain.com.br", 1, MSO);
        usuario.setTerritorioMercadoDesenvolvimentoId(null);

        doReturn(Optional.of(usuario))
            .when(repository)
            .findById(1);
        when(repository.findById(2))
            .thenReturn(Optional.of(usuario));
        when(repository.getCanaisByUsuarioIds(anyList()))
            .thenReturn(List.of(new Canal(2, ECanal.INTERNET)));
        doReturn(umUsuarioAutenticadoCanalInternet(SUPERVISOR_OPERACAO))
            .when(autenticacaoService)
            .getUsuarioAutenticado();
        when(cargoService.findByUsuarioId(1))
            .thenReturn(umCargo(1, SUPERVISOR_OPERACAO));
        when(permissaoEspecialService.hasPermissaoEspecialAtiva(usuario.getId(), ROLE_SHB))
            .thenReturn(true);

        assertThatCode(() -> service.save(usuario))
            .doesNotThrowAnyException();

        verify(permissaoEspecialRepository).deletarPermissaoEspecialBy(List.of(ROLE_SHB), List.of(usuario.getId()));
    }

    @Test
    public void save_naoDeveRemoverPermissaoSocialHub_quandoOutroNivelSemTerritorioDesenvolvimento() {
        var usuario = umUsuarioSocialHub("emailteste@xbrain.com.br", 1, XBRAIN);
        usuario.setTerritorioMercadoDesenvolvimentoId(null);

        doReturn(Optional.of(usuario))
            .when(repository)
            .findById(1);
        when(repository.findById(2))
            .thenReturn(Optional.of(usuario));
        when(repository.getCanaisByUsuarioIds(anyList()))
            .thenReturn(List.of(new Canal(2, ECanal.INTERNET)));
        doReturn(umUsuarioAutenticadoCanalInternet(SUPERVISOR_OPERACAO))
            .when(autenticacaoService)
            .getUsuarioAutenticado();
        when(cargoService.findByUsuarioId(1))
            .thenReturn(umCargo(1, SUPERVISOR_OPERACAO));
        when(permissaoEspecialService.hasPermissaoEspecialAtiva(usuario.getId(), ROLE_SHB))
            .thenReturn(true);

        assertThatCode(() -> service.save(usuario))
            .doesNotThrowAnyException();

        verify(permissaoEspecialRepository, never()).deletarPermissaoEspecialBy(anyList(), anyList());
    }

    @Test
    public void findByIdComAa_deveLancarException_quandoUsuarioNaoEncontrado() {
        when(repository.findById(1)).thenReturn(Optional.empty());

        assertThatExceptionOfType(ValidacaoException.class)
            .isThrownBy(() -> service.findByIdComAa(1))
            .withMessage("Usuario não encontrado.");

        verify(repository).findById(1);
    }

    @Test
    public void findByIdComAa_deveRetornarUmUsuario_quandoUsuarioEncontrado() {
        var usuario = umUsuarioCompleto();
        when(repository.findById(1)).thenReturn(Optional.of(usuario));

        assertThat(service.findByIdComAa(1))
            .extracting("nome", "cpf")
            .containsExactly("NOME UM", "111.111.111-11");

        verify(repository).findById(1);
    }

    @Test
    public void findByAndCpfAndSituacaoIsNot_deveRetornarUsuarioDto_quandoSolicitar() {
        doReturn(Optional.of(umUsuarioCompleto()))
            .when(repository)
            .findByCpfAndSituacaoIsNot("38957979875", ESituacao.R);

        assertThat(service.findByCpfAndSituacaoIsNot("38957979875", ESituacao.R))
            .extracting("id", "nome", "cpf")
            .containsExactly(1, "NOME UM", "111.111.111-11");

        verify(repository).findByCpfAndSituacaoIsNot("38957979875", ESituacao.R);
    }

    @Test
    public void findByAndCpfAndSituacaoIsNot_deveRetornarException_quandoNaoEncontrarUsuario() {
        assertThatExceptionOfType(ValidacaoException.class)
            .isThrownBy(() -> service.findByCpfAndSituacaoIsNot("123456789", ESituacao.R))
            .withMessage("Usuário não encontrado.");
    }

    private Usuario outroUsuarioNivelOpCanalAa() {
        var usuario = Usuario
            .builder()
            .id(2)
            .nome("NOME DOIS")
            .email("email@email.com")
            .cpf("111.111.111-11")
            .situacao(ESituacao.A)
            .loginNetSales("login123")
            .cargo(Cargo
                .builder()
                .codigo(EXECUTIVO_HUNTER)
                .nivel(Nivel
                    .builder()
                    .codigo(OPERACAO)
                    .situacao(ESituacao.A)
                    .nome("OPERACAO")
                    .build())
                .build())
            .departamento(Departamento
                .builder()
                .nome("DEPARTAMENTO UM")
                .build())
            .unidadesNegocios(List.of(UnidadeNegocio
                .builder()
                .codigo(CodigoUnidadeNegocio.CLARO_RESIDENCIAL)
                .build()))
            .empresas(List.of(Empresa
                .builder()
                .codigo(CodigoEmpresa.CLARO_TV)
                .build()))
            .canais(Set.of(ECanal.AGENTE_AUTORIZADO))
            .build();
        return usuario;
    }

    private UsuarioResponse outroUsuarioNivelOpCanalAaResponse() {
        var usuarioResponse = UsuarioResponse
            .builder()
            .id(2)
            .nome("NOME DOIS")
            .email("email@email.com")
            .cpf("111.111.111-11")
            .rg(null)
            .telefone(null)
            .telefone02(null)
            .telefone03(null)
            .situacao(ESituacao.A)
            .loginNetSales("login123")
            .dataCadastro(null)
            .codigoNivel(OPERACAO)
            .nomeNivel("OPERACAO")
            .codigoDepartamento(null)
            .codigoCargo(EXECUTIVO_HUNTER)
            .canais(Set.of(ECanal.AGENTE_AUTORIZADO))
            .permissoes(null)
            .nascimento(null)
            .aaId(null)
            .canais(Set.of(ECanal.AGENTE_AUTORIZADO))
            .tipoCanal(null)
            .codigoUnidadesNegocio(List.of(CodigoUnidadeNegocio.CLARO_RESIDENCIAL))
            .codigoEmpresas(List.of(CodigoEmpresa.CLARO_TV))
            .build();

        return usuarioResponse;
    }

    private Canal umCanal() {
        return Canal
            .builder()
            .usuarioId(1)
            .canal(ECanal.AGENTE_AUTORIZADO)
            .build();
    }

    private Canal umOutroCanal() {
        return Canal
            .builder()
            .usuarioId(1)
            .canal(ECanal.VAREJO)
            .build();
    }

    private AgenteAutorizadoUsuarioDto umAgenteAutorizadoUsuarioDto() {
        return AgenteAutorizadoUsuarioDto
            .builder()
            .usuarioId(2)
            .cnpj("78300110000166")
            .razaoSocial("Razao Social")
            .build();
    }

    private UsuarioCsvResponse umUsuarioOperacaoCsv() {
        return UsuarioCsvResponse
            .builder()
            .id(1)
            .nome("Usuario_1_teste")
            .email("usuario1@teste.com")
            .telefone("999999999")
            .cpf("11111111111")
            .cargo("cargo")
            .departamento("departamento")
            .unidadesNegocios("unidadeNegocio")
            .empresas("empresa")
            .situacao(ESituacao.A)
            .dataUltimoAcesso(LocalDateTime.of(2021, 1, 1, 1, 1))
            .loginNetSales("loginNetSales")
            .nivel("Operação")
            .hierarquia("hierarquia")
            .razaoSocial("razaoSocial")
            .cnpj("cnpj")
            .organizacaoEmpresa("organizacao")
            .build();
    }

    private UsuarioCsvResponse umUsuarioAaCsv() {
        return UsuarioCsvResponse
            .builder()
            .id(2)
            .nome("Usuario_2_teste")
            .email("usuario2@teste.com")
            .telefone("999999998")
            .cpf("22222222222")
            .cargo("cargo")
            .departamento("departamento")
            .unidadesNegocios("unidadeNegocio")
            .empresas("empresa")
            .situacao(ESituacao.A)
            .dataUltimoAcesso(LocalDateTime.of(2021, 1, 1, 1, 1))
            .loginNetSales("loginNetSales")
            .nivel("Agente Autorizado")
            .organizacaoEmpresa("organizacao")
            .build();
    }

    private UsuarioDtoVendas umUsuarioDtoVendas(Integer id) {
        return UsuarioDtoVendas
            .builder()
            .id(id)
            .build();
    }

    private Usuario umUsuarioCompleto(CodigoCargo codigoCargo, Integer idCargo,
                                      CodigoNivel nivel, CodigoDepartamento departamento, ECanal canal) {
        var usuario = Usuario
            .builder()
            .id(1)
            .email("email@email.com")
            .nome("NOME UM")
            .cpf("111.111.111-11")
            .situacao(ESituacao.A)
            .loginNetSales("login123")
            .cargo(Cargo
                .builder()
                .id(idCargo)
                .codigo(codigoCargo)
                .nivel(Nivel
                    .builder()
                    .codigo(nivel)
                    .build())
                .build())
            .departamento(Departamento
                .builder()
                .codigo(departamento)
                .nome("DEPARTAMENTO UM")
                .build())
            .unidadesNegocios(List.of(UnidadeNegocio
                .builder()
                .id(1)
                .nome("UNIDADE NEGÓCIO UM")
                .build()))
            .empresas(List.of(Empresa
                .builder()
                .nome("EMPRESA UM")
                .build()))
            .build();

        usuario.setCidades(
            Sets.newHashSet(
                List.of(UsuarioCidade.criar(
                    usuario,
                    3237,
                    100
                ))
            )
        );
        usuario.setUsuariosHierarquia(
            Sets.newHashSet(
                UsuarioHierarquia.criar(
                    usuario,
                    65,
                    100)
            )
        );
        usuario.setCanais(
            Sets.newHashSet(
                List.of(canal)
            )
        );

        return usuario;
    }

    private Usuario umUsuarioCompleto(ESituacao situacao, CodigoCargo codigoCargo, Integer idCargo,
                                      CodigoNivel nivel, CodigoDepartamento departamento, ECanal canal) {
        var usuario = Usuario
            .builder()
            .id(1)
            .email("email@email.com")
            .nome("NOME UM")
            .cpf("111.111.111-11")
            .situacao(situacao)
            .loginNetSales("login123")
            .cargo(Cargo
                .builder()
                .id(idCargo)
                .codigo(codigoCargo)
                .nivel(Nivel
                    .builder()
                    .codigo(nivel)
                    .build())
                .build())
            .departamento(Departamento
                .builder()
                .codigo(departamento)
                .nome("DEPARTAMENTO UM")
                .build())
            .unidadesNegocios(List.of(UnidadeNegocio
                .builder()
                .id(1)
                .nome("UNIDADE NEGÓCIO UM")
                .build()))
            .empresas(List.of(Empresa
                .builder()
                .nome("EMPRESA UM")
                .build()))
            .build();

        usuario.setCidades(
            Sets.newHashSet(
                List.of(UsuarioCidade.criar(
                    usuario,
                    3237,
                    100
                ))
            )
        );
        usuario.setUsuariosHierarquia(
            Sets.newHashSet(
                UsuarioHierarquia.criar(
                    usuario,
                    65,
                    100)
            )
        );
        usuario.setCanais(
            Sets.newHashSet(
                List.of(canal)
            )
        );

        return usuario;
    }

    private Usuario umUsuarioCompleto() {
        var usuario = Usuario
            .builder()
            .id(1)
            .nome("NOME UM")
            .email("email@email.com")
            .cpf("111.111.111-11")
            .situacao(ESituacao.A)
            .loginNetSales("login123")
            .cargo(Cargo
                .builder()
                .codigo(OPERACAO_TELEVENDAS)
                .nivel(Nivel
                    .builder()
                    .codigo(CodigoNivel.AGENTE_AUTORIZADO)
                    .nome("AGENTE AUTORIZADO")
                    .build())
                .build())
            .departamento(Departamento
                .builder()
                .nome("DEPARTAMENTO UM")
                .build())
            .unidadesNegocios(List.of(UnidadeNegocio
                .builder()
                .id(1)
                .nome("UNIDADE NEGÓCIO UM")
                .build()))
            .empresas(List.of(Empresa
                .builder()
                .nome("EMPRESA UM")
                .build()))
            .build();

        usuario.setCidades(
            Sets.newHashSet(
                List.of(UsuarioCidade.criar(
                    usuario,
                    3237,
                    100
                ))
            )
        );
        usuario.setUsuariosHierarquia(
            Sets.newHashSet(
                UsuarioHierarquia.criar(
                    usuario,
                    65,
                    100)
            )
        );
        usuario.setCanais(
            Sets.newHashSet(
                List.of(ECanal.ATIVO_PROPRIO)
            )
        );

        return usuario;
    }

    private Usuario outroUsuarioCompleto() {
        var usuario = Usuario
            .builder()
            .id(2)
            .nome("NOME DOIS")
            .email("email@email.com")
            .cpf("111.111.111-11")
            .situacao(ESituacao.A)
            .loginNetSales("login123")
            .cargo(Cargo
                .builder()
                .codigo(EXECUTIVO_HUNTER)
                .nivel(Nivel
                    .builder()
                    .codigo(OPERACAO)
                    .situacao(ESituacao.A)
                    .nome("OPERACAO")
                    .build())
                .build())
            .departamento(Departamento
                .builder()
                .nome("DEPARTAMENTO UM")
                .build())
            .unidadesNegocios(List.of(UnidadeNegocio
                .builder()
                .nome("UNIDADE NEGÓCIO UM")
                .build()))
            .empresas(List.of(Empresa
                .builder()
                .nome("EMPRESA UM")
                .build()))
            .build();

        usuario.setCidades(
            Sets.newHashSet(
                List.of(UsuarioCidade.criar(
                    usuario,
                    3237,
                    100
                ))
            )
        );
        usuario.setUsuariosHierarquia(
            Sets.newHashSet(
                UsuarioHierarquia.criar(
                    usuario,
                    65,
                    100)
            )
        );
        usuario.setCanais(
            Sets.newHashSet(
                List.of(ECanal.ATIVO_PROPRIO)
            )
        );

        return usuario;
    }

    private Usuario umVendedorReceptivo() {
        var usuario = umUsuarioCompleto();
        var cargo = Cargo.builder()
            .codigo(CodigoCargo.VENDEDOR_RECEPTIVO)
            .nivel(Nivel.builder().codigo(CodigoNivel.RECEPTIVO).build())
            .build();
        var organizacaoEmpresa = OrganizacaoEmpresa.builder().id(1).nome("Org teste").build();
        usuario.setCargo(cargo);
        usuario.setOrganizacaoEmpresa(organizacaoEmpresa);
        return usuario;
    }

    private SelectResponse umSelectResponseDeVendedorReceptivoInativo() {
        var vendedorReceptivo = umVendedorReceptivo();
        return SelectResponse
            .builder()
            .label(vendedorReceptivo.getNome().concat(" (INATIVO)"))
            .value(vendedorReceptivo.getId())
            .build();
    }

    private SelectResponse umSelectResponseDeVendedorReceptivoRealocado() {
        var vendedorReceptivo = umVendedorReceptivo();
        return SelectResponse
            .builder()
            .label(vendedorReceptivo.getNome().concat(" (REALOCADO)"))
            .value(vendedorReceptivo.getId())
            .build();
    }

    private UsuarioFiltros umUsuarioFiltro() {
        return UsuarioFiltros.builder()
            .codigosCargos(List.of(SUPERVISOR_OPERACAO, ASSISTENTE_OPERACAO))
            .canal(ECanal.D2D_PROPRIO)
            .build();
    }

    private UsuarioFiltros umUsuarioFiltroInternet() {
        return UsuarioFiltros.builder()
            .codigosCargos(List.of(INTERNET_VENDEDOR))
            .canal(ECanal.INTERNET)
            .build();
    }

    private UsuarioAtivacaoDto umUsuarioAtivacaoDto() {
        return UsuarioAtivacaoDto.builder()
            .idUsuario(10)
            .idUsuarioAtivacao(20)
            .observacao("Teste")
            .build();
    }

    private Usuario umUsuarioSocioPrincipalEAa() {
        var usuario = umUsuarioCompleto();
        usuario.setCargo(Cargo
            .builder()
            .codigo(AGENTE_AUTORIZADO_SOCIO)
            .nivel(Nivel
                .builder()
                .codigo(CodigoNivel.AGENTE_AUTORIZADO)
                .nome("AGENTE AUTORIZADO")
                .build())
            .build());
        return usuario;
    }

    private Usuario umUsuarioComCargoEOrganizacao(Integer cargoId, Integer organizacaoId) {
        return Usuario.builder()
            .id(100)
            .cargo(Cargo.builder().id(cargoId).codigo(OPERADOR_SUPORTE_VENDAS).build())
            .organizacaoEmpresa(OrganizacaoEmpresa.builder()
                .id(organizacaoId)
                .nivel(Nivel.builder().codigo(BACKOFFICE_SUPORTE_VENDAS).build())
                .build())
            .email("email@google.com")
            .build();
    }

    @TestConfiguration
    static class MockitoPublisherConfiguration {

        @Bean
        @Primary
        static ApplicationEventPublisher publisher() {
            return mock(ApplicationEventPublisher.class);
        }
    }
}
