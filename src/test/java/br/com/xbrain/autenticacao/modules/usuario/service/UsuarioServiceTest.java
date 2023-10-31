package br.com.xbrain.autenticacao.modules.usuario.service;

import br.com.xbrain.autenticacao.modules.agenteautorizadonovo.dto.UsuarioDtoVendas;
import br.com.xbrain.autenticacao.modules.agenteautorizadonovo.service.AgenteAutorizadoNovoService;
import br.com.xbrain.autenticacao.modules.autenticacao.dto.UsuarioAutenticado;
import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.comum.dto.PageRequest;
import br.com.xbrain.autenticacao.modules.comum.dto.SelectResponse;
import br.com.xbrain.autenticacao.modules.comum.enums.CodigoEmpresa;
import br.com.xbrain.autenticacao.modules.comum.enums.CodigoUnidadeNegocio;
import br.com.xbrain.autenticacao.modules.comum.enums.EErrors;
import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.comum.exception.IntegracaoException;
import br.com.xbrain.autenticacao.modules.comum.exception.ValidacaoException;
import br.com.xbrain.autenticacao.modules.comum.model.Empresa;
import br.com.xbrain.autenticacao.modules.comum.model.Organizacao;
import br.com.xbrain.autenticacao.modules.comum.model.SubCluster;
import br.com.xbrain.autenticacao.modules.comum.model.UnidadeNegocio;
import br.com.xbrain.autenticacao.modules.comum.repository.EmpresaRepository;
import br.com.xbrain.autenticacao.modules.comum.repository.UnidadeNegocioRepository;
import br.com.xbrain.autenticacao.modules.equipevenda.dto.EquipeVendaUsuarioResponse;
import br.com.xbrain.autenticacao.modules.equipevenda.service.EquipeVendaD2dService;
import br.com.xbrain.autenticacao.modules.equipevenda.service.EquipeVendasUsuarioService;
import br.com.xbrain.autenticacao.modules.feeder.service.FeederUtil;
import br.com.xbrain.autenticacao.modules.mailing.service.MailingService;
import br.com.xbrain.autenticacao.modules.notificacao.service.NotificacaoService;
import br.com.xbrain.autenticacao.modules.parceirosonline.dto.AgenteAutorizadoResponse;
import br.com.xbrain.autenticacao.modules.parceirosonline.dto.UsuarioAgenteAutorizadoResponse;
import br.com.xbrain.autenticacao.modules.parceirosonline.service.AgenteAutorizadoClient;
import br.com.xbrain.autenticacao.modules.parceirosonline.service.AgenteAutorizadoService;
import br.com.xbrain.autenticacao.modules.site.service.SiteService;
import br.com.xbrain.autenticacao.modules.usuario.dto.*;
import br.com.xbrain.autenticacao.modules.usuario.enums.*;
import br.com.xbrain.autenticacao.modules.usuario.helpers.UsuarioAutenticadoHelper;
import br.com.xbrain.autenticacao.modules.usuario.helpers.UsuarioHelper;
import br.com.xbrain.autenticacao.modules.usuario.model.*;
import br.com.xbrain.autenticacao.modules.usuario.predicate.UsuarioPredicate;
import br.com.xbrain.autenticacao.modules.usuario.rabbitmq.UsuarioEquipeVendaMqSender;
import br.com.xbrain.autenticacao.modules.usuario.repository.CargoRepository;
import br.com.xbrain.autenticacao.modules.usuario.repository.UsuarioCidadeRepository;
import br.com.xbrain.autenticacao.modules.usuario.repository.UsuarioHierarquiaRepository;
import br.com.xbrain.autenticacao.modules.usuario.repository.UsuarioRepository;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import helpers.TestBuilders;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static br.com.xbrain.autenticacao.modules.comum.enums.EErrors.ERRO_VALIDAR_EMAIL_CADASTRADO;
import static br.com.xbrain.autenticacao.modules.feeder.helper.VendedoresFeederFiltrosHelper.umVendedoresFeederFiltros;
import static br.com.xbrain.autenticacao.modules.site.helper.SiteHelper.umSite;
import static br.com.xbrain.autenticacao.modules.usuario.controller.UsuarioGerenciaControllerTest.*;
import static br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo.*;
import static br.com.xbrain.autenticacao.modules.usuario.enums.CodigoFuncionalidade.AUT_VISUALIZAR_GERAL;
import static br.com.xbrain.autenticacao.modules.usuario.enums.CodigoFuncionalidade.CTR_VISUALIZAR_CARTEIRA_HIERARQUIA;
import static br.com.xbrain.autenticacao.modules.usuario.enums.CodigoNivel.OPERACAO;
import static br.com.xbrain.autenticacao.modules.usuario.helpers.CargoHelper.umCargo;
import static br.com.xbrain.autenticacao.modules.usuario.helpers.UsuarioPredicateHelper.umVendedoresFeederPredicateComSocioPrincipalESituacaoAtiva;
import static br.com.xbrain.autenticacao.modules.usuario.helpers.UsuarioPredicateHelper.umVendedoresFeederPredicateComSocioPrincipalETodasSituacaoes;
import static br.com.xbrain.autenticacao.modules.usuario.helpers.UsuarioResponseHelper.umUsuarioResponse;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.*;
import static org.springframework.data.domain.Sort.Direction.ASC;

@RunWith(MockitoJUnitRunner.class)
public class UsuarioServiceTest {

    public static final String MSG_ERRO_ATIVAR_USUARIO_INATIVADO_POR_MUITAS_SIMULACOES = "Não foi possível ativar usuário. "
        + "O usuário foi inativado por realizar muitas simulações, por favor entre em contato com algum usuário XBrain "
        + "para que ele possa reativar o usuário.";
    @InjectMocks
    private UsuarioService usuarioService;
    @Mock
    private UsuarioService service;
    @Mock
    private UsuarioRepository usuarioRepository;
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
    private AgenteAutorizadoService agenteAutorizadoService;
    @Mock
    private AgenteAutorizadoNovoService agenteAutorizadoNovoService;
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
    private UsuarioClientService usuarioClientService;
    @Mock
    private EquipeVendasUsuarioService equipeVendasUsuarioService;
    @Mock
    private AgenteAutorizadoClient agenteAutorizadoClient;
    @Captor
    private ArgumentCaptor<Usuario> usuarioCaptor;

    private static UsuarioExecutivoResponse umUsuarioExecutivo() {
        return new UsuarioExecutivoResponse(1, "bakugo@teste.com", "BAKUGO");
    }

    private static UsuarioSituacaoResponse umUsuarioSituacaoResponse(Integer id, String nome, ESituacao situacao) {
        return UsuarioSituacaoResponse
            .builder()
            .id(id)
            .nome(nome)
            .situacao(situacao)
            .build();
    }

    private static UsuarioAgenteAutorizadoResponse umUsuarioAgenteAutorizadoResponse(Integer id, Integer aaId) {
        return UsuarioAgenteAutorizadoResponse.builder()
            .id(id)
            .nome("FULANO DE TESTE")
            .email("TESTE@TESTE.COM")
            .agenteAutorizadoId(aaId)
            .build();
    }

    @Test
    public void buscarNaoRealocadoByCpf_deveRetornarUsuarioNaoRealocado_quandoCpfForValido() {
        when(usuarioRepository
            .findTop1UsuarioByCpfAndSituacaoNotOrderByDataCadastroDesc(eq("09723864592"), eq(ESituacao.R)))
            .thenReturn(Optional.of(umUsuario()));

        assertThat(usuarioService.buscarNaoRealocadoByCpf("097.238.645-92"))
            .extracting(UsuarioResponse::getId, UsuarioResponse::getCpf)
            .containsExactly(1, "097.238.645-92");
    }

    @Test
    public void buscarNaoRealocadoByCpf_deveRetornarNull_quandoCpfNaoExistir() {
        when(usuarioRepository
            .findTop1UsuarioByCpfAndSituacaoNotOrderByDataCadastroDesc(anyString(), eq(ESituacao.R)))
            .thenReturn(Optional.empty());
        assertThat(usuarioService.buscarNaoRealocadoByCpf("86271666418"))
            .extracting(UsuarioResponse::getId, UsuarioResponse::getNome, UsuarioResponse::getCpf,
                UsuarioResponse::getEmail, UsuarioResponse::getSituacao, UsuarioResponse::getCodigoCargo)
            .containsExactly(null, null, null, null, null, null);
    }

    @Test
    public void ativar_deveAlterarSituacaoUsuario_quandoOMesmoForSocioPrincialEAa() {
        doReturn(TestBuilders.umUsuarioAutenticadoAdmin(1))
            .when(autenticacaoService)
            .getUsuarioAutenticado();

        doReturn(Optional.of("INATIVADO POR REALIZAR MUITAS SIMULAÇÕES"))
            .when(usuarioHistoricoService)
            .findMotivoInativacaoByUsuarioId(1);

        doReturn(Optional.of(umUsuarioSocioPrincipalEAa()))
            .when(usuarioRepository)
            .findComplete(anyInt());

        doReturn(true)
            .when(agenteAutorizadoNovoService)
            .existeAaAtivoBySocioEmail(anyString());

        usuarioService.ativar(umUsuarioAtivacaoDto());
        verify(usuarioClientService, times(1)).alterarSituacao(1);
    }

    @Test
    public void ativar_NaoDeveAlterarSituacaoUsuario_quandoOMesmoForSocioPrincialEAa() {
        doReturn(TestBuilders.umUsuarioAutenticadoAdmin(1))
            .when(autenticacaoService)
            .getUsuarioAutenticado();

        doReturn(Optional.of(outroUsuarioCompleto()))
            .when(usuarioRepository)
            .findComplete(anyInt());

        usuarioService.ativar(umUsuarioAtivacaoDto());
        verify(usuarioClientService, never()).alterarSituacao(2);
    }

    @Test
    public void ativarUsuarioOperadorTelevendas_deveAlterarSituacaoUsuario_quandoUsuarioAtivacaoForUsuarioXBrainOuMSO() {
        doReturn(Optional.of(umUsuarioCompleto(ESituacao.I, OPERACAO_TELEVENDAS, 120,
            OPERACAO, CodigoDepartamento.COMERCIAL, ECanal.ATIVO_PROPRIO)))
            .when(usuarioRepository)
            .findComplete(anyInt());

        doReturn(TestBuilders.umUsuarioAutenticadoAdmin(1))
            .when(autenticacaoService)
            .getUsuarioAutenticado();

        doReturn(Optional.of("INATIVADO POR REALIZAR MUITAS SIMULAÇÕES"))
            .when(usuarioHistoricoService)
            .findMotivoInativacaoByUsuarioId(anyInt());

        usuarioService.ativar(umUsuarioAtivacaoDto());

        assertThat(umUsuarioCompleto(OPERACAO_TELEVENDAS, 120,
            OPERACAO, CodigoDepartamento.COMERCIAL, ECanal.ATIVO_PROPRIO))
            .extracting("situacao", "cargo.id", "cargo.codigo")
            .containsExactly(ESituacao.A, 120, OPERACAO_TELEVENDAS);
    }

    @Test(expected = ValidacaoException.class)
    public void ativarUsuarioOperadorTelevendas_naoDeveAlterarSituacaoUsuario_quandoUsuarioAtivacaoNaoForUsuarioXBrainOuMSO() {
        doReturn(Optional.of(umUsuarioCompleto(ESituacao.I, OPERACAO_TELEVENDAS, 120,
            OPERACAO, CodigoDepartamento.COMERCIAL, ECanal.ATIVO_PROPRIO)))
            .when(usuarioRepository)
            .findComplete(anyInt());

        doReturn(TestBuilders.umUsuarioAutenticado(1, DIRETOR_OPERACAO, "Operação"))
            .when(autenticacaoService)
            .getUsuarioAutenticado();

        doReturn(Optional.of("INATIVADO POR REALIZAR MUITAS SIMULAÇÕES"))
            .when(usuarioHistoricoService)
            .findMotivoInativacaoByUsuarioId(anyInt());

        usuarioService.ativar(umUsuarioAtivacaoDto());

    }

    @Test
    public void inativar_deveRetornarExcecao_quandoUsuarioAtivoLocalEPossuiAgendamento() {
        when(mailingService.countQuantidadeAgendamentosProprietariosDoUsuario(eq(umUsuario().getId()), eq(ECanal.ATIVO_PROPRIO)))
            .thenReturn(Long.valueOf(1));
        when(usuarioRepository.findComplete(eq(1))).thenReturn(Optional.of(umUsuarioCompleto()));

        assertThatExceptionOfType(ValidacaoException.class)
            .isThrownBy(() -> usuarioService.inativar(umUsuarioInativoDto()))
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
        when(usuarioRepository.findComplete(eq(1))).thenReturn(Optional.of(usuario));

        assertThatCode(() -> usuarioService.inativar(umUsuarioInativoDto()))
            .doesNotThrowAnyException();

        verify(mailingService, never()).countQuantidadeAgendamentosProprietariosDoUsuario(any(), any());
    }

