package br.com.xbrain.autenticacao.modules.importacaousuario.controller;

import helpers.Usuarios;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import static br.com.xbrain.autenticacao.modules.importacaousuario.util.FileUtil.getFile;
import static helpers.TestsHelper.getAccessToken;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Sql(scripts = {"classpath:/tests_database.sql"})
public class ImportacaoUsuarioControllerTest {
    @Autowired
    private MockMvc mvc;

    @Test
    public void deveRetornarComOk() throws Exception {

        mvc.perform(MockMvcRequestBuilders
                .fileUpload("/api/importacao-usuarios")
                .file(new MockMultipartFile("file", "planilha.xlsx",
                        "application/vnd.ms-excel", getFile("arquivo_usuario/planilha.xlsx")))
                .param("usuarioImportacaoJson",
                        "{\"file\":[{\"preview\":"
                                + "\"blob:http://localhost:3100/5fa6d20c-8b61-4500-a0e9-5c9184e2c36d\"}],"
                                + "\"senhaPadrao\":true}")
                .header("Authorization", getAccessToken(mvc, Usuarios.HELP_DESK))
                .accept(MediaType.ALL_VALUE))
                .andExpect(status().isOk());
    }

    @Test
    public void deveValidarAQuantidadeDeColunasDaPlanilha() throws Exception {

        mvc.perform(MockMvcRequestBuilders
                .fileUpload("/api/importacao-usuarios")
                .file(new MockMultipartFile("file", "planilha_sem_fone.xlsx",
                        "application/vnd.ms-excel", getFile("arquivo_usuario/planilha_sem_fone.xlsx")))
                .param("usuarioImportacaoJson",
                        "{\"file\":[{\"preview\":"
                                + "\"blob:http://localhost:3100/5fa6d20c-8b61-4500-a0e9-5c9184e2c36d\"}],"
                                + "\"senhaPadrao\":true}")
                .header("Authorization", getAccessToken(mvc, Usuarios.HELP_DESK))
                .accept(MediaType.ALL_VALUE))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[*].message",
                        containsInAnyOrder("Erro. Arquivo Inválido.")));
    }

    @Test
    public void deveRetornarErroSeArquivoXlsSejaInvalido() throws Exception {

        mvc.perform(MockMvcRequestBuilders
                .fileUpload("/api/importacao-usuarios")
                .file(new MockMultipartFile("file", "planilha_invertida.xlsx",
                        "application/vnd.ms-excel", getFile("arquivo_usuario/planilha_invertida.xlsx")))
                .param("usuarioImportacaoJson",
                        "{\"file\":[{\"preview\":"
                                + "\"blob:http://localhost:3100/5fa6d20c-8b61-4500-a0e9-5c9184e2c36d\"}],"
                                + "\"senhaPadrao\":true}")
                .header("Authorization", getAccessToken(mvc, Usuarios.HELP_DESK))
                .accept(MediaType.ALL_VALUE))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[*].message",
                        containsInAnyOrder("Erro. Arquivo Inválido.")));
    }

}