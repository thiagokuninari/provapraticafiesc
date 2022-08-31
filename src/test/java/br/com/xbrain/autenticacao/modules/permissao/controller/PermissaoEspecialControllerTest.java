package br.com.xbrain.autenticacao.modules.permissao.controller;

import br.com.xbrain.autenticacao.modules.parceirosonline.service.AgenteAutorizadoClient;
import br.com.xbrain.autenticacao.modules.permissao.dto.PermissaoEspecialRequest;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo;
import helpers.TestsHelper;
import lombok.SneakyThrows;
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

import java.util.Arrays;
import java.util.List;

import static helpers.TestsHelper.getAccessToken;
import static helpers.Usuarios.ADMIN;
import static helpers.Usuarios.SOCIO_AA;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Sql(scripts = "classpath:/tests_database.sql")
public class PermissaoEspecialControllerTest {

    private static final String URL = "/api/permissoes-especiais";

    @Autowired
    private MockMvc mvc;

    @MockBean
    private AgenteAutorizadoClient agenteAutorizadoClient;

    @Test
    public void getAll_unauthorized_quandoNaoPassarAToken() throws Exception {
        mvc.perform(get(URL)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized());
    }

    @Test
    public void save_forbidden_quandoNaoTiverPermissaoParaPermissoesEspeciais() throws Exception {
        mvc.perform(post(URL)
                .header("Authorization", getAccessToken(mvc, SOCIO_AA))
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestsHelper.convertObjectToJsonBytes(novasPermissoes())))
                .andExpect(status().isForbidden());
    }

    @Test
    public void deveSalvar() throws Exception {
        mvc.perform(post("/api/permissoes-especiais")
                .header("Authorization", getAccessToken(mvc, ADMIN))
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestsHelper.convertObjectToJsonBytes(novasPermissoes())))
                .andExpect(status().isOk());
    }

    @Test
    public void deveRemoverUmaPermissao() throws Exception {
        mvc.perform(put("/api/permissoes-especiais/remover/101/26")
                .header("Authorization", getAccessToken(mvc, ADMIN))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.dataBaixa", notNullValue()))
            .andExpect(jsonPath("$.usuarioBaixa", notNullValue()));
    }

    @Test
    @SneakyThrows
    public void processarPermissoesEspeciaisGerentesCoordenadores_deveRetornarUnauthorized_quandoNaoPassarAToken() {
        mvc.perform(post(URL + "/processar-permissoes-gerentes-coordenadores")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized());

        verify(agenteAutorizadoClient, times(0)).getAaFeederPorCargo(anyList());
    }

    @Test
    @SneakyThrows
    public void processarPermissoesEspeciaisGerentesCoordenadores_deveRetornarOk_quandoSolicitar() {
        when(agenteAutorizadoClient.getAaFeederPorCargo(umaListaCargos())).thenReturn(List.of(1, 2));
        mvc.perform(post(URL + "/processar-permissoes-gerentes-coordenadores")
                .header("Authorization", getAccessToken(mvc, ADMIN))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(agenteAutorizadoClient, times(1)).getAaFeederPorCargo(anyList());
    }

    private PermissaoEspecialRequest novasPermissoes() {
        PermissaoEspecialRequest res = new PermissaoEspecialRequest();
        res.setUsuarioId(1);
        res.setFuncionalidadesIds(Arrays.asList(1, 2, 3, 4));
        return res;
    }

    private List<CodigoCargo> umaListaCargos() {
        return List.of(
            CodigoCargo.AGENTE_AUTORIZADO_GERENTE,
            CodigoCargo.AGENTE_AUTORIZADO_COORDENADOR);
    }
}
