package br.com.xbrain.autenticacao.modules.permissao.controller;

import br.com.xbrain.autenticacao.config.OAuth2ResourceConfig;
import br.com.xbrain.autenticacao.modules.equipevenda.service.EquipeVendaD2dService;
import br.com.xbrain.autenticacao.modules.gestaocolaboradorespol.client.ColaboradorVendasClient;
import br.com.xbrain.autenticacao.modules.permissao.dto.PermissaoEspecialRequest;
import br.com.xbrain.autenticacao.modules.permissao.service.PermissaoEspecialService;
import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioDto;
import br.com.xbrain.autenticacao.modules.usuario.event.UsuarioSubCanalObserver;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.util.List;

import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@WebMvcTest(PermissaoEspecialController.class)
@MockBeans({
    @MockBean(EquipeVendaD2dService.class),
    @MockBean(TokenStore.class),
    @MockBean(UsuarioSubCanalObserver.class),
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
    @MockBean
    private ColaboradorVendasClient colaboradorVendasClient;

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

        verify(colaboradorVendasClient, times(0)).getUsuariosAaFeederPorCargo(anyList(), anyList());
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

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void reprocessarPermissoesEspeciaisSociosSecundarios_deveRetornarUnauthorized_quandoTokenInvalido() {
        mvc.perform(post(URL + "/reprocessar-permissoes-socios-secundarios")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized());

        verifyZeroInteractions(permissaoEspecialService);
    }

    @Test
    @SneakyThrows
    @WithMockUser
    public void reprocessarPermissoesEspeciaisSociosSecundarios_deveRetornarOk_quandoListaVazia() {
        mvc.perform(post(URL + "/reprocessar-permissoes-socios-secundarios")
                .param("aaIds", "")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(permissaoEspecialService).reprocessarPermissoesEspeciaisSociosSecundarios(List.of());
    }

    @Test
    @SneakyThrows
    @WithMockUser
    public void reprocessarPermissoesEspeciaisSociosSecundarios_deveRetornarOk_quandoListaPreenchida() {
        mvc.perform(post(URL + "/reprocessar-permissoes-socios-secundarios")
                .param("aaIds", "123")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(permissaoEspecialService).reprocessarPermissoesEspeciaisSociosSecundarios(List.of(123));
    }

    @Test
    @SneakyThrows
    @WithMockUser
    public void reprocessarPermissoesEspeciaisSociosSecundarios_deveRetornarOk_quandoListaNull() {
        mvc.perform(post(URL + "/reprocessar-permissoes-socios-secundarios")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(permissaoEspecialService).reprocessarPermissoesEspeciaisSociosSecundarios(null);
    }

    @Test
    @SneakyThrows
    @WithMockUser
    public void atualizarPermissoesEspeciaisNovoSocioPrincipal_deveRetornarOk_seTodosOsDadosCorreto() {
        var objectMapper = new ObjectMapper();
        var requestBody = objectMapper.writeValueAsString(umUsuarioDto());

        mvc.perform(put(URL + "/atualizar-permissoes-novo-socio")
                .accept(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(permissaoEspecialService).atualizarPermissoesEspeciaisNovoSocioPrincipal(umUsuarioDto());
    }

    @Test
    @SneakyThrows
    @WithMockUser
    public void atualizarPermissoesEspeciaisNovoSocioPrincipal_deveRetornarBadRequest_quandoBodyForVazio() {
        mvc.perform(put(URL + "/atualizar-permissoes-novo-socio")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());

        verifyZeroInteractions(permissaoEspecialService);
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void atualizarPermissoesEspeciaisNovoSocioPrincipal_deveRetornarUnauthorized_quandoUsuarioNaoAutenticado() {
        mvc.perform(put(URL + "/atualizar-permissoes-novo-socio")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized());

        verifyZeroInteractions(permissaoEspecialService);
    }

    private UsuarioDto umUsuarioDto() {
        return UsuarioDto.builder()
            .id(1)
            .nome("USUARIO")
            .usuarioCadastroId(2)
            .agenteAutorizadoId(1)
            .antigosSociosPrincipaisIds(List.of(1, 2))
            .build();
    }

    private PermissaoEspecialRequest novasPermissoes() {
        PermissaoEspecialRequest request = new PermissaoEspecialRequest();
        request.setUsuarioId(1);
        request.setFuncionalidadesIds(Arrays.asList(1, 2, 3, 4));
        return request;
    }
}
