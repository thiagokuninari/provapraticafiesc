package br.com.xbrain.autenticacao.modules.feriado.controller;

import br.com.xbrain.autenticacao.modules.comum.util.DataHoraAtual;
import br.com.xbrain.autenticacao.modules.feriado.dto.FeriadoImportacaoRequest;
import br.com.xbrain.autenticacao.modules.feriado.dto.FeriadoRequest;
import br.com.xbrain.autenticacao.modules.feriado.enums.ETipoFeriado;
import br.com.xbrain.autenticacao.modules.feriado.service.FeriadoService;
import com.google.common.io.ByteStreams;
import helpers.TestsHelper;
import lombok.SneakyThrows;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.NestedServletException;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static helpers.TestsHelper.convertObjectToJsonBytes;
import static helpers.TestsHelper.getAccessToken;
import static helpers.Usuarios.ADMIN;
import static helpers.Usuarios.HELP_DESK;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.any;
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

    private static final String URL_BASE = "/api/feriado";
    private static final String URL_GERENCIAR = "/api/feriado/gerenciar";

    @SpyBean
    private FeriadoService service;
    @Autowired
    private MockMvc mvc;
    @MockBean
    private DataHoraAtual dataHoraAtual;
    @MockBean
    RestTemplate restTemplate;

    @Test
    public void deveConsultarEncontrarFeriadoPelaData() throws Exception {
        mvc.perform(get(URL_BASE + "/consulta?data=07/09/2018")
            .header("Authorization", getAccessToken(mvc, ADMIN))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", is(Boolean.TRUE)));
    }

    @Test
    public void deveConsultarNaoEncontrarFeriadoPelaData() throws Exception {
        mvc.perform(get(URL_BASE + "/consulta?data=13/02/2018")
            .header("Authorization", getAccessToken(mvc, ADMIN))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", is(Boolean.FALSE)));
    }

    @Test
    public void deveConsultarEncontrarFeriadoLocalPelaData() throws Exception {
        mvc.perform(get(URL_BASE + "/consulta/5578?data=10/12/2018")
            .header("Authorization", getAccessToken(mvc, ADMIN))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", is(Boolean.TRUE)));
    }

    @Test
    public void deveConsultarNaoEncontrarFeriadoLocalPelaData() throws Exception {
        mvc.perform(get(URL_BASE + "/consulta/?data=10/12/2018")
            .header("Authorization", getAccessToken(mvc, ADMIN))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", is(Boolean.FALSE)));
    }

    @Test
    public void deveRetornarTodosFeriadosAnoAtual() throws Exception {
        when(dataHoraAtual.getData()).thenReturn(LocalDate.of(2018, 06, 06));
        mvc.perform(get(URL_BASE)
            .header("Authorization", getAccessToken(mvc, ADMIN))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(10)));
    }

    @Test
    public void consultarFeriadoComCidadeUf_deveLancarExcecao_seUltimoParametroContiverPonto() {
        assertThatExceptionOfType(NestedServletException.class)
            .isThrownBy(() ->
                mvc.perform(get(URL_BASE + "/cidade/Arapongas/P.R")
                    .header("Authorization", getAccessToken(mvc, ADMIN))
                    .contentType(MediaType.APPLICATION_JSON))
                    .andReturn()
            )
            .withMessageContaining("java.lang.NoClassDefFoundError: com/sun/activation/registries/LogSupport");
    }

    @Test
    public void consultarFeriadoComCidadeUf_deveRetornarOk_seParametrosValidos() throws Exception {
        doReturn(true).when(service).isFeriadoHojeNaCidadeUf(anyString(), anyString());
        mvc.perform(get(URL_BASE + "/cidade/Arapongas/PR")
            .header("Authorization", getAccessToken(mvc, ADMIN))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();
    }

    @Test
    public void cacheClearFeriados_deveChamarMetodo_seUsuarioAutenticado() throws Exception {
        doNothing().when(service).flushCacheFeriados();

        mvc.perform(delete(URL_BASE + "/cache/clear")
            .header("Authorization", getAccessToken(mvc, ADMIN))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();

        verify(service, times(1)).flushCacheFeriados();
    }

    @Test
    public void cacheClearFeriados_naoDeveChamarMetodo_seNaoEstiverAutenticado() throws Exception {
        mvc.perform(delete(URL_BASE + "/cache/clear")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized())
            .andReturn();

        verify(service, never()).flushCacheFeriados();
    }

    @Test
    @SneakyThrows
    public void obterFeriadosByFiltros_deveRetornarUnauthorizied_quandoNaoExistirUsuarioAutenticado() {
        mvc.perform(get(URL_GERENCIAR + "/obter-feriados")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized());

        verify(service, never()).obterFeriadosByFiltros(any(), any());
    }

    @Test
    @SneakyThrows
    public void obterFeriadosByFiltros_deveRetornarForbidden_quandoUsuarioNaoTemPermissao() {
        mvc.perform(get(URL_GERENCIAR + "/obter-feriados")
            .header("Authorization", getAccessToken(mvc, HELP_DESK))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isForbidden());

        verify(service, never()).obterFeriadosByFiltros(any(), any());
    }

    @Test
    @SneakyThrows
    public void obterFeriadosByFiltros_deveRetornarTodosFeriados_quandoUsuarioTiverPermissao() {
        mvc.perform(get(URL_GERENCIAR + "/obter-feriados")
            .header("Authorization", getAccessToken(mvc, ADMIN))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content", hasSize(10)));

        verify(service, times(1)).obterFeriadosByFiltros(any(), any());
    }

    @Test
    @SneakyThrows
    public void obterFeriadosByFiltros_deveRetornarFeriadosFiltrados_quandoTiverFiltro() {
        mvc.perform(get(URL_GERENCIAR + "/obter-feriados")
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
        mvc.perform(get(URL_GERENCIAR + "/10000")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized());

        verify(service, never()).getFeriadoById(anyInt());
    }

    @Test
    @SneakyThrows
    public void obterFeriadoPorId_deveRetornarForbidden_quandoUsuarioNaoTemPermissao() {
        mvc.perform(get(URL_GERENCIAR + "/10000")
            .header("Authorization", getAccessToken(mvc, HELP_DESK))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isForbidden());

        verify(service, never()).getFeriadoById(anyInt());
    }

    @Test
    @SneakyThrows
    public void obterFeriadoPorId_deveThrowException_quandoFeriadoNaoExiste() {
        mvc.perform(get(URL_GERENCIAR + "/99")
            .header("Authorization", getAccessToken(mvc, ADMIN))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());

        verify(service, times(1)).getFeriadoById(99);
    }

    @Test
    @SneakyThrows
    public void obterFeriadoPorId_deveRetornarFeriado_quandoUsuarioTiverPermissao() {
        mvc.perform(get(URL_GERENCIAR + "/10000")
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
        mvc.perform(post(URL_GERENCIAR + "/salvar")
            .contentType(MediaType.APPLICATION_JSON)
            .content(convertObjectToJsonBytes(umFeriadoRequest())))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @SneakyThrows
    public void salvar_deveRetornarForbidden_quandoUsuarioNaoTiverPermissao() {
        mvc.perform(post(URL_GERENCIAR + "/salvar")
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

        mvc.perform(post(URL_GERENCIAR + "/salvar")
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", getAccessToken(mvc, ADMIN))
            .content(convertObjectToJsonBytes(feriadoRequestInvalido)))
            .andExpect(status().isBadRequest());

        var feriadoEstadualRequestInvalido = umFeriadoRequest();
        feriadoEstadualRequestInvalido.setTipoFeriado(ETipoFeriado.ESTADUAL);

        mvc.perform(post(URL_BASE + "/gerenciar/salvar")
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", getAccessToken(mvc, ADMIN))
            .content(convertObjectToJsonBytes(feriadoEstadualRequestInvalido)))
            .andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    public void salvar_deveRetornarCreated_quandoUsuarioTiverPermissao() {
        mvc.perform(post(URL_GERENCIAR + "/salvar")
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
        mvc.perform(put(URL_GERENCIAR + "/editar")
            .contentType(MediaType.APPLICATION_JSON)
            .content(convertObjectToJsonBytes(umFeriadoRequest())))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @SneakyThrows
    public void editar_deveRetornarForbidden_quandoUsuarioNaoTiverPermissao() {
        mvc.perform(put(URL_GERENCIAR + "/editar")
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

        mvc.perform(put(URL_GERENCIAR + "/editar")
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", getAccessToken(mvc, ADMIN))
            .content(convertObjectToJsonBytes(feriadoRequestInvalido)))
            .andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    public void editar_deveThrowException_quandoFeriadoIdNaoExiste() {
        mvc.perform(put(URL_GERENCIAR + "/editar")
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

        mvc.perform(put(URL_GERENCIAR + "/editar")
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

        mvc.perform(put(URL_GERENCIAR + "/editar")
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
        mvc.perform(put(URL_GERENCIAR + "/excluir/10000")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @SneakyThrows
    public void excluirFeriado_deveRetornarForbidden_quandoUsuarioNaoTiverPermissao() {
        mvc.perform(put(URL_GERENCIAR + "/excluir/10000")
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", getAccessToken(mvc, HELP_DESK)))
            .andExpect(status().isForbidden());
    }

    @Test
    @SneakyThrows
    public void excluirFeriado_deveThrowException_quandoFeriadoIdNaoExiste() {
        mvc.perform(put(URL_GERENCIAR + "/excluir/99")
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", getAccessToken(mvc, ADMIN)))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$[0].message", is("Feriado não encontrado.")));
    }

    @Test
    @SneakyThrows
    public void excluirFeriado_deveRetornarOk_quandoFeriadoExiste() {
        mvc.perform(put(URL_GERENCIAR + "/excluir/10000")
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", getAccessToken(mvc, ADMIN)))
            .andExpect(status().isOk());
    }

    @Test
    @SneakyThrows
    public void importarFeriados_deveRetornarUnauthorizied_quandoNaoTiverUsuarioAutenticado() {
        byte[] bytes = ByteStreams.toByteArray(getFileInputStream("test_importacao_feriado/test_feriado.csv"));
        var request = FeriadoImportacaoRequest.builder()
            .anoReferencia(2019)
            .descricao("Feriados 2019")
            .build();

        mvc.perform(fileUpload(URL_GERENCIAR + "/importar")
            .file(umFile(bytes, "file"))
            .param("feriadoImportacaoJson", TestsHelper.convertObjectToJsonString(request))
            .accept(MediaType.ALL_VALUE))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @SneakyThrows
    public void importarFeriados_deveRetornarForbidden_quandoUsuarioNaoTiverPermissao() {
        byte[] bytes = ByteStreams.toByteArray(getFileInputStream("test_importacao_feriado/test_feriado.csv"));

        mvc.perform(fileUpload(URL_GERENCIAR + "/importar")
            .file(umFile(bytes, "file"))
            .param("feriadoImportacaoJson", TestsHelper.convertObjectToJsonString(umFeriadoImportacaoRequest()))
            .accept(MediaType.ALL_VALUE)
            .header("Authorization", getAccessToken(mvc, HELP_DESK)))
            .andExpect(status().isForbidden());
    }

    @Test
    @SneakyThrows
    public void importarFeriados_deveRetornarBadRequest_quandoNaoTiverTodasColunasObrigatoriaNoArquivo() {
        byte[] bytes = ByteStreams.toByteArray(getFileInputStream("test_importacao_feriado/test_feriado_sem_uf.csv"));

        mvc.perform(fileUpload(URL_GERENCIAR + "/importar")
            .file(umFile(bytes, "file"))
            .param("feriadoImportacaoJson", TestsHelper.convertObjectToJsonString(umFeriadoImportacaoRequest()))
            .accept(MediaType.ALL_VALUE)
            .header("Authorization", getAccessToken(mvc, ADMIN)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$[*].message", containsInAnyOrder(
                "O cabeçalho do arquivo não pode ser diferente do exemplo.")));
    }

    @Test
    @SneakyThrows
    public void importarFeriados_deveRetornarBadRequest_quandoOrdemDasColunasDiferenteDoLayout() {
        byte[] bytes = ByteStreams.toByteArray(getFileInputStream("test_importacao_feriado/test_feriado_ordem_invertida.csv"));

        mvc.perform(fileUpload(URL_GERENCIAR + "/importar")
            .file(umFile(bytes, "file"))
            .param("feriadoImportacaoJson", TestsHelper.convertObjectToJsonString(umFeriadoImportacaoRequest()))
            .accept(MediaType.ALL_VALUE)
            .header("Authorization", getAccessToken(mvc, ADMIN)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$[*].message", containsInAnyOrder(
                "O cabeçalho do arquivo não pode ser diferente do exemplo.")));
    }

    @Test
    @SneakyThrows
    public void importarFeriados_deveRetornarBadRequest_quandoArquivoVazio() {
        byte[] bytes = ByteStreams.toByteArray(getFileInputStream("test_importacao_feriado/test_feriado_vazio.csv"));

        mvc.perform(fileUpload(URL_GERENCIAR + "/importar")
            .file(umFile(bytes, "file"))
            .param("feriadoImportacaoJson", TestsHelper.convertObjectToJsonString(umFeriadoImportacaoRequest()))
            .accept(MediaType.ALL_VALUE)
            .header("Authorization", getAccessToken(mvc, ADMIN)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$[*].message", containsInAnyOrder(
                "O arquivo não pode ser vazio.")));
    }

    @Test
    @SneakyThrows
    public void importarFeriados_deveRetornarQuantidadeDeItensIndiferenteSePossuiLinhasVazias_quandoArquivoOk() {
        byte[] bytes = ByteStreams.toByteArray(getFileInputStream("test_importacao_feriado/test_feriado.csv"));

        mvc.perform(fileUpload(URL_GERENCIAR + "/importar")
            .file(umFile(bytes, "file"))
            .param("feriadoImportacaoJson", TestsHelper.convertObjectToJsonString(umFeriadoImportacaoRequest()))
            .accept(MediaType.ALL_VALUE)
            .header("Authorization", getAccessToken(mvc, ADMIN)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(14)))
            .andExpect(jsonPath("$[0].feriadoImportadoComSucesso", is("V")))
            .andExpect(jsonPath("$[1].feriadoImportadoComSucesso", is("V")))
            .andExpect(jsonPath("$[2].feriadoImportadoComSucesso", is("V")))
            .andExpect(jsonPath("$[3].feriadoImportadoComSucesso", is("V")))
            .andExpect(jsonPath("$[4].feriadoImportadoComSucesso", is("F")));
    }

    private FeriadoRequest umFeriadoRequest() {
        return FeriadoRequest.builder()
            .nome("FERIADO TESTE")
            .tipoFeriado(ETipoFeriado.NACIONAL)
            .dataFeriado("12/11/2019")
            .build();
    }

    private MockMultipartFile umFile(byte[] bytes, String nome) {
        return new MockMultipartFile("file", LocalDateTime.now().toString().concat(nome) + ".csv", "text/csv", bytes);
    }

    private InputStream getFileInputStream(String file) throws Exception {
        return new ByteArrayInputStream(
            Files.readAllBytes(Paths.get(
                getClass().getClassLoader().getResource(file)
                    .getPath())));
    }

    private FeriadoImportacaoRequest umFeriadoImportacaoRequest() {
        return FeriadoImportacaoRequest.builder()
            .anoReferencia(2019)
            .descricao("Feriados 2019")
            .build();
    }
}
