package br.com.xbrain.autenticacao.modules.solicitacaoramal;

import br.com.xbrain.autenticacao.modules.equipevendas.service.EquipeVendasService;
import br.com.xbrain.autenticacao.modules.parceirosonline.service.AgenteAutorizadoService;
import br.com.xbrain.autenticacao.modules.solicitacaoramal.dto.SolicitacaoRamalRequest;
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
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;

import static helpers.TestsHelper.convertObjectToJsonBytes;
import static helpers.TestsHelper.getAccessToken;
import static helpers.Usuarios.SOCIO_AA;
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

    @Before
    public void setUp() {
        when(agenteAutorizadoService.getAgentesAutorizadosPermitidos(any())).thenReturn(Arrays.asList(1,2));
        when(equipeVendasService.getEquipesPorSupervisor(anyInt())).thenReturn(Collections.emptyList());
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
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    public void deveAtualizarUmaSolicitacaoDeRamal() throws Exception {
        mvc.perform(put(URL_API_SOLICITACAO_RAMAL)
                .header("Authorization", getAccessToken(mvc, SOCIO_AA))
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(criaSolicitacaoRamal(1))))
                .andExpect(status().isOk());
    }

    @Test
    public void deveCriarUmaSolicitacaoDeRamal() throws Exception {
        SolicitacaoRamalRequest request = criaSolicitacaoRamal();

        mvc.perform(post(URL_API_SOLICITACAO_RAMAL)
                .header("Authorization", getAccessToken(mvc, SOCIO_AA))
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.quantidadeRamais", is(request.getQuantidadeRamais())))
                .andExpect(jsonPath("$.situacao", is("PD")));

        verify(historicoService, times(1)).save(any());
    }

    @Test
    public void deveNaoCriarUmaSolicitacaoDeRamal() throws Exception {
        mvc.perform(post(URL_API_SOLICITACAO_RAMAL)
                .header("Authorization", getAccessToken(mvc, SOCIO_AA))
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(new SolicitacaoRamalRequest())))
                .andExpect(jsonPath("$[*].message", containsInAnyOrder("O campo usuarioId é obrigatório.",
                        "O campo agenteAutorizadoId é obrigatório.",
                        "O campo agenteAutorizadoNome é obrigatório.",
                        "O campo melhorHorarioImplantacao é obrigatório.",
                        "O campo quantidadeRamais é obrigatório.",
                        "O campo melhorDataImplantacao é obrigatório.")));
    }

    /*@Test
    public void deveFalharQuandoUsuarioNaoForSocio() throws Exception {
        mvc.perform(get(URL_API_SOLICITACAO_RAMAL + "/1")
            .header("Authorization", getAccessToken(mvc, ADMIN))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized());
    }*/

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

    private SolicitacaoRamalRequest criaSolicitacaoRamal() {
        return criaSolicitacaoRamal(null);
    }

    private SolicitacaoRamalRequest criaSolicitacaoRamal(Integer id) {
        SolicitacaoRamalRequest request = new SolicitacaoRamalRequest();
        request.setId(id);
        request.setQuantidadeRamais(38);
        request.setUsuarioId(100);
        request.setAgenteAutorizadoId(1);
        request.setAgenteAutorizadoNome("Renato");
        request.setMelhorHorarioImplantacao(LocalTime.of(10, 00, 00));
        request.setMelhorDataImplantacao(LocalDate.of(2019, 01, 25));
        request.setDataCadastro(LocalDateTime.now());

        return request;
    }

    private String dateFormatter(LocalDate data) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return formatter.format(data);
    }

}
