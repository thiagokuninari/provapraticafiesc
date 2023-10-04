package br.com.xbrain.autenticacao.modules.usuario.controller;

import br.com.xbrain.autenticacao.config.OAuth2ResourceConfig;
import br.com.xbrain.autenticacao.modules.comum.dto.PageRequest;
import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.comum.enums.Eboolean;
import br.com.xbrain.autenticacao.modules.equipevenda.service.EquipeVendaD2dService;
import br.com.xbrain.autenticacao.modules.usuario.dto.*;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoMotivoInativacao;
import br.com.xbrain.autenticacao.modules.usuario.enums.ECanal;
import br.com.xbrain.autenticacao.modules.usuario.event.UsuarioSubCanalObserver;
import br.com.xbrain.autenticacao.modules.usuario.service.UsuarioService;
import com.google.common.collect.Sets;
import lombok.SneakyThrows;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.MockBeans;
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

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static com.google.common.io.ByteStreams.toByteArray;
import static helpers.TestsHelper.convertObjectToJsonBytes;
import static helpers.TestsHelper.convertObjectToJsonString;
import static helpers.Usuarios.ADMIN;
import static java.util.Collections.singletonList;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@WebMvcTest(controllers = UsuarioGerenciaController.class)
@MockBeans({
    @MockBean(EquipeVendaD2dService.class),
    @MockBean(UsuarioSubCanalObserver.class),
    @MockBean(TokenStore.class),
})
@Import(OAuth2ResourceConfig.class)
public class UsuarioGerenciaControllerTest {

    private static final int ID_USUARIO_HELPDESK = 101;
    private static final String API_URI = "/api/usuarios/gerencia";
    private static final String API_URI_BACKOFFICE = "/api/usuarios/gerencia/backoffice";

    @Autowired
    private MockMvc mvc;
    @MockBean
    private UsuarioService usuarioService;

    @Test
    @SneakyThrows
    @WithMockUser(username = ADMIN, roles = {"AUT_VISUALIZAR_USUARIO"})
    public void save_deveSalvarNovoUsuario_seUsuarioTiverPermissao() {
        var usuario = umUsuario("usuario teste");

        mvc.perform(MockMvcRequestBuilders
                .fileUpload(API_URI)
                .file(umUsuario(usuario))
                .accept(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(usuario)))
            .andExpect(status().isOk());

        verify(usuarioService).save(usuario, null);
    }

    @Test
    @SneakyThrows
    @WithMockUser(username = ADMIN, roles = {"AUT_VISUALIZAR_USUARIO"})
    public void save_deveRetornarBadRequest_seCamposObrigatoriosNaoPreenchidos() {
        mvc.perform(MockMvcRequestBuilders
                .fileUpload(API_URI)
                .file(umUsuario(new UsuarioDto()))
                .contentType(MediaType.MULTIPART_FORM_DATA))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$", hasSize(7)))
            .andExpect(jsonPath("$[*].message", containsInAnyOrder(
                "O campo nome é obrigatório.",
                "O campo email é obrigatório.",
                "O campo unidadesNegociosId é obrigatório.",
                "O campo empresasId é obrigatório.",
                "O campo cargoId é obrigatório.",
                "O campo departamentoId é obrigatório.",
                "O campo loginNetSales may not be empty")));
    }

