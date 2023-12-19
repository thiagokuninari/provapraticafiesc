package br.com.xbrain.autenticacao.modules.solicitacaoramal.controller;

import br.com.xbrain.autenticacao.modules.agenteautorizado.service.AgenteAutorizadoService;
import br.com.xbrain.autenticacao.modules.call.dto.RamalResponse;
import br.com.xbrain.autenticacao.modules.call.dto.TelefoniaResponse;
import br.com.xbrain.autenticacao.modules.call.service.CallService;
import br.com.xbrain.autenticacao.modules.email.service.EmailService;
import br.com.xbrain.autenticacao.modules.gestaocolaboradorespol.service.EquipeVendasService;
import br.com.xbrain.autenticacao.modules.parceirosonline.dto.AgenteAutorizadoResponse;
import br.com.xbrain.autenticacao.modules.parceirosonline.dto.SocioResponse;
import br.com.xbrain.autenticacao.modules.parceirosonline.dto.UsuarioAgenteAutorizadoResponse;
import br.com.xbrain.autenticacao.modules.parceirosonline.service.ParceirosOnlineService;
import br.com.xbrain.autenticacao.modules.parceirosonline.service.SocioService;
import br.com.xbrain.autenticacao.modules.solicitacaoramal.dto.SolicitacaoRamalAtualizarStatusRequest;
import br.com.xbrain.autenticacao.modules.solicitacaoramal.dto.SolicitacaoRamalFiltros;
import br.com.xbrain.autenticacao.modules.solicitacaoramal.dto.SolicitacaoRamalRequest;
import br.com.xbrain.autenticacao.modules.solicitacaoramal.enums.ESituacaoSolicitacao;
import br.com.xbrain.autenticacao.modules.solicitacaoramal.enums.ETipoImplantacao;
import br.com.xbrain.autenticacao.modules.solicitacaoramal.model.SolicitacaoRamal;
import br.com.xbrain.autenticacao.modules.solicitacaoramal.service.SolicitacaoRamalHistoricoService;
import br.com.xbrain.autenticacao.modules.solicitacaoramal.service.SolicitacaoRamalService;
import br.com.xbrain.autenticacao.modules.usuario.enums.ECanal;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static br.com.xbrain.autenticacao.modules.solicitacaoramal.enums.ESituacaoSolicitacao.REJEITADO;
import static helpers.TestsHelper.convertObjectToJsonBytes;
import static helpers.TestsHelper.getAccessToken;
import static helpers.Usuarios.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Sql(scripts = {"classpath:/tests_database.sql", "classpath:/tests_solicitacao_ramal_database.sql"})
public class SolicitacaoRamalControllerTest {

    private static final String URL_API_SOLICITACAO_RAMAL = "/api/solicitacao-ramal";
    private static final String URL_API_SOLICITACAO_RAMAL_GERENCIAL = "/api/solicitacao-ramal/gerencia";
    @Autowired
    private SolicitacaoRamalService solicitacaoRamalService;
    @MockBean
    private SolicitacaoRamalFiltros solicitacaoRamalFiltros;
    @Autowired
    private MockMvc mvc;
    @MockBean
    private SolicitacaoRamalHistoricoService historicoService;
    @MockBean
    private ParceirosOnlineService parceirosOnlineService;
    @MockBean
    private AgenteAutorizadoService agenteAutorizadoService;
    @MockBean
    private EquipeVendasService equipeVendasService;
    @MockBean
    private EmailService emailService;
    @MockBean
    private CallService callService;
    @MockBean
    private SocioService socioService;

    @Before
    public void setUp() {
        when(agenteAutorizadoService.getAgentesAutorizadosPermitidos(any())).thenReturn(Arrays.asList(1, 2));
        when(equipeVendasService.getEquipesPorSupervisor(anyInt())).thenReturn(Collections.emptyList());
        when(agenteAutorizadoService.getAaById(anyInt())).thenReturn(criaAa());
    }

