package br.com.xbrain.autenticacao.modules.horarioacesso.controller;

import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.comum.dto.PageRequest;
import br.com.xbrain.autenticacao.modules.comum.dto.SelectResponse;
import br.com.xbrain.autenticacao.modules.comum.util.DataHoraAtual;
import br.com.xbrain.autenticacao.modules.horarioacesso.dto.HorarioAcessoFiltros;
import br.com.xbrain.autenticacao.modules.horarioacesso.dto.HorarioAcessoResponse;
import br.com.xbrain.autenticacao.modules.horarioacesso.enums.EDiaSemana;
import br.com.xbrain.autenticacao.modules.horarioacesso.model.HorarioAtuacao;
import br.com.xbrain.autenticacao.modules.horarioacesso.repository.HorarioAcessoRepository;
import br.com.xbrain.autenticacao.modules.horarioacesso.repository.HorarioAtuacaoRepository;
import br.com.xbrain.autenticacao.modules.horarioacesso.service.HorarioAcessoService;
import br.com.xbrain.autenticacao.modules.site.service.SiteService;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static br.com.xbrain.autenticacao.modules.horarioacesso.helper.HorarioHelper.*;
import static helpers.TestsHelper.*;
import static helpers.Usuarios.ADMIN;
import static helpers.Usuarios.OPERACAO_GERENTE_COMERCIAL;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Sql(scripts = "classpath:/tests_database.sql")
public class HorarioAcessoControllerTest {

    private static final String URL = "/api/horarios-acesso";

    @Autowired
    private MockMvc mvc;
    @MockBean
    private HorarioAcessoService service;
    @MockBean
    private HorarioAcessoRepository repository;
    @MockBean
    private HorarioAtuacaoRepository atuacaoRepository;
    @MockBean
    private AutenticacaoService autenticacaoService;
    @MockBean
    private SiteService siteService;
    @MockBean
    private DataHoraAtual dataHoraAtual;

    @Test
    public void getHorariosAcesso_unauthorized_quandoNaoPassarToken() throws Exception {
        mvc.perform(get(URL)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized());
    }

    @Test
    public void getHorariosAcesso_forbidden_quandoNaoTiverPermissao() throws Exception {
        mvc.perform(get(URL)
            .header("Authorization", getAccessToken(mvc, OPERACAO_GERENTE_COMERCIAL))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isForbidden());
    }

