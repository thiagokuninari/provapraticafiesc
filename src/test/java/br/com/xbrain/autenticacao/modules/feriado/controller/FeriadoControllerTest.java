package br.com.xbrain.autenticacao.modules.feriado.controller;

import br.com.xbrain.autenticacao.modules.comum.util.DataHoraAtual;
import br.com.xbrain.autenticacao.modules.feriado.dto.FeriadoRequest;
import br.com.xbrain.autenticacao.modules.feriado.enums.ETipoFeriado;
import br.com.xbrain.autenticacao.modules.feriado.service.FeriadoService;
import lombok.SneakyThrows;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.NestedServletException;

import java.time.LocalDate;

import static helpers.TestsHelper.convertObjectToJsonBytes;
import static helpers.TestsHelper.getAccessToken;
import static helpers.Usuarios.ADMIN;
import static helpers.Usuarios.HELP_DESK;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Sql(scripts = {"classpath:/tests_database.sql"})
public class FeriadoControllerTest {

    private static final String URL = "/api/feriado";

    @SpyBean
    private FeriadoService service;
    @Autowired
    private MockMvc mvc;
    @MockBean
    private DataHoraAtual dataHoraAtual;

    @Test
    public void deveConsultarEncontrarFeriadoPelaData() throws Exception {
        mvc.perform(get(URL + "/consulta?data=07/09/2018")
            .header("Authorization", getAccessToken(mvc, ADMIN))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", is(Boolean.TRUE)));
    }

    @Test
    public void deveConsultarNaoEncontrarFeriadoPelaData() throws Exception {
        mvc.perform(get(URL + "/consulta?data=13/02/2018")
            .header("Authorization", getAccessToken(mvc, ADMIN))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", is(Boolean.FALSE)));
    }

    @Test
    public void deveConsultarEncontrarFeriadoLocalPelaData() throws Exception {
        mvc.perform(get(URL + "/consulta/5578?data=10/12/2018")
            .header("Authorization", getAccessToken(mvc, ADMIN))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", is(Boolean.TRUE)));
    }

    @Test
    public void deveConsultarNaoEncontrarFeriadoLocalPelaData() throws Exception {
        mvc.perform(get(URL + "/consulta/?data=10/12/2018")
            .header("Authorization", getAccessToken(mvc, ADMIN))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", is(Boolean.FALSE)));
    }

    @Test
    public void deveRetornarTodosFeriadosAnoAtual() throws Exception {
        when(dataHoraAtual.getData()).thenReturn(LocalDate.of(2018, 06, 06));
        mvc.perform(get(URL)
            .header("Authorization", getAccessToken(mvc, ADMIN))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(10)));
    }

//    @Test
//    public void deveSalvarFeriado() throws Exception {
//        FeriadoRequest request = new FeriadoRequest();
//        request.setNome("Feriado Nacional");
//        request.setDataFeriado("05/06/2018");
//        mvc.perform(post(URL)
//                .header("Authorization", getAccessToken(mvc, ADMIN))
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(convertObjectToJsonBytes(request)))
//                .andExpect(status().isCreated())
//                .andExpect(jsonPath("$.nome", is(request.getNome())))
//                .andExpect(jsonPath("$.feriadoNacional", is("V")));
//    }

//    @Test
//    public void deveSalvarFeriadoLocal() throws Exception {
//        FeriadoRequest request = new FeriadoRequest();
//        request.setNome("Feriado Nacional");
//        request.setDataFeriado("05/06/2018");
//        request.setCidadeId(5578);
//        mvc.perform(post(URL)
//                .header("Authorization", getAccessToken(mvc, ADMIN))
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(convertObjectToJsonBytes(request)))
//                .andExpect(status().isCreated())
//                .andExpect(jsonPath("$.nome", is(request.getNome())))
//                .andExpect(jsonPath("$.feriadoNacional", is("F")));
//    }

    @Test
    public void consultarFeriadoComCidadeUf_deveLancarExcecao_seUltimoParametroContiverPonto() {
        assertThatExceptionOfType(NestedServletException.class)
            .isThrownBy(() ->
                mvc.perform(get(URL + "/cidade/Arapongas/P.R")
                    .header("Authorization", getAccessToken(mvc, ADMIN))
                    .contentType(MediaType.APPLICATION_JSON))
                    .andReturn()
            )
            .withMessageContaining("java.lang.NoClassDefFoundError: com/sun/activation/registries/LogSupport");
    }

    @Test
    public void consultarFeriadoComCidadeUf_deveRetornarOk_seParametrosValidos() throws Exception {
        doReturn(true).when(service).isFeriadoHojeNaCidadeUf(anyString(), anyString());
        mvc.perform(get(URL + "/cidade/Arapongas/PR")
            .header("Authorization", getAccessToken(mvc, ADMIN))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();
    }

    @Test
    public void cacheClearFeriados_deveChamarMetodo_seUsuarioAutenticado() throws Exception {
        doNothing().when(service).flushCacheFeriados();

        mvc.perform(delete(URL + "/cache/clear")
            .header("Authorization", getAccessToken(mvc, ADMIN))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();

        verify(service, times(1)).flushCacheFeriados();
    }

    @Test
    public void cacheClearFeriados_naoDeveChamarMetodo_seNaoEstiverAutenticado() throws Exception {
        mvc.perform(delete(URL + "/cache/clear")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized())
            .andReturn();

        verify(service, never()).flushCacheFeriados();
    }

    @Test
    @SneakyThrows
    public void obterFeriadosByFiltros_deveRetornarUnauthorizied_quandoNaoExistirUsuarioAutenticado() {
        mvc.perform(get(URL + "/obter-feriados")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized());

        verify(service, never()).obterFeriadosByFiltros(any(), any());
    }

    @Test
    @SneakyThrows
    public void obterFeriadosByFiltros_deveRetornarForbidden_quandoUsuarioNaoTemPermissao() {
        mvc.perform(get(URL + "/obter-feriados")
            .header("Authorization", getAccessToken(mvc, HELP_DESK))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isForbidden());

        verify(service, never()).obterFeriadosByFiltros(any(), any());
    }

    @Test
    @SneakyThrows
    public void obterFeriadosByFiltros_deveRetornarTodosFeriados_quandoUsuarioTiverPermissao() {
        mvc.perform(get(URL + "/obter-feriados")
            .header("Authorization", getAccessToken(mvc, ADMIN))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content", hasSize(10)));

        verify(service, times(1)).obterFeriadosByFiltros(any(), any());
    }

    @Test
    @SneakyThrows
    public void obterFeriadosByFiltros_deveRetornarFeriadosFiltrados_quandoTiverFiltro() {
        mvc.perform(get(URL + "/obter-feriados")
            .header("Authorization", getAccessToken(mvc, ADMIN))
            .param("tipoFeriado", "MUNICIPAL")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content", hasSize(1)));

        verify(service, times(1)).obterFeriadosByFiltros(any(), any());
    }

    @Test
    @SneakyThrows
    public void obterFeriadosPorId_deveRetornarUnauthorizied_quandoNaoExistirUsuarioAutenticado() {
        mvc.perform(get(URL + "/10000")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized());

        verify(service, never()).getFeriadoById(anyInt());
    }

    @Test
    @SneakyThrows
    public void obterFeriadoPorId_deveRetornarForbidden_quandoUsuarioNaoTemPermissao() {
        mvc.perform(get(URL + "/10000")
            .header("Authorization", getAccessToken(mvc, HELP_DESK))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isForbidden());

        verify(service, never()).getFeriadoById(anyInt());
    }

    @Test
    @SneakyThrows
    public void obterFeriadoPorId_deveThrowException_quandoFeriadoNaoExiste() {
        mvc.perform(get(URL + "/99")
            .header("Authorization", getAccessToken(mvc, ADMIN))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());

        verify(service, times(1)).getFeriadoById(99);
    }

    @Test
    @SneakyThrows
    public void obterFeriadoPorId_deveRetornarFeriado_quandoUsuarioTiverPermissao() {
        mvc.perform(get(URL + "/10000")
            .header("Authorization", getAccessToken(mvc, ADMIN))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.nome", is("Aniversário da cidade")))
            .andExpect(jsonPath("$.dataFeriado", is("10/12/2018")));

        verify(service, times(1)).getFeriadoById(10000);
    }

    @Test
    @SneakyThrows
    public void salvar_deveRetornarUnauthorizied_quandoNaoExistirUsuarioAutenticado() {
        mvc.perform(post(URL + "/salvar")
            .contentType(MediaType.APPLICATION_JSON)
            .content(convertObjectToJsonBytes(umFeriadoRequest())))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @SneakyThrows
    public void salvar_deveRetornarForbidden_quandoUsuarioNaoTiverPermissao() {
        mvc.perform(post(URL + "/salvar")
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", getAccessToken(mvc, HELP_DESK))
            .content(convertObjectToJsonBytes(umFeriadoRequest())))
            .andExpect(status().isForbidden());
    }

    @Test
    @SneakyThrows
    public void salvar_deveRetornarBadRequest_quandoCampoObrigatorioNaoPreenchido() {
        var feriadoRequestInvalido = umFeriadoRequest();
        feriadoRequestInvalido.setDataFeriado(null);

        mvc.perform(post(URL + "/salvar")
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", getAccessToken(mvc, ADMIN))
            .content(convertObjectToJsonBytes(feriadoRequestInvalido)))
            .andExpect(status().isBadRequest());

        var feriadoEstadualRequestInvalido = umFeriadoRequest();
        feriadoEstadualRequestInvalido.setTipoFeriado(ETipoFeriado.ESTADUAL);

        mvc.perform(post(URL + "/salvar")
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", getAccessToken(mvc, ADMIN))
            .content(convertObjectToJsonBytes(feriadoEstadualRequestInvalido)))
            .andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    public void salvar_deveRetornarCreated_quandoUsuarioTiverPermissao() {
        mvc.perform(post(URL + "/salvar")
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", getAccessToken(mvc, ADMIN))
            .content(convertObjectToJsonBytes(umFeriadoRequest())))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.nome", is("FERIADO TESTE")))
            .andExpect(jsonPath("$.dataFeriado", is("12/11/2019")))
            .andExpect(jsonPath("$.tipoFeriado", is("NACIONAL")))
            .andExpect(jsonPath("$.feriadoNacional", is("V")));
    }

    @Test
    @SneakyThrows
    public void editar_deveRetornarUnauthorizied_quandoNaoExistirUsuarioAutenticado() {
        mvc.perform(put(URL + "/editar")
            .contentType(MediaType.APPLICATION_JSON)
            .content(convertObjectToJsonBytes(umFeriadoRequest())))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @SneakyThrows
    public void editar_deveRetornarForbidden_quandoUsuarioNaoTiverPermissao() {
        mvc.perform(put(URL + "/editar")
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", getAccessToken(mvc, HELP_DESK))
            .content(convertObjectToJsonBytes(umFeriadoRequest())))
            .andExpect(status().isForbidden());
    }

    @Test
    @SneakyThrows
    public void editar_deveRetornarBadRequest_quandoCampoObrigatorioNaoPreenchido() {
        var feriadoRequestInvalido = umFeriadoRequest();
        feriadoRequestInvalido.setDataFeriado(null);

        mvc.perform(put(URL + "/editar")
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", getAccessToken(mvc, ADMIN))
            .content(convertObjectToJsonBytes(feriadoRequestInvalido)))
            .andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    public void editar_deveThrowException_quandoFeriadoIdNaoExiste() {
        mvc.perform(put(URL + "/editar")
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", getAccessToken(mvc, ADMIN))
            .content(convertObjectToJsonBytes(umFeriadoRequest())))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$[0].message", is("Feriado não encontrado.")));
    }

    @Test
    @SneakyThrows
    public void editar_deveThrowException_quandoTipoFeriadoDiferente() {
        var feriadoEditado = umFeriadoRequest();
        feriadoEditado.setId(10000);

        mvc.perform(put(URL + "/editar")
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", getAccessToken(mvc, ADMIN))
            .content(convertObjectToJsonBytes(feriadoEditado)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$[0].message", is("Não é permitido editar o Tipo do Feriado.")));
    }

    @Test
    @SneakyThrows
    public void editar_deveRetornarOk_quandoUsuarioTiverPermissao() {
        var feriadoEditado = umFeriadoRequest();
        feriadoEditado.setId(10000);
        feriadoEditado.setTipoFeriado(ETipoFeriado.MUNICIPAL);
        feriadoEditado.setEstadoId(1);
        feriadoEditado.setCidadeId(5578);

        mvc.perform(put(URL + "/editar")
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", getAccessToken(mvc, ADMIN))
            .content(convertObjectToJsonBytes(feriadoEditado)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.nome", is("FERIADO TESTE")))
            .andExpect(jsonPath("$.id", is(10000)))
            .andExpect(jsonPath("$.dataFeriado", is("12/11/2019")))
            .andExpect(jsonPath("$.cidadeId", is(5578)))
            .andExpect(jsonPath("$.tipoFeriado", is("MUNICIPAL")));
    }

    @Test
    @SneakyThrows
    public void excluirFeriado_deveRetornarUnauthorizied_quandoNaoExistirUsuarioAutenticado() {
        mvc.perform(put(URL + "/excluir/10000")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @SneakyThrows
    public void excluirFeriado_deveRetornarForbidden_quandoUsuarioNaoTiverPermissao() {
        mvc.perform(put(URL + "/excluir/10000")
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", getAccessToken(mvc, HELP_DESK)))
            .andExpect(status().isForbidden());
    }

    @Test
    @SneakyThrows
    public void excluirFeriado_deveThrowException_quandoFeriadoIdNaoExiste() {
        mvc.perform(put(URL + "/excluir/99")
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", getAccessToken(mvc, ADMIN)))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$[0].message", is("Feriado não encontrado.")));
    }

    @Test
    @SneakyThrows
    public void excluirFeriado_deveRetornarOk_quandoFeriadoExiste() {
        mvc.perform(put(URL + "/excluir/10000")
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", getAccessToken(mvc, ADMIN)))
            .andExpect(status().isOk());
    }

    private FeriadoRequest umFeriadoRequest() {
        return FeriadoRequest.builder()
            .nome("FERIADO TESTE")
            .tipoFeriado(ETipoFeriado.NACIONAL)
            .dataFeriado("12/11/2019")
            .build();
    }
}
