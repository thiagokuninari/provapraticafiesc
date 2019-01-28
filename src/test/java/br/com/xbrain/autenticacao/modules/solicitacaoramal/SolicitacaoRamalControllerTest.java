package br.com.xbrain.autenticacao.modules.solicitacaoramal;

import br.com.xbrain.autenticacao.modules.equipevendas.service.EquipeVendasService;
import br.com.xbrain.autenticacao.modules.parceirosonline.dto.AgenteAutorizadoResponse;
import br.com.xbrain.autenticacao.modules.parceirosonline.service.AgenteAutorizadoService;
import br.com.xbrain.autenticacao.modules.solicitacaoramal.dto.SolicitacaoRamalRequest;
import br.com.xbrain.autenticacao.modules.solicitacaoramal.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.solicitacaoramal.service.SolicitacaoRamalHistoricoService;
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
    private MockMvc mvc;
    @MockBean
    private SolicitacaoRamalHistoricoService historicoService;
    @MockBean
    private AgenteAutorizadoService agenteAutorizadoService;
    @MockBean
    private EquipeVendasService equipeVendasService;

    private static final String URL_API_SOLICITACAO_RAMAL = "/api/solicitacao-ramal";
    private static final String URL_API_SOLICITACAO_RAMAL_GERENCIAL = "/api/solicitacao-ramal/gerencia";

    @Before
    public void setUp() {
        when(agenteAutorizadoService.getAgentesAutorizadosPermitidos(any())).thenReturn(Arrays.asList(1,2));
        when(equipeVendasService.getEquipesPorSupervisor(anyInt())).thenReturn(Collections.emptyList());
        when(agenteAutorizadoService.getAaById(anyInt())).thenReturn(criaAa());
    }

    @Test
    public void devePermitirAcessoParaPermissaoHelpDesk() throws Exception {
        mvc.perform(get(URL_API_SOLICITACAO_RAMAL_GERENCIAL)
                .header("Authorization", getAccessToken(mvc, HELP_DESK))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(10)));
    }

    @Test
    public void devePermitirAcessoParaPermissaoAdmin() throws Exception {
        mvc.perform(get(URL_API_SOLICITACAO_RAMAL_GERENCIAL)
                .header("Authorization", getAccessToken(mvc, ADMIN))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(10)));
    }

    @Test
    public void devePermitirAcessoParaPermissaoSocioAa() throws Exception {
        mvc.perform(get(URL_API_SOLICITACAO_RAMAL)
                .header("Authorization", getAccessToken(mvc, SOCIO_AA))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(6)));
    }

    @Test
    public void deveFalharQuandoTentarAcessarUrlGerenciaComPermissaoGerente() throws Exception {
        mvc.perform(get(URL_API_SOLICITACAO_RAMAL_GERENCIAL)
                .header("Authorization", getAccessToken(mvc, OPERACAO_GERENTE_COMERCIAL))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    public void deveFalharQuandoTentarAcessarUrlGerenciaComPermissaoSocioAa() throws Exception {
        mvc.perform(get(URL_API_SOLICITACAO_RAMAL_GERENCIAL)
                .header("Authorization", getAccessToken(mvc, SOCIO_AA))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    public void deveSolicitarAutorizacao() throws Exception {
        mvc.perform(get(URL_API_SOLICITACAO_RAMAL).accept(MediaType.APPLICATION_JSON)).andExpect(status().isUnauthorized());
    }

    @Test
    public void deveRetornarTodasSolicitacoesDoUsuarioLogado() throws Exception {
        mvc.perform(get(URL_API_SOLICITACAO_RAMAL)
                .header("Authorization", getAccessToken(mvc, SOCIO_AA))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(6)));
    }

    @Test
    public void deveAtualizarUmaSolicitacaoDeRamal() throws Exception {
        mvc.perform(put(URL_API_SOLICITACAO_RAMAL)
                .header("Authorization", getAccessToken(mvc, SOCIO_AA))
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(criaSolicitacaoRamal(5))))
                .andExpect(status().isOk());
    }

    @Test
    public void deveCriarUmaSolicitacaoDeRamal() throws Exception {
        SolicitacaoRamalRequest request = criaSolicitacaoRamal(null);

        mvc.perform(post(URL_API_SOLICITACAO_RAMAL)
                .header("Authorization", getAccessToken(mvc, SOCIO_AA))
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(request)))
                .andExpect(status().isCreated());

        verify(historicoService, times(1)).save(any());
    }

    @Test
    public void deveNaoCriarUmaSolicitacaoDeRamal() throws Exception {
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
    public void deveFalharQuandoUsuarioForSocioMasNaoTemPermissaoSobreOAgenteAutorizado() throws Exception {
        mvc.perform(get(URL_API_SOLICITACAO_RAMAL + "/50")
                .header("Authorization", getAccessToken(mvc, SOCIO_AA))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    public void deveValidarQuandoUsuarioPossuirPermissaoSobreOAgenteAutorizado() throws Exception {
        mvc.perform(get(URL_API_SOLICITACAO_RAMAL + "/1")
            .header("Authorization", getAccessToken(mvc, SOCIO_AA))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }

    @Test
    public void deveValidarQuandoUsuarioPossuirPermissaoSobreOAgenteAutorizadoTeste() throws Exception {

        mvc.perform(get(URL_API_SOLICITACAO_RAMAL + "/2")
                .header("Authorization", getAccessToken(mvc, SOCIO_AA))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void deveRetornarAsSolicitacoesComSituacaoPendente() throws Exception {
        mvc.perform(get(URL_API_SOLICITACAO_RAMAL + "/?situacao=PD")
                .header("Authorization", getAccessToken(mvc, SOCIO_AA))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)));
    }

    @Test
    public void deveRetornarAsSolicitacoesComSituacaoEmAndamento() throws Exception {
        mvc.perform(get(URL_API_SOLICITACAO_RAMAL + "/?situacao=EA")
                .header("Authorization", getAccessToken(mvc, SOCIO_AA))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(3)));
    }

    @Test
    public void deveRetornarAsSolicitacoesComSituacaoRejeitada() throws Exception {
        mvc.perform(get(URL_API_SOLICITACAO_RAMAL + "/?situacao=RJ")
                .header("Authorization", getAccessToken(mvc, SOCIO_AA))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)));
    }

    @Test
    public void deveRetornarAsSolicitacoesPeloFiltroDataCadastroESituacaoPendente() throws Exception {
        mvc.perform(get(URL_API_SOLICITACAO_RAMAL + "/?data=2019-01-03&situacao=PD")
                .header("Authorization", getAccessToken(mvc, SOCIO_AA))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)));
    }

    @Test
    public void deveRetornarAsSolicitacoesPeloFiltroDataCadastroESituacaoEmAndamento() throws Exception {
        mvc.perform(get(URL_API_SOLICITACAO_RAMAL + "/?data=2019-01-02&situacao=EA")
                .header("Authorization", getAccessToken(mvc, SOCIO_AA))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(3)));
    }

    @Test
    public void deveRetornarAsSolicitacoesPeloFiltroDataCadastro() throws Exception {
        mvc.perform(get(URL_API_SOLICITACAO_RAMAL + "/?data=2019-01-02")
                .header("Authorization", getAccessToken(mvc, SOCIO_AA))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(3)));
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
}
