package br.com.xbrain.autenticacao.modules.parceirosonline.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.HashMap;
import java.util.List;

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
    public void getUsuarioEEquipeByUsuarioIds_deveRetornarNull_quandoListaVazia() {
        assertThat(equipeVendasService.getUsuarioEEquipeByUsuarioIds(List.of()))
            .isEqualTo(null);

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
}