    @Test
    public void getColaboradoresBySolicitacaoId_listaComQuatroRegistros_quandoVisualizarColaboradoresPeloSolicitacaoid()
        throws Exception {
        mvc.perform(get(URL_API_SOLICITACAO_RAMAL + "/colaboradores/1")
                .header("Authorization", getAccessToken(mvc, HELP_DESK))
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(5)));
    }

    @Test
    public void atualizarSituacao_solicitacaoComSituacaoEnviado_quandoAlterarASituacaoPraEnviado() throws Exception {
        mvc.perform(post(URL_API_SOLICITACAO_RAMAL_GERENCIAL + "/atualiza-status")
                .header("Authorization", getAccessToken(mvc, HELP_DESK))
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(criaSolicitacaoRamalRequest())))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(4)))
            .andExpect(jsonPath("$.situacao", is("ENVIADO")));

        verify(historicoService, times(1)).save(any());
    }

    @Test
    public void atualizarSituacao_isForbidden_seUsuarioNaoPossuirPermissaoParaAlterarSituacao() throws Exception {
        mvc.perform(post(URL_API_SOLICITACAO_RAMAL_GERENCIAL + "/atualiza-status")
                .header("Authorization", getAccessToken(mvc, SOCIO_AA))
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(criaSolicitacaoRamalRequest())))
            .andExpect(status().isForbidden());

        verify(historicoService, times(0)).save(any());
    }

    @Test
    public void getDadosAdicionais_dadosDoAa_quandoPassarAgenteAutorizadoPorParametroUrl() throws Exception {
        when(agenteAutorizadoService.getUsuariosByAaId(anyInt(), anyBoolean())).thenReturn(criaListaUsuariosAtivos());
        when(callService.obterNomeTelefoniaPorId(anyInt())).thenReturn(criaTelefonia());
        when(callService.obterRamaisParaCanal(ECanal.AGENTE_AUTORIZADO, 1)).thenReturn(criaListaRamal());
        when(socioService.findSocioPrincipalByAaId(anyInt())).thenReturn(criaSocio());

        mvc.perform(get(URL_API_SOLICITACAO_RAMAL
                + "/dados-canal?canal=AGENTE_AUTORIZADO&agenteAutorizadoId=1")
                .header("Authorization", getAccessToken(mvc, ADMIN))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.discadora", is("DISCADORA UN")))
            .andExpect(jsonPath("$.socioPrincipal", is("FULANO")))
            .andExpect(jsonPath("$.usuariosAtivos", is(0)))
            .andExpect(jsonPath("$.quantidadeRamais", is(2)))
            .andExpect(jsonPath("$.agenteAutorizadoRazaoSocial", is("RAZAO SOCIAL AA")));
    }

    @Test
    public void getAll_listaComQuatroRegistro_quandoHouverSolicitacoesPendenteOuEmAndamento() {
        List<SolicitacaoRamal> resultList =
            solicitacaoRamalService.getAllSolicitacoesPendenteOuEmAndamentoComEmailExpiracaoFalse();
        Assert.assertEquals(6, resultList.size());
    }

    @Test
    public void enviarEmailSolicitacoesQueVaoExpirar_enviarEmailFoiInvocadoQuatroVezes_quandoSolicitacaoForExpirar() {
        solicitacaoRamalService.enviarEmailSolicitacoesQueVaoExpirar();

        verify(emailService, times(6)).enviarEmailTemplate(anyList(), any(), any(), any());
    }

    @Test
    public void getAllGerencia_forbidden_quandoUsuarioNaoTiverPermissaoETentaAcessarTelaDeGerencia() throws Exception {
        mvc.perform(get(URL_API_SOLICITACAO_RAMAL_GERENCIAL)
                .header("Authorization", getAccessToken(mvc, SOCIO_AA))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isForbidden());
    }

    @Test
    public void getAllGerencia_listaComRegistros_quandoAcessarListagemDaTelaDeGerencia() throws Exception {
        mvc.perform(get(URL_API_SOLICITACAO_RAMAL_GERENCIAL + "/?page=0&size=10&canal=D2D_PROPRIO")
                .header("Authorization", getAccessToken(mvc, HELP_DESK))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content", hasSize(1)));
    }

    @Test
    public void getAllDetalhar_forbidden_quandoUsuarioNaoTiverPermissaoPraDetalharUmaSolicitacao() throws Exception {
        mvc.perform(get(URL_API_SOLICITACAO_RAMAL_GERENCIAL + "/detalhar/?agenteAutorizadoId=1")
                .header("Authorization", getAccessToken(mvc, SOCIO_AA))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isForbidden());
    }

    @Test
    public void getAll_listaComDezRegistro_seUsuarioPossuirPermissaoHelpDesk() throws Exception {
        mvc.perform(get(URL_API_SOLICITACAO_RAMAL)
                .header("Authorization", getAccessToken(mvc, HELP_DESK))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content", hasSize(10)));
    }

    @Test
    public void getAll_listaComDezRegistro_seUsuarioPossuirPermissaoAdmin() throws Exception {
        mvc.perform(get(URL_API_SOLICITACAO_RAMAL)
                .header("Authorization", getAccessToken(mvc, ADMIN))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content", hasSize(10)));
    }

    @Test
    public void getAll_listaComSeteRegistros_seUsuarioPossuirPermissaoSocioAa() throws Exception {
        mvc.perform(get(URL_API_SOLICITACAO_RAMAL + "/?agenteAutorizadoId=2")
                .header("Authorization", getAccessToken(mvc, SOCIO_AA))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content", hasSize(7)));
    }

    @Test
    public void getAll_isForbiden_seUsuarioPossuirPermissaoOperacaoGerenteComercialETentarAcessarGerencial()
        throws Exception {
        mvc.perform(get(URL_API_SOLICITACAO_RAMAL_GERENCIAL)
                .header("Authorization", getAccessToken(mvc, OPERACAO_GERENTE_COMERCIAL))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isForbidden());
    }

    @Test
    public void getAll_isForbiden_seUsuarioPossuirPermissaoSocioAaETentarAcessarGerencial() throws Exception {
        mvc.perform(get(URL_API_SOLICITACAO_RAMAL_GERENCIAL)
                .header("Authorization", getAccessToken(mvc, SOCIO_AA))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isForbidden());
    }

    @Test
    public void getAll_isUnauthorized_quandoUsuarioNaoEstaAutenticado() throws Exception {
        mvc.perform(get(URL_API_SOLICITACAO_RAMAL).accept(MediaType.APPLICATION_JSON)).andExpect(status().isUnauthorized());
    }

    @Test
    public void getAll_listaComSeteRegistros_quandoLocalizarPeloAaId() throws Exception {
        mvc.perform(get(URL_API_SOLICITACAO_RAMAL + "/?agenteAutorizadoId=2")
                .header("Authorization", getAccessToken(mvc, SOCIO_AA))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content", hasSize(7)));
    }

    @Test
    public void update_isOk_quandoTentarAtualizarUmaSolicitacao() throws Exception {
        mvc.perform(put(URL_API_SOLICITACAO_RAMAL)
                .header("Authorization", getAccessToken(mvc, SOCIO_AA))
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(criaSolicitacaoRamal(5, 7129))))
            .andExpect(status().isOk());
    }

    @Test
    public void save_isCreated_quandoTentarSalvarUmaNovaSolicitacao() throws Exception {
        SolicitacaoRamalRequest request = criaSolicitacaoRamal(null, 7129);

        mvc.perform(post(URL_API_SOLICITACAO_RAMAL)
                .header("Authorization", getAccessToken(mvc, SOCIO_AA))
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.quantidadeRamais", is(request.getQuantidadeRamais())))
            .andExpect(jsonPath("$.situacao", is("PENDENTE")));

        verify(historicoService, times(1)).save(any());
        verify(emailService, times(1)).enviarEmailTemplate(anyList(), anyString(), any(), any());
    }

    @Test
    public void save_deveRetornarForbidden_quandoUsuarioNaoAutorizado() throws Exception {
        SolicitacaoRamalRequest request = criaSolicitacaoRamal(null, 7129);

        mvc.perform(post(URL_API_SOLICITACAO_RAMAL)
                .header("Authorization", getAccessToken(mvc, OPERACAO_ASSISTENTE))
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(request)))
            .andExpect(status().isForbidden());

        verify(historicoService, never()).save(any());
        verify(emailService, never()).enviarEmailTemplate(anyList(), anyString(), any(), any());
    }

    @Test
    public void save_badRequest_quandoTentarSalvarSolicitacaoHavendoUmaEmPendenteOuEmAndamento() throws Exception {
        SolicitacaoRamalRequest request = criaSolicitacaoRamal(null, 1);

        mvc.perform(post(URL_API_SOLICITACAO_RAMAL)
                .header("Authorization", getAccessToken(mvc, SOCIO_AA))
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.[*].message", containsInAnyOrder(
                "Não é possível salvar a solicitação de ramal, pois já existe uma pendente ou em andamento.")));
    }

    @Test
    public void atualizarSituacao_isOk_quandoAtualizarOStatusDeUmaSolicitacaoParaRejeitada() throws Exception {
        SolicitacaoRamalAtualizarStatusRequest request = criaSolicitacaoRamalAtualizarStatusRequest();

        mvc.perform(post(URL_API_SOLICITACAO_RAMAL_GERENCIAL + "/atualiza-status")
                .header("Authorization", getAccessToken(mvc, HELP_DESK))
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(1)))
            .andExpect(jsonPath("$.situacao", is("REJEITADO")));

        verify(historicoService, times(1)).save(any());
    }

    @Test
    public void save_validacaoCamposObrigatorio_quandoTentarSalvarSemOsCamposObrigatorios() throws Exception {
        mvc.perform(post(URL_API_SOLICITACAO_RAMAL)
                .header("Authorization", getAccessToken(mvc, OPERACAO_GERENTE))
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(new SolicitacaoRamalRequest())))
            .andExpect(jsonPath("$[*].message", containsInAnyOrder(
                "O campo canal é obrigatório.",
                "O campo melhorHorarioImplantacao é obrigatório.",
                "O campo quantidadeRamais é obrigatório.",
                "O campo melhorDataImplantacao é obrigatório.",
                "O campo telefoneTi é obrigatório.",
                "O campo emailTi é obrigatório.",
                "O campo tipoImplantacao é obrigatório.",
                "O campo usuariosSolicitadosIds é obrigatório.")));
    }

    @Test
    public void getAll_isBadRequest_quandoNaoEnviarAaIdEUsuarioNaoPossuirRoleGerenciarRamais() throws Exception {
        mvc.perform(get(URL_API_SOLICITACAO_RAMAL)
                .header("Authorization", getAccessToken(mvc, SOCIO_AA))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$[*].message", containsInAnyOrder(
                "Campo agente autorizado é obrigatório")));
    }

    @Test
    public void getAll_listaComDezRegistros_quandoLocalizarSemAgenteAutorizadoIdParaUsuarioAdmin() throws Exception {
        mvc.perform(get(URL_API_SOLICITACAO_RAMAL)
                .header("Authorization", getAccessToken(mvc, ADMIN))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content", hasSize(10)));
    }

    @Test
    public void getAll_isForbidden_quandoUsuarioForSocioMasNaoTemPermissaoSobreOAgenteAutorizado() throws Exception {
        mvc.perform(get(URL_API_SOLICITACAO_RAMAL + "/?agenteAutorizadoId=50")
                .header("Authorization", getAccessToken(mvc, SOCIO_AA))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isForbidden());
    }

    @Test
    public void getAll_isOk_quandoUsuarioPossuirPermissaoSobreOAgenteAutorizado() throws Exception {
        mvc.perform(get(URL_API_SOLICITACAO_RAMAL + "/?agenteAutorizadoId=1")
                .header("Authorization", getAccessToken(mvc, SOCIO_AA))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }

    @Test
    public void getAll_listaComDoisRegistros_quandoLocalizarAsSolicitacoesComSituacaoPendentePeloAaId()
        throws Exception {
        mvc.perform(get(URL_API_SOLICITACAO_RAMAL + "/?situacao=PENDENTE&agenteAutorizadoId=1")
                .header("Authorization", getAccessToken(mvc, SOCIO_AA))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content", hasSize(2)));
    }

    @Test
    public void getAll_listaComCincoRegistros_quandoLocalizarAsSolicitacoesComSituacaoEmAndamentoPeloAaId()
        throws Exception {
        mvc.perform(get(URL_API_SOLICITACAO_RAMAL + "/?situacao=EM_ANDAMENTO&agenteAutorizadoId=2")
                .header("Authorization", getAccessToken(mvc, SOCIO_AA))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content", hasSize(5)));
    }

    @Test
    public void getAll_listaComDoisRegistros_quandoLocalizarAsSolicitacoesComSituacaoRejeitada() throws Exception {
        mvc.perform(get(URL_API_SOLICITACAO_RAMAL + "/?situacao=REJEITADO&agenteAutorizadoId=2")
                .header("Authorization", getAccessToken(mvc, SOCIO_AA))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content", hasSize(2)));
    }

    @Test
    public void getAll_listaComUmRegistro_quandoLocalizarAsSolicitacoesPelaDataCadastroESituacaoPendenteEAaId()
        throws Exception {
        mvc.perform(get(URL_API_SOLICITACAO_RAMAL
                + "/?dataInicialSolicitacao=03/01/2019&dataFinalSolicitacao=04/01/"
                + "2019&situacao=PENDENTE&agenteAutorizadoId=1")
                .header("Authorization", getAccessToken(mvc, SOCIO_AA))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content", hasSize(1)));
    }

    @Test
    public void getAll_listaComQuatroRegistros_quandoLocalizarAsSolicitacoesPelaDataCadastroESituacaoEmAndamentoEAaId()
        throws Exception {
        mvc.perform(get(URL_API_SOLICITACAO_RAMAL
                + "/?dataInicialSolicitacao=02/01/2019&dataFinalSolicitacao=03/01/2019&situacao=EM_ANDAMENT"
                + "O&agenteAutorizadoId=2")
                .header("Authorization", getAccessToken(mvc, SOCIO_AA))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content", hasSize(4)));
    }

    @Test
    public void getAll_listaComQuatroRegistros_quandoLocalizarAsSolicitacoesPelaDataCadastroEAaId()
        throws Exception {
        mvc.perform(get(URL_API_SOLICITACAO_RAMAL
                + "/?dataInicialSolicitacao=02/01/2019&dataFinalSolicitacao=02/01/2019&agenteAutorizadoId=2")
                .header("Authorization", getAccessToken(mvc, SOCIO_AA))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content", hasSize(4)));
    }

    @Test
    public void getAll_solicitacaoRamalLocalizada_quandoLocalizarPeloIdSolicitacao() throws Exception {
        mvc.perform(get(URL_API_SOLICITACAO_RAMAL + "/solicitacao/1")
                .header("Authorization", getAccessToken(mvc, SOCIO_AA))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.quantidadeRamais", is(35)))
            .andExpect(jsonPath("$.situacao", is("PENDENTE")))
            .andExpect(jsonPath("$.id", is(1)))
            .andExpect(jsonPath("$.agenteAutorizadoId", is(1)))
            .andExpect(jsonPath("$.agenteAutorizadoNome", is("JoãoAA")))
            .andExpect(jsonPath("$.agenteAutorizadoCnpj", is("25.280.843/0001-10")))
            .andExpect(jsonPath("$.telefoneTi", is("(43) 3322-44444")))
            .andExpect(jsonPath("$.emailTi", is("joaoaa@hotmail.com")))
            .andExpect(jsonPath("$.dataCadastro", is("01/01/2019 10:30")));
    }

    @Test
    public void getAll_notFound_quandoIdSolicitacaoNaoExistir() throws Exception {
        mvc.perform(get(URL_API_SOLICITACAO_RAMAL + "/solicitacao/9999")
                .header("Authorization", getAccessToken(mvc, SOCIO_AA))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }

    @Test
    public void getAll_listaComDoisRegistros_quandoLocalizarTodosOsHistoricosPeloSolicitacaoId() throws Exception {
        mvc.perform(get(URL_API_SOLICITACAO_RAMAL + "/historico/1")
                .header("Authorization", getAccessToken(mvc, SOCIO_AA))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    public void getAllTipoImplantacao_deveRetornarTipoImplantacao_seEnumPossuirValores() throws Exception {
        mvc.perform(get(URL_API_SOLICITACAO_RAMAL + "/tipo-implantacao")
                .header("Authorization", getAccessToken(mvc, SOCIO_AA))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$.[0].codigo", is("ESCRITORIO")))
            .andExpect(jsonPath("$.[0].descricao", is("ESCRITÓRIO")))
            .andExpect(jsonPath("$.[1].codigo", is("HOME_OFFICE")))
            .andExpect(jsonPath("$.[1].descricao", is("HOME OFFICE")));
    }

    @Test
    public void calcularDataFinalizacao_deveRetornarIsOk_seTudoCerto() throws Exception {
        mvc.perform(put(URL_API_SOLICITACAO_RAMAL + "/calcular-data-finalizacao")
                .param("dataInicialSolicitacao", "20/01/2022")
                .param("dataFinalSolicitacao", "21/01/2022")
                .header("Authorization", getAccessToken(mvc, ADMIN)))
            .andExpect(status().isOk());
    }

    @Test
    public void calcularDataFinalizacao_deveRetornarIsOk_mesmoSemReceberDatas() throws Exception {
        mvc.perform(put(URL_API_SOLICITACAO_RAMAL + "/calcular-data-finalizacao")
                .header("Authorization", getAccessToken(mvc, ADMIN)))
            .andExpect(status().isOk());
    }

    @Test
    public void calcularDataFinalizacao_deveRetornarNaoAutorizado_seNaoHouverUsuarioAutenticado() throws Exception {
        mvc.perform(put(URL_API_SOLICITACAO_RAMAL + "/calcular-data-finalizacao")
                .param("dataInicialSolicitacao", "20/01/2022")
                .param("dataFinalSolicitacao", "21/01/2022"))
            .andExpect(status().isUnauthorized());
    }

    private SolicitacaoRamalRequest criaSolicitacaoRamal(Integer id, Integer aaId) {
        return SolicitacaoRamalRequest.builder()
            .id(id)
            .quantidadeRamais(38)
            .canal(ECanal.AGENTE_AUTORIZADO)
            .agenteAutorizadoId(aaId)
            .melhorHorarioImplantacao(LocalTime.of(10, 00))
            .melhorDataImplantacao(LocalDate.of(2019, 01, 25))
            .tipoImplantacao(ETipoImplantacao.ESCRITORIO.getCodigo())
            .emailTi("reanto@ti.com.br")
            .telefoneTi("(18) 3322-2388")
            .usuariosSolicitadosIds(Arrays.asList(100, 101))
            .build();
    }

    private AgenteAutorizadoResponse criaAa() {
        return AgenteAutorizadoResponse.builder()
            .id("303030")
            .cnpj("81733187000134")
            .nomeFantasia("Fulano")
            .discadoraId(1)
            .razaoSocial("RAZAO SOCIAL AA")
            .build();
    }

    private SolicitacaoRamalAtualizarStatusRequest criaSolicitacaoRamalAtualizarStatusRequest() {
        return SolicitacaoRamalAtualizarStatusRequest.builder()
            .idSolicitacao(1)
            .observacao("Rejeitada teste")
            .situacao(REJEITADO)
            .build();
    }

    private TelefoniaResponse criaTelefonia() {
        return TelefoniaResponse.builder()
            .id(13)
            .nome("DISCADORA UN")
            .build();
    }

    private List<RamalResponse> criaListaRamal() {
        return Arrays.asList(new RamalResponse(), new RamalResponse());
    }

    private SocioResponse criaSocio() {
        return SocioResponse.builder()
            .cpf("33333333333")
            .nome("FULANO")
            .build();
    }

    private List<UsuarioAgenteAutorizadoResponse> criaListaUsuariosAtivos() {
        return Arrays.asList(new UsuarioAgenteAutorizadoResponse(), new UsuarioAgenteAutorizadoResponse());
    }

    private SolicitacaoRamalAtualizarStatusRequest criaSolicitacaoRamalRequest() {
        return SolicitacaoRamalAtualizarStatusRequest.builder()
            .idSolicitacao(4)
            .situacao(ESituacaoSolicitacao.ENVIADO)
            .build();
    }
}
