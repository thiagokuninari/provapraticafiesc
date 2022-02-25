package br.com.xbrain.autenticacao.modules.equipevenda.service;

import br.com.xbrain.autenticacao.modules.equipevenda.dto.EquipeVendaUsuarioResponse;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class EquipeVendasUsuarioServiceTest {

    @InjectMocks
    private EquipeVendasUsuarioService equipeVendasUsuarioService;

    @Mock
    private EquipeVendasUsuarioClient equipeVendasUsuarioClient;

    @Test
    public void getAll_deveRetornarListaComUsuariosDaEquipeAtiva_quandoBuscarPelaEquipeVendaId() {
        var usuarios = umaListaUsuariosDaEquipeVenda();

        Map<String, Object> filtros = Map.of("ativo", true, "equipeVendaId", 10);

        when(equipeVendasUsuarioClient.getAll(filtros)).thenReturn(usuarios);

        assertThat(equipeVendasUsuarioService.getAll(filtros)).isEqualTo(usuarios);
    }

    @Test
    public void buscarUsuarioPorId_deveRetornarListValorUnico_quandoObtiverResultado() {
        when(equipeVendasUsuarioClient.buscarUsuarioPorId(any()))
            .thenReturn(List.of(1001));
        Assertions.assertThat(equipeVendasUsuarioService.buscarUsuarioEquipeVendasPorId(1))
            .containsExactlyInAnyOrder(1001);
    }

    @Test
    public void buscarUsuarioPorId_deveRetornarListVazio_quandoObtiverResultado() {
        when(equipeVendasUsuarioClient.buscarUsuarioPorId(any()))
            .thenReturn(List.of());
        Assertions.assertThat(equipeVendasUsuarioService.buscarUsuarioEquipeVendasPorId(1))
            .isEmpty();
    }

    private List<EquipeVendaUsuarioResponse> umaListaUsuariosDaEquipeVenda() {
        return List.of(
            EquipeVendaUsuarioResponse.builder()
                .usuarioId(1)
                .usuarioNome("RENATO")
                .equipeVendaId(10)
                .build(),
            EquipeVendaUsuarioResponse.builder()
                .usuarioId(1)
                .usuarioNome("JOAO")
                .equipeVendaId(10)
                .build()
        );
    }
}
