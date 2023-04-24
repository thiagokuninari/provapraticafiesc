package br.com.xbrain.autenticacao.modules.permissao.controller;

import br.com.xbrain.autenticacao.config.OAuth2ResourceConfig;
import br.com.xbrain.autenticacao.modules.equipevenda.service.EquipeVendaD2dService;
import br.com.xbrain.autenticacao.modules.permissao.dto.PermissaoEspecialRequest;
import br.com.xbrain.autenticacao.modules.permissao.service.PermissaoEspecialService;
import helpers.TestsHelper;
import lombok.SneakyThrows;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.MockBeans;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@WebMvcTest(PermissaoEspecialController.class)
@MockBeans({
    @MockBean(EquipeVendaD2dService.class),
    @MockBean(TokenStore.class),
})
@Import(OAuth2ResourceConfig.class)
public class PermissaoEspecialControllerTest {

    private static final String URL = "/api/permissoes-especiais";
    private static final String USUARIO_SOCIO = "USUARIO SOCIO";
    private static final String USUARIO_ADMIN = "USUARIO ADMIN";
    private static final String AUT_VISUALIZAR_USUARIO = "AUT_VISUALIZAR_USUARIO";
    private static final String AUT_GER_PERMISSAO_ESPECIAL_USUARIO = "AUT_GER_PERMISSAO_ESPECIAL_USUARIO";

    @Autowired
    private MockMvc mvc;
    @MockBean
    private PermissaoEspecialService permissaoEspecialService;

    @Test
    @SneakyThrows
    public void getAll_unauthorized_quandoNaoPassarAToken() {
        mvc.perform(get(URL)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @SneakyThrows
    @WithMockUser(username = USUARIO_SOCIO, roles = { AUT_VISUALIZAR_USUARIO })
    public void save_forbidden_quandoNaoTiverPermissaoParaPermissaoEspecialUsuario() {
        mvc.perform(post(URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestsHelper.convertObjectToJsonBytes(novasPermissoes())))
            .andExpect(status().isForbidden());
    }

    @Test
    @SneakyThrows
    @WithMockUser(username = USUARIO_ADMIN, roles = { AUT_GER_PERMISSAO_ESPECIAL_USUARIO })
    public void save_deveSalvarPermissoesEspeciais_seUsuarioTiverPermissaoParaPermissaoEspecialUsuario() {
        mvc.perform(post(URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestsHelper.convertObjectToJsonBytes(novasPermissoes())))
            .andExpect(status().isOk());
    }

    @Test
    @SneakyThrows
    @WithMockUser
    public void remover_deveRemoverUmaPermissao_seUsuarioAutenticado() {
        mvc.perform(put(URL + "/remover/{usuarioId}/{funcionalidadeId}",101, 26)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void processarPermissoesEspeciaisGerentesCoordenadores_deveRetornarUnauthorized_quandoUsuarioNaoAutenticado() {
        mvc.perform(post(URL + "/processar-permissoes-gerentes-coordenadores")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @SneakyThrows
    @WithMockUser
    public void processarPermissoesEspeciaisGerentesCoordenadores_deveRetornarOk_seListaIdsVazia() {
        mvc.perform(post(URL + "/processar-permissoes-gerentes-coordenadores")
                .param("aaIds", "")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }

    private PermissaoEspecialRequest novasPermissoes() {
        PermissaoEspecialRequest request = new PermissaoEspecialRequest();
        request.setUsuarioId(1);
        request.setFuncionalidadesIds(Arrays.asList(1, 2, 3, 4));
        return request;
    }
}
