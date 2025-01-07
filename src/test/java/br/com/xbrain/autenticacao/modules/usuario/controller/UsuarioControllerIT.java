package br.com.xbrain.autenticacao.modules.usuario.controller;

import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioDistribuicaoResponse;
import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioHierarquiaDto;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo;
import br.com.xbrain.autenticacao.modules.usuario.service.UsuarioAgendamentoService;
import br.com.xbrain.autenticacao.modules.usuario.service.UsuarioService;
import lombok.SneakyThrows;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

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
public class UsuarioControllerIT {

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
    @WithMockUser
    public void getSubordinadosAndAasDoUsuario_deveRetornarSubordinadosDoUsuario_quandoTudoOk() {
        when(usuarioService.getSubordinadosAndAasDoUsuario(false))
            .thenReturn(umaListaDeUsuariosHierarquiaDtos(1, 2));

        mvc.perform(get("/api/usuarios/hierarquia/subordinados-aas")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[0].id").value(1))
            .andExpect(jsonPath("$[0].cpf").value("12345678911"))
            .andExpect(jsonPath("$[1].id").value(2))
            .andExpect(jsonPath("$[1].cnpj").value("87389372672"));

        verify(usuarioService, times(1))
            .getSubordinadosAndAasDoUsuario(eq(false));
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

    private List<UsuarioHierarquiaDto> umaListaDeUsuariosHierarquiaDtos(Integer id, Integer outroId) {
        return List.of(umUsuarioHierarquiaDto(id),
            umOutroUsuarioHierarquiaDto(outroId));
    }

    private UsuarioHierarquiaDto umUsuarioHierarquiaDto(Integer id) {
        return UsuarioHierarquiaDto.builder()
            .id(id)
            .razaoSocialNome("Uma razao social")
            .situacao("Ativo")
            .cpf("12345678911")
            .build();
    }

    private UsuarioHierarquiaDto umOutroUsuarioHierarquiaDto(Integer id) {
        return UsuarioHierarquiaDto.builder()
            .id(id)
            .razaoSocialNome("Uma outra razao social")
            .situacao("CONTRATO ATIVO")
            .cnpj("87389372672")
            .build();
    }
}
