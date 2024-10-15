package br.com.xbrain.autenticacao.modules.usuario.controller;

import br.com.xbrain.autenticacao.config.OAuth2ResourceConfig;
import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.comum.dto.SelectResponse;
import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.comum.enums.Eboolean;
import br.com.xbrain.autenticacao.modules.comum.exception.NotFoundException;
import br.com.xbrain.autenticacao.modules.comum.service.DeslogarUsuarioPorExcessoDeUsoService;
import br.com.xbrain.autenticacao.modules.equipevenda.service.EquipeVendaD2dService;
import br.com.xbrain.autenticacao.modules.feeder.dto.VendedoresFeederFiltros;
import br.com.xbrain.autenticacao.modules.feeder.service.FeederService;
import br.com.xbrain.autenticacao.modules.usuario.dto.*;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoNivel;
import br.com.xbrain.autenticacao.modules.usuario.enums.ECanal;
import br.com.xbrain.autenticacao.modules.usuario.event.UsuarioSubCanalObserver;
import br.com.xbrain.autenticacao.modules.usuario.service.*;
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

import java.util.List;
import java.util.Set;

import static br.com.xbrain.autenticacao.modules.comum.enums.ESituacao.A;
import static br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo.*;
import static helpers.TestBuilders.umUsuario;
import static helpers.TestsHelper.convertObjectToJsonBytes;
import static helpers.Usuarios.ADMIN;
import static org.hamcrest.Matchers.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@WebMvcTest(UsuarioController.class)
@MockBeans({
    @MockBean(UsuarioSubCanalObserver.class),
    @MockBean(EquipeVendaD2dService.class),
    @MockBean(TokenStore.class)
})
@Import(OAuth2ResourceConfig.class)
public class UsuarioControllerTest {

    private static final String BASE_URL = "/api/usuarios";
    private static final String ROLE_MLG_5013 = "MLG_5013";
    private static final String ROLE_APPLICATION = "APPLICATION";

    @Autowired
    private MockMvc mvc;
    @MockBean
    private UsuarioService usuarioService;
    @MockBean
    private UsuarioServiceEsqueciSenha usuarioServiceEsqueciSenha;
    @MockBean
    private UsuarioAgendamentoService usuarioAgendamentoService;
    @MockBean
    private UsuarioFunilProspeccaoService usuarioFunilProspeccaoService;
    @MockBean
    private DeslogarUsuarioPorExcessoDeUsoService deslogarUsuarioPorExcessoDeUsoService;
    @MockBean
    private FeederService feederService;
    @MockBean
    private SubCanalService subCanalService;
    @MockBean
    private AutenticacaoService autenticacaoService;

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void getUsuario_deveRetornarUnauthorized_quandoUsuarioNaoAutenticado() {
        mvc.perform(get(BASE_URL)).andExpect(status().isUnauthorized());
    }

