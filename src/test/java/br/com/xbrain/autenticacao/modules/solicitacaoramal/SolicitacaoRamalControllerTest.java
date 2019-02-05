package br.com.xbrain.autenticacao.modules.solicitacaoramal;

import br.com.xbrain.autenticacao.modules.email.service.EmailService;
import br.com.xbrain.autenticacao.modules.equipevendas.service.EquipeVendasService;
import br.com.xbrain.autenticacao.modules.parceirosonline.dto.AgenteAutorizadoResponse;
import br.com.xbrain.autenticacao.modules.parceirosonline.service.AgenteAutorizadoService;
import br.com.xbrain.autenticacao.modules.solicitacaoramal.dto.SolicitacaoRamalAtualizarStatusRequest;
import br.com.xbrain.autenticacao.modules.solicitacaoramal.dto.SolicitacaoRamalRequest;
import br.com.xbrain.autenticacao.modules.solicitacaoramal.enums.ESituacaoSolicitacao;
import br.com.xbrain.autenticacao.modules.solicitacaoramal.model.SolicitacaoRamal;
import br.com.xbrain.autenticacao.modules.solicitacaoramal.service.SolicitacaoRamalHistoricoService;
import br.com.xbrain.autenticacao.modules.solicitacaoramal.service.SolicitacaoRamalService;
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

    @Autowired
    private SolicitacaoRamalService solicitacaoRamalService;
    @Autowired
    private MockMvc mvc;
    @MockBean
    private SolicitacaoRamalHistoricoService historicoService;
    @MockBean
    private AgenteAutorizadoService agenteAutorizadoService;
    @MockBean
    private EquipeVendasService equipeVendasService;
    @MockBean
    private EmailService emailService;

    private static final String URL_API_SOLICITACAO_RAMAL = "/api/solicitacao-ramal";
    private static final String URL_API_SOLICITACAO_RAMAL_GERENCIAL = "/api/solicitacao-ramal/gerencia";

    @Before
    public void setUp() {
        when(agenteAutorizadoService.getAgentesAutorizadosPermitidos(any())).thenReturn(Arrays.asList(1,2));
        when(equipeVendasService.getEquipesPorSupervisor(anyInt())).thenReturn(Collections.emptyList());
        when(agenteAutorizadoService.getAaById(anyInt())).thenReturn(criaAa());
    }

    @Test
    public void getAll_listaComQuatroRegistro_quandoHouverSolicitacoesPendenteOuEmAndamento() {
        List<SolicitacaoRamal> resultList =
                solicitacaoRamalService.getAllSolicitacoesPendenteOuEmAndamentoComEmailExpiracaoFalse();
        Assert.assertEquals(4, resultList.size());
    }

    @Test
    public void enviadorDeEmail_verificaSeMetodoEnviarEmailFoiInvocadoQuatroVezes_quandoHouverEmailParaEnviar() {
        solicitacaoRamalService.enviadorDeEmailParaSolicitacoesQueVaoExpirar();

        verify(emailService, times(4)).enviarEmailTemplate(anyList(), any(), any(), any());
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
    public void getAll_isForbiden_seUsuarioPossuirPermissaoOperacaoGerenteComercialETentarAcessarGerencial() throws Exception {
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
    public void getAll_isUnauthorized_quandoUsuarioNaoExisteAutenticado() throws Exception {
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
                .content(convertObjectToJsonBytes(criaSolicitacaoRamal(5))))
                .andExpect(status().isOk());
    }

    @Test
    public void save_isCreated_quandoTentarSalvarUmaNovaSolicitacao() throws Exception {
        SolicitacaoRamalRequest request = criaSolicitacaoRamal(null);

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
                .header("Authorization", getAccessToken(mvc, SOCIO_AA))
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(new SolicitacaoRamalRequest())))
                .andExpect(jsonPath("$[*].message", containsInAnyOrder("O campo agenteAutorizadoId é obrigatório.",
                        "O campo melhorHorarioImplantacao é obrigatório.",
                        "O campo quantidadeRamais é obrigatório.",
                        "O campo melhorDataImplantacao é obrigatório.",
                        "O campo telefoneTi é obrigatório.",
                        "O campo emailTi é obrigatório.",
                        "O campo usuariosSolicitadosIds é obrigatório.")));
    }

    @Test
    public void getAll_isBadRequest_quandoNaoEnviarAaIdEUsuarioNaoPossuirRoleGerenciarRamais() throws Exception {
        mvc.perform(get(URL_API_SOLICITACAO_RAMAL)
                .header("Authorization", getAccessToken(mvc, SOCIO_AA))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[*].message", containsInAnyOrder(
                        "É necessário enviar o parâmetro agente autorizado id.")));
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
                + "/?dataInicialSolicitacao=03/01/2019&dataFinalSolicitacao=04/01/2019&situacao=PENDENTE&agenteAutorizadoId=1")
                .header("Authorization", getAccessToken(mvc, SOCIO_AA))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)));
    }

    @Test
    public void getAll_listaComQuatroRegistros_quandoLocalizarAsSolicitacoesPelaDataCadastroESituacaoEmAndamentoEAaId()
            throws Exception {
        mvc.perform(get(URL_API_SOLICITACAO_RAMAL
        + "/?dataInicialSolicitacao=02/01/2019&dataFinalSolicitacao=03/01/2019&situacao=EM_ANDAMENTO&agenteAutorizadoId=2")
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
                .andExpect(jsonPath("$.id", is(1)));
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

    private SolicitacaoRamalRequest criaSolicitacaoRamal(Integer id) {
        SolicitacaoRamalRequest request = SolicitacaoRamalRequest.builder()
                .id(id)
                .quantidadeRamais(38)
                .agenteAutorizadoId(7129)
                .melhorHorarioImplantacao(LocalTime.of(10, 00))
                .melhorDataImplantacao(LocalDate.of(2019, 01, 25))
                .emailTi("reanto@ti.com.br")
                .telefoneTi("(18) 3322-2388")
                .usuariosSolicitadosIds(Arrays.asList(100,101))
                .build();

        return request;
    }

    private AgenteAutorizadoResponse criaAa() {
        AgenteAutorizadoResponse agenteAutorizadoResponse = new AgenteAutorizadoResponse();
        agenteAutorizadoResponse.setId("303030");
        agenteAutorizadoResponse.setCnpj("81733187000134");
        agenteAutorizadoResponse.setNomeFantasia("Fulano");

        return agenteAutorizadoResponse;
    }

    private SolicitacaoRamalAtualizarStatusRequest criaSolicitacaoRamalAtualizarStatusRequest() {
        return SolicitacaoRamalAtualizarStatusRequest.builder()
                .idSolicitacao(1)
                .observacao("Rejeitada teste")
                .situacao(ESituacaoSolicitacao.REJEITADO)
                .build();
    }
}