    @Test
    @SneakyThrows
    @WithMockUser(username = ADMIN, roles = {"AUT_VISUALIZAR_USUARIO"})
    public void save_deveRetornarBadRequest_seCampoNomeEstiverComSizeMaiorQue80() {
        var usuario = umUsuario("Exemplo de um nome grande demais ".repeat(6));

        mvc.perform(MockMvcRequestBuilders
                .fileUpload(API_URI)
                .file(umUsuario(usuario))
                .contentType(MediaType.MULTIPART_FORM_DATA))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[*].message", containsInAnyOrder(
                "O campo nome precisa ter entre 0 e 80 caracteres.")));

        verifyNoMoreInteractions(usuarioService);
    }

    @Test
    @SneakyThrows
    @WithMockUser(username = ADMIN, roles = {"AUT_VISUALIZAR_USUARIO"})
    public void save_deveRetornarBadRequest_seCampoEmailEstiverComSizeMaiorQue80() {
        var usuario = umUsuario("teste");
        usuario.setEmail("Exemplo de um email grande demais ".repeat(8));

        mvc.perform(MockMvcRequestBuilders
                .fileUpload(API_URI)
                .file(umUsuario(usuario))
                .contentType(MediaType.MULTIPART_FORM_DATA))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[*].message", containsInAnyOrder(
                "O campo email precisa ter entre 0 e 80 caracteres.")));

        verifyNoMoreInteractions(usuarioService);
    }

    @Test
    @SneakyThrows
    @WithMockUser(username = ADMIN, roles = {"AUT_VISUALIZAR_USUARIO"})
    public void save_deveRetornarBadRequest_seCampoEmail02EstiverComSizeMaiorQue80() {
        var usuario = umUsuario("teste");
        usuario.setEmail02("Exemplo de um email grande demais ".repeat(8));

        mvc.perform(MockMvcRequestBuilders
                .fileUpload(API_URI)
                .file(umUsuario(usuario))
                .contentType(MediaType.MULTIPART_FORM_DATA))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[*].message", containsInAnyOrder(
                "O campo email02 precisa ter entre 0 e 80 caracteres.")));

        verifyNoMoreInteractions(usuarioService);
    }

    @Test
    @SneakyThrows
    @WithMockUser(username = ADMIN, roles = {"AUT_VISUALIZAR_USUARIO"})
    public void save_deveRetornarBadRequest_seCampoTelefoneEstiverComSizeMaiorQue100() {
        var usuario = umUsuario("teste");
        usuario.setTelefone("Exemplo de um telefone grande demais ".repeat(8));

        mvc.perform(MockMvcRequestBuilders
                .fileUpload(API_URI)
                .file(umUsuario(usuario))
                .contentType(MediaType.MULTIPART_FORM_DATA))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[*].message", containsInAnyOrder(
                "O campo telefone precisa ter entre 0 e 100 caracteres.")));

        verifyNoMoreInteractions(usuarioService);
    }

    @Test
    @SneakyThrows
    @WithMockUser(username = ADMIN, roles = {"AUT_VISUALIZAR_USUARIO"})
    public void save_deveRetornarBadRequest_seCampoCpfEstiverComSizeMaiorQue14() {
        var usuario = umUsuario("teste");
        usuario.setCpf("123456781234567812");

        mvc.perform(MockMvcRequestBuilders
                .fileUpload(API_URI)
                .file(umUsuario(usuario))
                .contentType(MediaType.MULTIPART_FORM_DATA))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[*].message", containsInAnyOrder(
                "O campo cpf precisa ter entre 0 e 14 caracteres.")));

        verifyNoMoreInteractions(usuarioService);
    }

    @Test
    @SneakyThrows
    @WithMockUser(username = ADMIN, roles = {"AUT_VISUALIZAR_USUARIO"})
    public void save_deveRetornarBadRequest_seCampoRgEstiverComSizeMaiorQue25() {
        var usuario = umUsuario("teste");
        usuario.setRg("1234567812345678415462345154264516452164516257");

        mvc.perform(MockMvcRequestBuilders
                .fileUpload(API_URI)
                .file(umUsuario(usuario))
                .contentType(MediaType.MULTIPART_FORM_DATA))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[*].message", containsInAnyOrder(
                "O campo rg precisa ter entre 0 e 25 caracteres.")));

        verifyNoMoreInteractions(usuarioService);
    }

    @Test
    @SneakyThrows
    @WithMockUser(username = ADMIN, roles = {"AUT_VISUALIZAR_USUARIO"})
    public void save_deveRetornarBadRequest_seCampoOrgaoExpedidorEstiverComSizeMaiorQue30() {
        var usuario = umUsuario("teste");
        usuario.setOrgaoExpedidor("Exemplo de orgaoExpedidor grande demais".repeat(6));

        mvc.perform(MockMvcRequestBuilders
                .fileUpload(API_URI)
                .file(umUsuario(usuario))
                .contentType(MediaType.MULTIPART_FORM_DATA))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[*].message", containsInAnyOrder(
                "O campo orgaoExpedidor precisa ter entre 0 e 30 caracteres.")));

        verifyNoMoreInteractions(usuarioService);
    }

    @Test
    @SneakyThrows
    @WithMockUser(username = ADMIN, roles = {"AUT_VISUALIZAR_USUARIO"})
    public void save_deveRetornarBadRequest_seCampoLoginNetSalesEstiverComSizeMaiorQue120() {
        var usuario = umUsuario("teste");
        usuario.setLoginNetSales("Exemplo de um login grande demais".repeat(10));

        mvc.perform(MockMvcRequestBuilders
                .fileUpload(API_URI)
                .file(umUsuario(usuario))
                .contentType(MediaType.MULTIPART_FORM_DATA))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[*].message", containsInAnyOrder(
                "O campo loginNetSales precisa ter entre 0 e 120 caracteres.")));

        verifyNoMoreInteractions(usuarioService);
    }

    @Test
    @SneakyThrows
    @WithMockUser(username = ADMIN, roles = {"AUT_VISUALIZAR_USUARIO"})
    public void save_deveRetornarBadRequest_seCampoNomeEquipeVendaNetSalesEstiverComSizeMaiorQue120() {
        var usuario = umUsuario("teste");
        usuario.setNomeEquipeVendaNetSales("Exemplo de um nome grande demais".repeat(10));

        mvc.perform(MockMvcRequestBuilders
                .fileUpload(API_URI)
                .file(umUsuario(usuario))
                .contentType(MediaType.MULTIPART_FORM_DATA))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[*].message", containsInAnyOrder(
                "O campo nomeEquipeVendaNetSales precisa ter entre 0 e 120 caracteres.")));

        verifyNoMoreInteractions(usuarioService);
    }

    @Test
    @SneakyThrows
    @WithMockUser(username = ADMIN, roles = {"AUT_VISUALIZAR_USUARIO"})
    public void save_deveRetornarBadRequest_seCampoCodigoEquipeVendaNetSalesEstiverComSizeMaiorQue120() {
        var usuario = umUsuario("teste");
        usuario.setCodigoEquipeVendaNetSales("Exemplo de um codigo grande demais".repeat(10));

        mvc.perform(MockMvcRequestBuilders
                .fileUpload(API_URI)
                .file(umUsuario(usuario))
                .contentType(MediaType.MULTIPART_FORM_DATA))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[*].message", containsInAnyOrder(
                "O campo codigoEquipeVendaNetSales precisa ter entre 0 e 120 caracteres.")));

        verifyNoMoreInteractions(usuarioService);
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void save_deveDarUnauthorized_quandoUsuarioNaoTiverPermissao() {
        mvc.perform(post(API_URI)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @SneakyThrows
    @WithMockUser(username = ADMIN, roles = {"CTR_2033"})
    public void save_deveRetornarForbidden_quandoNaoTiverPermissaoParaGerenciaDeUsuario() {
        mvc.perform(post(API_URI)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isForbidden());

        verifyNoMoreInteractions(usuarioService);
    }

    @Test
    @SneakyThrows
    @WithMockUser(username = ADMIN, roles = {"AUT_VISUALIZAR_USUARIO"})
    public void saveBackoffice_deveRetornarUsuarioSalvo_quandoNivelForBackoffice() {
        var usuario = umUsuarioDtoBackoffice();

        mvc.perform(post(API_URI_BACKOFFICE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(usuario)))
            .andExpect(status().isOk());

        verify(usuarioService).salvarUsuarioBackoffice(UsuarioBackofficeDto.of(usuario));
    }

    @Test
    @SneakyThrows
    @WithMockUser(username = ADMIN, roles = {"AUT_VISUALIZAR_USUARIO"})
    public void saveBackoffice_deveRetornarbadRequest_seCamposObrigatoriosNaoPreenchidos() {
        mvc.perform(post(API_URI_BACKOFFICE)
                .content(convertObjectToJsonBytes(new UsuarioBackofficeDto()))
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$", hasSize(6)))
            .andExpect(jsonPath("$[*].message", containsInAnyOrder(
                "O campo nome é obrigatório.",
                "O campo cpf é obrigatório.",
                "O campo nascimento é obrigatório.",
                "O campo email é obrigatório.",
                "O campo cargoId é obrigatório.",
                "O campo departamentoId é obrigatório.")));

        verifyNoMoreInteractions(usuarioService);
    }

    @Test
    @SneakyThrows
    @WithMockUser(username = ADMIN, roles = {"AUT_VISUALIZAR_USUARIO"})
    public void saveBackoffice_deveRetornarBadRequest_seCampoNomeEstiverComSizeMaiorQue80() {
        var usuario = umUsuarioDtoBackoffice();
        usuario.setNome("um exemplo de nome grande demais".repeat(8));

        mvc.perform(post(API_URI_BACKOFFICE)
                .content(convertObjectToJsonBytes(usuario))
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[*].message", containsInAnyOrder(
                "O campo nome precisa ter entre 0 e 80 caracteres.")));

        verifyNoMoreInteractions(usuarioService);
    }

    @Test
    @SneakyThrows
    @WithMockUser(username = ADMIN, roles = {"AUT_VISUALIZAR_USUARIO"})
    public void saveBackoffice_deveRetornarBadRequest_seCampoEmailEstiverComSizeMaiorQue80() {
        var usuario = umUsuarioDtoBackoffice();
        usuario.setEmail("um exemplo de email grande demais".repeat(8));

        mvc.perform(post(API_URI_BACKOFFICE)
                .content(convertObjectToJsonBytes(usuario))
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[*].message", containsInAnyOrder(
                "O campo email precisa ter entre 0 e 80 caracteres.")));

        verifyNoMoreInteractions(usuarioService);
    }

    @Test
    @SneakyThrows
    @WithMockUser(username = ADMIN, roles = {"AUT_VISUALIZAR_USUARIO"})
    public void saveBackoffice_deveRetornarBadRequest_seCampoTelefoneEstiverComSizeMaiorQue100() {
        var usuario = umUsuarioDtoBackoffice();
        usuario.setTelefone("um exemplo de telefone grande demais".repeat(8));

        mvc.perform(post(API_URI_BACKOFFICE)
                .content(convertObjectToJsonBytes(usuario))
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[*].message", containsInAnyOrder(
                "O campo telefone precisa ter entre 0 e 100 caracteres.")));

        verifyNoMoreInteractions(usuarioService);
    }

    @Test
    @SneakyThrows
    @WithMockUser(username = ADMIN, roles = {"AUT_VISUALIZAR_USUARIO"})
    public void saveBackoffice_deveRetornarBadRequest_seCampoCpfEstiverComSizeMaiorQue14() {
        var usuario = umUsuarioDtoBackoffice();
        usuario.setCpf("123456781234567812");

        mvc.perform(post(API_URI_BACKOFFICE)
                .content(convertObjectToJsonBytes(usuario))
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[*].message", containsInAnyOrder(
                "O campo cpf precisa ter entre 0 e 14 caracteres.",
                "O campo cpf não é um cpf válido.")));

        verifyNoMoreInteractions(usuarioService);
    }

    @Test
    @SneakyThrows
    @WithMockUser(username = ADMIN, roles = {"AUT_VISUALIZAR_USUARIO"})
    public void saveBackoffice_deveRetornarBadRequest_seCampoRgEstiverComSizeMaiorQue25() {
        var usuario = umUsuarioDtoBackoffice();
        usuario.setRg("12345678912345678912345566788951");

        mvc.perform(post(API_URI_BACKOFFICE)
                .content(convertObjectToJsonBytes(usuario))
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[*].message", containsInAnyOrder(
                "O campo rg precisa ter entre 0 e 25 caracteres.")));

        verifyNoMoreInteractions(usuarioService);
    }

    @Test
    @SneakyThrows
    @WithMockUser(username = ADMIN, roles = {"CTR_2033"})
    public void save_deveLancarForbidden_seUsuarioNaoTiverPermissao() {
        var usuario = umUsuarioDtoBackoffice();

        mvc.perform(post(API_URI_BACKOFFICE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(usuario)))
            .andExpect(status().isForbidden());

        verifyNoMoreInteractions(usuarioService);
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void save_deveLancarUnauthorized_seUsuarioNaoTiverAutorizacao() {
        var usuario = umUsuarioDtoBackoffice();

        mvc.perform(post(API_URI_BACKOFFICE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(usuario)))
            .andExpect(status().isUnauthorized());

        verifyNoMoreInteractions(usuarioService);
    }

    @Test
    @SneakyThrows
    @WithMockUser(username = ADMIN, roles = {"AUT_VISUALIZAR_USUARIO"})
    public void alterar_deveRetornarOk_seUsuarioTiverPermissao() {
        var usuario = umUsuarioParaAtualizacao("Pedro", 431, 10, 3, "122.861.350-88");

        mvc.perform(put(API_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(usuario)))
            .andExpect(status().isOk());

        verify(usuarioService).save(UsuarioDto.convertFrom(usuario));
    }

    @Test
    @SneakyThrows
    @WithMockUser(username = ADMIN, roles = {"AUT_VISUALIZAR_USUARIO"})
    public void alterar_deveRetornarBadRequest_seCampoNomeEstiverComSizeMaiorQue80() {
        var usuario = umUsuarioParaAtualizacao("Pedro", 431, 10, 3, "122.861.350-88");
        usuario.setNome("Exemplo de um nome grande demais ".repeat(6));

        mvc.perform(put(API_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(usuario)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[*].message", containsInAnyOrder(
                "O campo nome precisa ter entre 0 e 80 caracteres.")));

        verifyNoMoreInteractions(usuarioService);
    }

    @Test
    @SneakyThrows
    @WithMockUser(username = ADMIN, roles = {"AUT_VISUALIZAR_USUARIO"})
    public void alterar_deveRetornarBadRequest_seCampoEmailEstiverComSizeMaiorQue80() {
        var usuario = umUsuarioParaAtualizacao("Pedro", 431, 10, 3, "122.861.350-88");
        usuario.setEmail("Exemplo de um email grande demais ".repeat(6));

        mvc.perform(put(API_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(usuario)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[*].message", containsInAnyOrder(
                "O campo email precisa ter entre 0 e 80 caracteres.")));

        verifyNoMoreInteractions(usuarioService);
    }

    @Test
    @SneakyThrows
    @WithMockUser(username = ADMIN, roles = {"AUT_VISUALIZAR_USUARIO"})
    public void alterar_deveRetornarBadRequest_seCampoEmail02EstiverComSizeMaiorQue80() {
        var usuario = umUsuarioParaAtualizacao("Pedro", 431, 10, 3, "122.861.350-88");
        usuario.setEmail02("Exemplo de um email grande demais ".repeat(6));

        mvc.perform(put(API_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(usuario)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[*].message", containsInAnyOrder(
                "O campo email02 precisa ter entre 0 e 80 caracteres.")));

        verifyNoMoreInteractions(usuarioService);
    }

    @Test
    @SneakyThrows
    @WithMockUser(username = ADMIN, roles = {"AUT_VISUALIZAR_USUARIO"})
    public void alterar_deveRetornarBadRequest_seCampoTelefoneEstiverComSizeMaiorQue100() {
        var usuario = umUsuarioParaAtualizacao("Pedro", 431, 10, 3, "122.861.350-88");
        usuario.setTelefone("Exemplo de um telefone grande demais ".repeat(6));

        mvc.perform(put(API_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(usuario)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[*].message", containsInAnyOrder(
                "O campo telefone precisa ter entre 0 e 100 caracteres.")));

        verifyNoMoreInteractions(usuarioService);
    }

    @Test
    @SneakyThrows
    @WithMockUser(username = ADMIN, roles = {"AUT_VISUALIZAR_USUARIO"})
    public void alterar_deveRetornarBadRequest_seCampoCpfEstiverComSizeMaiorQue14() {
        var usuario = umUsuarioParaAtualizacao("Pedro", 431, 10, 3, "122.861.350-88");
        usuario.setCpf("12345678912345678899");

        mvc.perform(put(API_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(usuario)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[*].message", containsInAnyOrder(
                "O campo cpf precisa ter entre 0 e 14 caracteres.")));

        verifyNoMoreInteractions(usuarioService);
    }

    @Test
    @SneakyThrows
    @WithMockUser(username = ADMIN, roles = {"AUT_VISUALIZAR_USUARIO"})
    public void alterar_deveRetornarBadRequest_seCampoRgEstiverComSizeMaiorQue25() {
        var usuario = umUsuarioParaAtualizacao("Pedro", 431, 10, 3, "122.861.350-88");
        usuario.setRg("123456789123456788992435346436436453356786");

        mvc.perform(put(API_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(usuario)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[*].message", containsInAnyOrder(
                "O campo rg precisa ter entre 0 e 25 caracteres.")));

        verifyNoMoreInteractions(usuarioService);
    }

    @Test
    @SneakyThrows
    @WithMockUser(username = ADMIN, roles = {"AUT_VISUALIZAR_USUARIO"})
    public void alterar_deveRetornarBadRequest_seCampoOrgaoExpedidorEstiverComSizeMaiorQue30() {
        var usuario = umUsuarioParaAtualizacao("Pedro", 431, 10, 3, "122.861.350-88");
        usuario.setOrgaoExpedidor("Exemplo de orgão grande demais".repeat(5));

        mvc.perform(put(API_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(usuario)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[*].message", containsInAnyOrder(
                "O campo orgaoExpedidor precisa ter entre 0 e 30 caracteres.")));

        verifyNoMoreInteractions(usuarioService);
    }

    @Test
    @SneakyThrows
    @WithMockUser(username = ADMIN, roles = {"AUT_VISUALIZAR_USUARIO"})
    public void alterar_deveRetornarBadRequest_seCampologinNetSalesEstiverComSizeMaiorQue120() {
        var usuario = umUsuarioParaAtualizacao("Pedro", 431, 10, 3, "122.861.350-88");
        usuario.setLoginNetSales("Exemplo de loginNetSales grande demais".repeat(20));

        mvc.perform(put(API_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(usuario)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[*].message", containsInAnyOrder(
                "O campo loginNetSales precisa ter entre 0 e 120 caracteres.")));

        verifyNoMoreInteractions(usuarioService);
    }

    @Test
    @SneakyThrows
    @WithMockUser(username = ADMIN, roles = {"AUT_VISUALIZAR_USUARIO"})
    public void alterar_deveRetornarBadRequest_seCamposObrigatoriosNaoPreenchidos() {
        mvc.perform(put(API_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(new UsuarioDto())))
            .andExpect(status().isBadRequest()).andExpect(jsonPath("$", hasSize(7)))
            .andExpect(jsonPath("$[*].message", containsInAnyOrder(
                "O campo email é obrigatório.",
                "O campo nome é obrigatório.",
                "O campo departamentoId é obrigatório.",
                "O campo loginNetSales may not be empty",
                "O campo unidadesNegociosId é obrigatório.",
                "O campo cargoId é obrigatório.",
                "O campo empresasId é obrigatório.")));

        verifyNoMoreInteractions(usuarioService);
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void alterar_deveRetornarUnauthorized_seUsuarioSemAutorizacao() {
        var usuario = umUsuarioParaAtualizacao("Pedro", 431, 10, 3, "122.861.350-88");

        mvc.perform(put(API_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(usuario)))
            .andExpect(status().isUnauthorized());

        verifyNoMoreInteractions(usuarioService);
    }

    @Test
    @SneakyThrows
    @WithMockUser(username = ADMIN, roles = {"CTR_2033"})
    public void alterar_deveRetornarForbidden_seUsuarioSemPermissao() {
        var usuario = umUsuarioParaAtualizacao("Pedro", 431, 10, 3, "122.861.350-88");

        mvc.perform(put(API_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(usuario)))
            .andExpect(status().isForbidden());

        verifyNoMoreInteractions(usuarioService);
    }

    @Test
    @SneakyThrows
    @WithMockUser(username = ADMIN, roles = {"CTR_2033"})
    public void getAll_deveRetornarForbidden_quandoNaoTiverPermissaoParaGerenciaDeUsuario() {
        mvc.perform(get(API_URI)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isForbidden());

        verifyNoMoreInteractions(usuarioService);
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void getAll_deveRetornarUnauthorized_quandoNaoAutorizado() {
        mvc.perform(get(API_URI)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized());

        verifyNoMoreInteractions(usuarioService);
    }

    @Test
    @SneakyThrows
    @WithMockUser(username = ADMIN, roles = {"AUT_VISUALIZAR_USUARIO"})
    public void getAll_deveRetornarTodosOsUsuarios_quandoForAdmin() {
        mvc.perform(get(API_URI)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(usuarioService).getAll(any(PageRequest.class), any(UsuarioFiltros.class));
    }

    @Test
    @SneakyThrows
    @WithMockUser(username = ADMIN, roles = {"AUT_VISUALIZAR_USUARIO"})
    public void getUsuariosCargoSuperior_deveRetornarTodos_porCargoSuperior() {
        mvc.perform(post(API_URI + "/cargo-superior/4")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(
                    UsuarioCargoSuperiorPost
                        .builder()
                        .cidadeIds(List.of(1, 5578))
                        .build())))
            .andExpect(status().isOk());

        verify(usuarioService).getUsuariosCargoSuperior(4, List.of(1, 5578));
    }

    @Test
    @SneakyThrows
    @WithMockUser(username = ADMIN, roles = {"AUT_VISUALIZAR_USUARIO"})
    public void getUsuariosCargoSuperiorByCanal_deveRetornarTodos_porCargoSuperiorEFiltroOrganizacaoId() {
        mvc.perform(post(API_URI + "/cargo-superior/501/INTERNET")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(
                    UsuarioCargoSuperiorPost
                        .builder()
                        .organizacaoId(43)
                        .build())))
            .andExpect(status().isOk());
    }

    @Test
    @SneakyThrows
    @WithMockUser(username = ADMIN, roles = {"AUT_VISUALIZAR_USUARIO"})
    public void getUsuariosCargoSuperiorByCanal_naoDeveRetornar_quandoNaoLocalizarAtravesDeOrganizacaoId() {
        mvc.perform(post(API_URI + "/cargo-superior/501/INTERNET")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(
                    UsuarioCargoSuperiorPost
                        .builder()
                        .organizacaoId(12399)
                        .build())))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void getUsuariosCargoSuperior_deveRetornarUnauthorized_seUsuarioSemAutorizacao() {
        mvc.perform(post(API_URI + "/cargo-superior/4")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(
                    UsuarioCargoSuperiorPost
                        .builder()
                        .cidadeIds(List.of(1, 5578))
                        .build())))
            .andExpect(status().isUnauthorized());

        verifyNoMoreInteractions(usuarioService);
    }

    @Test
    @SneakyThrows
    @WithMockUser(username = ADMIN, roles = {"CTR_2033"})
    public void getUsuariosCargoSuperior_deveRetornarForbidden_seUsuarioSemPermissao() {
        mvc.perform(post(API_URI + "/cargo-superior/4")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(
                    UsuarioCargoSuperiorPost
                        .builder()
                        .cidadeIds(List.of(1, 5578))
                        .build())))
            .andExpect(status().isForbidden());

        verifyNoMoreInteractions(usuarioService);
    }

    @Test
    @SneakyThrows
    @WithMockUser(username = ADMIN, roles = {"AUT_VISUALIZAR_USUARIO"})
    public void listarUsuario_deveRetornarTodosByCnpjAa_quandoFiltrar() {
        mvc.perform(get(API_URI + "?cnpjAa=09.489.617/0001-97")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(usuarioService).getAll(any(PageRequest.class), any(UsuarioFiltros.class));
    }

    @Test
    @SneakyThrows
    @WithMockUser(username = ADMIN, roles = {"CTR_2033"})
    public void listarUsuario_deveRetornarForbidden_seUsuarioSemPermissao() {
        mvc.perform(get(API_URI + "?cnpjAa=09.489.617/0001-97")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isForbidden());

        verifyNoMoreInteractions(usuarioService);
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void listarUsuario_deveRetornarUnauthorized_seUsuarioSemAutorizacao() {
        mvc.perform(get(API_URI + "?cnpjAa=09.489.617/0001-97")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized());

        verifyNoMoreInteractions(usuarioService);
    }

    @Test
    @SneakyThrows
    @WithMockUser(username = ADMIN, roles = {"AUT_VISUALIZAR_USUARIO"})
    public void save_deveSalvarSemFoto_quandoSolicitado() {
        var usuario = umUsuario("JOAO");

        mvc.perform(MockMvcRequestBuilders
                .fileUpload(API_URI)
                .file(umUsuario(usuario))
                .contentType(MediaType.MULTIPART_FORM_DATA))
            .andExpect(status().isOk());

        verify(usuarioService).save(usuario, null);
    }

    @Test
    @SneakyThrows
    @WithMockUser(username = ADMIN, roles = {"AUT_VISUALIZAR_USUARIO"})
    public void save_deveSalvarComFoto_quandoSolicitado() {
        var usuario = umUsuario("JOAO");
        var foto = umFileFoto();

        mvc.perform(MockMvcRequestBuilders
                .fileUpload(API_URI)
                .file(foto)
                .file(umUsuario(usuario))
                .contentType(MediaType.MULTIPART_FORM_DATA))
            .andExpect(status().isOk());

        verify(usuarioService).save(usuario, foto);
    }

    @Test
    @SneakyThrows
    @WithMockUser(username = ADMIN, roles = {"AUT_VISUALIZAR_USUARIO"})
    public void getById_deveSalvarComFoto_quandoSolicitado() {
        mvc.perform(get(API_URI + "/1")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(usuarioService).getUsuarioById(1);
    }

    @Test
    @SneakyThrows
    @WithMockUser(username = ADMIN, roles = {"CTR_2033"})
    public void getById_deveRetornarForbidden_quandoSemPermissao() {
        mvc.perform(get(API_URI + "/1")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isForbidden());

        verifyNoMoreInteractions(usuarioService);
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void getById_deveRetornarUnauthorized_quandoUsuarioSemAutorizacao() {
        mvc.perform(get(API_URI + "/1")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized());

        verifyNoMoreInteractions(usuarioService);
    }

    @Test
    @SneakyThrows
    @WithMockUser(username = ADMIN, roles = {"AUT_VISUALIZAR_USUARIO"})
    public void saveConfiguracao_deveSalvarAConfiguracaoDoUsuario_seUsuarioPossuirPermissao() {
        var dto = new UsuarioConfiguracaoSaveDto();
        dto.setUsuarioId(ID_USUARIO_HELPDESK);
        dto.setRamal(1234);

        mvc.perform(post(API_URI + "/configuracao")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(dto)))
            .andExpect(status().isOk());

        verify(usuarioService).saveUsuarioConfiguracao(dto);
    }

    @Test
    @SneakyThrows
    @WithMockUser(username = ADMIN, roles = {"CTR_2033"})
    public void saveConfiguracao_deveRetornarForbidden_seUsuarioSemPermissao() {
        var dto = new UsuarioConfiguracaoSaveDto();
        dto.setUsuarioId(ID_USUARIO_HELPDESK);
        dto.setRamal(1234);

        mvc.perform(post(API_URI + "/configuracao")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(dto)))
            .andExpect(status().isForbidden());

        verifyNoMoreInteractions(usuarioService);
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void saveConfiguracao_deveRetornarUnauthorized_seUsuarioSemAutorizacao() {
        var dto = new UsuarioConfiguracaoSaveDto();
        dto.setUsuarioId(ID_USUARIO_HELPDESK);
        dto.setRamal(1234);

        mvc.perform(post(API_URI + "/configuracao")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(dto)))
            .andExpect(status().isUnauthorized());

        verifyNoMoreInteractions(usuarioService);
    }

    @Test
    @SneakyThrows
    @WithMockUser(username = ADMIN, roles = {"AUT_VISUALIZAR_USUARIO"})
    public void saveConfiguracao_deveRetornarBadRequest_quandoDadosObrigatoriosNaoPreenchidos() {
        var dto = new UsuarioConfiguracaoSaveDto();

        mvc.perform(post(API_URI + "/configuracao")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(dto)))
            .andExpect(status().isBadRequest()).andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[*].message", containsInAnyOrder(
                "O campo usuarioId é obrigatório.",
                "O campo ramal é obrigatório.")));

        verifyNoMoreInteractions(usuarioService);
    }

    @Test
    @SneakyThrows
    @WithMockUser(username = ADMIN, roles = {"AUT_VISUALIZAR_USUARIO"})
    public void inativar_deveInativarUsuario_seUsuarioPossuirAutorizacao() {
        mvc.perform(post(API_URI + "/inativar")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(umUsuarioParaInativar())))
            .andExpect(status().isOk());

        verify(usuarioService).inativar(umUsuarioParaInativar());
    }

    @Test
    @SneakyThrows
    @WithMockUser(username = ADMIN, roles = {"AUT_VISUALIZAR_USUARIO"})
    public void inativar_deveRetornarBadRequest_seCamposObrigatoriosNaoPreenchidos() {
        mvc.perform(post(API_URI + "/inativar")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(new UsuarioInativacaoDto())))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[*].message", containsInAnyOrder(
                "O campo idUsuario é obrigatório.")));

        verifyNoMoreInteractions(usuarioService);
    }

    @Test
    @SneakyThrows
    @WithMockUser(username = ADMIN, roles = {"AUT_VISUALIZAR_USUARIO"})
    public void inativar_deveRetornarBadRequest_seAlgumCampoEstiverComSizeMaiorQueOPermitido() {
        var usuarioInativacao = umUsuarioParaInativar();
        usuarioInativacao.setObservacao("exemplo de uma descrição grande".repeat(20));

        mvc.perform(post(API_URI + "/inativar")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(new UsuarioInativacaoDto())))
            .andExpect(status().isBadRequest());

        verifyNoMoreInteractions(usuarioService);
    }

    @Test
    @SneakyThrows
    @WithMockUser(username = ADMIN, roles = {"CTR_2033"})
    public void inativar_deveRetornarForbidden_seUsuarioSemPermissao() {
        mvc.perform(post(API_URI + "/inativar")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(umUsuarioParaInativar())))
            .andExpect(status().isForbidden());

        verifyNoMoreInteractions(usuarioService);
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void inativar_deveRetornarUnauthorized_seUsuarioSemAutorizacao() {
        mvc.perform(post(API_URI + "/inativar")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(umUsuarioParaInativar())))
            .andExpect(status().isUnauthorized());

        verifyNoMoreInteractions(usuarioService);
    }

    @Test
    @SneakyThrows
    @WithMockUser(username = ADMIN, roles = {"AUT_VISUALIZAR_USUARIO"})
    public void ativar_deveAtivarUsuario_seUsuarioPossuirAutorizacao() {
        mvc.perform(put(API_URI + "/ativar")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(umUsuarioParaAtivar())))
            .andExpect(status().isOk());

        verify(usuarioService).ativar(any(UsuarioAtivacaoDto.class));
    }

    @Test
    @SneakyThrows
    @WithMockUser(username = ADMIN, roles = {"AUT_VISUALIZAR_USUARIO"})
    public void ativar_deveRetornarBadRequest_seCamposObrigatoriosNaoPreenchidos() {
        mvc.perform(put(API_URI + "/ativar")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(new UsuarioAtivacaoDto())))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[*].message", containsInAnyOrder(
                "O campo idUsuario é obrigatório.")));

        verifyNoMoreInteractions(usuarioService);
    }

    @Test
    @SneakyThrows
    @WithMockUser(username = ADMIN, roles = {"AUT_VISUALIZAR_USUARIO"})
    public void ativar_deveRetornarBadRequest_seAlgumCampoEstiverComSizeMaiorQueOPermitido() {
        var usuarioAtivacao = umUsuarioParaAtivar();
        usuarioAtivacao.setObservacao("exemplo de uma descrição grande".repeat(20));

        mvc.perform(put(API_URI + "/ativar")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(usuarioAtivacao)))
            .andExpect(status().isBadRequest());

        verifyNoMoreInteractions(usuarioService);
    }

    @Test
    @SneakyThrows
    @WithMockUser(username = ADMIN, roles = {"CTR_2033"})
    public void ativar_deveRetornarForbidden_seUsuarioSemPermissao() {
        mvc.perform(put(API_URI + "/ativar")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(umUsuarioParaInativar())))
            .andExpect(status().isForbidden());

        verifyNoMoreInteractions(usuarioService);
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void ativar_deveRetornarUnauthorized_seUsuarioSemAutorizacao() {
        mvc.perform(put(API_URI + "/ativar")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(umUsuarioParaInativar())))
            .andExpect(status().isUnauthorized());

        verifyNoMoreInteractions(usuarioService);
    }

    @Test
    @SneakyThrows
    @WithMockUser(username = ADMIN, roles = {"AUT_VISUALIZAR_USUARIO"})
    public void alterarSenhaEReenviarPorEmail_deveAlterarASenhaDoUsuario_seUsuarioPossuirPermissao() {
        mvc.perform(put(API_URI + "/100/senha")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(usuarioService).alterarSenhaEReenviarPorEmail(anyInt());
    }

    @Test
    @SneakyThrows
    @WithMockUser(username = ADMIN, roles = {"CTR_2033"})
    public void alterarSenhaEReenviarPorEmail_deveRetornarForbidden_seUsuarioSemPermissao() {
        mvc.perform(put(API_URI + "/100/senha")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isForbidden());

        verifyNoMoreInteractions(usuarioService);
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void alterarSenhaEReenviarPorEmail_deveRetornarUnauthorized_seUsuarioSemAutorizacao() {
        mvc.perform(put(API_URI + "/100/senha")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized());

        verifyNoMoreInteractions(usuarioService);
    }

    @Test
    @SneakyThrows
    @WithMockUser(username = ADMIN, roles = {"AUT_VISUALIZAR_USUARIO"})
    public void getFuncionalidadeByUsuario_deveRetornarPermissoes_seUsuarioPossuirPermissao() {
        mvc.perform(get(API_URI + "/100/permissoes")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(usuarioService).findPermissoesByUsuario(anyInt());
    }

    @Test
    @SneakyThrows
    @WithMockUser(username = ADMIN, roles = {"CTR_2033"})
    public void getFuncionalidadeByUsuario_deveRetornarForbidden_seUsuarioSemPermissao() {
        mvc.perform(get(API_URI + "/100/permissoes")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isForbidden());

        verifyNoMoreInteractions(usuarioService);
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void getFuncionalidadeByUsuario_deveRetornarUnauthorized_seUsuarioSemAutorizacao() {
        mvc.perform(get(API_URI + "/100/permissoes")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized());

        verifyNoMoreInteractions(usuarioService);
    }

    @Test
    @SneakyThrows
    @WithMockUser(username = ADMIN, roles = {"AUT_VISUALIZAR_USUARIO"})
    public void getCidadesByUsuario_deveRetornarCidadesAtreladasAoUsuario_seUsuarioPossuirPermissao() {
        mvc.perform(get(API_URI + "/100/cidades")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(usuarioService).getCidadeByUsuario(anyInt());
    }

    @Test
    @SneakyThrows
    @WithMockUser(username = ADMIN, roles = {"CTR_2033"})
    public void getCidadesByUsuario_deveRetornarForbidden_seUsuarioSemPermissao() {
        mvc.perform(get(API_URI + "/100/cidades")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isForbidden());

        verifyNoMoreInteractions(usuarioService);
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void getCidadesByUsuario_deveRetornarUnauthorized_seUsuarioSemAutorizacao() {
        mvc.perform(get(API_URI + "/100/cidades")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized());

        verifyNoMoreInteractions(usuarioService);
    }

    @Test
    @SneakyThrows
    @WithMockUser(username = ADMIN, roles = {"AUT_VISUALIZAR_USUARIO"})
    public void alterarDadosAcessoEmail_deveAlterarOEmailDoUsuario_seUsuarioPossuirPermissao() {
        mvc.perform(put(API_URI + "/acesso/email")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(umRequestDadosAcessoEmail())))
            .andExpect(status().isOk());

        verify(usuarioService).alterarDadosAcessoEmail(umRequestDadosAcessoEmail());
    }

    @Test
    @SneakyThrows
    @WithMockUser(username = ADMIN, roles = {"CTR_2033"})
    public void alterarDadosAcessoEmail_deveRetornarForbidden_seUsuarioSemPermissao() {
        mvc.perform(put(API_URI + "/acesso/email")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(umRequestDadosAcessoEmail())))
            .andExpect(status().isForbidden());

        verifyNoMoreInteractions(usuarioService);
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void alterarDadosAcessoEmail_deveRetornarUnauthorized_seUsuarioSemAutorizacao() {
        mvc.perform(put(API_URI + "/acesso/email")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(umRequestDadosAcessoEmail())))
            .andExpect(status().isUnauthorized());

        verifyNoMoreInteractions(usuarioService);
    }

    @Test
    @SneakyThrows
    public void alterarDadosAcessoSenha_deveAlterarASenhaDoUsuario_seSolicitado() {
        mvc.perform(put(API_URI + "/acesso/senha")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(umRequestDadosAcessoSenha())))
            .andExpect(status().isOk());

        verify(usuarioService).alterarDadosAcessoSenha(umRequestDadosAcessoSenha());
    }

    @Test
    @SneakyThrows
    public void getUsuarioSuperior_deveRetornarOSuperiorDoUsuario_seSolicitado() {
        mvc.perform(get(API_URI + "/101/supervisor")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(usuarioService).getUsuarioSuperior(101);
    }

    @Test
    @SneakyThrows
    @WithMockUser(username = ADMIN, roles = {"AUT_VISUALIZAR_USUARIO"})
    public void getUsuarioSuperiores_deveRetornarOSuperioresDoUsuario_seUsuarioPossuirPermissao() {
        mvc.perform(get(API_URI + "/101/supervisores")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(usuarioService).getUsuarioSuperiores(101);
    }

    @Test
    @SneakyThrows
    @WithMockUser(username = ADMIN, roles = {"CTR_2033"})
    public void getUsuarioSuperiores_deveRetornarForbidden_seUsuarioSemPermissao() {
        mvc.perform(get(API_URI + "/101/supervisores")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isForbidden());

        verifyNoMoreInteractions(usuarioService);
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void getUsuarioSuperiores_deveRetornarUnauthorized_seUsuarioSemAutorizacao() {
        mvc.perform(get(API_URI + "/101/supervisores")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized());

        verifyNoMoreInteractions(usuarioService);
    }

    @Test
    @SneakyThrows
    @WithMockUser(username = ADMIN, roles = {"AUT_VISUALIZAR_USUARIO"})
    public void getCsv_deveRetornarCsvFormatadoCorretamente_seUsuarioPossuirPermissao() {
        mvc.perform(get(API_URI + "/csv")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(usuarioService).exportUsuariosToCsv(any(UsuarioFiltros.class), any(HttpServletResponse.class));
    }

    @Test
    @SneakyThrows
    @WithMockUser(username = ADMIN, roles = {"CTR_2033"})
    public void getCsv_deveRetornarForbidden_seUsuarioSemPermissao() {
        mvc.perform(get(API_URI + "/csv")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isForbidden());

        verifyNoMoreInteractions(usuarioService);
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void getCsv_deveRetornarUnauthorized_seUsuarioSemAutorizacao() {
        mvc.perform(get(API_URI + "/csv")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized());

        verifyNoMoreInteractions(usuarioService);
    }

    @Test
    @SneakyThrows
    @WithMockUser(username = ADMIN, roles = {"AUT_VISUALIZAR_USUARIO"})
    public void validarSeUsuarioNovoCadastro_deveValidarSeENovoCadastro_quandoSolicitado() {
        mvc.perform(get(API_URI + "/existir/usuario")
                .param("email", "JOHN@GMAIL.COM")
                .param("cpf", "48503182076"))
            .andExpect(status().isOk());

        verify(usuarioService)
            .validarSeUsuarioCpfEmailNaoCadastrados(any(UsuarioExistenteValidacaoRequest.class));
    }

    @Test
    @SneakyThrows
    @WithMockUser(username = ADMIN, roles = {"AUT_VISUALIZAR_USUARIO"})
    public void getUsuariosHierarquia_deveBuscarUsuariosHierarquia_seUsuarioAutenticado() {
        mvc.perform(get(API_URI + "/hierarquia/1"))
            .andExpect(status().isOk());

        verify(usuarioService).getUsuariosHierarquia(1);
    }

    @Test
    @SneakyThrows
    @WithMockUser(username = ADMIN, roles = {"CTR_2033"})
    public void getUsuariosHierarquia_deveRetornarForbidden_seUsuarioSemPermissao() {
        mvc.perform(get(API_URI + "/hierarquia/1"))
            .andExpect(status().isForbidden());

        verifyNoMoreInteractions(usuarioService);
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void getUsuariosHierarquia_deveRetornarUnauthorized_seUsuarioSemAutorizacao() {
        mvc.perform(get(API_URI + "/hierarquia/1"))
            .andExpect(status().isUnauthorized());

        verifyNoMoreInteractions(usuarioService);
    }

    @Test
    @SneakyThrows
    @WithMockUser(username = ADMIN, roles = {"AUT_VISUALIZAR_USUARIO"})
    public void getByEmail_deveBuscarUsuarioPorEmail_seUsuarioAutenticado() {
        mvc.perform(get(API_URI)
                .param("email", "teste"))
            .andExpect(status().isOk());

        verify(usuarioService).findByEmail("teste");
    }

    @Test
    @SneakyThrows
    @WithMockUser(username = ADMIN, roles = {"CTR_2033"})
    public void getByEmail_deveRetornarForbidden_seUsuarioSemPermissao() {
        mvc.perform(get(API_URI)
                .param("email", "teste"))
            .andExpect(status().isForbidden());

        verifyNoMoreInteractions(usuarioService);
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void getByEmail_deveRetornarUnauthorized_seUsuarioSemAutorizacao() {
        mvc.perform(get(API_URI)
                .param("email", "teste"))
            .andExpect(status().isUnauthorized());

        verifyNoMoreInteractions(usuarioService);
    }

    @Test
    @SneakyThrows
    @WithMockUser(username = ADMIN, roles = {"AUT_VISUALIZAR_USUARIO"})
    public void limparCpf_deveLimparCpfUsuario_seUsuarioAutenticado() {
        mvc.perform(put(API_URI + "/limpar-cpf/1"))
            .andExpect(status().isOk());

        verify(usuarioService).limparCpfUsuario(1);
    }

    @Test
    @SneakyThrows
    @WithMockUser(username = ADMIN, roles = {"CTR_2033"})
    public void limparCpf_deveRetornarForbidden_seUsuarioSemPermissao() {
        mvc.perform(put(API_URI + "/limpar-cpf/1"))
            .andExpect(status().isForbidden());

        verifyNoMoreInteractions(usuarioService);
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void limparCpf_deveRetornarUnauthorized_seUsuarioSemAutorizacao() {
        mvc.perform(put(API_URI + "/limpar-cpf/1"))
            .andExpect(status().isUnauthorized());

        verifyNoMoreInteractions(usuarioService);
    }

    private UsuarioDadosAcessoRequest umRequestDadosAcessoEmail() {
        var dto = new UsuarioDadosAcessoRequest();
        dto.setUsuarioId(101);
        dto.setEmailAtual("HELPDESK@XBRAIN.COM.BR");
        dto.setEmailNovo("NOVOEMAIL@XBRAIN.COM.BR");
        return dto;
    }

    private UsuarioDadosAcessoRequest umRequestDadosAcessoSenha() {
        var dto = new UsuarioDadosAcessoRequest();
        dto.setUsuarioId(101);
        dto.setAlterarSenha(Eboolean.V);
        dto.setSenhaAtual("123456");
        dto.setSenhaNova("654321");
        dto.setIgnorarSenhaAtual(Boolean.FALSE);
        return dto;
    }

    private UsuarioInativacaoDto umUsuarioParaInativar() {
        var dto = new UsuarioInativacaoDto();
        dto.setIdUsuario(ID_USUARIO_HELPDESK);
        dto.setObservacao("Teste inativação");
        dto.setCodigoMotivoInativacao(CodigoMotivoInativacao.FERIAS);
        return dto;
    }

    private UsuarioAtivacaoDto umUsuarioParaAtivar() {
        var dto = new UsuarioAtivacaoDto();
        dto.setIdUsuario(ID_USUARIO_HELPDESK);
        dto.setObservacao("Teste inativação");
        return dto;
    }

    private UsuarioDto umUsuarioParaEditar() {
        return UsuarioDto.builder()
            .nome("JOAOZINHO")
            .loginNetSales("MIDORIYA SHOUNEN")
            .build();
    }

    private UsuarioDto umUsuario(String nome) {
        var usuario = new UsuarioDto();
        usuario.setNome(nome);
        usuario.setCargoId(2);
        usuario.setDepartamentoId(1);
        usuario.setCpf("097.238.645-92");
        usuario.setUnidadesNegociosId(List.of(1));
        usuario.setEmpresasId(singletonList(4));
        usuario.setEmail("usuario@teste.com");
        usuario.setTelefone("43 995565661");
        usuario.setHierarquiasId(List.of(100));
        usuario.setCidadesId(Arrays.asList(736, 2921, 527));
        usuario.setLoginNetSales("MIDORIYA SHOUNEN");
        usuario.setCanais(Sets.newHashSet(ECanal.AGENTE_AUTORIZADO, ECanal.D2D_PROPRIO));
        usuario.setSubCanaisId(Sets.newHashSet(1));
        usuario.setNomeEquipeVendaNetSales("EQUIPE NET");
        usuario.setCodigoEquipeVendaNetSales("654321");
        return usuario;
    }

    private MockMultipartFile umUsuario(UsuarioDto usuario) {
        byte[] json = convertObjectToJsonString(usuario).getBytes(StandardCharsets.UTF_8);
        return new MockMultipartFile("usuario", "json", "application/json", json);
    }

    private UsuarioBackofficeDto umUsuarioDtoBackoffice() {
        var usuario = new UsuarioBackofficeDto();
        usuario.setNome("USUARIO BACKOFFICE");
        usuario.setCargoId(110);
        usuario.setEmail("usuarioBackoffice@gmail.com");
        usuario.setDepartamentoId(69);
        usuario.setCpf("870.371.018-18");
        usuario.setNascimento(LocalDate.of(2000, 1, 1));
        return usuario;
    }

    private UsuarioDto umUsuarioParaAtualizacao(String nome, Integer id, Integer cargo, Integer departamento, String cpf) {
        var usuario = new UsuarioDto();
        usuario.setId(id);
        usuario.setNome(nome);
        usuario.setCargoId(cargo);
        usuario.setDepartamentoId(departamento);
        usuario.setCpf(cpf);
        usuario.setUnidadesNegociosId(List.of(1));
        usuario.setEmpresasId(singletonList(4));
        usuario.setEmail("usuario@teste.com");
        usuario.setTelefone("43 995565661");
        usuario.setHierarquiasId(List.of(100));
        usuario.setCidadesId(Arrays.asList(736, 2921, 527));
        usuario.setLoginNetSales("MIDORIYA SHOUNEN");
        usuario.setCanais(Sets.newHashSet(ECanal.D2D_PROPRIO));
        usuario.setSituacao(ESituacao.A);
        usuario.setSubCanaisId(Sets.newHashSet(1));
        usuario.setNomeEquipeVendaNetSales("EQUIPE NET");
        usuario.setCodigoEquipeVendaNetSales("654321");
        return usuario;
    }

    private MockMultipartFile umFileFoto() throws Exception {
        var bytes = toByteArray(getFileInputStream("foto_usuario/file.png"));
        return new MockMultipartFile("foto",
            LocalDateTime.now().toString().concat("file.png"),
            "image/png",
            bytes);
    }

    private InputStream getFileInputStream(String file) throws Exception {
        return new ByteArrayInputStream(
            Files.readAllBytes(Paths.get(
                getClass().getClassLoader().getResource(file)
                    .getPath())));
    }
}
