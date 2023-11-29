package br.com.xbrain.autenticacao.modules.usuario.controller;

import br.com.xbrain.autenticacao.config.OAuth2ResourceConfig;
import br.com.xbrain.autenticacao.modules.equipevenda.service.EquipeVendaD2dService;
import br.com.xbrain.autenticacao.modules.usuario.event.UsuarioSubCanalObserver;
import lombok.SneakyThrows;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.MockBeans;
import org.springframework.context.annotation.Import;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@Import(OAuth2ResourceConfig.class)
@WebMvcTest(CanalNetSalesController.class)
@MockBeans({
    @MockBean(UsuarioSubCanalObserver.class),
    @MockBean(EquipeVendaD2dService.class),
    @MockBean(TokenStore.class)
})
public class CanalNetSalesControllerTest {

    static final String API_CANAL_NETSALES = "/api/usuario-canal-netsales";

    @Autowired
    private MockMvc mvc;

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void getAllCanaisNetsales_deveRetornarUnauthorized_quandoNaoAutenticado() {
        mvc.perform(get(API_CANAL_NETSALES))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @SneakyThrows
    @WithMockUser
    public void getAllCanaisNetsales_deveRetornarListaSelectReponse_quandoAutenticado() {

        mvc.perform(get(API_CANAL_NETSALES))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(27)))
            .andExpect(jsonPath("$[0].value").value("D2D_ACOES_ESPECIAIS"))
            .andExpect(jsonPath("$[0].label").value("D2D-ACOES ESPECIAIS"))
            .andExpect(jsonPath("$[1].value").value("D2D_CLARO_PESSOAL"))
            .andExpect(jsonPath("$[1].label").value("D2D-CLARO PESSOAL"))
            .andExpect(jsonPath("$[2].value").value("D2D_CONDOMINIO"))
            .andExpect(jsonPath("$[2].label").value("D2D-CONDOMINIO"))
            .andExpect(jsonPath("$[3].value").value("D2D_CONVERSAO_MDU"))
            .andExpect(jsonPath("$[3].label").value("D2D-CONVERSAO MDU"))
            .andExpect(jsonPath("$[4].value").value("D2D_ESPECIALISTA"))
            .andExpect(jsonPath("$[4].label").value("D2D-ESPECIALISTA"))
            .andExpect(jsonPath("$[5].value").value("D2D_INDIRETO"))
            .andExpect(jsonPath("$[5].label").value("D2D-INDIRETO"))
            .andExpect(jsonPath("$[6].value").value("D2D_PESSOA_JURIDICA"))
            .andExpect(jsonPath("$[6].label").value("D2D-PESSOA JURIDICA"))
            .andExpect(jsonPath("$[7].value").value("D2D_PME"))
            .andExpect(jsonPath("$[7].label").value("D2D-PME"))
            .andExpect(jsonPath("$[8].value").value("D2D_TECNICO"))
            .andExpect(jsonPath("$[8].label").value("D2D-TECNICO"))
            .andExpect(jsonPath("$[9].value").value("D2D_VENDAS_PESSOAIS"))
            .andExpect(jsonPath("$[9].label").value("D2D-VENDAS PESSOAIS"))
            .andExpect(jsonPath("$[10].value").value("PROP_TECNICO"))
            .andExpect(jsonPath("$[10].label").value("PROP-TECNICO"))
            .andExpect(jsonPath("$[11].value").value("PROP_CONDOMINIO_NET_CURITIBA"))
            .andExpect(jsonPath("$[11].label").value("PROP-CONDOMINIO-NET CURITIBA"))
            .andExpect(jsonPath("$[12].value").value("PROP_VENDAS_MDU_NET_FLO"))
            .andExpect(jsonPath("$[12].label").value("PROP-VENDAS MDU-NET FLO"))
            .andExpect(jsonPath("$[13].value").value("PROP_VENDAS_PESSOAIS_NET_ANA"))
            .andExpect(jsonPath("$[13].label").value("PROP-VENDAS PESSOAIS-NET ANA"))
            .andExpect(jsonPath("$[14].value").value("PROP_VENDAS_PESSOAIS_NET_BLU"))
            .andExpect(jsonPath("$[14].label").value("PROP-VENDAS PESSOAIS-NET BLU"))
            .andExpect(jsonPath("$[15].value").value("PROP_VENDAS_PESSOAIS_NET_CGR"))
            .andExpect(jsonPath("$[15].label").value("PROP-VENDAS PESSOAIS-NET CGR"))
            .andExpect(jsonPath("$[16].value").value("PROP_VENDAS_PESSOAIS_NET_RIB"))
            .andExpect(jsonPath("$[16].label").value("PROP-VENDAS PESSOAIS-NET RIB"))
            .andExpect(jsonPath("$[17].value").value("PROP_VENDAS_PESSOAIS_NET_RIO"))
            .andExpect(jsonPath("$[17].label").value("PROP-VENDAS PESSOAIS-NET RIO"))
            .andExpect(jsonPath("$[18].value").value("PROP_VENDAS_PESSOAIS_NET_SAN"))
            .andExpect(jsonPath("$[18].label").value("PROP-VENDAS PESSOAIS-NET SAN"))
            .andExpect(jsonPath("$[19].value").value("VENDAS_CORPORATIVAS"))
            .andExpect(jsonPath("$[19].label").value("VENDAS CORPORATIVAS"))
            .andExpect(jsonPath("$[20].value").value("VENDAS_PARA_FUNCIONARIO"))
            .andExpect(jsonPath("$[20].label").value("VENDAS PARA FUNCIONARIO"))
            .andExpect(jsonPath("$[21].value").value("VENDAS_PDV"))
            .andExpect(jsonPath("$[21].label").value("VENDAS PDV"))
            .andExpect(jsonPath("$[22].value").value("VENDAS_POR_FORNECEDOR"))
            .andExpect(jsonPath("$[22].label").value("VENDAS POR FORNECEDOR"))
            .andExpect(jsonPath("$[23].value").value("VENDAS_POR_PARCEIROS"))
            .andExpect(jsonPath("$[23].label").value("VENDAS POR PARCEIROS"))
            .andExpect(jsonPath("$[24].value").value("VENDAS_PORTA_A_PORTA"))
            .andExpect(jsonPath("$[24].label").value("VENDAS PORTA A PORTA"))
            .andExpect(jsonPath("$[25].value").value("VENDAS_VIA_NOVITECH"))
            .andExpect(jsonPath("$[25].label").value("VENDAS VIA NOVITECH"))
            .andExpect(jsonPath("$[26].value").value("VENDAS_VIA_PORTAL"))
            .andExpect(jsonPath("$[26].label").value("VENDAS VIA PORTAL"));
    }
}
