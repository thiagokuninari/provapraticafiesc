package br.com.xbrain.autenticacao.modules.parceirosonline.service;

import feign.RetryableException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class EquipeVendasServiceTest {

    @InjectMocks
    private EquipeVendasService equipeVendasService;

    @Mock
    private EquipeVendasClient equipeVendasClient;

    @Test
    public void getUsuarioEEquipeByUsuarioIds_deveRetornarMapVazio_quandoListaVazia() {
        assertThat(equipeVendasService.getUsuarioEEquipeByUsuarioIds(List.of()))
            .isEqualTo(Map.of());

        verify(equipeVendasClient, never()).getUsuarioEEquipeByUsuarioIds(anyList());
    }

    @Test
    public void getUsuarioEEquipeByUsuarioIds_deveRetornarMap_quandoEncontrar() {
        var resultMap = new HashMap<Integer, Integer>();
        resultMap.put(1, 1);
        resultMap.put(2, 2);

        when(equipeVendasClient.getUsuarioEEquipeByUsuarioIds(anyList())).thenReturn(resultMap);

        assertThat(equipeVendasService.getUsuarioEEquipeByUsuarioIds(List.of(1, 2)))
            .isEqualTo(resultMap);

        verify(equipeVendasClient, times(1)).getUsuarioEEquipeByUsuarioIds(List.of(1, 2));
    }

    @Test
    public void getUsuarioEEquipeByUsuarioIds_deveRetornarMapVazio_quandoClientRetornarErro() {
        doThrow(RetryableException.class)
            .when(equipeVendasClient).getUsuarioEEquipeByUsuarioIds(anyList());

        assertThat(equipeVendasService.getUsuarioEEquipeByUsuarioIds(List.of(1, 2)))
            .isEqualTo(Map.of());

        verify(equipeVendasClient, times(1)).getUsuarioEEquipeByUsuarioIds(List.of(1, 2));
    }
}