    @Test
    public void getHorariosAcesso_ok_quandoTiverPermissao() throws Exception {
        when(autenticacaoService.getUsuarioAutenticado()).thenReturn(umAdmin());
        mvc.perform(get(URL)
            .header("Authorization", getAccessToken(mvc, ADMIN))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }

    @Test
    public void getHorarioAcesso_deveRetornarHorarioAcessoResponse_quandoTiverPermissao() throws Exception {
        when(service.getHorarioAcesso(anyInt())).thenReturn(umHorarioAcessoResponse());

        mvc.perform(get(URL + "/1")
            .header("Authorization", getAccessToken(mvc, ADMIN))
            .contentType(MediaType.APPLICATION_JSON)
            .content(convertObjectToJsonBytes(umHorarioAcessoResponse())))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.horarioAcessoId", is(1)))
            .andExpect(jsonPath("$.siteId", is(100)))
            .andExpect(jsonPath("$.siteNome", is("SITE TESTE")));
    }

    @Test
    public void getHorarios_deveRetornarListaHorarioAcessoResponse_quandoTiverPermissao() throws Exception {
        when(service.getHorariosAcesso(any(PageRequest.class), any(HorarioAcessoFiltros.class)))
            .thenReturn(umaListaHorarioAcessoResponse());

        mvc.perform(get(URL)
            .header("Authorization", getAccessToken(mvc, ADMIN))
            .contentType(MediaType.APPLICATION_JSON)
            .content(convertObjectToJsonBytes(umaListaHorarioAcessoResponse())))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content[0].horarioAcessoId", is(1)))
            .andExpect(jsonPath("$.content[0].siteId", is(100)))
            .andExpect(jsonPath("$.content[0].siteNome", is("SITE TESTE")))
            .andExpect(jsonPath("$.content[0].dataAlteracao", is("22/11/2021 13:53:10")))
            .andExpect(jsonPath("$.content[0].usuarioAlteracaoNome", is("USUARIO TESTE")))
            .andExpect(jsonPath("$.content[0].horariosAtuacao[0].id", is(1)))
            .andExpect(jsonPath("$.content[0].horariosAtuacao[0].diaSemana", is("Segunda-Feira")))
            .andExpect(jsonPath("$.content[0].horariosAtuacao[0].horarioInicio", is("09:00")))
            .andExpect(jsonPath("$.content[0].horariosAtuacao[0].horarioFim", is("15:00")))
            .andExpect(jsonPath("$.content[0].horariosAtuacao[1].id", is(2)))
            .andExpect(jsonPath("$.content[0].horariosAtuacao[1].diaSemana", is("Quarta-Feira")))
            .andExpect(jsonPath("$.content[0].horariosAtuacao[1].horarioInicio", is("09:00")))
            .andExpect(jsonPath("$.content[0].horariosAtuacao[1].horarioFim", is("15:00")))
            .andExpect(jsonPath("$.content[0].horariosAtuacao[2].id", is(3)))
            .andExpect(jsonPath("$.content[0].horariosAtuacao[2].diaSemana", is("Sexta-Feira")))
            .andExpect(jsonPath("$.content[0].horariosAtuacao[2].horarioInicio", is("09:00")))
            .andExpect(jsonPath("$.content[0].horariosAtuacao[2].horarioFim", is("15:00")));
    }

    @Test
    public void getHistoricos_deveRetornarListaHorarioAcessoResponse_quandoTiverPermissao() throws Exception {
        when(service.getHistoricos(any(PageRequest.class), anyInt())).thenReturn(umaListaHorarioHistoricoResponse());

        mvc.perform(get(URL + "/1/historico")
            .header("Authorization", getAccessToken(mvc, ADMIN))
            .contentType(MediaType.APPLICATION_JSON)
            .content(convertObjectToJsonBytes(umaListaHorarioHistoricoResponse())))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content[0].horarioAcessoId", is(1)))
            .andExpect(jsonPath("$.content[0].horarioHistoricoId", is(1)))
            .andExpect(jsonPath("$.content[0].siteId", is(100)))
            .andExpect(jsonPath("$.content[0].siteNome", is("SITE TESTE")))
            .andExpect(jsonPath("$.content[0].dataAlteracao", is("22/11/2021 13:53:10")))
            .andExpect(jsonPath("$.content[0].usuarioAlteracaoNome", is("USUARIO TESTE")))
            .andExpect(jsonPath("$.content[0].horariosAtuacao[0].id", is(1)))
            .andExpect(jsonPath("$.content[0].horariosAtuacao[0].diaSemana", is("Segunda-Feira")))
            .andExpect(jsonPath("$.content[0].horariosAtuacao[0].horarioInicio", is("09:00")))
            .andExpect(jsonPath("$.content[0].horariosAtuacao[0].horarioFim", is("15:00")))
            .andExpect(jsonPath("$.content[0].horariosAtuacao[1].id", is(2)))
            .andExpect(jsonPath("$.content[0].horariosAtuacao[1].diaSemana", is("Quarta-Feira")))
            .andExpect(jsonPath("$.content[0].horariosAtuacao[1].horarioInicio", is("09:00")))
            .andExpect(jsonPath("$.content[0].horariosAtuacao[1].horarioFim", is("15:00")))
            .andExpect(jsonPath("$.content[0].horariosAtuacao[2].id", is(3)))
            .andExpect(jsonPath("$.content[0].horariosAtuacao[2].diaSemana", is("Sexta-Feira")))
            .andExpect(jsonPath("$.content[0].horariosAtuacao[2].horarioInicio", is("09:00")))
            .andExpect(jsonPath("$.content[0].horariosAtuacao[2].horarioFim", is("15:00")));
    }

    @Test
    public void getStatus_deveRetornarTrue_seHorarioAtualEstiverDentroDoPermitido() throws Exception {
        when(autenticacaoService.getUsuarioAutenticado()).thenReturn(umOperadorTelevendas());
        when(siteService.getSitesPorPermissao(any(Usuario.class)))
            .thenReturn(List.of(SelectResponse.of(100, "SITE TEST")));
        when(siteService.findById(anyInt())).thenReturn(umSite());
        when(repository.findBySiteId(anyInt())).thenReturn(Optional.of(umHorarioAcesso()));
        when(dataHoraAtual.getDataHora()).thenReturn(LocalDateTime.of(2021, 12, 13, 10, 0, 0));
        var horarioAtuacao = HorarioAtuacao.builder()
            .diaSemana(EDiaSemana.SEGUNDA)
            .horarioInicio(LocalTime.of(9,0))
            .horarioFim(LocalTime.of(18,0))
            .build();
        when(atuacaoRepository.findByHorarioAcessoId(anyInt())).thenReturn(List.of(horarioAtuacao));
        when(service.getStatus()).thenReturn(true);

        mvc.perform(get(URL + "/status")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", is(true)));
    }

    @Test
    public void getStatus_deveRetornarFalse_seHorarioAtualNaoEstiverDentroDoPermitido() throws Exception {
        when(autenticacaoService.getUsuarioAutenticado()).thenReturn(umOperadorTelevendas());
        when(siteService.getSitesPorPermissao(any(Usuario.class)))
            .thenReturn(List.of(SelectResponse.of(100, "SITE TEST")));
        when(siteService.findById(anyInt())).thenReturn(umSite());
        when(repository.findBySiteId(anyInt())).thenReturn(Optional.of(umHorarioAcesso()));
        when(dataHoraAtual.getDataHora()).thenReturn(LocalDateTime.of(2021, 12, 13, 10, 0, 0));
        var horarioAtuacao = HorarioAtuacao.builder()
            .diaSemana(EDiaSemana.SEGUNDA)
            .horarioInicio(LocalTime.of(9,0))
            .horarioFim(LocalTime.of(9,1))
            .build();
        when(atuacaoRepository.findByHorarioAcessoId(anyInt())).thenReturn(List.of(horarioAtuacao));
        when(service.getStatus()).thenReturn(false);

        mvc.perform(get(URL + "/status")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", is(false)));
    }

    @Test
    public void getStatus_deveRetornarFalse_seHorarioAtualNaoSeEncaixarEmNenhumDia() throws Exception {
        when(autenticacaoService.getUsuarioAutenticado()).thenReturn(umOperadorTelevendas());
        when(siteService.getSitesPorPermissao(any(Usuario.class)))
            .thenReturn(List.of(SelectResponse.of(100, "SITE TEST")));
        when(siteService.findById(anyInt())).thenReturn(umSite());
        when(repository.findBySiteId(anyInt())).thenReturn(Optional.of(umHorarioAcesso()));
        when(dataHoraAtual.getDataHora()).thenReturn(LocalDateTime.of(2021, 12, 13, 10, 0, 0));
        var horarioAtuacao = HorarioAtuacao.builder()
            .diaSemana(EDiaSemana.DOMINGO)
            .build();
        when(atuacaoRepository.findByHorarioAcessoId(anyInt())).thenReturn(List.of(horarioAtuacao));
        when(service.getStatus()).thenReturn(false);

        mvc.perform(get(URL + "/status")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", is(false)));
    }

    @Test
    public void getStatusComParametroSiteId_deveRetornarTrue_seHorarioAtualEstiverDentroDoPermitido() throws Exception {
        when(repository.findBySiteId(anyInt())).thenReturn(Optional.of(umHorarioAcesso()));
        when(dataHoraAtual.getDataHora()).thenReturn(LocalDateTime.of(2021, 12, 13, 10, 0, 0));
        var horarioAtuacao = HorarioAtuacao.builder()
            .diaSemana(EDiaSemana.SEGUNDA)
            .horarioInicio(LocalTime.of(9,0))
            .horarioFim(LocalTime.of(18,0))
            .build();
        when(atuacaoRepository.findByHorarioAcessoId(anyInt())).thenReturn(List.of(horarioAtuacao));
        when(service.getStatus(anyInt())).thenReturn(true);

        mvc.perform(get(URL + "/status/100")
            .header("Authorization", getAccessToken(mvc, ADMIN))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", is(true)));
    }

    @Test
    public void getStatusComParametroSiteId_deveRetornarFalse_seHorarioAtualNaoEstiverDentroDoPermitido() throws Exception {
        when(repository.findBySiteId(anyInt())).thenReturn(Optional.of(umHorarioAcesso()));
        when(dataHoraAtual.getDataHora()).thenReturn(LocalDateTime.of(2021, 12, 13, 10, 0, 0));
        var horarioAtuacao = HorarioAtuacao.builder()
            .diaSemana(EDiaSemana.SEGUNDA)
            .horarioInicio(LocalTime.of(9,0))
            .horarioFim(LocalTime.of(9,1))
            .build();
        when(atuacaoRepository.findByHorarioAcessoId(anyInt())).thenReturn(List.of(horarioAtuacao));
        when(service.getStatus(anyInt())).thenReturn(false);

        mvc.perform(get(URL + "/status/100")
            .header("Authorization", getAccessToken(mvc, ADMIN))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", is(false)));
    }

    @Test
    public void getStatusComParametroSiteId_deveRetornarFalse_seHorarioAtualNaoSeEncaixarEmNenhumDia() throws Exception {
        when(repository.findBySiteId(anyInt())).thenReturn(Optional.of(umHorarioAcesso()));
        when(dataHoraAtual.getDataHora()).thenReturn(LocalDateTime.of(2021, 12, 13, 10, 0, 0));
        var horarioAtuacao = HorarioAtuacao.builder()
            .diaSemana(EDiaSemana.DOMINGO)
            .build();
        when(atuacaoRepository.findByHorarioAcessoId(anyInt())).thenReturn(List.of(horarioAtuacao));
        when(service.getStatus(anyInt())).thenReturn(false);

        mvc.perform(get(URL + "/status/100")
            .header("Authorization", getAccessToken(mvc, ADMIN))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", is(false)));
    }

    private Page<HorarioAcessoResponse> umaListaHorarioAcessoResponse() {
        return new PageImpl<>(List.of(umHorarioAcessoResponse()));
    }

    private Page<HorarioAcessoResponse> umaListaHorarioHistoricoResponse() {
        return new PageImpl<>(List.of(umHorarioHistoricoResponse()));
    }

}