    @Test
    public void inativar_deveInativarUsuario_quandoUsuarioAtivoLocalESemAgendamento() {
        when(mailingService.countQuantidadeAgendamentosProprietariosDoUsuario(eq(umUsuario().getId()), eq(ECanal.ATIVO_PROPRIO)))
            .thenReturn(Long.valueOf(0));
        when(usuarioRepository.findComplete(eq(1))).thenReturn(Optional.of(umUsuarioCompleto()));
        when(motivoInativacaoService.findByCodigoMotivoInativacao(eq(CodigoMotivoInativacao.DEMISSAO)))
            .thenReturn(MotivoInativacao.builder().codigo(CodigoMotivoInativacao.DEMISSAO).build());

        assertThatCode(() -> usuarioService.inativar(umUsuarioInativoDto()))
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
                .id(22)
                .canais(Set.of(ECanal.ATIVO_PROPRIO, ECanal.AGENTE_AUTORIZADO))
                .build())
            .canais(Set.of(ECanal.D2D_PROPRIO))
            .build();

        assertThatExceptionOfType(ValidacaoException.class)
            .isThrownBy(() -> usuarioService.save(usuario))
            .withMessage("Usuário sem permissão para o cargo com os canais.");
    }

    @Test
    public void findCompleteByIdComLoginNetSales_deveDispararExcecao_seUsuarioNaoEncontrado() {
        when(usuarioRepository.findComplete(eq(1))).thenReturn(Optional.empty());

        assertThatExceptionOfType(ValidacaoException.class)
            .isThrownBy(() -> usuarioService.findCompleteByIdComLoginNetSales(1))
            .withMessage("Usuário não encontrado.");
    }

    @Test
    public void findCompleteByIdComLoginNetSales_deveDispararExcecao_seUsuarioNaoPossuirLoginNetsales() {
        var usuario = umUsuarioCompleto();
        usuario.setLoginNetSales(null);

        when(usuarioRepository.findComplete(eq(1))).thenReturn(Optional.of(usuario));

        assertThatExceptionOfType(ValidacaoException.class)
            .isThrownBy(() -> usuarioService.findCompleteByIdComLoginNetSales(1))
            .withMessage("Usuário não possui login NetSales válido.");
    }

    @Test
    public void findCompleteByIdComLoginNetSales_deveRetornarUsuarioCompleto_seUsuarioPossuirLoginNetsales() {
        when(usuarioRepository.findComplete(eq(1))).thenReturn(Optional.of(umUsuarioCompleto()));
        assertThat(usuarioService.findCompleteByIdComLoginNetSales(1)).isEqualTo(umUsuarioCompleto());
    }

    @Test
    public void getSubclustersUsuario_deveConverterORetornoEmSelectResponse_conformeListaDeSubclusters() {
        when(usuarioRepository.getSubclustersUsuario(anyInt()))
            .thenReturn(List.of(
                SubCluster.of(1, "TESTE1"),
                SubCluster.of(2, "TESTE2")));

        assertThat(usuarioService.getSubclusterUsuario(1))
            .extracting("value", "label")
            .containsExactly(
                tuple(1, "TESTE1"),
                tuple(2, "TESTE2"));
    }

    @Test
    public void buscarExecutivosPorSituacao_deveRetornarOsExecutivos() {
        when(usuarioRepository.findAllExecutivosBySituacao(eq(ESituacao.A)))
            .thenReturn(List.of(umUsuarioExecutivo()));

        assertThat(usuarioService.buscarExecutivosPorSituacao(ESituacao.A))
            .hasSize(1)
            .extracting("id", "nome")
            .containsExactly(
                tuple(1, "BAKUGO"));
    }

    @Test
    public void findById_deveRetornarUsuarioResponse_quandoSolicitado() {
        when(usuarioRepository.findById(1))
            .thenReturn(Optional.of(Usuario.builder()
                .id(1)
                .nome("RENATO")
                .build()));

        assertThat(usuarioService.findById(1))
            .extracting("id", "nome")
            .containsExactly(1, "RENATO");
    }

    @Test
    public void findById_deveRetornarException_quandoNaoEncontrarUsuarioById() {
        when(usuarioRepository.findById(1))
            .thenReturn(Optional.empty());

        assertThatCode(() -> usuarioService.findById(1))
            .hasMessage("Usuário não encontrado.")
            .isInstanceOf(ValidacaoException.class);
    }

    @Test
    public void findUsuariosByCodigoCargo_deveRetornarUsuariosAtivos_peloCodigoDoCargo() {
        when(usuarioRepository.findUsuariosByCodigoCargo(CodigoCargo.EXECUTIVO))
            .thenReturn(umaListaUsuariosExecutivosAtivo());

        assertThat(usuarioService.findUsuariosByCodigoCargo(CodigoCargo.EXECUTIVO))
            .extracting("id", "nome", "email", "codigoNivel", "codigoCargo", "codigoDepartamento", "situacao")
            .containsExactly(
                tuple(1, "JOSÉ", "JOSE@HOTMAIL.COM", CodigoNivel.AGENTE_AUTORIZADO,
                    CodigoCargo.EXECUTIVO, CodigoDepartamento.AGENTE_AUTORIZADO, ESituacao.A),
                tuple(2, "HIGOR", "HIGOR@HOTMAIL.COM", CodigoNivel.AGENTE_AUTORIZADO,
                    CodigoCargo.EXECUTIVO, CodigoDepartamento.AGENTE_AUTORIZADO, ESituacao.A));

        verify(usuarioRepository, times(1)).findUsuariosByCodigoCargo(CodigoCargo.EXECUTIVO);
    }

    @Test
    public void findIdUsuariosAtivosByCodigoCargos_deveRetornarListaIdUsuariosAtivos_pelosCodigosDosCargos() {
        var listaCargos = List.of(MSO_CONSULTOR, ADMINISTRADOR);
        when(usuarioRepository.findIdUsuariosAtivosByCodigoCargos(eq(listaCargos)))
            .thenReturn(List.of(24, 34));

        assertThat(usuarioService.findIdUsuariosAtivosByCodigoCargos(listaCargos))
            .isEqualTo(List.of(24, 34));
    }

    @Test
    public void salvarUsuarioBackoffice_deveSalvar() {
        when(autenticacaoService.getUsuarioAutenticado())
            .thenReturn(UsuarioAutenticadoHelper.umUsuarioAutenticadoNivelBackoffice());

        usuarioService.salvarUsuarioBackoffice(umUsuarioBackoffice());

        verify(empresaRepository, atLeastOnce()).findAllAtivo();
        verify(unidadeNegocioRepository, atLeastOnce()).findAllAtivo();
        verify(notificacaoService, atLeastOnce())
            .enviarEmailDadosDeAcesso(argThat(arg -> arg.getNome().equals("Backoffice")), anyString());
    }

    @Test
    public void salvarUsuarioBackoffice_deveRemoverCaracteresEspeciais() {
        var usaurio = umUsuarioBackoffice();
        when(autenticacaoService.getUsuarioAutenticado())
            .thenReturn(UsuarioAutenticadoHelper.umUsuarioAutenticadoNivelBackoffice());

        Assertions.assertThat(usaurio)
            .extracting("cpf")
            .containsExactly("097.238.645-92");

        usuarioService.salvarUsuarioBackoffice(usaurio);

        verify(usuarioRepository, times(1)).save(usuarioCaptor.capture());
        Assertions.assertThat(usuarioCaptor.getValue())
            .extracting("cpf")
            .containsExactly("09723864592");
    }

    @Test
    public void salvarUsuarioBackoffice_validacaoException_quandoCpfExistente() {
        when(autenticacaoService.getUsuarioAutenticado())
            .thenReturn(UsuarioAutenticadoHelper.umUsuarioAutenticadoNivelBackoffice());
        when(usuarioRepository.findTop1UsuarioByCpfAndSituacaoNot(any(), any()))
            .thenReturn(Optional.of(umUsuario()));

        Assertions.assertThatExceptionOfType(ValidacaoException.class)
            .isThrownBy(() -> usuarioService.salvarUsuarioBackoffice(umUsuarioBackoffice()))
            .withMessage("CPF já cadastrado.");

        verify(empresaRepository, atLeastOnce()).findAllAtivo();
        verify(unidadeNegocioRepository, atLeastOnce()).findAllAtivo();
        verify(usuarioRepository, atLeastOnce()).findTop1UsuarioByCpfAndSituacaoNot(eq("09723864592"), any());
        verify(usuarioRepository, never()).findTop1UsuarioByEmailIgnoreCaseAndSituacaoNot(any(), any());
    }

    @Test
    public void salvarUsuarioBackoffice_validacaoException_quandoEmailExistente() {
        when(autenticacaoService.getUsuarioAutenticado())
            .thenReturn(UsuarioAutenticadoHelper.umUsuarioAutenticadoNivelBackoffice());
        when(usuarioRepository.findTop1UsuarioByEmailIgnoreCaseAndSituacaoNot(any(), any()))
            .thenReturn(Optional.of(umUsuario()));

        Assertions.assertThatExceptionOfType(ValidacaoException.class)
            .isThrownBy(() -> usuarioService.salvarUsuarioBackoffice(umUsuarioBackoffice()))
            .withMessage("Email já cadastrado.");

        verify(empresaRepository, atLeastOnce()).findAllAtivo();
        verify(unidadeNegocioRepository, atLeastOnce()).findAllAtivo();
        verify(usuarioRepository, atLeastOnce()).findTop1UsuarioByCpfAndSituacaoNot(eq("09723864592"), any());
        verify(usuarioRepository, atLeastOnce()).findTop1UsuarioByEmailIgnoreCaseAndSituacaoNot(any(), any());
    }

    @Test
    public void salvarUsuarioBackoffice_validacaoException_quandoUsuarioNaoTiverPermissaoSobreOCanalParaOCargo() {
        when(autenticacaoService.getUsuarioAutenticado())
            .thenReturn(UsuarioAutenticadoHelper.umUsuarioAutenticadoNivelBackoffice());

        var usuario = Usuario.builder()
            .cargo(Cargo.builder()
                .id(22)
                .canais(Set.of(ECanal.ATIVO_PROPRIO, ECanal.AGENTE_AUTORIZADO))
                .build())
            .canais(Set.of(ECanal.D2D_PROPRIO))
            .build();

        assertThatExceptionOfType(ValidacaoException.class)
            .isThrownBy(() -> usuarioService.salvarUsuarioBackoffice(usuario))
            .withMessage("Usuário sem permissão para o cargo com os canais.");
    }

    private List<Usuario> umaListaUsuariosExecutivosAtivo() {
        return List.of(
            Usuario.builder()
                .id(1)
                .nome("JOSÉ")
                .email("JOSE@HOTMAIL.COM")
                .situacao(ESituacao.A)
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
                .situacao(ESituacao.A)
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
        when(usuarioRepository.findUsuariosByIds(any()))
            .thenReturn(List.of(
                umUsuarioSituacaoResponse(1, "JONATHAN", ESituacao.A),
                umUsuarioSituacaoResponse(2, "FLAVIA", ESituacao.I)));

        assertThat(usuarioService.findUsuariosByIds(List.of(1, 2)))
            .extracting("id", "nome", "situacao")
            .containsExactlyInAnyOrder(
                tuple(1, "JONATHAN", ESituacao.A),
                tuple(2, "FLAVIA", ESituacao.I));
    }

    @Test
    public void buscarUsuariosAtivosNivelOperacaoCanalAa_listaComDoisUsuarios_quandoSituacaoAtivoECanalAa() {
        when(usuarioRepository.findAllAtivosByNivelOperacaoCanalAa())
            .thenReturn(List.of(
                SelectResponse.of(100, "JOSÉ"),
                SelectResponse.of(101, "JOÃO")
            ));

        assertThat(usuarioService.buscarUsuariosAtivosNivelOperacaoCanalAa())
            .extracting("value", "label")
            .containsExactly(
                tuple(100, "JOSÉ"),
                tuple(101, "JOÃO")
            );
    }

    @Test
    public void getVendedoresByIds_deveRetornarUsuarios() {
        when(usuarioRepository.findByIdIn(List.of(1, 2, 3)))
            .thenReturn(umaUsuariosList());
        assertThat(usuarioService.getVendedoresByIds(List.of(1, 2, 3)))
            .extracting("id", "nome", "loginNetSales", "email")
            .containsExactly(
                tuple(1, "Caio", "H", "caio@teste.com"),
                tuple(2, "Mario", "QQ", "mario@teste.com"),
                tuple(3, "Maria", "LOG", "maria@teste.com")
            );
    }

    @Test
    public void getVendedoresByIds_deveDividirListaIds_seListaMaiorQueMil() {
        when(usuarioRepository.findByIdIn(IntStream.rangeClosed(3000, 3999).boxed().collect(Collectors.toList())))
            .thenReturn(umaUsuariosList());

        assertThat(usuarioService.getVendedoresByIds(IntStream.rangeClosed(0, 4000).boxed().collect(Collectors.toList())))
            .extracting("id", "nome", "loginNetSales", "email")
            .containsExactly(
                tuple(1, "Caio", "H", "caio@teste.com"),
                tuple(2, "Mario", "QQ", "mario@teste.com"),
                tuple(3, "Maria", "LOG", "maria@teste.com")
            );

        verify(usuarioRepository, times(5))
            .findByIdIn(any());
        verify(usuarioRepository, times(1))
            .findByIdIn(eq(IntStream.rangeClosed(0, 999).boxed().collect(Collectors.toList())));
        verify(usuarioRepository, times(1))
            .findByIdIn(eq(IntStream.rangeClosed(1000, 1999).boxed().collect(Collectors.toList())));
        verify(usuarioRepository, times(1))
            .findByIdIn(eq(IntStream.rangeClosed(2000, 2999).boxed().collect(Collectors.toList())));
        verify(usuarioRepository, times(1))
            .findByIdIn(eq(IntStream.rangeClosed(3000, 3999).boxed().collect(Collectors.toList())));
        verify(usuarioRepository, times(1))
            .findByIdIn(eq(List.of(4000)));
    }

    @Test
    public void getVendedoresByIds_naoDeveDividirListaIds_seListaMenorQueMil() {
        when(usuarioRepository.findByIdIn(IntStream.rangeClosed(0, 800).boxed().collect(Collectors.toList())))
            .thenReturn(umaUsuariosList());

        assertThat(usuarioService.getVendedoresByIds(IntStream.rangeClosed(0, 800).boxed().collect(Collectors.toList())))
            .extracting("id", "nome", "loginNetSales", "email")
            .containsExactly(
                tuple(1, "Caio", "H", "caio@teste.com"),
                tuple(2, "Mario", "QQ", "mario@teste.com"),
                tuple(3, "Maria", "LOG", "maria@teste.com")
            );

        verify(usuarioRepository, times(1))
            .findByIdIn(any());
        verify(usuarioRepository, times(1))
            .findByIdIn(eq(IntStream.rangeClosed(0, 800).boxed().collect(Collectors.toList())));
    }

    private List<UnidadeNegocio> umaListaUnidadesNegocio() {
        return List.of(
            umaUnidadeNegocio(CodigoUnidadeNegocio.CLARO_RESIDENCIAL),
            umaUnidadeNegocio(CodigoUnidadeNegocio.RESIDENCIAL_COMBOS)
        );
    }

    @Test
    public void getAllUsuariosDaHierarquiaD2dDoUserLogado_usuarios_quandoUsuarioDiferenteDeAaExbrain() {
        var usuarioComPermissaoDeVisualizarAa = umUsuarioAutenticado(1, "AGENTE_AUTORIZADO",
            CodigoCargo.AGENTE_AUTORIZADO_SOCIO, AUT_VISUALIZAR_GERAL);
        when(autenticacaoService.getUsuarioAutenticado()).thenReturn(usuarioComPermissaoDeVisualizarAa);

        usuarioService.getAllUsuariosDaHierarquiaD2dDoUserLogado();

        verify(usuarioRepository, times(1))
            .findAll(eq(new UsuarioPredicate().ignorarAa(true).ignorarXbrain(true).build()));
    }

    @Test
    public void getAllUsuariosDaHierarquiaD2dDoUserLogado_usuariosDaEquipe_quandoUsuarioEquipeVendas() {
        var usuarioEquipeVendas = umUsuarioAutenticado(1, "OPERACAO",
            CodigoCargo.VENDEDOR_OPERACAO, AUT_VISUALIZAR_GERAL);
        when(autenticacaoService.getUsuarioAutenticado()).thenReturn(usuarioEquipeVendas);

        when(equipeVendaD2dService.getUsuariosPermitidos(any())).thenReturn(List.of());
        when(autenticacaoService.getUsuarioId()).thenReturn(3);
        when(usuarioRepository.getUsuariosSubordinados(any())).thenReturn(new ArrayList<>(List.of(2, 4, 5)));

        usuarioService.getAllUsuariosDaHierarquiaD2dDoUserLogado();

        verify(usuarioRepository, times(1))
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
        when(usuarioRepository.getUsuariosSubordinados(any())).thenReturn(Lists.newArrayList(List.of(2, 4, 5)));

        usuarioService.buscarUsuariosDaHierarquiaDoUsuarioLogado(null);

        verify(equipeVendaD2dService, times(1))
            .getUsuariosPermitidos(argThat(arg -> arg.size() == 3));
        verify(usuarioRepository, times(1))
            .findAll(any(Predicate.class), eq(new Sort(ASC, "nome")));
    }

    @Test
    public void buscarUsuariosDaHierarquiaDoUsuarioLogado_usuarios_quandoUsuarioCoordenador() {
        var usuarioEquipeVendas = umUsuarioAutenticado(1, "OPERACAO",
            CodigoCargo.COORDENADOR_OPERACAO, CTR_VISUALIZAR_CARTEIRA_HIERARQUIA);
        when(autenticacaoService.getUsuarioAutenticado()).thenReturn(usuarioEquipeVendas);

        when(usuarioRepository.getUsuariosSubordinados(any())).thenReturn(Lists.newArrayList(List.of(2, 4, 5)));

        usuarioService.buscarUsuariosDaHierarquiaDoUsuarioLogado(null);

        verify(usuarioRepository, times(1))
            .findAll(any(Predicate.class), eq(new Sort(ASC, "nome")));
    }

    @Test
    public void findByCpfAa_deveRetornarUsuario_quandoBuscarPorCpfNaoInformandoFiltro() {
        when(usuarioRepository.findTop1UsuarioByCpf(anyString())).thenReturn(Optional.of(umUsuarioAtivo()));

        var usuario = usuarioService.findByCpfAa("31114231827", null);

        assertThat(usuario).isPresent().get().extracting("id", "cpf", "nome", "situacao", "email")
            .containsExactly(10, "98471883007", "Usuario Ativo", ESituacao.A, "usuarioativo@email.com");
    }

    @Test
    public void findByCpfAa_deveRetornarUsuario_quandoBuscarPorCpfIgnorandoBuscaPorSomenteUsuarioAtivo() {
        when(usuarioRepository.findTop1UsuarioByCpf(anyString())).thenReturn(Optional.of(umUsuarioInativo()));

        var usuario = usuarioService.findByCpfAa("31114231827", false);

        assertThat(usuario).isPresent().get().extracting("id", "cpf", "nome", "situacao", "email")
            .containsExactly(11, "31114231827", "Usuario Inativo", ESituacao.I, "usuarioinativo@email.com");
    }

    @Test
    public void findByCpfAa_deveRetornarUsuario_quandoBuscarPorCpfBuscandoSomenteUsuarioAtivo() {
        when(usuarioRepository.findTop1UsuarioByCpfAndSituacao(anyString(), any())).thenReturn(Optional.of(umUsuarioAtivo()));

        var usuario = usuarioService.findByCpfAa("98471883007", true);

        assertThat(usuario).isPresent().get().extracting("id", "cpf", "nome", "situacao", "email")
            .containsExactly(10, "98471883007", "Usuario Ativo", ESituacao.A, "usuarioativo@email.com");
    }

    @Test
    public void findByCpfAa_deveRetornarVazio_quandoBuscarPorCpfENaoEncontrarUsuarioCorrespondente() {
        when(usuarioRepository.findTop1UsuarioByCpf(anyString())).thenReturn(Optional.empty());

        var usuario = usuarioService.findByCpfAa("12345678901", null);

        assertThat(usuario).isEmpty();
    }

    @Test
    public void findByCpfAa_deveRetornarVazio_quandoBuscarPorCpfSomenteSituacaoAtivoEUsuarioEstiverInativoOuRealocado() {
        when(usuarioRepository.findTop1UsuarioByCpfAndSituacao(anyString(), any())).thenReturn(Optional.empty());

        var usuario = usuarioService.findByCpfAa("31114231827", true);

        assertThat(usuario).isEmpty();
    }

    @Test
    public void findByEmailAa_deveRetornarUsuario_quandoBuscarPorEmailNaoInformandoFiltro() {
        when(usuarioRepository.findByEmail(anyString())).thenReturn(Optional.of(umUsuarioAtivo()));

        var usuario = usuarioService.findByEmailAa("usuarioativo@email.com", null);

        assertThat(usuario).isPresent().get().extracting("id", "cpf", "nome", "situacao", "email")
            .containsExactly(10, "98471883007", "Usuario Ativo", ESituacao.A, "usuarioativo@email.com");
    }

    @Test
    public void findByEmailAa_deveRetornarUsuario_quandoBuscarPorEmailIgnorandoBuscaPorSomenteUsuarioAtivo() {
        when(usuarioRepository.findByEmail(anyString())).thenReturn(Optional.of(umUsuarioInativo()));

        var usuario = usuarioService.findByEmailAa("usuarioinativo@email.com", false);

        assertThat(usuario).isPresent().get().extracting("id", "cpf", "nome", "situacao", "email")
            .containsExactly(11, "31114231827", "Usuario Inativo", ESituacao.I, "usuarioinativo@email.com");
    }

    @Test
    public void findByEmailAa_deveRetornarUsuario_quandoBuscarPorEmailBuscandoSomenteUsuarioAtivo() {
        when(usuarioRepository.findByEmailAndSituacao(anyString(), any())).thenReturn(Optional.of(umUsuarioAtivo()));

        var usuario = usuarioService.findByEmailAa("usuarioativo@email.com", true);

        assertThat(usuario).isPresent().get().extracting("id", "cpf", "nome", "situacao", "email")
            .containsExactly(10, "98471883007", "Usuario Ativo", ESituacao.A, "usuarioativo@email.com");
    }

    @Test
    public void findByEmailAa_deveRetornarVazio_quandoBuscarPorEmailENaoEncontrarUsuarioCorrespondente() {
        when(usuarioRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        var usuario = usuarioService.findByEmailAa("teste@teste.com", null);

        assertThat(usuario).isEmpty();
    }

    @Test
    public void findByEmailAa_deveRetornarVazio_quandoBuscarPorEmailSomenteSituacaoAtivoEUsuarioEstiverInativoOuRealocado() {
        when(usuarioRepository.findByEmailAndSituacao(anyString(), any())).thenReturn(Optional.empty());

        var usuario = usuarioService.findByEmailAa("usuarioinativo@email.com", true);

        assertThat(usuario).isEmpty();
    }

    private Usuario umUsuarioAtivo() {
        return Usuario.builder()
            .id(10)
            .cpf("98471883007")
            .nome("Usuario Ativo")
            .situacao(ESituacao.A)
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
            .organizacao(new Organizacao(5))
            .cpf("097.238.645-92")
            .email("usuario@teste.com")
            .telefone("43995565661")
            .hierarquiasId(List.of())
            .usuariosHierarquia(new HashSet<>())
            .build();
    }

    private Usuario umUsuario() {
        return Usuario.builder()
            .id(1)
            .cpf("097.238.645-92")
            .nome("Seiya")
            .build();
    }

    private List<Usuario> umaUsuariosList() {
        return List.of(
            Usuario.builder()
                .id(1)
                .nome("Caio")
                .loginNetSales("H")
                .email("caio@teste.com")
                .situacao(ESituacao.A)
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
        when(usuarioRepository.getUsuarioSuperior(100)).thenReturn(Optional.empty());

        assertThat(usuarioService.getUsuarioSuperior(100))
            .isEqualTo(new UsuarioResponse());
    }

    @Test
    public void getUsuarioSuperior_usuarioResponse_quandoBuscarSuperiorDoUsuario() {
        when(usuarioRepository.getUsuarioSuperior(100))
            .thenReturn(Optional.of(umUsuarioHierarquia()));

        assertThat(usuarioService.getUsuarioSuperior(100))
            .isEqualTo(UsuarioResponse.builder()
                .id(100)
                .nome("RENATO")
                .telefone("43 3322-0000")
                .email("RENATO@GMAIL.COM")
                .situacao(ESituacao.A)
                .codigoUnidadesNegocio(List.of(
                    CodigoUnidadeNegocio.CLARO_RESIDENCIAL,
                    CodigoUnidadeNegocio.RESIDENCIAL_COMBOS))
                .codigoCargo(CodigoCargo.SUPERVISOR_OPERACAO)
                .codigoDepartamento(CodigoDepartamento.COMERCIAL)
                .codigoEmpresas(List.of(CodigoEmpresa.CLARO_RESIDENCIAL))
                .codigoNivel(OPERACAO)
                .nomeNivel("OPERACAO")
                .cpf("097.238.645-92")
                .build());
    }

    @Test
    public void obterNomeUsuarioPorId_deveRetornarNome_quandoSolicitado() {
        when(usuarioRepository.findById(eq(1))).thenReturn(Optional.of(Usuario.builder().nome("NOME UM").build()));

        assertThat(usuarioService.obterNomeUsuarioPorId(1)).isEqualTo("NOME UM");
    }

    @Test
    public void buscarSubordinadosAtivosPorSuperioresIdsECodigosCargos_listUsuarioResponse_seSolicitado() {
        when(usuarioRepository
            .buscarSubordinadosAtivosPorSuperioresIdsECodigosCargos(eq(List.of(1)), eq(Set.of(ASSISTENTE_OPERACAO.name()))))
            .thenReturn(List.of(
                umUsuarioResponse(1, "NOME 1", ESituacao.A, ASSISTENTE_OPERACAO),
                umUsuarioResponse(2, "NOME 2", ESituacao.A, ASSISTENTE_OPERACAO),
                umUsuarioResponse(3, "NOME 3", ESituacao.A, ASSISTENTE_OPERACAO)));

        assertThat(usuarioService
            .buscarSubordinadosAtivosPorSuperioresIdsECodigosCargos(List.of(1), Set.of(ASSISTENTE_OPERACAO.name())))
            .extracting("id", "nome", "situacao", "codigoCargo")
            .containsExactlyInAnyOrder(
                tuple(1, "NOME 1", ESituacao.A, ASSISTENTE_OPERACAO),
                tuple(2, "NOME 2", ESituacao.A, ASSISTENTE_OPERACAO),
                tuple(3, "NOME 3", ESituacao.A, ASSISTENTE_OPERACAO));
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
            .situacao(ESituacao.A)
            .cargo(umCargoSupervisorOperacao())
            .departamento(umDepartamentoComercial())
            .nome("RENATO")
            .email("RENATO@GMAIL.COM")
            .situacao(ESituacao.A)
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
            .situacao(ESituacao.A)
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
            .codigo(codigoUnidadeNegocio)
            .nome(codigoUnidadeNegocio.name())
            .situacao(ESituacao.A)
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

        when(usuarioRepository.findById(umUsuarioComLogin))
            .thenReturn(Optional.of(umUsuarioComLoginNetSales(umUsuarioComLogin)));

        var response = usuarioService.getUsuarioByIdComLoginNetSales(umUsuarioComLogin);

        assertThat(response)
            .extracting(UsuarioComLoginNetSalesResponse::getId,
                UsuarioComLoginNetSalesResponse::getNome,
                UsuarioComLoginNetSalesResponse::getLoginNetSales,
                UsuarioComLoginNetSalesResponse::getNivelCodigo,
                UsuarioComLoginNetSalesResponse::getCpfNetSales)
            .containsExactly(
                umUsuarioComLogin,
                "UM USUARIO COM LOGIN",
                "UM LOGIN NETSALES",
                "ATIVO_LOCAL_PROPRIO",
                "123.456.887-91");
    }

    @Test
    public void getUsuarioByIdComLoginNetSales_deveRetornarUsuario_sePossuirLoginNetSalesEForOperador() {
        var umUsuarioComLogin = 1000;
        var user = umUsuarioComLoginNetSales(umUsuarioComLogin);
        user.setCanais(Set.of(ECanal.AGENTE_AUTORIZADO));
        user.getCargo().setNivel(Nivel.builder().codigo(OPERACAO).build());
        when(usuarioRepository.findById(umUsuarioComLogin))
            .thenReturn(Optional.of(user));

        var response = usuarioService.getUsuarioByIdComLoginNetSales(umUsuarioComLogin);

        assertThat(response)
            .extracting(UsuarioComLoginNetSalesResponse::getId,
                UsuarioComLoginNetSalesResponse::getNome,
                UsuarioComLoginNetSalesResponse::getLoginNetSales,
                UsuarioComLoginNetSalesResponse::getNivelCodigo,
                UsuarioComLoginNetSalesResponse::getCpfNetSales)
            .containsExactly(
                umUsuarioComLogin,
                "UM USUARIO COM LOGIN",
                "UM LOGIN NETSALES",
                "OPERACAO_AGENTE_AUTORIZADO",
                "123.456.887-91");
    }

    @Test
    public void getUsuarioByIdComLoginNetSales_deveRetornarUsuario_sePossuirLoginNetSalesEForReceptivo() {
        var umUsuarioComLogin = 1000;
        var user = umUsuarioComLoginNetSales(umUsuarioComLogin);
        user.setOrganizacao(Organizacao.builder().codigo("ATENTO").build());
        user.getCargo().setNivel(Nivel.builder().codigo(CodigoNivel.RECEPTIVO).build());
        when(usuarioRepository.findById(umUsuarioComLogin))
            .thenReturn(Optional.of(user));

        var response = usuarioService.getUsuarioByIdComLoginNetSales(umUsuarioComLogin);

        assertThat(response)
            .extracting(UsuarioComLoginNetSalesResponse::getId,
                UsuarioComLoginNetSalesResponse::getNome,
                UsuarioComLoginNetSalesResponse::getLoginNetSales,
                UsuarioComLoginNetSalesResponse::getNivelCodigo,
                UsuarioComLoginNetSalesResponse::getCpfNetSales)
            .containsExactly(
                umUsuarioComLogin,
                "UM USUARIO COM LOGIN",
                "UM LOGIN NETSALES",
                "RECEPTIVO_ATENTO",
                "123.456.887-91");
    }

    @Test
    public void getUsuarioByIdComLoginNetSales_deveLancarException_seUsuarioNaoPossuirLoginNetSales() {
        var umUsuarioSemLogin = 1001;

        when(usuarioRepository.findById(umUsuarioSemLogin))
            .thenReturn(Optional.of(umUsuarioSemLoginNetSales(umUsuarioSemLogin)));

        assertThatExceptionOfType(ValidacaoException.class)
            .isThrownBy(() -> usuarioService.getUsuarioByIdComLoginNetSales(umUsuarioSemLogin))
            .withMessage("Usuário não possui login NetSales válido.");
    }

    @Test
    public void getUsuarioByIdComLoginNetSales_deveLancarException_seUsuarioNaoEncontrado() {
        var umUsuarioInexistente = 1002;

        when(usuarioRepository.findById(umUsuarioInexistente))
            .thenReturn(Optional.empty());

        assertThatExceptionOfType(ValidacaoException.class)
            .isThrownBy(() -> usuarioService.getUsuarioByIdComLoginNetSales(umUsuarioInexistente))
            .withMessage("Usuário não encontrado.");
    }

    @Test
    public void obterIdsPorUsuarioCadastroId_deveRetornarListaVazia_quandoNaoEncontrarUsuarios() {
        when(usuarioRepository.obterIdsPorUsuarioCadastroId(eq(1000))).thenReturn(List.of());

        assertThat(usuarioService.obterIdsPorUsuarioCadastroId(1000))
            .isEmpty();
    }

    @Test
    public void obterIdsPorUsuarioCadastroId_deveRetornarListaIds_quandoEncontrarUsuarios() {
        when(usuarioRepository.obterIdsPorUsuarioCadastroId(eq(400))).thenReturn(List.of(100, 200, 300));

        assertThat(usuarioService.obterIdsPorUsuarioCadastroId(400))
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
        when(usuarioRepository.obterIdsPorUsuarioCadastroId(eq(3000))).thenReturn(List.of());
        when(usuarioRepository.getUsuariosSubordinados(eq(3000))).thenReturn(idsUsuariosSubordinados);
        when(usuarioRepository.findAll(eq(predicate.build()), eq(new PageRequest())))
            .thenReturn(umaPageUsuario(new PageRequest(), List.of(umUsuario())));

        assertThat(usuarioService.getAll(new PageRequest(), new UsuarioFiltros()))
            .isNotEmpty();
    }

    @Test
    public void buscarBackOfficesAndSociosAaPorAaIds_deveRetornarResponse_seEncontradoUsuariosIdPorUmAaId() {
        when(agenteAutorizadoNovoService.getUsuariosByAaId(100, false)).thenReturn(List.of(
            umUsuarioAgenteAutorizadoResponse(100, 100),
            umUsuarioAgenteAutorizadoResponse(101, 100)));
        when(usuarioRepository.findAll(umUsuarioPredicateComCargoCodigoBackOfficeESocioAaDosIds(List.of(100, 101)).build()))
            .thenReturn(List.of(
                umUsuarioDoIdECodigoCargo(100, CodigoCargo.BACKOFFICE_ANALISTA_TRATAMENTO),
                umUsuarioDoIdECodigoCargo(101, CodigoCargo.AGENTE_AUTORIZADO_SOCIO)));

        when(agenteAutorizadoNovoService.getUsuariosByAaId(200, false)).thenReturn(Collections.emptyList());

        assertThat(usuarioService.buscarBackOfficesAndSociosAaPorAaIds(List.of(100, 200)))
            .extracting("id", "nome", "email", "agenteAutorizadoId")
            .containsExactly(
                tuple(100, "FULANO DE TESTE", "TESTE@TESTE.COM", 100),
                tuple(101, "FULANO DE TESTE", "TESTE@TESTE.COM", 100));
    }

    @Test
    public void buscarBackOfficesAndSociosAaPorAaIds_deveRetornarResponse_seEncontradoUsuariosPorUmAaId() {
        when(agenteAutorizadoNovoService.getUsuariosByAaId(100, false)).thenReturn(List.of(
            umUsuarioAgenteAutorizadoResponse(100, 100),
            umUsuarioAgenteAutorizadoResponse(101, 100)));
        when(usuarioRepository.findAll(umUsuarioPredicateComCargoCodigoBackOfficeESocioAaDosIds(List.of(100, 101)).build()))
            .thenReturn(Collections.emptyList());

        when(agenteAutorizadoNovoService.getUsuariosByAaId(200, false)).thenReturn(List.of(
            umUsuarioAgenteAutorizadoResponse(200, 200),
            umUsuarioAgenteAutorizadoResponse(201, 200)));
        when(usuarioRepository.findAll(umUsuarioPredicateComCargoCodigoBackOfficeESocioAaDosIds(List.of(200, 201)).build()))
            .thenReturn(List.of(
                umUsuarioDoIdECodigoCargo(200, CodigoCargo.BACKOFFICE_ANALISTA_TRATAMENTO),
                umUsuarioDoIdECodigoCargo(201, CodigoCargo.AGENTE_AUTORIZADO_SOCIO)));

        assertThat(usuarioService.buscarBackOfficesAndSociosAaPorAaIds(List.of(100, 200)))
            .extracting("id", "nome", "email", "agenteAutorizadoId")
            .containsExactly(
                tuple(200, "FULANO DE TESTE", "TESTE@TESTE.COM", 200),
                tuple(201, "FULANO DE TESTE", "TESTE@TESTE.COM", 200));
    }

    @Test
    public void buscarBackOfficesAndSociosAaPorAaIds_deveRetornarUsuarioAgenteAutorizadoResponse_seEncontradoPorTodosAaId() {
        when(agenteAutorizadoNovoService.getUsuariosByAaId(100, false)).thenReturn(List.of(
            umUsuarioAgenteAutorizadoResponse(100, 100),
            umUsuarioAgenteAutorizadoResponse(101, 100)));
        when(usuarioRepository.findAll(umUsuarioPredicateComCargoCodigoBackOfficeESocioAaDosIds(List.of(100, 101)).build()))
            .thenReturn(List.of(
                umUsuarioDoIdECodigoCargo(100, CodigoCargo.BACKOFFICE_ANALISTA_TRATAMENTO),
                umUsuarioDoIdECodigoCargo(101, CodigoCargo.AGENTE_AUTORIZADO_SOCIO)));

        when(agenteAutorizadoNovoService.getUsuariosByAaId(200, false)).thenReturn(List.of(
            umUsuarioAgenteAutorizadoResponse(200, 200),
            umUsuarioAgenteAutorizadoResponse(201, 200)));
        when(usuarioRepository.findAll(umUsuarioPredicateComCargoCodigoBackOfficeESocioAaDosIds(List.of(200, 201)).build()))
            .thenReturn(List.of(
                umUsuarioDoIdECodigoCargo(200, CodigoCargo.BACKOFFICE_ANALISTA_TRATAMENTO),
                umUsuarioDoIdECodigoCargo(201, CodigoCargo.AGENTE_AUTORIZADO_SOCIO)));

        assertThat(usuarioService.buscarBackOfficesAndSociosAaPorAaIds(List.of(100, 200)))
            .extracting("id", "nome", "email", "agenteAutorizadoId")
            .containsExactly(
                tuple(100, "FULANO DE TESTE", "TESTE@TESTE.COM", 100),
                tuple(101, "FULANO DE TESTE", "TESTE@TESTE.COM", 100),
                tuple(200, "FULANO DE TESTE", "TESTE@TESTE.COM", 200),
                tuple(201, "FULANO DE TESTE", "TESTE@TESTE.COM", 200));
    }

    @Test
    public void buscarBackOfficesAndSociosAaPorAaIds_naoDeveRetornarUsuarioAgenteAutorizadoResponse_seNaoEncontrarUsuariosId() {
        when(agenteAutorizadoNovoService.getUsuariosByAaId(100, false)).thenReturn(Collections.emptyList());
        assertThat(usuarioService.buscarBackOfficesAndSociosAaPorAaIds(List.of(100, 200))).isEqualTo(Collections.emptyList());
    }

    @Test
    public void buscarBackOfficesAndSociosAaPorAaIds_naoDeveRetornarUsuarioAgenteAutorizadoResponse_seNaoEncontrarUsuarios() {
        when(agenteAutorizadoNovoService.getUsuariosByAaId(100, false)).thenReturn(List.of(
            umUsuarioAgenteAutorizadoResponse(100, 100),
            umUsuarioAgenteAutorizadoResponse(101, 100)));
        when(usuarioRepository.findAll(umUsuarioPredicateComCargoCodigoBackOfficeESocioAaDosIds(List.of(100, 101)).build()))
            .thenReturn(Collections.emptyList());

        assertThat(usuarioService.buscarBackOfficesAndSociosAaPorAaIds(List.of(100, 200))).isEqualTo(Collections.emptyList());
    }

    @Test
    public void buscarVendedoresFeeder_deveRetornarListaVazia_quandoNaoHouverUsuariosDosAgentesAutorizados() {
        when(agenteAutorizadoNovoService.buscarTodosUsuariosDosAas(eq(List.of(1)), eq(true)))
            .thenReturn(List.of());

        assertThat(usuarioService.buscarVendedoresFeeder(umVendedoresFeederFiltros(List.of(1), null, false)))
            .isEmpty();

        verify(usuarioRepository, never()).findAll(any(Predicate.class));
    }

    @Test
    public void buscarVendedoresFeeder_deveRetornarListaUsuarioConsultaDto_quandoBuscarInativosNull() {
        when(agenteAutorizadoNovoService.buscarTodosUsuariosDosAas(eq(List.of(1)), eq(true)))
            .thenReturn(List.of(umUsuarioDtoVendas(1)));
        when(usuarioRepository.findAll(eq(umVendedoresFeederPredicateComSocioPrincipalESituacaoAtiva(List.of(1)).build())))
            .thenReturn(List.of(umUsuarioCompleto()));

        assertThat(usuarioService.buscarVendedoresFeeder(umVendedoresFeederFiltros(List.of(1), true, false)))
            .hasSize(1)
            .extracting("id", "nome", "situacao", "nivelCodigo")
            .containsExactly(tuple(1, "NOME UM", "A", "AGENTE_AUTORIZADO"));
    }

    @Test
    public void buscarVendedoresFeeder_deveRetornarListaUsuarioConsultaDto_quandoBuscarInativosFalse() {
        when(agenteAutorizadoNovoService.buscarTodosUsuariosDosAas(eq(List.of(1)), eq(true)))
            .thenReturn(List.of(umUsuarioDtoVendas(1)));
        when(usuarioRepository.findAll(eq(umVendedoresFeederPredicateComSocioPrincipalESituacaoAtiva(List.of(1)).build())))
            .thenReturn(List.of(umUsuarioCompleto()));

        assertThat(usuarioService.buscarVendedoresFeeder(umVendedoresFeederFiltros(List.of(1), true, false)))
            .hasSize(1)
            .extracting("id", "nome", "situacao", "nivelCodigo")
            .containsExactly(tuple(1, "NOME UM", "A", "AGENTE_AUTORIZADO"));
    }

    @Test
    public void buscarVendedoresFeeder_deveRetornarListaUsuarioConsultaDto_quandoBuscarInativosTrue() {
        when(agenteAutorizadoNovoService.buscarTodosUsuariosDosAas(eq(List.of(1)), eq(true)))
            .thenReturn(List.of(umUsuarioDtoVendas(1)));
        when(usuarioRepository.findAll(eq(umVendedoresFeederPredicateComSocioPrincipalETodasSituacaoes(List.of(1)).build())))
            .thenReturn(List.of(umUsuarioCompleto()));

        assertThat(usuarioService.buscarVendedoresFeeder(umVendedoresFeederFiltros(List.of(1), true, true)))
            .hasSize(1)
            .extracting("id", "nome", "situacao", "nivelCodigo")
            .containsExactly(tuple(1, "NOME UM", "A", "AGENTE_AUTORIZADO"));
    }

    @Test
    public void buscarUsuarioSituacaoPorIds_listaDeUsuarioSituacao_seSolicitado() {
        var usuariosSituacao = List.of(
            UsuarioSituacaoResponse.builder().id(1).nome("NOME 1").situacao(ESituacao.A).build(),
            UsuarioSituacaoResponse.builder().id(2).nome("NOME 2").situacao(ESituacao.I).build(),
            UsuarioSituacaoResponse.builder().id(3).nome("NOME 3").situacao(ESituacao.R).build()
        );

        when(usuarioRepository.buscarUsuarioSituacao(eq(new BooleanBuilder(QUsuario.usuario.id.in(List.of(1, 2, 3))))))
            .thenReturn(usuariosSituacao);

        assertThat(usuarioService.buscarUsuarioSituacaoPorIds(new UsuarioSituacaoFiltro(List.of(1, 2, 3))))
            .isEqualTo(usuariosSituacao);
    }

    @Test
    public void findUsuariosOperadoresBackofficeByOrganizacao_deveRetornarResponseSemFiltrarAtivos_seBuscarInativosTrue() {
        when(usuarioRepository.findByOrganizacaoIdAndCargo_CodigoIn(
            eq(5), eq(List.of(BACKOFFICE_OPERADOR_TRATAMENTO, BACKOFFICE_ANALISTA_TRATAMENTO))))
            .thenReturn(List.of(umUsuarioAtivo(), umUsuarioInativo(), umUsuarioCompleto()));

        assertThat(usuarioService.findUsuariosOperadoresBackofficeByOrganizacao(5, true))
            .extracting("value", "label")
            .containsExactly(
                tuple(10, "Usuario Ativo"),
                tuple(11, "Usuario Inativo"),
                tuple(1, "NOME UM"));
    }

    @Test
    public void findUsuariosOperadoresBackofficeByOrganizacao_deveRetornarResponseEFiltrarAtivos_seBuscarInativosFalse() {
        when(usuarioRepository.findByOrganizacaoIdAndCargo_CodigoIn(
            eq(5), eq(List.of(BACKOFFICE_OPERADOR_TRATAMENTO, BACKOFFICE_ANALISTA_TRATAMENTO))))
            .thenReturn(List.of(umUsuarioAtivo(), umUsuarioInativo(), umUsuarioCompleto()));

        assertThat(usuarioService.findUsuariosOperadoresBackofficeByOrganizacao(5, false))
            .extracting("value", "label")
            .containsExactly(
                tuple(10, "Usuario Ativo"),
                tuple(1, "NOME UM"));
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
            .situacao(ESituacao.A)
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
            .situacao(ESituacao.A)
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
    public void ativar_void_quandoDesejarAlterarSituacaoDoUsuarioComId100ParaAtivo() {
        var usuarioInativo = Usuario.builder()
            .id(100)
            .nome("RENATO")
            .situacao(ESituacao.I)
            .build();

        when(usuarioRepository.findById(100))
            .thenReturn(Optional.of(usuarioInativo));

        usuarioService.ativar(100);

        assertThat(usuarioInativo.getSituacao()).isEqualTo(ESituacao.A);

        verify(usuarioClientService, times(1)).alterarSituacao(eq(100));
        verify(usuarioRepository).save(usuarioInativo);
    }

    @Test
    public void getUsuariosByIdsTodasSituacoes_deveEfetuarABuscaParticionada_quandoQtdeIdsMaiorQueMaximoOracle() {
        when(usuarioRepository.findByIdIn(IntStream.rangeClosed(1, 1000).boxed().collect(Collectors.toList())))
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
        when(usuarioRepository.findByIdIn(emptyUsersIdsPart)).thenReturn(List.of());

        when(usuarioRepository.findByIdIn(IntStream.rangeClosed(2001, 2700).boxed().collect(Collectors.toList())))
            .thenReturn(List.of(
                Usuario.builder()
                    .id(2029)
                    .nome("Lee Ji Eun")
                    .build()
            ));

        var idsUsuarios = IntStream.rangeClosed(1, 2700).boxed().collect(Collectors.toCollection(LinkedHashSet::new));
        var usuarios = usuarioService.getUsuariosByIdsTodasSituacoes(idsUsuarios);

        assertThat(usuarios)
            .extracting("id", "nome")
            .containsExactlyInAnyOrder(
                tuple(133, "Márcio Oliveira"),
                tuple(988, "Any Gabrielly"),
                tuple(2029, "Lee Ji Eun")
            );

        verify(usuarioRepository, times(1)).findByIdIn(eq(emptyUsersIdsPart));
    }

    @Test
    public void getTiposCanalOptions_opcoesDeSelectParaOsTiposCanal_quandoBuscarOpcoesParaOSelect() {
        assertThat(usuarioService.getTiposCanalOptions())
            .extracting("value", "label")
            .containsExactly(
                tuple("PAP", "PAP"),
                tuple("PAP_PME", "PAP PME"),
                tuple("PAP_PREMIUM", "PAP PREMIUM"),
                tuple("INSIDE_SALES_PME", "INSIDE SALES PME")
            );
    }

    @Test
    public void getAllForCsv_deveRetornarCsv_quandoEncontrarUsuarios() {
        UsuarioFiltros usuarioFiltros = new UsuarioFiltros();
        UsuarioPredicate usuarioPredicate = usuarioFiltros.toPredicate();

        when(autenticacaoService.getUsuarioAutenticado())
            .thenReturn(UsuarioAutenticadoHelper.umUsuarioAutenticadoNivelAa());

        usuarioPredicate.filtraPermitidos(autenticacaoService.getUsuarioAutenticado(), usuarioService, true);

        when(usuarioRepository.getUsuariosCsv(usuarioPredicate.build()))
            .thenReturn(List.of(umUsuarioOperacaoCsv(), umUsuarioAaCsv()));

        var usuarioCsvs = usuarioService.getAllForCsv(usuarioFiltros);

        assertThat(usuarioCsvs)
            .isEqualTo(List.of(umUsuarioOperacaoCsv(), umUsuarioAaCsv()));
    }

    @Test
    public void preencheUsuarioCsvsDeAa_devePreencherColunasDeAa_seUsuarioForAa() {
        when(agenteAutorizadoNovoService.getAgenteAutorizadosUsuarioDtosByUsuarioIds(UsuarioRequest.of(List.of(2))))
            .thenReturn(Collections.singletonList(umAgenteAutorizadoUsuarioDto()));

        List<UsuarioCsvResponse> usuarioCsvResponses = new ArrayList<>();
        usuarioCsvResponses.add(umUsuarioAaCsv());
        usuarioCsvResponses.add(umUsuarioOperacaoCsv());
        usuarioService.preencherUsuarioCsvsDeAa(usuarioCsvResponses);

        var usuarioAaCsvCompletado = umUsuarioAaCsv();
        usuarioAaCsvCompletado.setCnpj("78300110000166");
        usuarioAaCsvCompletado.setRazaoSocial("Razao Social");

        assertThat(usuarioCsvResponses)
            .isEqualTo(List.of(usuarioAaCsvCompletado, umUsuarioOperacaoCsv()));

    }

    @Test
    public void preencheUsuarioCsvsDeOperacao_devePreencherColunasDeCanal_seUsuarioForOperacao() {

        when(usuarioRepository.getCanaisByUsuarioIds(Collections.singletonList(1)))
            .thenReturn(List.of(umCanal(), umOutroCanal()));

        List<UsuarioCsvResponse> usuarioCsvResponses = new ArrayList<>();
        usuarioCsvResponses.add(umUsuarioAaCsv());
        usuarioCsvResponses.add(umUsuarioOperacaoCsv());

        usuarioService.preencherUsuarioCsvsDeOperacao(usuarioCsvResponses);

        var usuarioOperacaoCsvCompletado = umUsuarioOperacaoCsv();
        usuarioOperacaoCsvCompletado.setCanais(List.of(umCanal(), umOutroCanal()));

        assertThat(usuarioCsvResponses)
            .isEqualTo(List.of(umUsuarioAaCsv(), usuarioOperacaoCsvCompletado));

    }

    @Test
    @SuppressWarnings("LineLength")
    public void save_deveDispararValidacaoException_seUsuarioOperacaoEstiverNaCarteiraDeAlgumAgenteAutorizado() {
        when(usuarioRepository.findById(eq(1)))
            .thenReturn(Optional.of(umUsuarioCompleto(SUPERVISOR_OPERACAO, 1, OPERACAO,
                CodigoDepartamento.COMERCIAL, ECanal.AGENTE_AUTORIZADO)));
        when(agenteAutorizadoNovoService.findAgenteAutorizadoByUsuarioId(eq(1)))
            .thenReturn(List.of(AgenteAutorizadoResponse
                .builder()
                .id("1")
                .razaoSocial("TESTE AA")
                .cnpj("00.000.0000/0001-00")
                .build()));

        assertThatExceptionOfType(ValidacaoException.class)
            .isThrownBy(() -> usuarioService.save(umUsuarioCompleto(SUPERVISOR_OPERACAO, 1, OPERACAO,
                CodigoDepartamento.COMERCIAL, ECanal.ATIVO_PROPRIO)))
            .withMessage("Não é possível remover o canal Agente Autorizado, "
                + "pois o usuário possui vínculo com o(s) AA(s): TESTE AA 00.000.0000/0001-00.");
    }

    @Test
    @SuppressWarnings("LineLength")
    public void save_naoDeveDispararValidacaoException_seUsuarioDadosAlteradosNaoForCanalAgenteAutorizado() {
        var usuarioCompleto = umUsuarioCompleto(SUPERVISOR_OPERACAO, 1, OPERACAO,
            CodigoDepartamento.COMERCIAL, ECanal.AGENTE_AUTORIZADO);

        when(usuarioRepository.findById(eq(1))).thenReturn(Optional.of(usuarioCompleto));

        usuarioCompleto.setNome("AA Teste Dois");

        assertThatCode(() -> usuarioService.save(usuarioCompleto)).doesNotThrowAnyException();

        verifyNoMoreInteractions(agenteAutorizadoNovoService);
    }

    @Test
    @SuppressWarnings("LineLength")
    public void save_deveDispararValidacaoException_seUsuarioOriginalPossuirSitesVinculadosEUsuarioOriginalForCoordenadorOuSupervisorOperacaoECanalAtivoProprioRemovido() {
        when(usuarioRepository.findById(eq(1)))
            .thenReturn(Optional.of(UsuarioHelper.umUsuario(1, umCargo(1, CodigoCargo.COORDENADOR_OPERACAO), Set.of(ECanal.ATIVO_PROPRIO), 1)));
        when(siteService.buscarSitesAtivosPorCoordenadorOuSupervisor(eq(1)))
            .thenReturn(List.of(umSite(1, "SITE UM"), umSite(2, "SITE DOIS")));

        assertThatExceptionOfType(ValidacaoException.class)
            .isThrownBy(() -> usuarioService.save(UsuarioHelper.umUsuario(1, umCargo(1, CodigoCargo.COORDENADOR_OPERACAO), Set.of(), 1)))
            .withMessage("Não é possível remover o canal Ativo Local, "
                + "pois o usuário possui vínculo com o(s) Site(s): SITE UM, SITE DOIS.");
    }

    @Test
    @SuppressWarnings("LineLength")
    public void save_deveDispararValidacaoException_seUsuarioOriginalPossuirSitesVinculadosECargoCoordenadorOuSupervisorOperacaoDoUsuarioOriginalAlterado() {
        when(usuarioRepository.findById(eq(1)))
            .thenReturn(Optional.of(UsuarioHelper.umUsuario(1, umCargo(2, CodigoCargo.SUPERVISOR_OPERACAO), Set.of(ECanal.ATIVO_PROPRIO), 1)));
        when(siteService.buscarSitesAtivosPorCoordenadorOuSupervisor(eq(1)))
            .thenReturn(List.of(umSite(1, "SITE UM"), umSite(2, "SITE DOIS")));

        assertThatExceptionOfType(ValidacaoException.class)
            .isThrownBy(() -> usuarioService.save(UsuarioHelper.umUsuario(1, umCargo(1, CodigoCargo.COORDENADOR_OPERACAO), Set.of(ECanal.ATIVO_PROPRIO), 1)))
            .withMessage("Não é possível alterar o cargo, "
                + "pois o usuário possui vínculo com o(s) Site(s): SITE UM, SITE DOIS.");
    }

    @Test
    @SuppressWarnings("LineLength")
    public void save_naoDeveDispararValidacaoException_seUsuarioOriginalNaoPossuirSitesVinculados() {
        when(usuarioRepository.findById(eq(1)))
            .thenReturn(Optional.of(UsuarioHelper.umUsuario(1, umCargo(2, CodigoCargo.SUPERVISOR_OPERACAO), Set.of(ECanal.ATIVO_PROPRIO), 1)));
        when(siteService.buscarSitesAtivosPorCoordenadorOuSupervisor(eq(1)))
            .thenReturn(List.of());

        assertThatCode(() -> usuarioService.save(UsuarioHelper.umUsuario(1, umCargo(2, CodigoCargo.SUPERVISOR_OPERACAO), Set.of(), 1)))
            .doesNotThrowAnyException();
    }

    @Test
    @SuppressWarnings("LineLength")
    public void save_naoDeveDispararValidacaoException_seUsuarioOriginalPossuirSitesVinculadosENaoForCoordenadorOuSupervisorOperacao() {
        when(usuarioRepository.findById(eq(1)))
            .thenReturn(Optional.of(UsuarioHelper.umUsuario(1, umCargo(1, CodigoCargo.COORDENADOR_OPERACAO), Set.of(ECanal.ATIVO_PROPRIO), 1)));

        when(siteService.buscarSitesAtivosPorCoordenadorOuSupervisor(eq(1)))
            .thenReturn(List.of(umSite(1, "SITE UM"), umSite(2, "SITE DOIS")));

        assertThatCode(() -> usuarioService
            .save(UsuarioHelper.umUsuario(1, umCargo(1, CodigoCargo.COORDENADOR_OPERACAO), Set.of(ECanal.ATIVO_PROPRIO), 1)))
            .doesNotThrowAnyException();
    }

    @Test
    @SuppressWarnings("LineLength")
    public void save_naoDeveDispararValidacaoException_seUsuarioOriginalPossuirSitesVinculadosECargoCoordenadorOuSupervisorECanalAtivoLocalMantidos() {
        when(usuarioRepository.findById(eq(1)))
            .thenReturn(Optional.of(UsuarioHelper.umUsuario(1, umCargo(1, CodigoCargo.COORDENADOR_OPERACAO), Set.of(ECanal.ATIVO_PROPRIO), 1)));

        when(siteService.buscarSitesAtivosPorCoordenadorOuSupervisor(eq(1)))
            .thenReturn(List.of(umSite(1, "SITE UM"), umSite(2, "SITE DOIS")));

        assertThatCode(() -> usuarioService
            .save(UsuarioHelper.umUsuario(1, umCargo(1, CodigoCargo.COORDENADOR_OPERACAO), Set.of(ECanal.ATIVO_PROPRIO), 1)))
            .doesNotThrowAnyException();
    }

    @Test
    public void save_retornaValidacaoException_quandoUsuarioAtivoOutraEquipe() {
        when(usuarioRepository.findById(any()))
            .thenReturn(Optional.of(umUsuarioCompleto(ASSISTENTE_OPERACAO, 2,
                OPERACAO, CodigoDepartamento.COMERCIAL, ECanal.D2D_PROPRIO)));
        when(usuarioRepository.getCanaisByUsuarioIds(any())).thenReturn(List.of(new Canal(1, ECanal.D2D_PROPRIO)));
        when(equipeVendasUsuarioService.buscarUsuarioEquipeVendasPorId(anyInt()))
            .thenReturn(List.of(1));
        Assertions.assertThatExceptionOfType(ValidacaoException.class)
            .isThrownBy(() -> usuarioService.save(
                umUsuarioCompleto(VENDEDOR_OPERACAO, 8, CodigoNivel.OPERACAO,
                    CodigoDepartamento.COMERCIAL, ECanal.D2D_PROPRIO)))
            .withMessage("Usuário já está cadastrado em outra equipe");

    }

    @Test
    public void save_naoDeveLancarException_quandoUsuarioNaoPossuiOutraEquipe() {
        when(usuarioRepository.findById(any()))
            .thenReturn(Optional.of(umUsuarioCompleto(ASSISTENTE_OPERACAO, 2,
                OPERACAO, CodigoDepartamento.COMERCIAL, ECanal.D2D_PROPRIO)));
        when(usuarioRepository.getCanaisByUsuarioIds(any()))
            .thenReturn(List.of(new Canal(1, ECanal.D2D_PROPRIO)));
        when(equipeVendasUsuarioService.buscarUsuarioEquipeVendasPorId(anyInt()))
            .thenReturn(List.of());
        Assertions.assertThatCode(() -> usuarioService.save(
                umUsuarioCompleto(VENDEDOR_OPERACAO, 8,
                    CodigoNivel.OPERACAO, CodigoDepartamento.COMERCIAL, ECanal.D2D_PROPRIO)))
            .doesNotThrowAnyException();
    }

    @Test
    public void save_retornaValidacaoException_quandoLiderEquipe() {
        when(usuarioRepository.findById(any()))
            .thenReturn(Optional.of(umUsuarioCompleto(SUPERVISOR_OPERACAO, 10,
                OPERACAO, CodigoDepartamento.COMERCIAL, ECanal.D2D_PROPRIO)));
        when(usuarioRepository.getCanaisByUsuarioIds(any()))
            .thenReturn(List.of(new Canal(1, ECanal.D2D_PROPRIO)));
        when(equipeVendaD2dService.getEquipeVendasBySupervisorId(any()))
            .thenReturn(List.of(1));
        Assertions.assertThatExceptionOfType(ValidacaoException.class)
            .isThrownBy(() -> usuarioService.save(
                umUsuarioCompleto(COORDENADOR_OPERACAO, 4, CodigoNivel.OPERACAO,
                    CodigoDepartamento.COMERCIAL, ECanal.D2D_PROPRIO)))
            .withMessage("Usuário já está cadastrado em outra equipe");

    }

    @Test
    public void save_retornaValidacaoException_quandoCoordenadorLiderEquipe() {
        when(usuarioRepository.findById(any()))
            .thenReturn(Optional.of(umUsuarioCompleto(COORDENADOR_OPERACAO, 10,
                OPERACAO, CodigoDepartamento.COMERCIAL, ECanal.D2D_PROPRIO)));
        when(usuarioRepository.getCanaisByUsuarioIds(any()))
            .thenReturn(List.of(new Canal(1, ECanal.D2D_PROPRIO)));
        when(equipeVendaD2dService.getEquipeVendasBySupervisorId(any()))
            .thenReturn(List.of(1));
        Assertions.assertThatExceptionOfType(ValidacaoException.class)
            .isThrownBy(() -> usuarioService.save(
                umUsuarioCompleto(GERENTE_OPERACAO, 9, CodigoNivel.OPERACAO,
                    CodigoDepartamento.COMERCIAL, ECanal.D2D_PROPRIO)))
            .withMessage("Usuário já está cadastrado em outra equipe");
        verify(usuarioRepository, never()).saveAndFlush(any());
    }

    @Test
    public void save_naoDeveLancarException_quandoCoordenadorNaoPossuirOutraEquipe() {
        when(usuarioRepository.findById(any()))
            .thenReturn(Optional.of(umUsuarioCompleto(COORDENADOR_OPERACAO, 10,
                OPERACAO, CodigoDepartamento.COMERCIAL, ECanal.D2D_PROPRIO)));
        when(usuarioRepository.getCanaisByUsuarioIds(any()))
            .thenReturn(List.of(new Canal(1, ECanal.D2D_PROPRIO)));
        when(equipeVendaD2dService.getEquipeVendasBySupervisorId(any()))
            .thenReturn(List.of());
        Assertions.assertThatCode(() -> usuarioService.save(
                umUsuarioCompleto(GERENTE_OPERACAO, 7, CodigoNivel.OPERACAO,
                    CodigoDepartamento.COMERCIAL, ECanal.D2D_PROPRIO)))
            .doesNotThrowAnyException();
        verify(usuarioRepository, times(1)).saveAndFlush(any());
    }

    @Test
    public void save_naoDeveLancarException_quandoUsuarioPossuiCargoForaVerificacao() {
        when(usuarioRepository.findById(any()))
            .thenReturn(Optional.of(umUsuarioCompleto(OPERACAO_CONSULTOR, 3,
                OPERACAO, CodigoDepartamento.COMERCIAL, ECanal.D2D_PROPRIO)));
        Assertions.assertThatCode(() -> usuarioService.save(
                umUsuarioCompleto(GERENTE_OPERACAO, 7,
                    CodigoNivel.OPERACAO, CodigoDepartamento.COMERCIAL, ECanal.D2D_PROPRIO)))
            .doesNotThrowAnyException();
        verify(equipeVendaD2dService, never()).getEquipeVendasBySupervisorId(any());
        verify(equipeVendasUsuarioService, never()).buscarUsuarioEquipeVendasPorId(any());
        verify(usuarioRepository, times(1)).saveAndFlush(any());
    }

    @Test
    public void save_naoDeveLancarException_quandoUsuarioPossuiDepartamentoForaVerificacao() {
        when(usuarioRepository.findById(any()))
            .thenReturn(Optional.of(umUsuarioCompleto(ASSISTENTE_OPERACAO, 2, OPERACAO,
                CodigoDepartamento.AGENTE_AUTORIZADO, ECanal.D2D_PROPRIO)));
        Assertions.assertThatCode(() -> usuarioService.save(
                umUsuarioCompleto(COORDENADOR_OPERACAO, 8,
                    CodigoNivel.OPERACAO, CodigoDepartamento.AGENTE_AUTORIZADO, ECanal.D2D_PROPRIO)))
            .doesNotThrowAnyException();
        verify(equipeVendasUsuarioService, never()).buscarUsuarioEquipeVendasPorId(any());
        verify(usuarioRepository, times(1)).saveAndFlush(any());
    }

    @Test
    public void save_naoDeveLancarException_quandoUsuarioPossuiCanalForaVerificacao() {
        when(usuarioRepository.findById(any()))
            .thenReturn(Optional.of(umUsuarioCompleto(ASSISTENTE_OPERACAO, 2, OPERACAO,
                CodigoDepartamento.COMERCIAL, ECanal.ATIVO_PROPRIO)));
        Assertions.assertThatCode(() -> usuarioService.save(
                umUsuarioCompleto(COORDENADOR_OPERACAO, 8,
                    CodigoNivel.OPERACAO, CodigoDepartamento.AGENTE_AUTORIZADO, ECanal.ATIVO_PROPRIO)))
            .doesNotThrowAnyException();
        verify(equipeVendasUsuarioService, never()).buscarUsuarioEquipeVendasPorId(any());
        verify(usuarioRepository, times(1)).saveAndFlush(any());
    }

    @Test
    public void buscarTodosVendedoresReceptivos_deveRetornarVendedoresReceptivoComoSelectResponse_quandoValido() {
        when(usuarioRepository.findAllVendedoresReceptivos())
            .thenReturn(List.of(umVendedorReceptivo()));

        var vendedores = usuarioService.buscarTodosVendedoresReceptivos();
        verify(usuarioRepository, times(1)).findAllVendedoresReceptivos();
        assertThat(vendedores.stream().allMatch(this::isSelectResponse)).isTrue();
    }

    @Test
    public void buscarTodosVendedoresReceptivos_retornarVendedorReceptivoNomeComInativo_quandoTerUsuarioInativo() {
        var vendedorReceptivoInativo = umVendedorReceptivo();
        vendedorReceptivoInativo.setSituacao(ESituacao.I);
        when(usuarioRepository.findAllVendedoresReceptivos())
            .thenReturn(List.of(vendedorReceptivoInativo));

        var vendedores = usuarioService.buscarTodosVendedoresReceptivos();
        verify(usuarioRepository, times(1)).findAllVendedoresReceptivos();
        assertThat(vendedores.stream().allMatch(this::isSelectResponse)).isTrue();
        assertThat(vendedores).contains(umSelectResponseDeVendedorReceptivoInativo());
    }

    @Test
    public void buscarTodosVendedoresReceptivos_retornarVendedorReceptivoNomeComRealocado_quandoTerUsuarioRealocado() {
        var vendededorReceptivoRealocado = umVendedorReceptivo();
        vendededorReceptivoRealocado.setSituacao(ESituacao.R);

        when(usuarioRepository.findAllVendedoresReceptivos())
            .thenReturn(List.of(vendededorReceptivoRealocado));

        var vendedores = usuarioService.buscarTodosVendedoresReceptivos();
        verify(usuarioRepository, times(1)).findAllVendedoresReceptivos();
        assertThat(vendedores.stream().allMatch(this::isSelectResponse)).isTrue();
        assertThat(vendedores).contains(umSelectResponseDeVendedorReceptivoRealocado());
    }

    private boolean isSelectResponse(Object obj) {
        return obj instanceof SelectResponse;
    }

    @Test
    public void buscarVendedoresReceptivosPorId_deverRetornarUsuarioVendedorReceptivoResponse_quandoValido() {
        when(usuarioRepository.findAllVendedoresReceptivosByIds(anyList())).thenReturn(List.of(umVendedorReceptivo()));
        var vendedores = usuarioService.buscarVendedoresReceptivosPorId(List.of(1));
        assertThat(vendedores).extracting("nome", "email", "loginNetSales", "nivel", "organizacao")
            .containsExactly(
                tuple(
                    umVendedorReceptivo().getNome(),
                    umVendedorReceptivo().getEmail(),
                    umVendedorReceptivo().getLoginNetSales(),
                    umVendedorReceptivo().getNivelNome(),
                    umVendedorReceptivo().getOrganizacao().getNome()));
    }

    @Test
    public void buscarVendedoresReceptivosPorId_deverRetornarUsuarioVendedorReceptivo_quandoTiverVendedorInativo() {
        var vendedorReceptivo = umVendedorReceptivo();
        vendedorReceptivo.setSituacao(ESituacao.I);
        when(usuarioRepository.findAllVendedoresReceptivosByIds(anyList())).thenReturn(List.of((vendedorReceptivo)));
        var vendedores = usuarioService.buscarVendedoresReceptivosPorId(List.of(1));
        assertThat(vendedores).extracting("nome", "email", "loginNetSales", "nivel", "organizacao")
            .containsExactly(
                tuple(
                    umVendedorReceptivo().getNome() + " (INATIVO)",
                    umVendedorReceptivo().getEmail(),
                    umVendedorReceptivo().getLoginNetSales(),
                    umVendedorReceptivo().getNivelNome(),
                    umVendedorReceptivo().getOrganizacao().getNome()));
    }

    @Test
    public void buscarVendedoresReceptivosPorId_deverRetornarUsuarioVendedorReceptivo_quandoTiverVendedorRealocado() {
        var vendedorReceptivo = umVendedorReceptivo();
        vendedorReceptivo.setSituacao(ESituacao.R);
        when(usuarioRepository.findAllVendedoresReceptivosByIds(anyList())).thenReturn(List.of((vendedorReceptivo)));
        var vendedores = usuarioService.buscarVendedoresReceptivosPorId(List.of(1));
        assertThat(vendedores).extracting("nome", "email", "loginNetSales", "nivel", "organizacao")
            .containsExactly(
                tuple(
                    umVendedorReceptivo().getNome() + " (REALOCADO)",
                    umVendedorReceptivo().getEmail(),
                    umVendedorReceptivo().getLoginNetSales(),
                    umVendedorReceptivo().getNivelNome(),
                    umVendedorReceptivo().getOrganizacao().getNome()));
    }

    @Test
    public void buscarUsuariosDaHierarquiaDoUsuarioLogadoPorFiltros_usuarios_quandoUsuarioDiferenteDeAaEXbrain() {
        var usuarioComPermissaoDeVisualizarAa = umUsuarioAutenticado(1, "AGENTE_AUTORIZADO",
            CodigoCargo.AGENTE_AUTORIZADO_SOCIO, AUT_VISUALIZAR_GERAL);
        when(autenticacaoService.getUsuarioAutenticado()).thenReturn(usuarioComPermissaoDeVisualizarAa);
        when(usuarioRepository.findAll(any(Predicate.class), any(Sort.class))).thenReturn(umaListaUsuariosExecutivosAtivo());

        assertThat(usuarioService.buscarUsuariosDaHierarquiaDoUsuarioLogadoPorFiltros(umUsuarioFiltro()))
            .extracting("label", "value")
            .containsExactly(tuple("JOSÉ", 1),
                tuple("HIGOR", 2));

        verify(usuarioRepository, times(1))
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
        when(usuarioRepository.getUsuariosSubordinados(any())).thenReturn(new ArrayList<>(List.of(2, 4, 5)));
        when(usuarioRepository.findAll(any(Predicate.class), any(Sort.class))).thenReturn(umaUsuariosList());

        assertThat(usuarioService.buscarUsuariosDaHierarquiaDoUsuarioLogadoPorFiltros(umUsuarioFiltro()))
            .extracting("label", "value")
            .containsExactly(tuple("Caio", 1),
                tuple("Mario (INATIVO)", 2),
                tuple("Maria (REALOCADO)", 3));

        verify(usuarioRepository, times(1))
            .findAll(eq(new UsuarioPredicate()
                .comCanal(ECanal.D2D_PROPRIO)
                .comCodigosCargos(List.of(SUPERVISOR_OPERACAO, ASSISTENTE_OPERACAO))
                .ignorarAa(true).ignorarXbrain(true)
                .comIds(List.of(3, 2, 4, 5, 1, 1))
                .build()), eq(new Sort(ASC, "situacao", "nome")));
    }

    @Test
    public void getUsuariosDaHierarquiaAtivoLocalDoUsuarioLogado_deveRetornarUsuarios_seEncontrado() {
        when(autenticacaoService.getUsuarioAutenticado())
            .thenReturn(UsuarioAutenticadoHelper.umUsuarioAutenticadoNivelMso());
        when(usuarioRepository.findAll(eq(
            new UsuarioPredicate().filtraPermitidos(
                    autenticacaoService.getUsuarioAutenticado(), usuarioService, true)
                .build())))
            .thenReturn(umaUsuariosList());

        assertThat(usuarioService.getUsuariosDaHierarquiaAtivoLocalDoUsuarioLogado())
            .isEqualTo(umaUsuariosList());
    }

    @Test
    public void getUsuariosDaHierarquiaAtivoLocalDoUsuarioLogado_naoDeveRetornarUsuarios_seNaoEncontrado() {
        when(autenticacaoService.getUsuarioAutenticado())
            .thenReturn(UsuarioAutenticadoHelper.umUsuarioAutenticadoNivelMso());
        when(usuarioRepository.findAll(eq(
            new UsuarioPredicate().filtraPermitidos(
                    autenticacaoService.getUsuarioAutenticado(), usuarioService, true)
                .build())))
            .thenReturn(Collections.emptyList());

        assertThat(usuarioService.getUsuariosDaHierarquiaAtivoLocalDoUsuarioLogado())
            .isEmpty();
    }

    @Test
    public void getUsuariosPermitidosPelaEquipeDeVenda_deveBuscarPorCargosDoAtivo_seCanalForAtivoLocal() {
        when(autenticacaoService.getUsuarioCanal()).thenReturn(ECanal.ATIVO_PROPRIO);

        usuarioService.getUsuariosPermitidosPelaEquipeDeVenda();

        verify(equipeVendaD2dService, times(1))
            .getUsuariosPermitidos(eq(List.of(SUPERVISOR_OPERACAO, ASSISTENTE_OPERACAO, OPERACAO_TELEVENDAS)));
    }

    @Test
    public void getUsuariosPermitidosPelaEquipeDeVenda_deveBuscarPorCargosDoD2d_seCanalForD2d() {
        when(autenticacaoService.getUsuarioCanal()).thenReturn(ECanal.D2D_PROPRIO);

        usuarioService.getUsuariosPermitidosPelaEquipeDeVenda();

        verify(equipeVendaD2dService, times(1))
            .getUsuariosPermitidos(eq(List.of(SUPERVISOR_OPERACAO, ASSISTENTE_OPERACAO, VENDEDOR_OPERACAO)));
    }

    @Test
    public void getUsuariosOperacaoCanalAa_deveRetornarListaUsuariosCanalOpEnivelAa() {
        var codigoNivel = OPERACAO;
        when(usuarioRepository.getUsuariosOperacaoCanalAa(eq(codigoNivel)))
            .thenReturn(List.of(outroUsuarioNivelOpCanalAa()));

        assertThat(usuarioService.getUsuariosOperacaoCanalAa(codigoNivel))
            .containsExactly(outroUsuarioNivelOpCanalAaResponse());
    }

    @Test
    public void validarSeUsuarioCpfEmailNaoCadastrados_deveRetornarValidacaoException_quandoCpfJaCadastrado() {
        doThrow(new ValidacaoException(CPF_JA_CADASTRADO))
            .when(usuarioRepository)
            .findTop1UsuarioByCpfAndSituacaoNot(eq("07981056233"), eq(ESituacao.R));

        assertThatCode(() -> usuarioService
            .validarSeUsuarioCpfEmailNaoCadastrados("07981056233", "NOVOSOCIO@EMPRESA.COM.BR"))
            .isInstanceOf(ValidacaoException.class)
            .hasMessage(CPF_JA_CADASTRADO);

        verify(usuarioRepository, times(1))
            .findTop1UsuarioByCpfAndSituacaoNot(eq("07981056233"), eq(ESituacao.R));
        verify(usuarioRepository, never())
            .findTop1UsuarioByEmailIgnoreCaseAndSituacaoNot(eq("NOVOSOCIO@EMPRESA.COM.BR"), eq(ESituacao.R));
    }

    @Test
    public void validarSeUsuarioCpfEmailNaoCadastrados_deveRetornarValidacaoException_quandoEmailJaCadastrado() {
        when(usuarioRepository.findTop1UsuarioByCpfAndSituacaoNot(eq("07981056233"), eq(ESituacao.R)))
            .thenReturn(Optional.empty());

        doThrow(new ValidacaoException(EMAIL_JA_CADASTRADO))
            .when(usuarioRepository)
            .findTop1UsuarioByEmailIgnoreCaseAndSituacaoNot(eq("NOVOSOCIO@EMPRESA.COM.BR"), eq(ESituacao.R));

        assertThatCode(() -> usuarioService
            .validarSeUsuarioCpfEmailNaoCadastrados("07981056233", "NOVOSOCIO@EMPRESA.COM.BR"))
            .isInstanceOf(ValidacaoException.class)
            .hasMessage(EMAIL_JA_CADASTRADO);

        verify(usuarioRepository, times(1))
            .findTop1UsuarioByCpfAndSituacaoNot(eq("07981056233"), eq(ESituacao.R));
        verify(usuarioRepository, times(1))
            .findTop1UsuarioByEmailIgnoreCaseAndSituacaoNot(eq("NOVOSOCIO@EMPRESA.COM.BR"), eq(ESituacao.R));
    }

    @Test
    public void validarSeUsuarioCpfEmailNaoCadastrados_naoDeveRetornarValidacaoException_quandoCpfEEmailNaoCadastrados() {
        when(usuarioRepository.findTop1UsuarioByCpfAndSituacaoNot(eq("07981056233"), eq(ESituacao.R)))
            .thenReturn(Optional.empty());

        when(usuarioRepository.findTop1UsuarioByEmailIgnoreCaseAndSituacaoNot(eq("NOVOSOCIO@EMPRESA.COM.BR"), eq(ESituacao.R)))
            .thenReturn(Optional.empty());

        assertThatCode(() -> usuarioService
            .validarSeUsuarioCpfEmailNaoCadastrados("07981056233", "NOVOSOCIO@EMPRESA.COM.BR"))
            .doesNotThrowAnyException();

        verify(usuarioRepository, times(1))
            .findTop1UsuarioByCpfAndSituacaoNot(eq("07981056233"), eq(ESituacao.R));
        verify(usuarioRepository, times(1))
            .findTop1UsuarioByEmailIgnoreCaseAndSituacaoNot(eq("NOVOSOCIO@EMPRESA.COM.BR"), eq(ESituacao.R));
    }

    @Test
    public void inativarAntigoSocioPrincipal_deveLancarException_quandoUsuarioNaoLocalizado() {
        doReturn(Optional.empty())
            .when(usuarioRepository)
            .findByEmail("ANTIGOSOCIO@EMPRESA.COM.BR");

        assertThatCode(() -> usuarioService
            .inativarAntigoSocioPrincipal("ANTIGOSOCIO@EMPRESA.COM.BR"))
            .isInstanceOf(ValidacaoException.class)
            .hasMessage("Usuário não encontrado.");

        verify(usuarioRepository).findByEmail("ANTIGOSOCIO@EMPRESA.COM.BR");
        verify(usuarioRepository, never()).save(any(Usuario.class));
        verify(autenticacaoService, never()).logout(anyInt());
        verify(agenteAutorizadoService, never()).inativarAntigoSocioPrincipal("ANTIGOSOCIO@EMPRESA.COM.BR");
    }

    @Test
    public void inativarAntigoSocioPrincipal_naoDeveInativarAntigoSocioPrincipal_seSituacaoDoUsuarioForAtivo() {
        doReturn(Optional.of(umAntigoSocioPrincipal(ESituacao.I)))
            .when(usuarioRepository)
            .findByEmail("ANTIGOSOCIO@EMPRESA.COM.BR");

        assertThatCode(() -> usuarioService
            .inativarAntigoSocioPrincipal("ANTIGOSOCIO@EMPRESA.COM.BR"))
            .doesNotThrowAnyException();

        verify(usuarioRepository).findByEmail("ANTIGOSOCIO@EMPRESA.COM.BR");
        verify(usuarioRepository, never()).save(any(Usuario.class));
        verify(autenticacaoService, never()).logout(anyInt());
        verify(agenteAutorizadoService).inativarAntigoSocioPrincipal("ANTIGOSOCIO@EMPRESA.COM.BR");
    }

    @Test
    public void inativarAntigoSocioPrincipal_deveLancarException_quandoSocioNaoInativadoNoPol() {
        doReturn(Optional.of(umAntigoSocioPrincipal(ESituacao.A)))
            .when(usuarioRepository)
            .findByEmail("ANTIGOSOCIO@EMPRESA.COM.BR");

        doThrow(new IntegracaoException(EErrors.ERRO_SOCIO_NAO_INATIVADO_NO_POL.getDescricao()))
            .when(agenteAutorizadoService)
            .inativarAntigoSocioPrincipal("ANTIGOSOCIO@EMPRESA.COM.BR");

        assertThatCode(() -> usuarioService
            .inativarAntigoSocioPrincipal("ANTIGOSOCIO@EMPRESA.COM.BR"))
            .isInstanceOf(IntegracaoException.class)
            .hasMessage(EErrors.ERRO_SOCIO_NAO_INATIVADO_NO_POL.getDescricao());

        verify(usuarioRepository).findByEmail("ANTIGOSOCIO@EMPRESA.COM.BR");
        verify(usuarioRepository).save(umAntigoSocioPrincipal(ESituacao.I));
        verify(autenticacaoService).logout(22);
        verify(agenteAutorizadoService).inativarAntigoSocioPrincipal("ANTIGOSOCIO@EMPRESA.COM.BR");
    }

    @Test
    public void inativarAntigoSocioPrincipal_deveInativarSocioPrincipal_quandoLocalizadoESituacaoAtivo() {
        doReturn(Optional.of(umAntigoSocioPrincipal(ESituacao.A)))
            .when(usuarioRepository)
            .findByEmail("ANTIGOSOCIO@EMPRESA.COM.BR");

        assertThatCode(() -> usuarioService
            .inativarAntigoSocioPrincipal("ANTIGOSOCIO@EMPRESA.COM.BR"))
            .doesNotThrowAnyException();

        verify(usuarioRepository).findByEmail("ANTIGOSOCIO@EMPRESA.COM.BR");
        verify(usuarioRepository).save(umAntigoSocioPrincipal(ESituacao.I));
        verify(autenticacaoService).logout(22);
        verify(agenteAutorizadoService).inativarAntigoSocioPrincipal("ANTIGOSOCIO@EMPRESA.COM.BR");
    }

    @Test
    public void limparCpfAntigoSocioPrincipal_deveRetornarValidacaoException_quandoUsuarioNaoCadastrado() {
        doThrow(new ValidacaoException(USUARIO_NAO_ENCONTRADO)).when(usuarioRepository).findById(eq(21));

        assertThatCode(() -> usuarioService.limparCpfAntigoSocioPrincipal(21))
            .isInstanceOf(ValidacaoException.class)
            .hasMessage(USUARIO_NAO_ENCONTRADO);

        verify(usuarioRepository, times(1)).findById(eq(21));
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    public void limparCpfAntigoSocioPrincipal_deveRetornarOk_quandoTudoOk() {
        var umSocioPrincipalCpfLimpo = umSocioPrincipal();
        umSocioPrincipalCpfLimpo.setCpf(null);

        when(usuarioRepository.findById(eq(23))).thenReturn(Optional.of(umSocioPrincipal()));
        when(usuarioRepository.save(umSocioPrincipalCpfLimpo)).thenReturn(umSocioPrincipalCpfLimpo);

        usuarioService.limparCpfAntigoSocioPrincipal(23);

        verify(usuarioRepository, times(1)).findById(eq(23));
        verify(usuarioRepository, times(1)).save(umSocioPrincipalCpfLimpo);
    }

    @Test
    public void atualizarEmailSocioInativo_deveRetornarValidacaoException_quandoUsuarioNaoCadastrado() {
        doThrow(new ValidacaoException(USUARIO_NAO_ENCONTRADO)).when(usuarioRepository).findById(eq(21));

        assertThatCode(() -> usuarioService.atualizarEmailSocioInativo(21))
            .isInstanceOf(ValidacaoException.class)
            .hasMessage(USUARIO_NAO_ENCONTRADO);

        verify(usuarioRepository, times(1)).findById(eq(21));
        verify(usuarioRepository, never()).save(any(Usuario.class));
        verify(agenteAutorizadoService, never()).atualizarEmailSocioPrincipalInativo(anyString(), anyString(), anyInt());
    }

    @Test
    public void atualizarEmailSocioInativo_deveRetornarValidacaoException_quandoEmailDoUsuarioForVazio() {
        var umSocioPrincipalComEmailVazio = umSocioPrincipal();
        umSocioPrincipalComEmailVazio.setEmail("");

        when(usuarioRepository.findById(eq(23))).thenReturn(Optional.of(umSocioPrincipalComEmailVazio));

        assertThatCode(() -> usuarioService.atualizarEmailSocioInativo(23))
            .isInstanceOf(ValidacaoException.class)
            .hasMessage(ERRO_VALIDAR_EMAIL_CADASTRADO.getDescricao());

        verify(usuarioRepository, times(1)).findById(eq(23));
        verify(usuarioRepository, never()).save(any(Usuario.class));
        verify(agenteAutorizadoService, never()).atualizarEmailSocioPrincipalInativo(anyString(), anyString(), anyInt());
    }

    @Test
    public void atualizarEmailSocioInativo_deveRetornarValidacaoException_quandoEmailDoUsuarioForNulo() {
        var umSocioPrincipalComEmailNulo = umSocioPrincipal();
        umSocioPrincipalComEmailNulo.setEmail(null);

        when(usuarioRepository.findById(eq(23))).thenReturn(Optional.of(umSocioPrincipalComEmailNulo));

        assertThatCode(() -> usuarioService.atualizarEmailSocioInativo(23))
            .isInstanceOf(ValidacaoException.class)
            .hasMessage(ERRO_VALIDAR_EMAIL_CADASTRADO.getDescricao());

        verify(usuarioRepository, times(1)).findById(eq(23));
        verify(usuarioRepository, never()).save(any(Usuario.class));
        verify(agenteAutorizadoService, never()).atualizarEmailSocioPrincipalInativo(anyString(), anyString(), anyInt());
    }

    @Test
    public void atualizarEmailSocioInativo_deveRetornarValidacaoException_quandoEmailDoUsuarioForSemDominio() {
        var umSocioPrincipalComEmailSemDominio = umSocioPrincipal();
        umSocioPrincipalComEmailSemDominio.setEmail("NOVOSOCIO@EMPRESA");

        when(usuarioRepository.findById(eq(23))).thenReturn(Optional.of(umSocioPrincipalComEmailSemDominio));

        assertThatCode(() -> usuarioService.atualizarEmailSocioInativo(23))
            .isInstanceOf(ValidacaoException.class)
            .hasMessage(ERRO_VALIDAR_EMAIL_CADASTRADO.getDescricao());

        verify(usuarioRepository, times(1)).findById(eq(23));
        verify(usuarioRepository, never()).save(any(Usuario.class));
        verify(agenteAutorizadoService, never()).atualizarEmailSocioPrincipalInativo(anyString(), anyString(), anyInt());
    }

    @Test
    public void atualizarEmailSocioInativo_deveRetornarValidacaoException_quandoEmailDoUsuarioForComMaisDeUmArroba() {
        var umSocioPrincipalComEmailComMaisDeUmArroba = umSocioPrincipal();
        umSocioPrincipalComEmailComMaisDeUmArroba.setEmail("NOVO@SOCIO@EMPRESA.COM.BR");

        when(usuarioRepository.findById(eq(23))).thenReturn(Optional.of(umSocioPrincipalComEmailComMaisDeUmArroba));

        assertThatCode(() -> usuarioService.atualizarEmailSocioInativo(23))
            .isInstanceOf(ValidacaoException.class)
            .hasMessage(ERRO_VALIDAR_EMAIL_CADASTRADO.getDescricao());

        verify(usuarioRepository, times(1)).findById(eq(23));
        verify(usuarioRepository, never()).save(any(Usuario.class));
        verify(agenteAutorizadoService, never()).atualizarEmailSocioPrincipalInativo(anyString(), anyString(), anyInt());
    }

    @Test
    public void atualizarEmailSocioInativo_deveRetornarIntegracaoException_quandoEmailSocioPrincipalNaoAtualizadoNoPol() {
        var umSocioPrincipalComEmailAtualizado = umSocioPrincipal();
        umSocioPrincipalComEmailAtualizado.setEmail("NOVOSOCIO.INATIVO@EMPRESA.COM.BR");

        when(usuarioRepository.findById(eq(23))).thenReturn(Optional.of(umSocioPrincipal()));
        when(usuarioRepository.save(eq(umSocioPrincipalComEmailAtualizado))).thenReturn(umSocioPrincipalComEmailAtualizado);

        doThrow(new IntegracaoException(EErrors.ERRO_EMAIL_SOCIO_NAO_ATUALIZADO_NO_POL.getDescricao()))
            .when(agenteAutorizadoService)
            .atualizarEmailSocioPrincipalInativo(eq("NOVOSOCIO@EMPRESA.COM.BR"), eq("NOVOSOCIO.INATIVO@EMPRESA.COM.BR"), eq(23));

        assertThatCode(() -> usuarioService
            .atualizarEmailSocioInativo(23))
            .isInstanceOf(IntegracaoException.class)
            .hasMessage(EErrors.ERRO_EMAIL_SOCIO_NAO_ATUALIZADO_NO_POL.getDescricao());

        verify(usuarioRepository, times(1))
            .findById(eq(23));
        verify(usuarioRepository, times(1))
            .save(eq(umSocioPrincipalComEmailAtualizado));
        verify(agenteAutorizadoService, times(1))
            .atualizarEmailSocioPrincipalInativo(eq("NOVOSOCIO@EMPRESA.COM.BR"), eq("NOVOSOCIO.INATIVO@EMPRESA.COM.BR"), eq(23));
    }

    @Test
    public void atualizarEmailSocioInativo_deveRetornarOk_quandoTudoOk() {
        var umSocioPrincipalComEmailAtualizado = umSocioPrincipal();
        umSocioPrincipalComEmailAtualizado.setEmail("NOVOSOCIO.INATIVO@EMPRESA.COM.BR");

        when(usuarioRepository.findById(eq(23))).thenReturn(Optional.of(umSocioPrincipal()));
        when(usuarioRepository.save(eq(umSocioPrincipalComEmailAtualizado))).thenReturn(umSocioPrincipalComEmailAtualizado);

        usuarioService.atualizarEmailSocioInativo(23);

        verify(usuarioRepository, times(1))
            .findById(eq(23));
        verify(usuarioRepository, times(1))
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
            .organizacao("organizacao")
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
            .organizacao("organizacao")
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

    private Usuario umUsuarioCompleto(int cargoId, CodigoNivel nivel, int departamentoId) {
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
                .codigo(VENDEDOR_OPERACAO)
                .nivel(Nivel
                    .builder()
                    .codigo(nivel)
                    .nome(nivel.name())
                    .build())
                .build())
            .departamento(Departamento
                .builder()
                .id(departamentoId)
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
        var organizacao = Organizacao.builder().id(1).nome("Org teste").build();
        usuario.setCargo(cargo);
        usuario.setOrganizacao(organizacao);
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

    private List<EquipeVendaUsuarioResponse> listaVazia() {
        var lista = new ArrayList<EquipeVendaUsuarioResponse>();
        return lista;
    }

    private Usuario criaNovoUsuario(int cargoId, CodigoDepartamento departamento) {
        return Usuario.builder().id(1)
            .cargo(new Cargo(cargoId))
            .departamento(new Departamento(3))
            .build();
    }

    private EquipeVendaUsuarioResponse criaEquipeVendaUsuarioResponse() {
        return EquipeVendaUsuarioResponse.builder().id(1)
            .build();
    }
}
