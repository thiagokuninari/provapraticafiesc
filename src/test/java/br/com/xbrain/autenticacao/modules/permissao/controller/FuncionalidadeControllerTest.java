package br.com.xbrain.autenticacao.modules.permissao.controller;

import br.com.xbrain.autenticacao.modules.equipevenda.service.EquipeVendaD2dService;
import br.com.xbrain.autenticacao.modules.permissao.repository.CargoDepartamentoFuncionalidadeRepository;
import br.com.xbrain.autenticacao.modules.permissao.repository.FuncionalidadeRepository;
import br.com.xbrain.autenticacao.modules.permissao.repository.PermissaoEspecialRepository;
import br.com.xbrain.autenticacao.modules.permissao.service.FuncionalidadeService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static br.com.xbrain.autenticacao.modules.permissao.helper.FuncionalidadeHelper.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@WebMvcTest(controllers = FuncionalidadeController.class)
@AutoConfigureMockMvc
public class FuncionalidadeControllerTest {

    private static final String URL = "/api/funcionalidades";

    private MockMvc mvc;
    @Autowired
    private WebApplicationContext context;
    @SpyBean
    private FuncionalidadeService funcionalidadeService;
    @MockBean
    private FuncionalidadeRepository funcionalidadeRepository;
    @MockBean
    private CargoDepartamentoFuncionalidadeRepository cargoDepartamentoFuncionalidadeRepository;
    @MockBean
    private PermissaoEspecialRepository permissaoEspecialRepository;
    @MockBean
    private EquipeVendaD2dService equipeVendaD2dService;

    @Before
    public void setUp() throws Exception {
        mvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();
    }

    @Test
    @WithAnonymousUser
    public void getAll_unauthorized_quandoNaoPassarAToken() throws Exception {
        mvc.perform(get(URL)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "usuario-helpdesk", roles = {"Abrir chamado"})
    public void getAll_forbidden_quandoNaoTiverPermissaoParaControleDeUsuarios() throws Exception {
        mvc.perform(get(URL)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser
    public void getAll_ok_quandoTiverPermissaoParaControleDeUsuarios() throws Exception {
        when(funcionalidadeRepository.findAllByOrderByNome()).thenReturn(List.of(
            funcionalidadeAbrirChamado(),
            funcionalidadeAbrirChamadoCrn(),
            funcionalidadeVisualizarRamalUsuario(),
            funcionalidadeVisualizarCampanha(),
            funcionalidadeVisualizarImportacaoMailing()
        ));

        mvc.perform(get(URL)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }
}
