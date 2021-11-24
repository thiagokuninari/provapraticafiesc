package br.com.xbrain.autenticacao.modules.usuario.controller;

import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioDistribuicaoResponse;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo;
import br.com.xbrain.autenticacao.modules.usuario.service.UsuarioAgendamentoService;
import br.com.xbrain.autenticacao.modules.usuario.service.UsuarioService;
import helpers.Usuarios;
import lombok.SneakyThrows;
import org.junit.Before;
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
import java.util.Set;

import static helpers.TestsHelper.getAccessToken;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Sql(scripts = {"classpath:/tests_database.sql", "classpath:/tests_hierarquia.sql"})
public class UsuarioControllerUnitTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private UsuarioService usuarioService;
    @MockBean
    private UsuarioAgendamentoService usuarioAgendamentoService;

    @Before
    public void setup() {
        when(usuarioService.getIdDosUsuariosSubordinados(any(), anyBoolean()))
                .thenReturn(Arrays.asList(1, 2, 3));

        when(usuarioService.getIdsSubordinadosDaHierarquia(100,
            Set.of(CodigoCargo.SUPERVISOR_OPERACAO.name())))
            .thenReturn(Arrays.asList(1, 2));

        when(usuarioService.getIdsSubordinadosDaHierarquia(100,
            Set.of(CodigoCargo.SUPERVISOR_OPERACAO.name(),
                   CodigoCargo.COORDENADOR_OPERACAO.name())))
            .thenReturn(Arrays.asList(1, 2, 3, 4));
    }

    @Test
    @SneakyThrows
    public void getIdsDasHierarquias_deveRetornarLista_quandoUnicoCargo() {
        mvc.perform(get("/api/usuarios/100/subordinados/cargos/SUPERVISOR_OPERACAO")
                .header("Authorization", getAccessToken(mvc, Usuarios.OPERACAO_GERENTE_COMERCIAL))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    @SneakyThrows
    public void getIdsDasHierarquias_deveRetornarLista_quandoMultiplosCargos() {
        mvc.perform(get("/api/usuarios/100/subordinados/cargos/SUPERVISOR_OPERACAO,COORDENADOR_OPERACAO")
                .header("Authorization", getAccessToken(mvc, Usuarios.OPERACAO_GERENTE_COMERCIAL))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(4)));
    }

    @Test
    public void deveRetornarNenhumaCidadeParaOUsuario() throws Exception {
        mvc.perform(get("/api/usuarios/101/subordinados?incluirProprio=true")
                .header("Authorization", getAccessToken(mvc, Usuarios.HELP_DESK))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)));
    }

    @Test
    public void deveRetornarUsuariosSubordinadosDoUsuarioSelecionado() throws Exception {
        mvc.perform(get("/api/usuarios/hierarquia/subordinados/110")
                .header("Authorization", getAccessToken(mvc, Usuarios.HELP_DESK))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    public void umaListaUsuarioDistribuicaoResponse_200_quandoBuscarUsuariosDaEquipeParaDistribuirAgendamentos()
        throws Exception {
        when(usuarioAgendamentoService.getUsuariosParaDistribuicaoByEquipeVendaId(100))
            .thenReturn(umaListaUsuarioDistribuicaoResponse());

        mvc.perform(get("/api/usuarios/distribuicao/agendamentos/equipe-venda/100")
            .header("Authorization", getAccessToken(mvc, Usuarios.ADMIN))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[0].id").value(1))
            .andExpect(jsonPath("$[0].nome").value("RENATO"))
            .andExpect(jsonPath("$[1].id").value(2))
            .andExpect(jsonPath("$[1].nome").value("JOAO"));
    }

    private List<UsuarioDistribuicaoResponse> umaListaUsuarioDistribuicaoResponse() {
        return List.of(
            UsuarioDistribuicaoResponse.builder()
                .id(1)
                .nome("RENATO")
                .build(),
            UsuarioDistribuicaoResponse.builder()
                .id(2)
                .nome("JOAO")
                .build()
        );
    }
}
