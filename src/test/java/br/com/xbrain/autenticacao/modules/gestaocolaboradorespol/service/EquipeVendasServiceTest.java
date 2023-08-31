package br.com.xbrain.autenticacao.modules.gestaocolaboradorespol.service;

import br.com.xbrain.autenticacao.modules.equipevenda.dto.EquipeVendaDto;
import br.com.xbrain.autenticacao.modules.gestaocolaboradorespol.client.EquipeVendasClient;
import br.com.xbrain.autenticacao.modules.gestaocolaboradorespol.dto.EquipeVendasSupervisionadasResponse;
import feign.RetryableException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class EquipeVendasServiceTest {

    @Mock
    private EquipeVendasClient client;
    @InjectMocks
    private EquipeVendasService service;

    @Test
    public void getUsuarioEEquipeByUsuarioIds_deveRetornarMapVazio_quandoListaVazia() {
        assertThat(service.getUsuarioEEquipeByUsuarioIds(List.of())).isEqualTo(Map.of());

        verify(client, never()).getUsuarioEEquipeByUsuarioIds(anyList());
    }

    @Test
    public void getUsuarioEEquipeByUsuarioIds_deveRetornarMap_quandoEncontrar() {
        var resultMap = new HashMap<Integer, Integer>();
        resultMap.put(1, 1);
        resultMap.put(2, 2);

        doReturn(resultMap)
            .when(client)
            .getUsuarioEEquipeByUsuarioIds(anyList());

        assertThat(service.getUsuarioEEquipeByUsuarioIds(List.of(1, 2))).isEqualTo(resultMap);

        verify(client, times(1)).getUsuarioEEquipeByUsuarioIds(List.of(1, 2));
    }

    @Test
    public void getUsuarioEEquipeByUsuarioIds_deveRetornarMapVazio_quandoClientRetornarErro() {
        doThrow(RetryableException.class)
            .when(client).getUsuarioEEquipeByUsuarioIds(anyList());

        assertThat(service.getUsuarioEEquipeByUsuarioIds(List.of(1, 2)))
            .isEqualTo(Map.of());

        verify(client, times(1)).getUsuarioEEquipeByUsuarioIds(List.of(1, 2));
    }

    @Test
    public void getEquipesPorSupervisor_deveRetornarEquipesVendasPorSupervisor_quandoSolicitado() {
        doReturn(List.of(umaEquipeVendasSupervisionadasResponse()))
            .when(client)
            .getEquipesPorSupervisor(1);

        assertThat(service.getEquipesPorSupervisor(1))
            .extracting(EquipeVendasSupervisionadasResponse::getId, EquipeVendasSupervisionadasResponse::getCanalVenda,
                EquipeVendasSupervisionadasResponse::getDescricao)
            .containsExactlyInAnyOrder(
                tuple(1, "D2D", "Equipe Teste")
            );

        verify(client).getEquipesPorSupervisor(1);
    }

    @Test
    public void getEquipesPorSupervisor_deveRetornarListaDeEquipesVendasVazia_quandoNaoEncontrarPorSupervisorInformado() {
        assertThat(service.getEquipesPorSupervisor(1)).isEmpty();

        verify(client).getEquipesPorSupervisor(1);
    }

    @Test
    public void getEquipesPorSupervisor_deveRetornarListaDeEquipesVendasVazia_quandoErroNaApi() {
        doThrow(RetryableException.class)
            .when(client)
            .getEquipesPorSupervisor(1);

        assertThat(service.getEquipesPorSupervisor(1)).isEmpty();

        verify(client).getEquipesPorSupervisor(1);
    }

    @Test
    public void getByUsuario_deveRetornarEquipeVendasDto_quandoSolicitado() {
        doReturn(umaEquipeVendaDto())
            .when(client)
            .getByUsuario(1);

        assertThat(service.getByUsuario(1))
            .extracting(EquipeVendaDto::getId, EquipeVendaDto::getCanalVenda, EquipeVendaDto::getDescricao)
            .containsExactlyInAnyOrder(1, "TELEVENDAS", "Equipe Teste 2");

        verify(client).getByUsuario(1);
    }

    @Test
    public void getByUsuario_deveRetornarListaDeEquipesVendasVazia_quandoNaoEncontrarPorSupervisorInformado() {
        assertThat(service.getByUsuario(1)).isNull();

        verify(client).getByUsuario(1);
    }

    @Test
    public void getByUsuario_deveRetornarListaDeEquipesVendasVazia_quandoErroNaApi() {
        doThrow(RetryableException.class)
            .when(client)
            .getByUsuario(1);

        assertThat(service.getByUsuario(1)).isNull();

        verify(client).getByUsuario(1);
    }

    public static EquipeVendasSupervisionadasResponse umaEquipeVendasSupervisionadasResponse() {
        return EquipeVendasSupervisionadasResponse
            .builder()
            .id(1)
            .canalVenda("D2D")
            .descricao("Equipe Teste")
            .build();
    }

    public static EquipeVendaDto umaEquipeVendaDto() {
        return EquipeVendaDto
            .builder()
            .id(1)
            .canalVenda("TELEVENDAS")
            .descricao("Equipe Teste 2")
            .build();
    }
}
