package br.com.xbrain.autenticacao.modules.feriado.controller;

import br.com.xbrain.autenticacao.config.OAuth2ResourceConfig;
import br.com.xbrain.autenticacao.modules.comum.dto.PageRequest;
import br.com.xbrain.autenticacao.modules.equipevenda.service.EquipeVendaD2dService;
import br.com.xbrain.autenticacao.modules.feriado.dto.FeriadoFiltros;
import br.com.xbrain.autenticacao.modules.feriado.dto.FeriadoImportacaoRequest;
import br.com.xbrain.autenticacao.modules.feriado.dto.FeriadoRequest;
import br.com.xbrain.autenticacao.modules.feriado.service.FeriadoImportacaoService;
import br.com.xbrain.autenticacao.modules.feriado.service.FeriadoService;
import br.com.xbrain.autenticacao.modules.usuario.event.UsuarioSubCanalObserver;
import com.google.common.io.ByteStreams;
import lombok.SneakyThrows;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

import static br.com.xbrain.autenticacao.modules.feriado.helper.FeriadoHelper.*;
import static helpers.TestsHelper.convertObjectToJsonBytes;
import static helpers.TestsHelper.converterObjectParaMultipart;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WithMockUser
@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@Import(OAuth2ResourceConfig.class)
@WebMvcTest(controllers = FeriadoGerenciamentoController.class)
public class FeriadoGerenciamentoControllerTest {

    private static final String URL_GERENCIAR = "/api/feriado/gerenciar";
    private static final String GERENCIAR_FERIADOS = "CTR_2050";

    @Autowired
    private MockMvc mvc;
    @MockBean
    private TokenStore tokenStore;
    @MockBean
    private FeriadoService service;
    @MockBean
    private EquipeVendaD2dService equipeVendaD2dService;
    @MockBean
    private UsuarioSubCanalObserver usuarioSubCanalObserver;
    @MockBean
    private FeriadoImportacaoService feriadoImportacaoService;

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void obterFeriadosByFiltros_deveRetornarUnauthorized_quandoNaoExistirUsuarioAutenticado() {
        mvc.perform(get(URL_GERENCIAR + "/obter-feriados")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.error", is("unauthorized")));

        verify(service, never()).obterFeriadosByFiltros(any(), any());
    }

    @Test
    @SneakyThrows
    public void obterFeriadosByFiltros_deveRetornarForbidden_quandoUsuarioNaoTemPermissao() {
        mvc.perform(get(URL_GERENCIAR + "/obter-feriados")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isForbidden())
            .andExpect(jsonPath("$.error", is("access_denied")));

        verify(service, never()).obterFeriadosByFiltros(any(), any());
    }

    @Test
    @SneakyThrows
    @WithMockUser(roles = {GERENCIAR_FERIADOS})
    public void obterFeriadosByFiltros_deveRetornarOk_quandoUsuarioTiverPermissao() {
        mvc.perform(get(URL_GERENCIAR + "/obter-feriados")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(service).obterFeriadosByFiltros(new PageRequest(), new FeriadoFiltros());
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void obterFeriadosPorId_deveRetornarUnauthorized_quandoNaoExistirUsuarioAutenticado() {
        mvc.perform(get(URL_GERENCIAR + "/10000")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.error", is("unauthorized")));

        verify(service, never()).getFeriadoById(anyInt());
    }

    @Test
    @SneakyThrows
    public void obterFeriadoPorId_deveRetornarForbidden_quandoUsuarioNaoTemPermissao() {
        mvc.perform(get(URL_GERENCIAR + "/10000")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isForbidden())
            .andExpect(jsonPath("$.error", is("access_denied")));

        verify(service, never()).getFeriadoById(anyInt());
    }

    @Test
    @SneakyThrows
    @WithMockUser(roles = {GERENCIAR_FERIADOS})
    public void obterFeriadoPorId_deveRetornarOk_quandoUsuarioTiverPermissao() {
        mvc.perform(get(URL_GERENCIAR + "/10000")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(service).getFeriadoById(10000);
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void salvar_deveRetornarUnauthorized_quandoNaoExistirUsuarioAutenticado() {
        mvc.perform(post(URL_GERENCIAR + "/salvar")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(umFeriadoRequest())))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.error", is("unauthorized")));

        verify(service, never()).salvarFeriado(any());
    }

    @Test
    @SneakyThrows
    public void salvar_deveRetornarForbidden_quandoUsuarioNaoTiverPermissao() {
        mvc.perform(post(URL_GERENCIAR + "/salvar")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(umFeriadoRequest())))
            .andExpect(status().isForbidden())
            .andExpect(jsonPath("$.error", is("access_denied")));

        verify(service, never()).salvarFeriado(any());
    }

    @Test
    @SneakyThrows
    @WithMockUser(roles = {GERENCIAR_FERIADOS})
    public void salvar_deveRetornarBadRequest_quandoDadoObrigatorioNull() {
        mvc.perform(post(URL_GERENCIAR + "/salvar")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(new FeriadoRequest())))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$[*].message", containsInAnyOrder(
                "O campo dataFeriado é obrigatório.",
                "O campo nome é obrigatório.",
                "O campo tipoFeriado é obrigatório.")));

        verify(service, never()).salvarFeriado(any());
    }