    @Test
    @SneakyThrows
    @WithMockUser(username = "123-user")
    public void getUsuario_deveRetornarOk_quandoUsuarioNaoAutenticado() {
        when(usuarioService.findByIdCompleto(123)).thenReturn(umUsuario(1, ADMINISTRADOR));

        mvc.perform(get(BASE_URL)).andExpect(status().isOk());
        verify(usuarioService).findByIdCompleto(123);
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void getAllPorIds_deveRetornarUnauthorized_quandoUsuarioNaoAutenticado() {
        mvc.perform(post(BASE_URL.concat("/por-ids"))).andExpect(status().isUnauthorized());
    }

    @Test
    @SneakyThrows
    @WithMockUser
    public void getAllPorIds_deveRetornarOk_quandoUsuarioNaoAutenticadoENaoPassarFiltroAtivo() {
        var filtro = UsuarioPorIdFiltro.builder()
            .usuariosIds(List.of(1))
            .build();

        mvc.perform(post(BASE_URL.concat("/por-ids"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(filtro))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
        verify(usuarioService).findAllResponsePorIds(filtro);
    }

    @Test
    @SneakyThrows
    @WithMockUser
    public void getAllPorIds_deveRetornarOk_quandoUsuarioNaoAutenticado() {
        var filtro = UsuarioPorIdFiltro.builder()
            .apenasAtivos(Eboolean.V)
            .usuariosIds(List.of(1))
            .build();

        mvc.perform(post(BASE_URL.concat("/por-ids"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(filtro))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
        verify(usuarioService).findAllResponsePorIds(filtro);
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void buscarUsuariosAtivosNivelOperacaoCanalAa_deveRetornarUnauthorized_quandoUsuarioNaoAutenticado() {
        mvc.perform(get(BASE_URL.concat("/ativos/nivel/operacao/canal-aa")))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @SneakyThrows
    @WithMockUser
    public void buscarUsuariosAtivosNivelOperacaoCanalAa_deveRetornarOk_quandoUsuarioAutenticado() {
        mvc.perform(get(BASE_URL.concat("/ativos/nivel/operacao/canal-aa")))
            .andExpect(status().isOk());

        verify(usuarioService).buscarUsuariosAtivosNivelOperacaoCanalAa();
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void ativarSocioPrincipal_deveRetornarUnauthorized_quandoUsuarioNaoAutenticado() {
        mvc.perform(put(BASE_URL.concat("/ativar-socio")))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @SneakyThrows
    @WithMockUser
    public void ativarSocioPrincipal_deveRetornarOk_quandoUsuarioAutenticado() {
        mvc.perform(put(BASE_URL.concat("/ativar-socio"))
                .param("email", "user@email.com"))
            .andExpect(status().isOk());

        verify(usuarioService).ativarSocioPrincipal("user@email.com");
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void ativar_deveRetornarUnauthorized_quandoUsuarioNaoAutenticado() {
        mvc.perform(put(BASE_URL.concat("/ativar/1")))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @SneakyThrows
    @WithMockUser
    public void ativar_deveRetornarOk_quandoUsuarioAutenticado() {
        mvc.perform(put(BASE_URL.concat("/ativar/1")))
            .andExpect(status().isOk());

        verify(usuarioService).ativar(1);
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void inativarSocioPrincipal_deveRetornarUnauthorized_quandoUsuarioNaoAutenticado() {
        mvc.perform(put(BASE_URL.concat("/inativar-socio")))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @SneakyThrows
    @WithMockUser
    public void inativarSocioPrincipal_deveRetornarOk_quandoUsuarioAutenticado() {
        mvc.perform(put(BASE_URL.concat("/inativar-socio"))
                .param("email", "email@test.com"))
            .andExpect(status().isOk());

        verify(usuarioService).inativarSocioPrincipal("email@test.com");
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void getAllUsuariosIdsSuperiores_deveRetornarUnauthorized_quandoUsuarioNaoAutenticado() {
        mvc.perform(get(BASE_URL.concat("/ids/superiores/usuario-logado")))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @SneakyThrows
    @WithMockUser
    public void getAllUsuariosIdsSuperiores_deveRetornarOk_quandoUsuarioAutenticado() {
        mvc.perform(get(BASE_URL.concat("/ids/superiores/usuario-logado")))
            .andExpect(status().isOk());

        verify(usuarioService).getAllUsuariosIdsSuperiores();
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void inativar_deveRetornarUnauthorized_quandoUsuarioNaoAutenticado() {
        mvc.perform(put(BASE_URL.concat("/inativar/1")))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @SneakyThrows
    @WithMockUser
    public void inativar_deveRetornarOk_quandoUsuarioAutenticado() {
        mvc.perform(put(BASE_URL.concat("/inativar/1")))
            .andExpect(status().isOk());

        verify(usuarioService).inativar(1);
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void getUsuarioAutenticadoById_deveRetornarUnauthorized_quandoUsuarioNaoAutenticado() {
        mvc.perform(get(BASE_URL.concat("/autenticado/1")))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @SneakyThrows
    @WithMockUser
    public void getUsuarioAutenticadoById_deveRetornarOk_quandoUsuarioAutenticado() {
        when(usuarioService.findCompleteById(1)).thenReturn(umUsuario(1, ADMINISTRADOR));

        mvc.perform(get(BASE_URL.concat("/autenticado/1")))
            .andExpect(status().isOk());

        verify(usuarioService).findCompleteById(1);
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void getUsuarioAutenticadoComLoginNetSalesById_deveRetornarUnauthorized_quandoUsuarioNaoAutenticado() {
        mvc.perform(get(BASE_URL.concat("/autenticado-com-login-netsales/1")))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @SneakyThrows
    @WithMockUser
    public void getUsuarioAutenticadoComLoginNetSalesById_deveRetornarOk_quandoUsuarioAutenticado() {
        when(usuarioService.findCompleteByIdComLoginNetSales(1))
            .thenReturn(umUsuario(1, ADMINISTRADOR));

        mvc.perform(get(BASE_URL.concat("/autenticado-com-login-netsales/1")))
            .andExpect(status().isOk());

        verify(usuarioService).findCompleteByIdComLoginNetSales(1);
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void getUsuarioVendedorById_deveRetornarUnauthorized_quandoUsuarioNaoAutenticado() {
        mvc.perform(get(BASE_URL.concat("/vendedores")))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @SneakyThrows
    @WithMockUser
    public void getUsuarioVendedorById_deveRetornarOk_quandoUsuarioAutenticado() {
        mvc.perform(get(BASE_URL.concat("/vendedores"))
                .param("ids", "1"))
            .andExpect(status().isOk());

        verify(usuarioService).getVendedoresByIds(List.of(1));
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void getUsuarioVendedorByIdPost_deveRetornarUnauthorized_quandoUsuarioNaoAutenticado() {
        mvc.perform(post(BASE_URL.concat("/vendedores")))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @SneakyThrows
    @WithMockUser
    public void getUsuarioVendedorByIdPost_deveRetornarOk_quandoUsuarioAutenticado() {
        mvc.perform(post(BASE_URL.concat("/vendedores"))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(List.of(1))))
            .andExpect(status().isOk());

        verify(usuarioService).getVendedoresByIds(List.of(1));
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void buscarColaboradoresAtivosOperacaoComericialPorCargo_deveRetornarUnauthorized_quandoUsuarioNaoAutenticado() {
        mvc.perform(get(BASE_URL.concat("/ativos/operacao-comercial/cargo/1")))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @SneakyThrows
    @WithMockUser
    public void buscarColaboradoresAtivosOperacaoComericialPorCargo_deveRetornarOk_quandoUsuarioAutenticado() {
        mvc.perform(get(BASE_URL.concat("/ativos/operacao-comercial/cargo/1")))
            .andExpect(status().isOk());
        verify(usuarioService).buscarColaboradoresAtivosOperacaoComericialPorCargo(1);
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void getUsuarioById_deveRetornarUnauthorized_quandoUsuarioNaoAutenticado() {
        mvc.perform(get(BASE_URL.concat("/1")))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @SneakyThrows
    @WithMockUser
    public void getUsuarioById_deveRetornarOk_quandoUsuarioAutenticado() {
        when(usuarioService.findCompleteById(1)).thenReturn(umUsuario(1, ADMINISTRADOR));

        mvc.perform(get(BASE_URL.concat("/1")))
            .andExpect(status().isOk());

        verify(usuarioService).findCompleteById(1);
        verify(usuarioService).getFuncionalidadeByUsuario(1);
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void getUsuarioByNivel_deveRetornarUnauthorized_quandoUsuarioNaoAutenticado() {
        mvc.perform(get(BASE_URL)
                .param("nivel", "AGENTE_AUTORIZADO"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @SneakyThrows
    @WithMockUser
    public void getUsuarioByNivel_deveRetornarOk_quandoUsuarioAutenticado() {
        mvc.perform(get(BASE_URL)
                .param("nivel", "AGENTE_AUTORIZADO"))
            .andExpect(status().isOk());

        verify(usuarioService).getUsuarioByNivel(CodigoNivel.AGENTE_AUTORIZADO);
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void getUsuariosIdsByNivel_deveRetornarUnauthorized_quandoUsuarioNaoAutenticado() {
        mvc.perform(get(BASE_URL.concat("/ids")))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @SneakyThrows
    @WithMockUser
    public void getUsuariosIdsByNivel_deveRetornarOk_quandoUsuarioAutenticado() {
        mvc.perform(get(BASE_URL.concat("/ids"))
                .param("nivel", "AGENTE_AUTORIZADO"))
            .andExpect(status().isOk());

        verify(usuarioService).getUsuariosIdsByNivel(CodigoNivel.AGENTE_AUTORIZADO);
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void getCidadesByUsuario_deveRetornarUnauthorized_quandoUsuarioNaoAutenticado() {
        mvc.perform(get(BASE_URL.concat("/1/cidades")))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @SneakyThrows
    @WithMockUser
    public void getCidadesByUsuario_deveRetornarOk_quandoUsuarioAutenticado() {
        mvc.perform(get(BASE_URL.concat("/1/cidades")))
            .andExpect(status().isOk());

        verify(usuarioService).findCidadesByUsuario(1);
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void getUsuariosOperacaoCanalAa_deveRetornarUnauthorized_quandoUsuarioNaoAutenticado() {
        mvc.perform(get(BASE_URL.concat("/nivel/canal")))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @SneakyThrows
    @WithMockUser
    public void getUsuariosOperacaoCanalAa_deveRetornarOk_quandoUsuarioAutenticado() {
        mvc.perform(get(BASE_URL.concat("/nivel/canal"))
                .param("codigoNivel", "AGENTE_AUTORIZADO"))
            .andExpect(status().isOk());

        verify(usuarioService).getUsuariosOperacaoCanalAa(CodigoNivel.AGENTE_AUTORIZADO);
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void getSubclustersUsuario_deveRetornarUnauthorized_quandoUsuarioNaoAutenticado() {
        mvc.perform(get(BASE_URL.concat("/1/subclusters")))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @SneakyThrows
    @WithMockUser
    public void getSubclustersUsuario_deveRetornarOk_quandoUsuarioAutenticado() {
        mvc.perform(get(BASE_URL.concat("/1/subclusters")))
            .andExpect(status().isOk());

        verify(usuarioService).getSubclusterUsuario(1);
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void getUfsUsuario_deveRetornarUnauthorized_quandoUsuarioNaoAutenticado() {
        mvc.perform(get(BASE_URL.concat("/1/ufs")))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @SneakyThrows
    @WithMockUser
    public void getUfsUsuario_deveRetornarOk_quandoUsuarioAutenticado() {
        mvc.perform(get(BASE_URL.concat("/1/ufs")))
            .andExpect(status().isOk());

        verify(usuarioService).getUfUsuario(1);
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void getSubordinados_deveRetornarUnauthorized_quandoUsuarioNaoAutenticado() {
        mvc.perform(get(BASE_URL.concat("/1/subordinados")))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @SneakyThrows
    @WithMockUser
    public void getSubordinados_deveRetornarOk_quandoUsuarioAutenticado() {
        mvc.perform(get(BASE_URL.concat("/1/subordinados"))
                .param("incluirProprio", "true"))
            .andExpect(status().isOk());

        verify(usuarioService).getIdDosUsuariosSubordinados(1, true);
    }

    @Test
    @SneakyThrows
    @WithMockUser
    public void getSubordinados_deveEnviarDefault_quandoNaoPassarParametro() {
        mvc.perform(get(BASE_URL.concat("/1/subordinados")))
            .andExpect(status().isOk());

        verify(usuarioService).getIdDosUsuariosSubordinados(1, false);
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void getSubordinadosVendas_deveRetornarUnauthorized_quandoUsuarioNaoAutenticado() {
        mvc.perform(get(BASE_URL.concat("/1/subordinados/vendas")))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @SneakyThrows
    @WithMockUser
    public void getSubordinadosVendas_deveRetornarOk_quandoUsuarioAutenticado() {
        mvc.perform(get(BASE_URL.concat("/1/subordinados/vendas")))
            .andExpect(status().isOk());

        verify(usuarioService).getIdDosUsuariosSubordinados(1, true);
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void getSuperioresByUsuario_deveRetornarUnauthorized_quandoUsuarioNaoAutenticado() {
        mvc.perform(get(BASE_URL.concat("/hierarquia/superiores/1")))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @SneakyThrows
    @WithMockUser
    public void getSuperioresByUsuario_deveRetornarOk_quandoUsuarioAutenticado() {
        mvc.perform(get(BASE_URL.concat("/hierarquia/superiores/1")))
            .andExpect(status().isOk());

        verify(usuarioService).getSuperioresDoUsuario(1);
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void getSuperioresByUsuarioPorCargo_deveRetornarUnauthorized_quandoUsuarioNaoAutenticado() {
        mvc.perform(get(BASE_URL.concat("/hierarquia/superiores/1/AGENTE_AUTORIZADO_SOCIO")))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @SneakyThrows
    @WithMockUser
    public void getSuperioresByUsuarioPorCargo_deveRetornarOk_quandoUsuarioAutenticado() {
        mvc.perform(get(BASE_URL.concat("/hierarquia/superiores/1/AGENTE_AUTORIZADO_SOCIO")))
            .andExpect(status().isOk());

        verify(usuarioService).getSuperioresDoUsuarioPorCargo(1, AGENTE_AUTORIZADO_SOCIO);
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void getSubordinadosByUsuario_deveRetornarUnauthorized_quandoUsuarioNaoAutenticado() {
        mvc.perform(get(BASE_URL.concat("/hierarquia/subordinados/1")))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @SneakyThrows
    @WithMockUser
    public void getSubordinadosByUsuario_deveRetornarOk_quandoUsuarioAutenticado() {
        mvc.perform(get(BASE_URL.concat("/hierarquia/subordinados/1")))
            .andExpect(status().isOk());

        verify(usuarioService).getSubordinadosDoUsuario(1);
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void getSubordinadosDoGerenteComCargoExecutivoOrExecutivoHunter_deveRetornar401_quandoUsuarioNaoAutenticado() {
        mvc.perform(get(BASE_URL.concat("/hierarquia/subordinados/gerente/1")))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @SneakyThrows
    @WithMockUser
    public void getSubordinadosDoGerenteComCargoExecutivoOrExecutivoHunter_deveRetornarOk_quandoUsuarioAutenticado() {
        mvc.perform(get(BASE_URL.concat("/hierarquia/subordinados/gerente/1")))
            .andExpect(status().isOk());

        verify(usuarioService).getSubordinadosDoGerenteComCargoExecutivoOrExecutivoHunter(1);
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void findAllExecutivosOperacaoDepartamentoComercial_deveRetornarUnauthorized_quandoUsuarioNaoAutenticado() {
        mvc.perform(get(BASE_URL.concat("/executivos-comerciais")))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @SneakyThrows
    @WithMockUser
    public void findAllExecutivosOperacaoDepartamentoComercial_deveRetornarOk_quandoUsuarioAutenticado() {
        mvc.perform(get(BASE_URL.concat("/executivos-comerciais"))
                .param("cargo", "VENDEDOR_OPERACAO"))
            .andExpect(status().isOk());

        verify(usuarioService).findAllExecutivosOperacaoDepartamentoComercial(CodigoCargo.VENDEDOR_OPERACAO);
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void findAllResponsaveisDdd_deveRetornarUnauthorized_quandoUsuarioNaoAutenticado() {
        mvc.perform(get(BASE_URL.concat("/responsaveis-ddd")))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @SneakyThrows
    @WithMockUser
    public void findAllResponsaveisDdd_deveRetornarOk_quandoUsuarioAutenticado() {
        var listaUsuarioAutoComplete = List.of(UsuarioAutoComplete.builder().value(1).text("nome").build());
        when(usuarioService.findAllResponsaveisDdd())
            .thenReturn(listaUsuarioAutoComplete);

        mvc.perform(get(BASE_URL.concat("/responsaveis-ddd")))
            .andExpect(jsonPath("$[0].value", is(1)))
            .andExpect(jsonPath("$[0].text", is("nome")))
            .andExpect(status().isOk());

        verify(usuarioService).findAllResponsaveisDdd();
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void vincularUsuariosComSuperior_vincular_deveRetornarUnauthorized_quandoUsuarioNaoAutenticado() {
        mvc.perform(post(BASE_URL.concat("/vincula/hierarquia")))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @SneakyThrows
    @WithMockUser
    public void vincularUsuariosComSuperior_vincular_deveRetornarOk_quandoUsuarioAutenticado() {
        mvc.perform(post(BASE_URL.concat("/vincula/hierarquia"))
                .param("idsUsuarios", "1")
                .param("idUsuarioSuperior", "2"))
            .andExpect(status().isOk());

        verify(usuarioService).vincularUsuario(List.of(1), 2);
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void vincularUsuariosComSuperior_alterar_deveRetornarUnauthorized_quandoUsuarioNaoAutenticado() {
        mvc.perform(post(BASE_URL.concat("/alterar/hierarquia")))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @SneakyThrows
    @WithMockUser
    public void vincularUsuariosComSuperior_alterar_deveRetornarOk_quandoUsuarioAutenticado() {
        mvc.perform(post(BASE_URL.concat("/alterar/hierarquia"))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(new AlteraSuperiorRequest())))
            .andExpect(status().isOk());

        verify(usuarioService).vincularUsuarioParaNovaHierarquia(any());
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void getUsuariosFilter_deveRetornarUnauthorized_quandoUsuarioNaoAutenticado() {
        mvc.perform(get(BASE_URL.concat("/filter")))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @SneakyThrows
    @WithMockUser
    public void getUsuariosFilter_deveRetornarOk_quandoUsuarioAutenticado() {
        mvc.perform(get(BASE_URL.concat("/filter")))
            .andExpect(status().isOk());

        verify(usuarioService).getUsuariosFiltros(any());
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void getUsuariosByIds_deveRetornarUnauthorized_quandoUsuarioNaoAutenticado() {
        mvc.perform(get(BASE_URL)
                .param("ids", "1"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @SneakyThrows
    @WithMockUser
    public void getUsuariosByIds_deveRetornarOk_quandoUsuarioAutenticado() {
        mvc.perform(get(BASE_URL)
                .param("ids", "1"))
            .andExpect(status().isOk());

        verify(usuarioService).getUsuariosByIds(List.of(1));
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void getUsuariosAtivosByIds_deveRetornarUnauthorized_quandoUsuarioNaoAutenticado() {
        mvc.perform(post(BASE_URL.concat("/ativos")))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @SneakyThrows
    @WithMockUser
    public void getUsuariosAtivosByIds_deveRetornarOk_quandoUsuarioAutenticado() {
        mvc.perform(post(BASE_URL.concat("/ativos"))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(List.of(1))))
            .andExpect(status().isOk());

        verify(usuarioService).getUsuariosAtivosByIds(List.of(1));
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void getUsuariosByIdsTodasSituacoes_deveRetornarUnauthorized_quandoUsuarioNaoAutenticado() {
        mvc.perform(get(BASE_URL.concat("/todas-situacoes")))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @SneakyThrows
    @WithMockUser
    public void getUsuariosByIdsTodasSituacoes_deveRetornarOk_quandoUsuarioAutenticado() {
        mvc.perform(get(BASE_URL.concat("/todas-situacoes"))
                .param("ids", "1"))
            .andExpect(status().isOk());

        verify(usuarioService).getUsuariosByIdsTodasSituacoes(Set.of(1));
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void getUsuariosInativosByIds_deveRetornarUnauthorized_quandoUsuarioNaoAutenticado() {
        mvc.perform(get(BASE_URL.concat("/inativos")))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @SneakyThrows
    @WithMockUser
    public void getUsuariosInativosByIds_deveRetornarOk_quandoUsuarioAutenticado() {
        mvc.perform(get(BASE_URL.concat("/inativos"))
                .param("usuariosInativosIds", "1"))
            .andExpect(status().isOk());

        verify(usuarioService).getUsuariosInativosByIds(List.of(1));
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void getUsuarioByEmail_deveRetornarUnauthorized_quandoUsuarioNaoAutenticado() {
        mvc.perform(get(BASE_URL)
                .param("email", "email@test.com"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @SneakyThrows
    @WithMockUser
    public void getUsuarioByEmail_deveRetornarOk_quandoUsuarioAutenticado() {
        mvc.perform(get(BASE_URL)
                .param("email", "email@test.com")
                .param("buscarAtivo", "true"))
            .andExpect(status().isOk());

        verify(usuarioService).findByEmailAa("email@test.com", true);
    }

    @Test
    @SneakyThrows
    @WithMockUser
    public void getUsuarioByEmail_deveEnviarNulo_quandoNaoPassarParametroNaoObrigatorio() {
        mvc.perform(get(BASE_URL)
                .param("email", "email@test.com"))
            .andExpect(status().isOk());

        verify(usuarioService).findByEmailAa("email@test.com", null);
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void getUsuariosByEmails_deveRetornarUnauthorized_quandoUsuarioNaoAutenticado() {
        mvc.perform(post(BASE_URL.concat("/emails")))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @SneakyThrows
    @WithMockUser
    public void getUsuariosByEmails_deveRetornarOk_quandoUsuarioAutenticado() {
        mvc.perform(post(BASE_URL.concat("/emails"))
                .param("buscarAtivo", "true")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(List.of("email@test.com"))))
            .andExpect(status().isOk());

        verify(usuarioService).findByEmails(List.of("email@test.com"), true);
    }

    @Test
    @SneakyThrows
    @WithMockUser
    public void getUsuariosByEmails_deveEnviarNulo_quandoNaoPassarParametroNaoObrigatorio() {
        mvc.perform(post(BASE_URL.concat("/emails"))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(List.of("email@test.com"))))
            .andExpect(status().isOk());

        verify(usuarioService).findByEmails(List.of("email@test.com"), null);
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void getUsuarioByCpf_deveRetornarUnauthorized_quandoUsuarioNaoAutenticado() {
        mvc.perform(get(BASE_URL).param("cpf", "123"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @SneakyThrows
    @WithMockUser
    public void getUsuarioByCpf_deveRetornarOk_quandoUsuarioAutenticado() {
        mvc.perform(get(BASE_URL)
                .param("cpf", "123")
                .param("buscarAtivo", "true"))
            .andExpect(status().isOk());

        verify(usuarioService).findByCpfAa("123", true);
    }

    @Test
    @SneakyThrows
    @WithMockUser
    public void getUsuarioByCpf_deveEnviarNulo_quandoNaoPassarParametroNaoObrigatorio() {
        mvc.perform(get(BASE_URL)
                .param("cpf", "123"))
            .andExpect(status().isOk());

        verify(usuarioService).findByCpfAa("123", null);
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void getUsuariosByCpfs_deveRetornarUnauthorized_quandoUsuarioNaoAutenticado() {
        mvc.perform(post(BASE_URL.concat("/cpfs")))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @SneakyThrows
    @WithMockUser
    public void getUsuariosByCpfs_deveRetornarOk_quandoUsuarioAutenticado() {
        mvc.perform(post(BASE_URL.concat("/cpfs"))
                .param("buscarAtivo", "true")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(List.of("123"))))
            .andExpect(status().isOk());

        verify(usuarioService).findByCpfs(List.of("123"), true);
    }

    @Test
    @SneakyThrows
    @WithMockUser
    public void getUsuariosByCpfs_deveEnviarNulo_quandoNaoPassarParametroNaoObrigatorio() {
        mvc.perform(post(BASE_URL.concat("/cpfs"))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(List.of("123"))))
            .andExpect(status().isOk());

        verify(usuarioService).findByCpfs(List.of("123"), null);
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void buscarAtualByCpf_deveRetornarUnauthorized_quandoUsuarioNaoAutenticado() {
        mvc.perform(get(BASE_URL.concat("/atual/cpf")))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @SneakyThrows
    @WithMockUser
    public void buscarAtualByCpf_deveRetornarOk_quandoUsuarioAutenticado() {
        mvc.perform(get(BASE_URL.concat("/atual/cpf"))
                .param("cpf", "123"))
            .andExpect(status().isOk());

        verify(usuarioService).buscarAtualByCpf("123");
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void buscarNaoRealocadosPorCpf_deveRetornarUnauthorized_quandoUsuarioNaoAutenticado() {
        mvc.perform(get(BASE_URL.concat("/nao-realocado")))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @SneakyThrows
    @WithMockUser
    public void buscarNaoRealocadosPorCpf_deveRetornarOk_quandoUsuarioAutenticado() {
        mvc.perform(get(BASE_URL.concat("/nao-realocado"))
                .param("cpf", "123"))
            .andExpect(status().isOk());

        verify(usuarioService).buscarNaoRealocadoByCpf("123");
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void buscarAtualByEmail_deveRetornarUnauthorized_quandoUsuarioNaoAutenticado() {
        mvc.perform(get(BASE_URL.concat("/atual/email")))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @SneakyThrows
    @WithMockUser
    public void buscarAtualByEmail_deveRetornarOk_quandoUsuarioAutenticado() {
        mvc.perform(get(BASE_URL.concat("/atual/email"))
                .param("email", "email@test.com"))
            .andExpect(status().isOk());

        verify(usuarioService).buscarAtualByEmail("email@test.com");
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void getEmpresasDoUsuario_deveRetornarUnauthorized_quandoUsuarioNaoAutenticado() {
        mvc.perform(get(BASE_URL.concat("/1/empresas")))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @SneakyThrows
    @WithMockUser
    public void getEmpresasDoUsuario_deveRetornarOk_quandoUsuarioAutenticado() {
        mvc.perform(get(BASE_URL.concat("/1/empresas")))
            .andExpect(status().isOk());

        verify(usuarioService).findEmpresasDoUsuario(1);
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void getUsuariosSupervisores_deveRetornarUnauthorized_quandoUsuarioNaoAutenticado() {
        mvc.perform(get(BASE_URL.concat("/hierarquia/supervisores")))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @SneakyThrows
    @WithMockUser
    public void getUsuariosSupervisores_deveRetornarOk_quandoUsuarioAutenticado() {
        mvc.perform(get(BASE_URL.concat("/hierarquia/supervisores")))
            .andExpect(status().isOk());

        verify(usuarioService).getUsuariosSuperiores(any());
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void getAllLideresComerciaisDoExecutivo_deveRetornarUnauthorized_quandoUsuarioNaoAutenticado() {
        mvc.perform(get(BASE_URL.concat("/hierarquia/supervisores/1")))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @SneakyThrows
    @WithMockUser
    public void getAllLideresComerciaisDoExecutivo_deveRetornarOk_quandoUsuarioAutenticado() {
        mvc.perform(get(BASE_URL.concat("/hierarquia/supervisores/1")))
            .andExpect(status().isOk());

        verify(usuarioService).findAllLideresComerciaisDoExecutivo(1);
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void getUsuariosSupervisoresDoAaAutoComplete_deveRetornarUnauthorized_quandoUsuarioNaoAutenticado() {
        mvc.perform(get(BASE_URL.concat("/hierarquia/supervisores-aa-auto-complete/1")))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @SneakyThrows
    @WithMockUser
    public void getUsuariosSupervisoresDoAaAutoComplete_deveRetornarOk_quandoUsuarioAutenticado() {
        mvc.perform(get(BASE_URL.concat("/hierarquia/supervisores-aa-auto-complete/1")))
            .andExpect(status().isOk());

        verify(usuarioService).getUsuariosSupervisoresDoAaAutoComplete(1);
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void findExecutivosPorIds_deveRetornarUnauthorized_quandoUsuarioNaoAutenticado() {
        mvc.perform(get(BASE_URL.concat("/executivos-comerciais-agente-autorizado")))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @SneakyThrows
    @WithMockUser
    public void findExecutivosPorIds_deveRetornarOk_quandoUsuarioAutenticado() {
        mvc.perform(get(BASE_URL.concat("/executivos-comerciais-agente-autorizado"))
                .param("usuariosExecutivos", "1"))
            .andExpect(status().isOk());

        verify(usuarioService).findExecutivosPorIds(List.of(1));
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void getUsuariosByPermissao_deveRetornarUnauthorized_quandoUsuarioNaoAutenticado() {
        mvc.perform(get(BASE_URL)
                .param("funcionalidade", "role_101"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @SneakyThrows
    @WithMockUser
    public void getUsuariosByPermissao_deveRetornarOk_quandoUsuarioAutenticado() {
        mvc.perform(get(BASE_URL)
                .param("funcionalidade", "role_101"))
            .andExpect(status().isOk());

        verify(usuarioService).getUsuarioByPermissaoEspecial("role_101");
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void getConfiguracaoByUsuario_deveRetornarUnauthorized_quandoUsuarioNaoAutenticado() {
        mvc.perform(get(BASE_URL.concat("/configuracao")))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @SneakyThrows
    @WithMockUser
    public void getConfiguracaoByUsuario_deveRetornarOk_quandoUsuarioAutenticado() {
        mvc.perform(get(BASE_URL.concat("/configuracao")))
            .andExpect(status().isOk());

        verify(usuarioService).getConfiguracaoByUsuario();
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void adicionarConfiguracao_deveRetornarUnauthorized_quandoUsuarioNaoAutenticado() {
        mvc.perform(post(BASE_URL.concat("/adicionar-configuracao")))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @SneakyThrows
    @WithMockUser
    public void adicionarConfiguracao_deveRetornarOk_quandoUsuarioAutenticado() {
        mvc.perform(post(BASE_URL.concat("/adicionar-configuracao"))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(new UsuarioConfiguracaoDto())))
            .andExpect(status().isOk());

        verify(usuarioService).adicionarConfiguracao(any());
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void saveUsuarioHierarquia_deveRetornarUnauthorized_quandoUsuarioNaoAutenticado() {
        mvc.perform(post(BASE_URL.concat("/usuarios-hierarquias-save")))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @SneakyThrows
    @WithMockUser
    public void saveUsuarioHierarquia_deveRetornarOk_quandoUsuarioAutenticado() {
        mvc.perform(post(BASE_URL.concat("/usuarios-hierarquias-save"))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(List.of(new UsuarioHierarquiaCarteiraDto()))))
            .andExpect(status().isOk());

        verify(usuarioService).saveUsuarioHierarquia(any());
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void removerConfiguracao_deveRetornarUnauthorized_quandoUsuarioNaoAutenticado() {
        mvc.perform(put(BASE_URL.concat("/remover-configuracao")))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @SneakyThrows
    @WithMockUser
    public void removerConfiguracao_deveRetornarOk_quandoUsuarioAutenticado() {
        mvc.perform(put(BASE_URL.concat("/remover-configuracao"))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(new UsuarioConfiguracaoDto())))
            .andExpect(status().isOk());

        verify(usuarioService).removerConfiguracao(any());
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void removerRamalConfiguracao_deveRetornarUnauthorized_quandoUsuarioNaoAutenticado() {
        mvc.perform(put(BASE_URL.concat("/remover-ramal-configuracao")))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @SneakyThrows
    @WithMockUser
    public void removerRamalConfiguracao_deveRetornarOk_quandoUsuarioAutenticado() {
        mvc.perform(put(BASE_URL.concat("/remover-ramal-configuracao"))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(new UsuarioConfiguracaoDto())))
            .andExpect(status().isOk());

        verify(usuarioService).removerRamalConfiguracao(any());
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void removerRamaisDeConfiguracao_deveRetornarUnauthorized_quandoUsuarioNaoAutenticado() {
        mvc.perform(put(BASE_URL.concat("/remover-ramais-configuracao")))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @SneakyThrows
    @WithMockUser
    public void removerRamaisDeConfiguracao_deveRetornarOk_quandoUsuarioAutenticado() {
        mvc.perform(put(BASE_URL.concat("/remover-ramais-configuracao"))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(List.of(new UsuarioConfiguracaoDto()))))
            .andExpect(status().isOk());

        verify(usuarioService).removerRamaisDeConfiguracao(any());
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void esqueceuSenha_deveRetornarUnauthorized_quandoUsuarioNaoAutenticado() {
        mvc.perform(put(BASE_URL.concat("/esqueci-senha")))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @SneakyThrows
    @WithMockUser
    public void esqueceuSenha_deveRetornarOk_quandoUsuarioAutenticado() {
        mvc.perform(put(BASE_URL.concat("/esqueci-senha"))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(new UsuarioDadosAcessoRequest())))
            .andExpect(status().isOk());

        verify(usuarioServiceEsqueciSenha).enviarConfirmacaoResetarSenha(any());
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void resetarSenha_deveRetornarOk_quandoUsuarioNaoAutenticado() {
        mvc.perform(get(BASE_URL.concat("/resetar-senha"))
                .param("token", "123abc"))
            .andExpect(status().isOk());

        verify(usuarioServiceEsqueciSenha).resetarSenha("123abc");
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void inativarColaboradores_deveRetornarUnauthorized_quandoUsuarioNaoAutenticado() {
        mvc.perform(put(BASE_URL.concat("/inativar-colaboradores")))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @SneakyThrows
    @WithMockUser
    public void inativarColaboradores_deveRetornarOk_quandoUsuarioAutenticado() {
        mvc.perform(put(BASE_URL.concat("/inativar-colaboradores"))
                .param("cnpj", "123"))
            .andExpect(status().isOk());

        verify(usuarioService).inativarColaboradores("123");
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void getCanais_deveRetornarUnauthorized_quandoUsuarioNaoAutenticado() {
        mvc.perform(get(BASE_URL.concat("/canais")))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @SneakyThrows
    @WithMockUser
    public void getCanais_deveRetornarOk_quandoUsuarioAutenticado() {
        mvc.perform(get(BASE_URL.concat("/canais")))
            .andExpect(status().isOk());
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void getTiposCanal_deveRetornarUnauthorized_quandoUsuarioNaoAutenticado() {
        mvc.perform(get(BASE_URL.concat("/tipos-canal")))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @SneakyThrows
    @WithMockUser
    public void getTiposCanal_deveRetornarOk_quandoUsuarioAutenticado() {
        mvc.perform(get(BASE_URL.concat("/tipos-canal")))
            .andExpect(status().isOk());

        verify(usuarioService).getTiposCanalOptions();
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void getIdsDaHierarquia_deveRetornarUnauthorized_quandoUsuarioNaoAutenticado() {
        mvc.perform(get(BASE_URL.concat("/1/subordinados/cargo/COD_CARGO")))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @SneakyThrows
    @WithMockUser
    public void getIdsDaHierarquia_deveRetornarOk_quandoUsuarioAutenticado() {
        mvc.perform(get(BASE_URL.concat("/1/subordinados/cargo/COD_CARGO")))
            .andExpect(status().isOk());

        verify(usuarioService).getIdsSubordinadosDaHierarquia(1, Set.of("COD_CARGO"));
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void getIdsDasHierarquias_deveRetornarUnauthorized_quandoUsuarioNaoAutenticado() {
        mvc.perform(get(BASE_URL.concat("/1/subordinados/cargos")))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @SneakyThrows
    @WithMockUser
    public void getIdsDasHierarquias_deveRetornarOk_quandoUsuarioAutenticado() {
        mvc.perform(get(BASE_URL.concat("/2/subordinados/cargos"))
                .param("codigosCargos", "COD_CARGO"))
            .andExpect(status().isOk());

        verify(usuarioService).getIdsSubordinadosDaHierarquia(2, Set.of("COD_CARGO"));
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void getIdsVendedoresDaHierarquia_deveRetornarUnauthorized_quandoUsuarioNaoAutenticado() {
        mvc.perform(get(BASE_URL.concat("/1/vendedores-hierarquia-ids")))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @SneakyThrows
    @WithMockUser
    public void getIdsVendedoresDaHierarquia_deveRetornarOk_quandoUsuarioAutenticado() {
        mvc.perform(get(BASE_URL.concat("/1/vendedores-hierarquia-ids")))
            .andExpect(status().isOk());

        verify(usuarioService).getIdsVendedoresOperacaoDaHierarquia(1);
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void getVendedoresDaHierarquia_deveRetornarUnauthorized_quandoUsuarioNaoAutenticado() {
        mvc.perform(get(BASE_URL.concat("/1/vendedores-hierarquia")))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @SneakyThrows
    @WithMockUser
    public void getVendedoresDaHierarquia_deveRetornarOk_quandoUsuarioAutenticado() {
        mvc.perform(get(BASE_URL.concat("/1/vendedores-hierarquia")))
            .andExpect(status().isOk());

        verify(usuarioService).getVendedoresOperacaoDaHierarquia(1);
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void getSupervisoresDaHierarquia_deveRetornarUnauthorized_quandoUsuarioNaoAutenticado() {
        mvc.perform(get(BASE_URL.concat("/1/supervisores-hierarquia")))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @SneakyThrows
    @WithMockUser
    public void getSupervisoresDaHierarquia_deveRetornarOk_quandoUsuarioAutenticado() {
        mvc.perform(get(BASE_URL.concat("/1/supervisores-hierarquia")))
            .andExpect(status().isOk());

        verify(usuarioService).getSupervisoresOperacaoDaHierarquia(1);
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void getPermissoesPorCanal_deveRetornarUnauthorized_quandoUsuarioNaoAutenticado() {
        mvc.perform(get(BASE_URL.concat("/permissoes-por-canal")))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @SneakyThrows
    @WithMockUser
    public void getPermissoesPorCanal_deveRetornarOk_quandoUsuarioAutenticado() {
        mvc.perform(get(BASE_URL.concat("/permissoes-por-canal")))
            .andExpect(status().isOk());

        verify(usuarioService).getPermissoesUsuarioAutenticadoPorCanal();
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void findUsuarioByPermissoes_deveRetornarUnauthorized_quandoUsuarioNaoAutenticado() {
        mvc.perform(post(BASE_URL.concat("/permissoes-por-usuario")))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @SneakyThrows
    @WithMockUser
    public void findUsuarioByPermissoes_deveRetornarBadRequest_quandoNaoEnviarCaomposObrigatorios() {
        mvc.perform(post(BASE_URL.concat("/permissoes-por-usuario"))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(new UsuarioPermissoesRequest())))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[*].message", containsInAnyOrder(
                "O campo usuariosId é obrigatório.",
                "O campo permissoes é obrigatório.")));
    }

    @Test
    @SneakyThrows
    @WithMockUser
    public void findUsuarioByPermissoes_deveRetornarOk_quandoUsuarioAutenticado() {
        var request = new UsuarioPermissoesRequest(List.of(1), List.of("ROLE_101"));

        mvc.perform(post(BASE_URL.concat("/permissoes-por-usuario"))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(request)))
            .andExpect(status().isOk());

        verify(usuarioService).findUsuariosByPermissoes(request);
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void getUsuariosDisponiveis_deveRetornarUnauthorized_quandoUsuarioNaoAutenticado() {
        mvc.perform(get(BASE_URL.concat("/distribuicao/agendamentos/1/disponiveis")))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @SneakyThrows
    @WithMockUser
    public void getUsuariosDisponiveis_deveRetornarForbidden_quandoUsuarioSemPermissao() {
        mvc.perform(get(BASE_URL.concat("/distribuicao/agendamentos/1/disponiveis")))
            .andExpect(status().isForbidden());
    }

    @Test
    @SneakyThrows
    @WithMockUser(roles = {ROLE_MLG_5013})
    public void getUsuariosDisponiveis_deveRetornarOk_quandoUsuarioComPermissao() {
        mvc.perform(get(BASE_URL.concat("/distribuicao/agendamentos/1/disponiveis")))
            .andExpect(status().isOk());

        verify(usuarioAgendamentoService).recuperarUsuariosDisponiveisParaDistribuicao(1);
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void getUsuariosParaDistribuicaoByEquipeVendaId_deveRetornarUnauthorized_quandoUsuarioNaoAutenticado() {
        mvc.perform(get(BASE_URL.concat("/distribuicao/agendamentos/equipe-venda/1")))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @SneakyThrows
    @WithMockUser
    public void getUsuariosParaDistribuicaoByEquipeVendaId_deveRetornarForbidden_quandoUsuarioSemPermissao() {
        mvc.perform(get(BASE_URL.concat("/distribuicao/agendamentos/equipe-venda/1")))
            .andExpect(status().isForbidden());
    }

    @Test
    @SneakyThrows
    @WithMockUser(roles = {ROLE_MLG_5013})
    public void getUsuariosParaDistribuicaoByEquipeVendaId_deveRetornarOk_quandoUsuarioComPermissao() {
        mvc.perform(get(BASE_URL.concat("/distribuicao/agendamentos/equipe-venda/1")))
            .andExpect(status().isOk());

        verify(usuarioAgendamentoService).getUsuariosParaDistribuicaoByEquipeVendaId(1);
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void getUsuariosParaDistribuicaoDeAgendamentos_deveRetornarUnauthorized_quandoUsuarioNaoAutenticado() {
        mvc.perform(get(BASE_URL.concat("/distribuicao/agendamentos/1/agenteautorizado/2"))
                .param("tipoContato", "PRESENCIAL"))
            .andExpect(status().isUnauthorized());

        verifyNoMoreInteractions(usuarioAgendamentoService);
    }

    @Test
    @SneakyThrows
    @WithMockUser
    public void getUsuariosParaDistribuicaoDeAgendamentos_deveRetornarForbidden_quandoUsuarioSemPermissao() {
        mvc.perform(get(BASE_URL.concat("/distribuicao/agendamentos/1/agenteautorizado/2"))
                .param("tipoContato", "PRESENCIAL"))
            .andExpect(status().isForbidden());

        verifyNoMoreInteractions(usuarioAgendamentoService);
    }

    @Test
    @SneakyThrows
    @WithMockUser(roles = {ROLE_MLG_5013})
    public void getUsuariosParaDistribuicaoDeAgendamentos_deveRetornarOk_quandoUsuarioComPermissao() {
        mvc.perform(get(BASE_URL.concat("/distribuicao/agendamentos/1/agenteautorizado/2"))
                .param("tipoContato", "PRESENCIAL"))
            .andExpect(status().isOk());

        verify(usuarioAgendamentoService).recuperarUsuariosParaDistribuicao(1, 2, "PRESENCIAL");
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void findUsuarioProspeccaoByCidade_deveRetornarOk_quandoUsuarioNaoAutenticado() {
        mvc.perform(get(BASE_URL.concat("/usuario-funil-prospeccao"))
                .param("cidade", "londrina"))
            .andExpect(status().isOk());

        verify(usuarioFunilProspeccaoService).findUsuarioDirecionadoByCidade("londrina");
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void findUsuariosExecutivos_deveRetornarUnauthorized_quandoUsuarioNaoAutenticado() {
        mvc.perform(get(BASE_URL.concat("/executivos")))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @SneakyThrows
    @WithMockUser
    public void findUsuariosExecutivos_deveRetornarOk_quandoUsuarioAutenticado() {
        mvc.perform(get(BASE_URL.concat("/executivos")))
            .andExpect(status().isOk());

        verify(usuarioService).buscarExecutivosPorSituacao(ESituacao.A);
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void findUsuarioIdsAlvoDosComunicados_deveRetornarUnauthorized_quandoUsuarioNaoAutenticado() {
        mvc.perform(get(BASE_URL.concat("/ids/alvo/comunicado")))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @SneakyThrows
    @WithMockUser
    public void findUsuarioIdsAlvoDosComunicados_deveRetornarOk_quandoUsuarioAutenticado() {
        mvc.perform(get(BASE_URL.concat("/ids/alvo/comunicado")))
            .andExpect(status().isOk());

        verify(usuarioService).getIdDosUsuariosAlvoDoComunicado(any());
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void findCidadesDoUsuarioLogado_deveRetornarUnauthorized_quandoUsuarioNaoAutenticado() {
        mvc.perform(get(BASE_URL.concat("/cidades")))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @SneakyThrows
    @WithMockUser
    public void findCidadesDoUsuarioLogado_deveRetornarOk_quandoUsuarioAutenticado() {
        mvc.perform(get(BASE_URL.concat("/cidades")))
            .andExpect(status().isOk());

        verify(usuarioService).findCidadesDoUsuarioLogado();
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void findUsuarioAlvoDosComunicados_deveRetornarUnauthorized_quandoUsuarioNaoAutenticado() {
        mvc.perform(get(BASE_URL.concat("/alvo/comunicado")))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @SneakyThrows
    @WithMockUser
    public void findUsuarioAlvoDosComunicados_deveRetornarOk_quandoUsuarioAutenticado() {
        mvc.perform(get(BASE_URL.concat("/alvo/comunicado")))
            .andExpect(status().isOk());

        verify(usuarioService).getUsuariosAlvoDoComunicado(any());
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void findById_deveRetornarUnauthorized_quandoUsuarioNaoAutenticado() {
        mvc.perform(get(BASE_URL.concat("/1/sem-permissoes")))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @SneakyThrows
    @WithMockUser
    public void findById_deveRetornarOk_quandoUsuarioAutenticado() {
        mvc.perform(get(BASE_URL.concat("/1/sem-permissoes")))
            .andExpect(status().isOk());

        verify(usuarioService).findById(1);
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void findUsuariosByCodigoCargo_deveRetornarUnauthorized_quandoUsuarioNaoAutenticado() {
        mvc.perform(get(BASE_URL.concat("/cargo/ADMINISTRADOR")))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @SneakyThrows
    @WithMockUser
    public void findUsuariosByCodigoCargo_deveRetornarOk_quandoUsuarioAutenticado() {
        mvc.perform(get(BASE_URL.concat("/cargo/ADMINISTRADOR")))
            .andExpect(status().isOk());

        verify(usuarioService).findUsuariosByCodigoCargo(ADMINISTRADOR);
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void findIdUsuariosAtivosByCodigoCargos_deveRetornarUnauthorized_quandoUsuarioNaoAutenticado() {
        mvc.perform(get(BASE_URL.concat("/cargos")))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @SneakyThrows
    @WithMockUser
    public void findIdUsuariosAtivosByCodigoCargos_deveRetornarOk_quandoUsuarioAutenticado() {
        mvc.perform(get(BASE_URL.concat("/cargos"))
                .param("codigoCargos", "ADMINISTRADOR"))
            .andExpect(status().isOk());

        verify(usuarioService).findIdUsuariosAtivosByCodigoCargos(List.of(ADMINISTRADOR));
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void findUsuariosByIds_deveRetornarUnauthorized_quandoUsuarioNaoAutenticado() {
        mvc.perform(get(BASE_URL.concat("/usuario-situacao")))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @SneakyThrows
    @WithMockUser
    public void findUsuariosByIds_deveRetornarOk_quandoUsuarioAutenticado() {
        mvc.perform(get(BASE_URL.concat("/usuario-situacao"))
                .param("usuariosIds", "1"))
            .andExpect(status().isOk());

        verify(usuarioService).findUsuariosByIds(List.of(1));
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void validarUsuarioBloqueadoPorExcessoDeUso_deveRetornarUnauthorized_quandoUsuarioNaoAutenticado() {
        mvc.perform(get(BASE_URL.concat("/inativado-por-excesso-de-uso/1")))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @SneakyThrows
    @WithMockUser
    public void validarUsuarioBloqueadoPorExcessoDeUso_deveRetornarOk_quandoUsuarioAutenticado() {
        mvc.perform(get(BASE_URL.concat("/inativado-por-excesso-de-uso/1")))
            .andExpect(status().isOk());

        verify(deslogarUsuarioPorExcessoDeUsoService).validarUsuarioBloqueadoPorExcessoDeUso(1);
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void getUrlLojaOnline_deveRetornarUnauthorized_quandoUsuarioNaoAutenticado() {
        mvc.perform(get(BASE_URL.concat("/1/url-loja-online")))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @SneakyThrows
    @WithMockUser
    public void getUrlLojaOnline_deveRetornarOk_quandoUsuarioAutenticado() {
        mvc.perform(get(BASE_URL.concat("/1/url-loja-online")))
            .andExpect(status().isOk());

        verify(usuarioService).getUrlLojaOnline(1);
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void getUsuarioByIdComLoginNetSales_deveRetornarUnauthorized_quandoUsuarioNaoAutenticado() {
        mvc.perform(get(BASE_URL.concat("/1/com-login-netsales")))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @SneakyThrows
    @WithMockUser
    public void getUsuarioByIdComLoginNetSales_deveRetornarOk_quandoUsuarioAutenticado() {
        mvc.perform(get(BASE_URL.concat("/1/com-login-netsales")))
            .andExpect(status().isOk());

        verify(usuarioService).getUsuarioByIdComLoginNetSales(1);
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void findUsuariosOperadoresBackofficeByOrganizacao_deveRetornarUnauthorized_quandoUsuarioNaoAutenticado() {
        mvc.perform(get(BASE_URL)
                .param("organizacaoId", "1"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @SneakyThrows
    @WithMockUser
    public void findUsuariosOperadoresBackofficeByOrganizacao_deveRetornarOk_quandoUsuarioAutenticado() {
        mvc.perform(get(BASE_URL)
                .param("organizacaoId", "1")
                .param("buscarInativos", "false"))
            .andExpect(status().isOk());

        verify(usuarioService).findUsuariosOperadoresBackofficeByOrganizacaoEmpresa(1, false, null);
    }

    @Test
    @SneakyThrows
    @WithMockUser
    public void findUsuariosOperadoresBackofficeByOrganizacao_deveEnviarTrue_quandoNaoPassarParametroNaoObrigatorio() {
        mvc.perform(get(BASE_URL)
                .param("organizacaoId", "1"))
            .andExpect(status().isOk());

        verify(usuarioService).findUsuariosOperadoresBackofficeByOrganizacaoEmpresa(1, true, null);
    }

    @Test
    @SneakyThrows
    @WithMockUser
    public void findUsuariosOperadoresBackofficeByOrganizacao_deveRetornarOk_quandoInformadoCargos() {
        mvc.perform(get(BASE_URL)
                .param("organizacaoId", "1")
                .param("buscarInativos", "false")
                .param("cargos", "BACKOFFICE_ANALISTA_DE_TRATAMENTO_DE_ANTI_FRAUDE"))
            .andExpect(status().isOk());

        verify(usuarioService).findUsuariosOperadoresBackofficeByOrganizacaoEmpresa(1, false,
            List.of(BACKOFFICE_ANALISTA_DE_TRATAMENTO_DE_ANTI_FRAUDE));
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void getAllUsuariosDaHierarquiaD2dDoUserLogado_deveRetornarUnauthorized_quandoUsuarioNaoAutenticado() {
        mvc.perform(get(BASE_URL.concat("/permitidos")))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @SneakyThrows
    @WithMockUser
    public void getAllUsuariosDaHierarquiaD2dDoUserLogado_deveRetornarOk_quandoUsuarioAutenticado() {
        mvc.perform(get(BASE_URL.concat("/permitidos")))
            .andExpect(status().isOk());

        verify(usuarioService).getAllUsuariosDaHierarquiaD2dDoUserLogado();
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void buscarUsuariosDaHierarquiaDoUsuarioLogadoPorCargp_deveRetornarUnauthorized_quandoUsuarioNaoAutenticado() {
        mvc.perform(get(BASE_URL.concat("/permitidos/select")))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @SneakyThrows
    @WithMockUser
    public void buscarUsuariosDaHierarquiaDoUsuarioLogadoPorCargp_deveRetornarOk_quandoUsuarioAutenticado() {
        mvc.perform(get(BASE_URL.concat("/permitidos/select"))
                .param("codigoCargo", "ADMINISTRADOR"))
            .andExpect(status().isOk());

        verify(usuarioService).buscarUsuariosDaHierarquiaDoUsuarioLogado(ADMINISTRADOR);
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void buscarBackOfficesAndSociosAaPorAaIds_deveRetornarUnauthorized_quandoUsuarioNaoAutenticado() {
        mvc.perform(get(BASE_URL.concat("/backoffices-socios-por-agentes-autorizado-id")))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @SneakyThrows
    @WithMockUser
    public void buscarBackOfficesAndSociosAaPorAaIds_deveRetornarOk_quandoUsuarioAutenticado() {
        mvc.perform(get(BASE_URL.concat("/backoffices-socios-por-agentes-autorizado-id"))
                .param("agentesAutorizadoId", "1"))
            .andExpect(status().isOk());

        verify(usuarioService).buscarBackOfficesAndSociosAaPorAaIds(List.of(1));
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void buscarVendedoresFeeder_deveRetornarUnauthorized_quandoUsuarioNaoAutenticado() {
        mvc.perform(get(BASE_URL.concat("/vendedores-feeder")))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @SneakyThrows
    @WithMockUser
    public void buscarVendedoresFeeder_deveRetornarForbidden_quandoUsuarioSemPermissao() {
        mvc.perform(get(BASE_URL.concat("/vendedores-feeder")))
            .andExpect(status().isForbidden());
    }

    @Test
    @SneakyThrows
    @WithMockUser(roles = {ROLE_APPLICATION})
    public void buscarVendedoresFeeder_deveRetornarBadRequest_quandoNaoEnviarCaomposObrigatorios() {
        mvc.perform(get(BASE_URL.concat("/vendedores-feeder")))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[*].message", containsInAnyOrder(
                "O campo aasIds é obrigatório.",
                "O campo comSocioPrincipal é obrigatório.")));
    }

    @Test
    @SneakyThrows
    @WithMockUser(roles = {ROLE_APPLICATION})
    public void buscarVendedoresFeeder_deveRetornarOk_quandoUsuarioAutenticado() {
        mvc.perform(get(BASE_URL.concat("/vendedores-feeder"))
                .param("aasIds", "1")
                .param("comSocioPrincipal", "false")
                .param("buscarInativos", "true"))
            .andExpect(status().isOk());

        var expectedFiltro = new VendedoresFeederFiltros(List.of(1), false, true);

        verify(usuarioService).buscarVendedoresFeeder(expectedFiltro);
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void obterNomeUsuarioPorId_deveRetornarUnauthorized_quandoUsuarioNaoAutenticado() {
        mvc.perform(get(BASE_URL.concat("/1/nome")))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @SneakyThrows
    @WithMockUser
    public void obterNomeUsuarioPorId_deveRetornarOk_quandoUsuarioAutenticado() {
        mvc.perform(get(BASE_URL.concat("/1/nome")))
            .andExpect(status().isOk());

        verify(usuarioService).obterNomeUsuarioPorId(1);
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void buscarUsuarioSituacaoPorIds_deveRetornarUnauthorized_quandoUsuarioNaoAutenticado() {
        mvc.perform(post(BASE_URL.concat("/usuario-situacao/por-ids")))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @SneakyThrows
    @WithMockUser
    public void buscarUsuarioSituacaoPorIds_deveRetornarBadRequest_quandoNaoPassarCampoObrigatorio() {
        mvc.perform(post(BASE_URL.concat("/usuario-situacao/por-ids"))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(new UsuarioSituacaoFiltro())))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[*].message", containsInAnyOrder(
                "O campo usuariosIds é obrigatório.")));
    }

    @Test
    @SneakyThrows
    @WithMockUser
    public void buscarUsuarioSituacaoPorIds_deveRetornarOk_quandoUsuarioAutenticado() {
        var filtro = new UsuarioSituacaoFiltro(List.of(1));
        mvc.perform(post(BASE_URL.concat("/usuario-situacao/por-ids"))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(filtro)))
            .andExpect(status().isOk());

        verify(usuarioService).buscarUsuarioSituacaoPorIds(filtro);
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void buscarUsuariosPorCanalECargo_deveRetornarUnauthorized_quandoUsuarioNaoAutenticado() {
        mvc.perform(get(BASE_URL.concat("/AGENTE_AUTORIZADO/cargo/ADMINISTRADOR")))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @SneakyThrows
    @WithMockUser
    public void buscarUsuariosPorCanalECargo_deveRetornarOk_quandoUsuarioAutenticado() {
        mvc.perform(get(BASE_URL.concat("/AGENTE_AUTORIZADO/cargo/ADMINISTRADOR")))
            .andExpect(status().isOk());

        verify(usuarioService).buscarUsuariosPorCanalECargo(ECanal.AGENTE_AUTORIZADO, ADMINISTRADOR);
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void buscarUsuariosSuperiores_deveRetornarUnauthorized_quandoUsuarioNaoAutenticado() {
        mvc.perform(get(BASE_URL.concat("/usuarios-superiores/1")))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @SneakyThrows
    @WithMockUser
    public void buscarUsuariosSuperiores_deveRetornarOk_quandoUsuarioAutenticado() {
        mvc.perform(get(BASE_URL.concat("/usuarios-superiores/1")))
            .andExpect(status().isOk());

        verify(usuarioService).getSuperioresPorId(1);
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void alterarSituacaoUsuarioBLoqueado_deveRetornarUnauthorized_quandoUsuarioNaoAutenticado() {
        mvc.perform(get(BASE_URL.concat("/alterar-situacao-usuario-bloqueado/1")))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @SneakyThrows
    @WithMockUser
    public void alterarSituacaoUsuarioBLoqueado_deveRetornarOk_quandoUsuarioAutenticado() {
        mvc.perform(get(BASE_URL.concat("/alterar-situacao-usuario-bloqueado/1")))
            .andExpect(status().isOk());

        verify(deslogarUsuarioPorExcessoDeUsoService).atualizarSituacaoUsuarioBloqueado(1);
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void getAllVendedoresReceptivos_deveRetornarUnauthorized_quandoUsuarioNaoAutenticado() {
        mvc.perform(get(BASE_URL.concat("/vendedores-receptivos")))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @SneakyThrows
    @WithMockUser
    public void getAllVendedoresReceptivos_deveRetornarOk_quandoUsuarioAutenticado() {
        mvc.perform(get(BASE_URL.concat("/vendedores-receptivos")))
            .andExpect(status().isOk());

        verify(usuarioService).buscarTodosVendedoresReceptivos();
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void getAllVendedoresReceptivosById_deveRetornarUnauthorized_quandoUsuarioNaoAutenticado() {
        mvc.perform(get(BASE_URL.concat("/vendedores-receptivos/por-ids")))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @SneakyThrows
    @WithMockUser
    public void getAllVendedoresReceptivosById_deveRetornarOk_quandoUsuarioAutenticado() {
        mvc.perform(get(BASE_URL.concat("/vendedores-receptivos/por-ids"))
                .param("ids", "1"))
            .andExpect(status().isOk());

        verify(usuarioService).buscarVendedoresReceptivosPorId(List.of(1));
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void getAllUsuariosReceptivosIdsByOrganizacaoId_deveRetornarUnauthorized_quandoUsuarioNaoAutenticado() {
        mvc.perform(get(BASE_URL + "/usuarios-receptivos/{id}/organizacao", 1))
            .andExpect(status().isUnauthorized());

        verifyZeroInteractions(usuarioService);
    }

    @Test
    @SneakyThrows
    @WithMockUser
    public void getAllUsuariosReceptivosIdsByOrganizacaoId_deveRetornarOk_quandoUsuarioAutenticado() {
        mvc.perform(get(BASE_URL + "/usuarios-receptivos/{id}/organizacao", 1))
            .andExpect(status().isOk());

        verify(usuarioService).buscarUsuariosReceptivosIdsPorOrganizacaoId(1);
    }

    @Test
    @SneakyThrows
    @WithMockUser
    public void getCanaisPermitidosParaOrganizacao_deveRetornarCanaisPermitidos_quandoSolicitado() {
        when(usuarioService.getCanaisPermitidosParaOrganizacao())
            .thenReturn(List.of(SelectResponse.of(ECanal.INTERNET.name(), ECanal.INTERNET.getDescricao())));

        mvc.perform(get(BASE_URL.concat("/canais/organizacao")))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].value", is(ECanal.INTERNET.name())))
            .andExpect(jsonPath("$[0].label", is(ECanal.INTERNET.getDescricao())));

        verify(usuarioService).getCanaisPermitidosParaOrganizacao();
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void buscarSelectUsuariosDaHierarquiaDoUsuarioLogadoPorFiltros_deveRetornarUnauthorized_quandoUsuarioNaoAutenticado() {
        mvc.perform(get(BASE_URL.concat("/permitidos/select/por-filtros")))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @SneakyThrows
    @WithMockUser
    public void buscarSelectUsuariosDaHierarquiaDoUsuarioLogadoPorFiltros_deveRetornarOk_quandoUsuarioAutenticado() {
        mvc.perform(get(BASE_URL.concat("/permitidos/select/por-filtros"))
                .param("ids", "1"))
            .andExpect(status().isOk());

        verify(usuarioService).buscarUsuariosDaHierarquiaDoUsuarioLogadoPorFiltros(any());
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void findByUsuarioId_deveRetornarUnauthorized_quandoUsuarioNaoAutenticado() {
        mvc.perform(get(BASE_URL.concat("/1/subcanal/nivel")))
            .andExpect(status().isUnauthorized());

        verify(usuarioService, never()).findByUsuarioId(anyInt());
    }

    @Test
    @SneakyThrows
    @WithMockUser
    public void findByUsuarioId_deveRetornarOk_quandoUsuarioAutenticado() {
        mvc.perform(get(BASE_URL.concat("/1/subcanal/nivel")))
            .andExpect(status().isOk());

        verify(usuarioService).findByUsuarioId(1);
    }

    @Test
    @SneakyThrows
    @WithMockUser
    public void findByUsuarioId_deveRetornarNotFound_quandoUsuarioNaoExistir() {
        doThrow(new NotFoundException("")).when(usuarioService).findByUsuarioId(1);

        mvc.perform(get(BASE_URL.concat("/1/subcanal/nivel")))
            .andExpect(status().isNotFound());

        verify(usuarioService).findByUsuarioId(1);
    }

    @Test
    @SneakyThrows
    public void getUsuariosById_deveRetornarUnauthorized_quandoUsuarioNaoAutenticado() {
        mvc.perform(post(BASE_URL.concat("/buscar-todos")))
            .andExpect(status().isUnauthorized());

        verify(usuarioService, never()).getUsuariosByIdsTodasSituacoes(any());
    }

    @Test
    @SneakyThrows
    @WithMockUser
    public void getUsuariosById_deveRetornarOk_quandoUsuarioAutenticado() {
        mvc.perform(post(BASE_URL.concat("/buscar-todos"))
                .content(String.valueOf(List.of(1, 2)))
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(usuarioService).getUsuariosByIdsTodasSituacoes(List.of(1, 2));
    }

    @Test
    @SneakyThrows
    public void getSubordinadosAndAasDoUsuario_deveRetornarUnathorized_quandoUsuarioNaoAutenticado() {
        mvc.perform(get(BASE_URL.concat("/hierarquia/subordinados-aas"))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized());

        verify(usuarioService, never()).getSubordinadosAndAasDoUsuario(anyBoolean());
    }

    @Test
    @SneakyThrows
    @WithMockUser
    public void getSubordinadosAndAasDoUsuario_deveRetornarOk_quandoUsuarioAutenticado() {
        mvc.perform(get(BASE_URL.concat("/hierarquia/subordinados-aas"))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(usuarioService).getSubordinadosAndAasDoUsuario(false);
    }

    @Test
    @SneakyThrows
    @WithMockUser
    public void findUsuarioByCpfComSituacaoAtivoOuInativo_deveRetornarOk_quandoUsuarioAutenticado() {
        var cpf = "98471883007";
        doReturn(umUsuarioResponse())
            .when(usuarioService)
            .findUsuarioByCpfComSituacaoAtivoOuInativo(cpf);

        mvc.perform(get(BASE_URL + "/obter-usuario-por-cpf")
                .param("cpf", cpf)
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(1)))
            .andExpect(jsonPath("$.situacao", is("A")))
            .andExpect(jsonPath("$.cpf", is("98471883007")))
            .andExpect(jsonPath("$.nome", is("Usuario Ativo")))
            .andExpect(jsonPath("$.email", is("usuarioativo@email.com")));

        verify(usuarioService).findUsuarioByCpfComSituacaoAtivoOuInativo(cpf);
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void findUsuarioByCpfComSituacaoAtivoOuInativo_deveRetornarUnauthorized_quandoUsuarioNaoAutenticado() {
        mvc.perform(get(BASE_URL + "/obter-usuario-por-cpf/31114231827"))
            .andExpect(status().isUnauthorized());

        verifyNoMoreInteractions(usuarioService);
    }

    @Test
    @SneakyThrows
    @WithMockUser
    public void findUsuarioByEmailComSituacaoAtivoOuInativo_deveRetornarOk_quandoUsuarioAutenticado() {
        var email = "usuarioativo@email.com";
        doReturn(umUsuarioResponse())
            .when(usuarioService)
            .findUsuarioByEmailComSituacaoAtivoOuInativo(email);

        mvc.perform(get(BASE_URL + "/obter-usuario-por-email")
                .param("email", email)
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(1)))
            .andExpect(jsonPath("$.situacao", is("A")))
            .andExpect(jsonPath("$.cpf", is("98471883007")))
            .andExpect(jsonPath("$.nome", is("Usuario Ativo")))
            .andExpect(jsonPath("$.email", is("usuarioativo@email.com")));

        verify(usuarioService).findUsuarioByEmailComSituacaoAtivoOuInativo(email);
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void findUsuarioByEmailComSituacaoAtivoOuInativo_deveRetornarUnauthorized_quandoUsuarioNaoAutenticado() {
        mvc.perform(get(BASE_URL + "/obter-usuario-por-email")
                .param("email", "usuarioativo@email.com")
            )
            .andExpect(status().isUnauthorized());

        verifyNoMoreInteractions(usuarioService);
    }

    private static UsuarioResponse umUsuarioResponse() {
        return UsuarioResponse
            .builder()
            .id(1)
            .situacao(A)
            .cpf("98471883007")
            .nome("Usuario Ativo")
            .email("usuarioativo@email.com")
            .build();
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void findByCpf_deveRetornarUnauthorized_quandoUsuarioNaoAutenticado() {
        mvc.perform(get(BASE_URL.concat("/cpf")))
            .andExpect(status().isUnauthorized());
        verify(usuarioService, never()).findByCpf(anyString());
    }

    @Test
    @SneakyThrows
    @WithMockUser
    public void findByCpf_deveRetornarOk_quandoUsuarioAutenticado() {
        var usuarioSubCanalNivelResponse = UsuarioSubCanalNivelResponse.builder()
            .id(1)
            .nome("nome")
            .build();
        when(usuarioService.findByCpf("00590878900"))
            .thenReturn(usuarioSubCanalNivelResponse);

        mvc.perform(get(BASE_URL.concat("/cpf"))
                .param("cpf", "00590878900"))
            .andExpect(jsonPath("$.id", is(1)))
            .andExpect(jsonPath("$.nome", is("nome")))
            .andExpect(status().isOk());

        verify(usuarioService).findByCpf("00590878900");
    }

    @Test
    @SneakyThrows
    public void moverAvatarMinio_deveRetornarUnauthorized_quandoNaoAutenticado() {
        mvc.perform(post(BASE_URL.concat("/mover-avatar-minio"))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized());

        verify(usuarioService, never()).moverAvatarMinio();
    }

    @Test
    @SneakyThrows
    @WithMockUser
    public void moverAvatarMinio_deveRetornarOk_quandoAutenticado() {
        mvc.perform(post(BASE_URL.concat("/mover-avatar-minio"))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(usuarioService).moverAvatarMinio();
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void findOperadoresBkoCentralizadoByFornecedor_deveRetornarUnauthorized_quandoUsuarioNaoAutenticado() {
        mvc.perform(get(BASE_URL.concat("/bko-centralizado/8")))
            .andExpect(status().isUnauthorized());

        verify(usuarioService, never()).findOperadoresBkoCentralizadoByFornecedor(anyInt(), anyBoolean());
    }

    @Test
    @SneakyThrows
    @WithMockUser
    public void findOperadoresBkoCentralizadoByFornecedor_deveRetornarForbidden_quandoUsuarioSemPermissao() {
        mvc.perform(get(BASE_URL.concat("/bko-centralizado/8")))
            .andExpect(status().isForbidden());

        verify(usuarioService, never()).findOperadoresBkoCentralizadoByFornecedor(anyInt(), anyBoolean());
    }

    @Test
    @SneakyThrows
    @WithMockUser(roles = {"BKO_PRIORIZAR_INDICACOES"})
    public void findOperadoresBkoCentralizadoByFornecedor_deveRetornarBadRequest_quandoNaoPassarParametro() {
        mvc.perform(get(BASE_URL.concat("/bko-centralizado/"))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());

        verify(usuarioService, never()).findOperadoresBkoCentralizadoByFornecedor(anyInt(), anyBoolean());
    }

    @Test
    @SneakyThrows
    @WithMockUser(roles = {"BKO_PRIORIZAR_INDICACOES"})
    public void findOperadoresBkoCentralizadoByFornecedor_deveRetornarOk_quandoUsuarioAutenticado() {
        mvc.perform(get(BASE_URL.concat("/bko-centralizado/8"))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(usuarioService).findOperadoresBkoCentralizadoByFornecedor(8, false);
    }

    @Test
    @SneakyThrows
    @WithMockUser
    public void findUsuarioD2dByCpf_deveRetornarOk_quandoUsuarioExistir() {
        mvc.perform(get(BASE_URL + "/d2d")
                .param("cpf", "38957979875"))
            .andExpect(status().isOk());

        verify(usuarioService).findUsuarioD2dByCpf("38957979875");
    }

    @Test
    @SneakyThrows
    @WithMockUser
    public void findUsuarioD2dByCpf_naoDeveRetornarNotFound_quandoUsuarioNaoExistir() {
        mvc.perform(get(BASE_URL + "/d2d")
                .param("cpf", "00000000000"))
            .andExpect(status().isOk());

        verify(usuarioService).findUsuarioD2dByCpf("00000000000");
    }

    @Test
    @SneakyThrows
    @WithMockUser(roles = "BKO_21420")
    public void findOperadoresSuporteVendasByOrganizacao_deveRetornarOk_quandoPossuirPermissao() {
        mvc.perform(get(BASE_URL.concat("/suporte-vendas/operadores/1"))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(usuarioService).findOperadoresSuporteVendasByOrganizacao(1);
    }

    @Test
    @SneakyThrows
    @WithMockUser
    public void findOperadoresSuporteVendasByOrganizacao_deveRetornarForbidden_quandoNaoPossuirPermissao() {
        mvc.perform(get(BASE_URL.concat("/suporte-vendas/operadores/1"))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isForbidden());

        verifyZeroInteractions(usuarioService);
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void findOperadoresSuporteVendasByOrganizacao_deveRetornarUnauthorized_quandoNaoAutenticado() {
        mvc.perform(get(BASE_URL.concat("/suporte-vendas/operadores/1"))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized());

        verifyZeroInteractions(usuarioService);
    }

    @Test
    @SneakyThrows
    @WithMockUser(username = ADMIN, roles = {"AUT_VISUALIZAR_USUARIO"})
    public void findByCpfAndSituacaoIsNot_deveBuscarUsuarioPorCpfESituacao_seUsuarioAutenticado() {
        mvc.perform(get(BASE_URL)
                .param("cpf", "123.456.789-10")
                .param("situacao", "R"))
            .andExpect(status().isOk());

        verify(usuarioService).findByCpfAndSituacaoIsNot("123.456.789-10", ESituacao.R);
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void findByCpfAndSituacaoIsNot_deveRetornarUnauthorized_seUsuarioSemAutorizacao() {
        mvc.perform(get(BASE_URL)
                .param("cpf", "123.456.789-10")
                .param("situacao", "R"))
            .andExpect(status().isUnauthorized());

        verifyNoMoreInteractions(usuarioService);
    }

    @Test
    @WithMockUser
    @SneakyThrows
    public void findColaboradoresPapIndireto_deveRetornarListaColaboradorPapIndireto_quandoUsuarioAutenticado() {
        mvc.perform(get(BASE_URL.concat("/obter-colaboradores-aa-pap-indireto"))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(usuarioService).findColaboradoresPapIndireto();
    }

    @Test
    @SneakyThrows
    public void findColaboradoresPapIndireto_deveRetornarUnauthorized_quandoUsuarioNaoAutenticado() {
        mvc.perform(get(BASE_URL.concat("/obter-colaboradores-aa-pap-indireto"))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized());

        verify(usuarioService, never()).findColaboradoresPapIndireto();
    }

    @Test
    @SneakyThrows
    @WithMockUser(username = ADMIN, roles = {"POL_GERENCIAR_AA"})
    public void obterIdSeUsuarioForSocioOuAceite_deveRetornarOk_seUsuarioAutenticado() {
        mvc.perform(get(BASE_URL + "/socio-principal/verificar-cpf-email")
                .param("cpf", "42675562700")
                .param("email", "NOVOSOCIO.PRINCIPAL@EMPRESA.COM.BR"))
            .andExpect(status().isOk());

        verify(usuarioService).obterIdSeUsuarioForSocioOuAceite(
            "42675562700",
            "NOVOSOCIO.PRINCIPAL@EMPRESA.COM.BR");
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void obterIdSeUsuarioForSocioOuAceite_deveRetornarUnauthorized_seUsuarioSemAutorizacao() {
        mvc.perform(get(BASE_URL + "/socio-principal/verificar-cpf-email")
                .param("cpf", "42675562700")
                .param("email", "NOVOSOCIO.PRINCIPAL@EMPRESA.COM.BR"))
            .andExpect(status().isUnauthorized());

        verifyNoMoreInteractions(usuarioService);
    }

    @Test
    @SneakyThrows
    @WithMockUser
    public void getEmailsByCargoId_deveRetornarOk_quandoUsuarioAutenticado() {
        mvc.perform(get(BASE_URL.concat("/buscar-emails/1"))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(usuarioService).getEmailsByCargoId(1);
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void getEmailsByCargoId_deveRetornarUnauthorized_quandoUsuarioNaoAutenticado() {
        mvc.perform(get(BASE_URL.concat("/buscar-emails/1"))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized());

        verifyNoMoreInteractions(usuarioService);
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void getUsuariosIdsByPermissao_deveRetornarUnauthorized_quandoUsuarioNaoAutenticado() {
        mvc.perform(get(BASE_URL + "/funcionalidade/role_101"))
            .andExpect(status().isUnauthorized());

        verifyNoMoreInteractions(usuarioService);
    }

    @Test
    @SneakyThrows
    @WithMockUser
    public void getUsuariosIdsByPermissao_deveRetornarOk_quandoUsuarioAutenticado() {
        mvc.perform(get(BASE_URL + "/funcionalidade/role_101"))
            .andExpect(status().isOk());

        verify(usuarioService).getUsuariosIdByPermissaoEspecial("role_101");
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    public void getSociosIdsAtivosByUsuariosIds_deveRetornarUnauthorized_quandoUsuarioNaoAutenticado() {
        mvc.perform(post(BASE_URL + "/socios-ids-ativos"))
            .andExpect(status().isUnauthorized());

        verifyNoMoreInteractions(usuarioService);
    }

    @Test
    @SneakyThrows
    @WithMockUser
    public void getSociosIdsAtivosByUsuariosIds_deveRetornarOk_quandoUsuarioAutenticado() {
        mvc.perform(post(BASE_URL + "/socios-ids-ativos")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(List.of(1, 2))))
            .andExpect(status().isOk());

        verify(usuarioService).findSociosIdsAtivosByUsuariosIds(List.of(1, 2));
    }
}
