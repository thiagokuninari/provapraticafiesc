package br.com.xbrain.autenticacao.modules.parceirosonline.dto;

import br.com.xbrain.autenticacao.modules.comum.exception.IntegracaoException;
import br.com.xbrain.autenticacao.modules.parceirosonline.service.AgenteAutorizadoClient;
import br.com.xbrain.autenticacao.modules.parceirosonline.service.AgenteAutorizadoService;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo;
import feign.RetryableException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class AgenteAutorizadoServiceTest {

    @InjectMocks
    private AgenteAutorizadoService service;
    @Mock
    private AgenteAutorizadoClient client;

    @Test
    public void buscarAasFeedePorUsuario_deveRetornarListaDeUsuariosFitrados_seSolicitado() {
        when(client.getAaFeederPorCargo((umaListaCargos()))).thenReturn(List.of(1, 2));
        client.getAaFeederPorCargo(umaListaCargos());
        verify(client, times(1)).getAaFeederPorCargo(umaListaCargos());
    }

    @Test
    public void buscarAasFeedePorUsuario_integracaoException_seApiIndisponivel() {
        when(client.getAaFeederPorCargo(umaListaCargos()))
            .thenThrow(new RetryableException("Connection refused (Connection refused) executing "
                + "GET http://localhost:8300/api/colaboradores-vendas/cargos", new Date()));

        assertThatExceptionOfType(IntegracaoException.class)
            .isThrownBy(() -> service.getAaFeederPorCargo(umaListaCargos()))
            .withMessage("#045 - Desculpe, ocorreu um erro interno. Contate a administrador.");
    }

    private List<CodigoCargo> umaListaCargos() {
        return List.of(
            CodigoCargo.AGENTE_AUTORIZADO_GERENTE,
            CodigoCargo.AGENTE_AUTORIZADO_COORDENADOR);
    }
}