    @Test
    @SneakyThrows
    @WithMockUser(roles = {GERENCIAR_FERIADOS})
    public void salvar_deveRetornarCreated_quandoNomeComMaisDe255Caracteres() {
        var requestNome256Caracteres = umFeriadoRequest();
        requestNome256Caracteres.setNome("ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff"
            + "ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff"
            + "ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff");

        mvc.perform(post(URL_GERENCIAR + "/salvar")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(requestNome256Caracteres)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$[*].message", containsInAnyOrder(
                "O campo nome precisa ter entre 0 e 255 caracteres.")));

        verify(service, never()).salvarFeriado(any());
    }

    @Test
    @SneakyThrows
    @WithMockUser(roles = {GERENCIAR_FERIADOS})
    public void salvar_deveRetornarCreated_quandoDadoObrigatorioStringBlank() {
        var requestBlank = umFeriadoRequest();
        requestBlank.setNome("  ");
        requestBlank.setDataFeriado("  ");

        mvc.perform(post(URL_GERENCIAR + "/salvar")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(requestBlank)))
            .andExpect(status().isCreated());

        verify(service).salvarFeriado(requestBlank);
    }

    @Test
    @SneakyThrows
    @WithMockUser(roles = {GERENCIAR_FERIADOS})
    public void salvar_deveRetornarCreated_quandoUsuarioTiverPermissao() {
        mvc.perform(post(URL_GERENCIAR + "/salvar")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(umFeriadoRequest())))
            .andExpect(status().isCreated());

        verify(service).salvarFeriado(umFeriadoRequest());
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void editar_deveRetornarUnauthorized_quandoNaoExistirUsuarioAutenticado() {
        mvc.perform(put(URL_GERENCIAR + "/editar")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(umFeriadoRequest())))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.error", is("unauthorized")));

        verify(service, never()).editarFeriado(any());
    }

    @Test
    @SneakyThrows
    public void editar_deveRetornarForbidden_quandoUsuarioNaoTiverPermissao() {
        mvc.perform(put(URL_GERENCIAR + "/editar")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(umFeriadoRequest())))
            .andExpect(status().isForbidden())
            .andExpect(jsonPath("$.error", is("access_denied")));

        verify(service, never()).editarFeriado(any());
    }

    @Test
    @SneakyThrows
    @WithMockUser(roles = {GERENCIAR_FERIADOS})
    public void editar_deveRetornarBadRequest_quandoDadoObrigatorioNull() {
        mvc.perform(put(URL_GERENCIAR + "/editar")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(new FeriadoRequest())))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$[*].message", containsInAnyOrder(
                "O campo dataFeriado é obrigatório.",
                "O campo nome é obrigatório.",
                "O campo tipoFeriado é obrigatório.")));

        verify(service, never()).editarFeriado(any());
    }

    @Test
    @SneakyThrows
    @WithMockUser(roles = {GERENCIAR_FERIADOS})
    public void editar_deveRetornarCreated_quandoNomeComMaisDe255Caracteres() {
        var requestNome256Caracteres = umFeriadoRequest();
        requestNome256Caracteres.setNome("ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff"
            + "ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff"
            + "ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff");

        mvc.perform(put(URL_GERENCIAR + "/editar")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(requestNome256Caracteres)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$[*].message", containsInAnyOrder(
                "O campo nome precisa ter entre 0 e 255 caracteres.")));

        verify(service, never()).editarFeriado(any());
    }

    @Test
    @SneakyThrows
    @WithMockUser(roles = {GERENCIAR_FERIADOS})
    public void editar_deveRetornarOk_quandoDadoObrigatorioStringBlank() {
        var requestBlank = umFeriadoRequest();
        requestBlank.setNome("  ");
        requestBlank.setDataFeriado("  ");

        mvc.perform(put(URL_GERENCIAR + "/editar")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(requestBlank)))
            .andExpect(status().isOk());

        verify(service).editarFeriado(eq(requestBlank));
    }

    @Test
    @SneakyThrows
    @WithMockUser(roles = {GERENCIAR_FERIADOS})
    public void editar_deveRetornarOk_quandoUsuarioTiverPermissao() {
        mvc.perform(put(URL_GERENCIAR + "/editar")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(umFeriadoRequest())))
            .andExpect(status().isOk());

        verify(service).editarFeriado(eq(umFeriadoRequest()));
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void excluirFeriado_deveRetornarUnauthorized_quandoNaoExistirUsuarioAutenticado() {
        mvc.perform(put(URL_GERENCIAR + "/excluir/10000")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.error", is("unauthorized")));

        verify(service, never()).excluirFeriado(any());
    }

    @Test
    @SneakyThrows
    public void excluirFeriado_deveRetornarForbidden_quandoUsuarioNaoTiverPermissao() {
        mvc.perform(put(URL_GERENCIAR + "/excluir/10000")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isForbidden())
            .andExpect(jsonPath("$.error", is("access_denied")));

        verify(service, never()).excluirFeriado(any());
    }

    @Test
    @SneakyThrows
    @WithMockUser(roles = {GERENCIAR_FERIADOS})
    public void excluirFeriado_deveRetornarOk_quandoFeriadoExiste() {
        mvc.perform(put(URL_GERENCIAR + "/excluir/10000")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(service).excluirFeriado(10000);
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void importarFeriados_deveRetornarUnauthorized_quandoNaoTiverUsuarioAutenticado() {
        byte[] bytes = ByteStreams.toByteArray(getFileInputStream("test_importacao_feriado/test_feriado.csv"));

        mvc.perform(fileUpload(URL_GERENCIAR + "/importar")
                .file(umFile(bytes, "file"))
                .file(converterObjectParaMultipart("feriadoImportacaoRequest", umFeriadoImportacaoRequest()))
                .accept(MediaType.ALL_VALUE))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.error", is("unauthorized")));

        verify(feriadoImportacaoService, never()).importarFeriadoArquivo(any(), any());
    }

    @Test
    @SneakyThrows
    public void importarFeriados_deveRetornarForbidden_quandoUsuarioNaoTiverPermissao() {
        byte[] bytes = ByteStreams.toByteArray(getFileInputStream("test_importacao_feriado/test_feriado.csv"));

        mvc.perform(fileUpload(URL_GERENCIAR + "/importar")
                .file(umFile(bytes, "file"))
                .file(converterObjectParaMultipart("feriadoImportacaoRequest", umFeriadoImportacaoRequest()))
                .accept(MediaType.ALL_VALUE))
            .andExpect(status().isForbidden())
            .andExpect(jsonPath("$.error", is("access_denied")));

        verify(feriadoImportacaoService, never()).importarFeriadoArquivo(any(), any());
    }

    @Test
    @SneakyThrows
    @WithMockUser(roles = {GERENCIAR_FERIADOS})
    public void importarFeriados_deveRetornarBadRequest_quandoDadoObrigatorioNaoInformado() {
        byte[] bytes = ByteStreams.toByteArray(getFileInputStream("test_importacao_feriado/test_feriado.csv"));

        mvc.perform(fileUpload(URL_GERENCIAR + "/importar")
                .file(umFile(bytes, "file"))
                .file(converterObjectParaMultipart("feriadoImportacaoRequest", new FeriadoImportacaoRequest()))
                .accept(MediaType.ALL_VALUE))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$[*].message", containsInAnyOrder(
                "O campo anoReferencia é obrigatório.")));

        verify(feriadoImportacaoService, never()).importarFeriadoArquivo(any(), any());
    }

    @Test
    @SneakyThrows
    @WithMockUser(roles = {GERENCIAR_FERIADOS})
    public void importarFeriados_deveRetornarOk_quandoDadosValidos() {
        byte[] bytes = ByteStreams.toByteArray(getFileInputStream("test_importacao_feriado/test_feriado.csv"));

        mvc.perform(fileUpload(URL_GERENCIAR + "/importar")
                .file(umFile(bytes, "file"))
                .file(converterObjectParaMultipart("feriadoImportacaoRequest", umFeriadoImportacaoRequest()))
                .accept(MediaType.ALL_VALUE))
            .andExpect(status().isOk());

        verify(feriadoImportacaoService).importarFeriadoArquivo(any(), eq(umFeriadoImportacaoRequest()));
    }

    private InputStream getFileInputStream(String file) throws Exception {
        return new ByteArrayInputStream(
            Files.readAllBytes(Paths.get(
                getClass().getClassLoader().getResource(file)
                    .getPath())));
    }
}
