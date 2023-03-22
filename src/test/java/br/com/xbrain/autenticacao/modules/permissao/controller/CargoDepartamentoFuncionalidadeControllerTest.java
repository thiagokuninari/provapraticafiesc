package br.com.xbrain.autenticacao.modules.permissao.controller;

import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.comum.dto.PageRequest;
import br.com.xbrain.autenticacao.modules.equipevenda.service.EquipeVendaD2dService;
import br.com.xbrain.autenticacao.modules.permissao.dto.CargoDepartamentoFuncionalidadeRequest;
import br.com.xbrain.autenticacao.modules.permissao.filtros.CargoDepartamentoFuncionalidadeFiltros;
import br.com.xbrain.autenticacao.modules.permissao.filtros.CargoDepartamentoFuncionalidadePredicate;
import br.com.xbrain.autenticacao.modules.permissao.model.CargoDepartamentoFuncionalidade;
import br.com.xbrain.autenticacao.modules.permissao.repository.CargoDepartamentoFuncionalidadeRepository;
import br.com.xbrain.autenticacao.modules.permissao.service.CargoDepartamentoFuncionalidadeService;
import br.com.xbrain.autenticacao.modules.permissao.service.FuncionalidadeService;
import br.com.xbrain.autenticacao.modules.usuario.model.Cargo;
import br.com.xbrain.autenticacao.modules.usuario.model.Departamento;
import br.com.xbrain.autenticacao.modules.usuario.repository.UsuarioRepository;
import helpers.TestsHelper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Arrays;
import java.util.List;

import static br.com.xbrain.autenticacao.modules.permissao.helper.CargoDepartamentoFuncionalidadeHelper.*;
import static br.com.xbrain.autenticacao.modules.usuario.helpers.UsuarioHelper.umUsuarioAutenticadoAdmin;
import static br.com.xbrain.autenticacao.modules.usuario.helpers.UsuarioHelper.umaListaDeUsuariosAdminSimples;
import static helpers.TestsHelper.getAccessToken;
import static helpers.Usuarios.SOCIO_AA;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@WebMvcTest(controllers = CargoDepartamentoFuncionalidadeController.class)
@AutoConfigureMockMvc
public class CargoDepartamentoFuncionalidadeControllerTest {

    private static final String URL = "/api/cargo-departamento-funcionalidade";

    private MockMvc mvc;
    @Autowired
    private WebApplicationContext context;
    @SpyBean
    private CargoDepartamentoFuncionalidadeService service;
    @MockBean
    private CargoDepartamentoFuncionalidadeRepository repository;
    @MockBean
    private AutenticacaoService autenticacaoService;
    @MockBean
    private EquipeVendaD2dService equipeVendaD2dService;
    @MockBean
    private UsuarioRepository usuarioRepository;
    @MockBean
    private FuncionalidadeService funcionalidadeService;
    @Captor
    private ArgumentCaptor<List<CargoDepartamentoFuncionalidade>> argumentCaptorListaCargoDeptoFuncionalidade;

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
    @WithMockUser(roles = { "Visualizar usuários dos Agentes Autorizados" })
    public void getAll_forbidden_quandoNaoTiverPermissaoParaGerenciaDePermissao() throws Exception {
        mvc.perform(get(URL)
                .header("Authorization", getAccessToken(mvc, SOCIO_AA))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser
    public void deveSalvar() throws Exception {
        when(autenticacaoService.getUsuarioAutenticado()).thenReturn(umUsuarioAutenticadoAdmin());

        var predicate = getPredicate();

        when(repository.findFuncionalidadesPorCargoEDepartamento(predicate.build()))
            .thenReturn(umaListaDeCargoDepartamentoFuncionalidadeDeAnalista());

        mvc.perform(MockMvcRequestBuilders.post("/api/cargo-departamento-funcionalidade")
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestsHelper.convertObjectToJsonBytes(novasPermissoes())))
            .andExpect(status().isOk());

        verify(repository, times(1)).save(argumentCaptorListaCargoDeptoFuncionalidade.capture());

        assertThat(argumentCaptorListaCargoDeptoFuncionalidade.getValue())
            .extracting("cargo.id", "departamento.id", "funcionalidade.id")
            .containsExactly(
                tuple(1, 1, 1),
                tuple(1, 1, 2),
                tuple(1, 1, 3),
                tuple(1, 1, 4)
            );
    }

    @Test
    @WithMockUser
    public void deveBuscarPermissoes() throws Exception {
        var filtros = new CargoDepartamentoFuncionalidadeFiltros();
        when(repository.findFuncionalidadesPorCargoEDepartamento(filtros.toPredicate()))
            .thenReturn(umaListaComTodosCargoDepartamentoFuncionalidade());

        mvc.perform(MockMvcRequestBuilders.get("/api/cargo-departamento-funcionalidade")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", is(not(empty()))));
    }

    @Test
    @WithMockUser
    public void devePaginarPermissoes() throws Exception {
        var filtros = new CargoDepartamentoFuncionalidadeFiltros();
        when(repository.findAll(filtros.toPredicate(), new PageRequest()))
            .thenReturn(new PageImpl<>(umaListaDeCargoDepartamentoFuncionalidadeDeAdministrador()));

        mvc.perform(MockMvcRequestBuilders.get("/api/cargo-departamento-funcionalidade/pages?page=0&size=10")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content", hasSize(10)))
            .andExpect(jsonPath("$.totalElements", is(not(empty()))));
    }

    @Test
    @WithMockUser
    public void deveRemoverUmaPermissao() throws Exception {
        doNothing().when(repository).delete(1);

        mvc.perform(put("/api/cargo-departamento-funcionalidade/remover/1")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    public void deveDeslogarUsuarios() throws Exception {
        when(usuarioRepository.findAllByCargoAndDepartamento(new Cargo(50), new Departamento(50)))
            .thenReturn(umaListaDeUsuariosAdminSimples());

        doNothing().when(autenticacaoService).logout(anyString());

        mvc.perform(put("/api/cargo-departamento-funcionalidade/deslogar/50/50")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(autenticacaoService, times(4)).logout(anyString());
    }

    private CargoDepartamentoFuncionalidadeRequest novasPermissoes() {
        CargoDepartamentoFuncionalidadeRequest res = new CargoDepartamentoFuncionalidadeRequest();
        res.setCargoId(1);
        res.setDepartamentoId(1);
        res.setFuncionalidadesIds(Arrays.asList(1, 2, 3, 4));
        return res;
    }

    private CargoDepartamentoFuncionalidadePredicate getPredicate() {
        var cargoId = 1;
        var departamentoId = 1;
        var predicate = new CargoDepartamentoFuncionalidadePredicate();
        predicate.comCargo(cargoId);
        predicate.comDepartamento(departamentoId);
        return predicate;
    }
}
