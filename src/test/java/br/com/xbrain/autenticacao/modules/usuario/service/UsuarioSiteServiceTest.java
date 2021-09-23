package br.com.xbrain.autenticacao.modules.usuario.service;

import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo;
import helpers.TestBuilders;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UsuarioSiteServiceTest {

    @InjectMocks
    private UsuarioSiteService service;
    @Mock
    private UsuarioService usuarioService;

    @Test
    public void getUsuariosDaHierarquiaDoUsuarioLogado_naoDeveRetornarUsuarios_seNaoEncontrado() {
        when(usuarioService.getUsuariosDaHierarquiaAtivoLocalDoUsuarioLogado())
            .thenReturn(Collections.emptyList());

        assertThat(service.getUsuariosDaHierarquiaDoUsuarioLogado())
            .isEmpty();
    }

    @Test
    public void getUsuariosDaHierarquiaDoUsuarioLogado_deveRetornarUsuarios_seEncontrado() {
        when(usuarioService.getUsuariosDaHierarquiaAtivoLocalDoUsuarioLogado())
            .thenReturn(List.of(
                TestBuilders.umUsuario(1, CodigoCargo.GERENTE_OPERACAO),
                TestBuilders.umUsuario(2, CodigoCargo.SUPERVISOR_OPERACAO)));

        assertThat(service.getUsuariosDaHierarquiaDoUsuarioLogado())
            .isEqualTo(List.of(
                TestBuilders.umUsuario(1, CodigoCargo.GERENTE_OPERACAO),
                TestBuilders.umUsuario(2, CodigoCargo.SUPERVISOR_OPERACAO)));
    }
}
