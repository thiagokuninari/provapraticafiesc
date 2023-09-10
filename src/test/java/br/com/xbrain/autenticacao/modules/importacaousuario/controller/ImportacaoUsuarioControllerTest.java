package br.com.xbrain.autenticacao.modules.importacaousuario.controller;

import br.com.xbrain.autenticacao.config.OAuth2ResourceConfig;
import br.com.xbrain.autenticacao.modules.equipevenda.service.EquipeVendaD2dService;
import br.com.xbrain.autenticacao.modules.importacaousuario.service.ImportacaoUsuarioService;
import br.com.xbrain.autenticacao.modules.usuario.event.UsuarioSubCanalObserver;
import lombok.SneakyThrows;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static br.com.xbrain.autenticacao.modules.importacaousuario.util.FileUtil.getFile;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WithMockUser
@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@Import(OAuth2ResourceConfig.class)
@WebMvcTest(controllers = ImportacaoUsuarioController.class)
public class ImportacaoUsuarioControllerTest {

    @Autowired
    private MockMvc mvc;
    @MockBean
    private TokenStore tokenStore;
    @MockBean
    private ImportacaoUsuarioService service;
    @MockBean
    private EquipeVendaD2dService equipeVendaD2dService;
    @MockBean
    private UsuarioSubCanalObserver usuarioSubCanalObserver;

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void uploadUsuario_deveRetornarUnauthorized_quandoNaoPassarToken() {
        mvc.perform(MockMvcRequestBuilders
                .fileUpload("/api/importacao-usuarios"))
            .andExpect(status().isUnauthorized());

        verify(service, never()).salvarUsuarioFile(any(), any());
    }

    @Test
    @SneakyThrows
    public void uploadUsuario_deveRetornarBadRequest_quandoNaoInformarCampoUsuarioImportacaoJson() {
        mvc.perform(MockMvcRequestBuilders
                .fileUpload("/api/importacao-usuarios")
                .file(new MockMultipartFile("file", "planilha.xlsx",
                    "application/vnd.ms-excel", getFile("arquivo_usuario/planilha.xlsx")))
                .accept(MediaType.ALL_VALUE))
            .andExpect(status().isBadRequest());

        verify(service, never()).salvarUsuarioFile(any(), any());
    }

    @Test
    @SneakyThrows
    public void uploadUsuario_deveRetornarBadRequest_quandoNaoInformarCampoFile() {
        mvc.perform(MockMvcRequestBuilders
                .fileUpload("/api/importacao-usuarios")
                .param("usuarioImportacaoJson",
                    "{\"file\":[{\"preview\":"
                        + "\"blob:http://localhost:3100/5fa6d20c-8b61-4500-a0e9-5c9184e2c36d\"}],"
                        + "\"senhaPadrao\":true}")
                .accept(MediaType.ALL_VALUE))
            .andExpect(status().isBadRequest());

        verify(service, never()).salvarUsuarioFile(any(), any());
    }

    @Test
    @SneakyThrows
    public void uploadUsuario_deveRetornarOk_quandoDadosValidos() {
        mvc.perform(MockMvcRequestBuilders
                .fileUpload("/api/importacao-usuarios")
                .file(new MockMultipartFile("file", "planilha.xlsx",
                    "application/vnd.ms-excel", getFile("arquivo_usuario/planilha.xlsx")))
                .param("usuarioImportacaoJson",
                    "{\"file\":[{\"preview\":"
                        + "\"blob:http://localhost:3100/5fa6d20c-8b61-4500-a0e9-5c9184e2c36d\"}],"
                        + "\"senhaPadrao\":true}")
                .accept(MediaType.ALL_VALUE))
            .andExpect(status().isOk());

        verify(service).salvarUsuarioFile(any(), any());
    }
}
