package br.com.xbrain.autenticacao.modules.parceirosonline.service;

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
    public void getUsuariosAaFeederPorCargo_deveRetornarListaDeUsuariosIdsFitrados_seSolicitado() {
        when(client.getUsuariosAaFeederPorCargo(umaListaAaIds(), umaListaCargos())).thenReturn(List.of(1, 2));
        client.getUsuariosAaFeederPorCargo(umaListaAaIds(), umaListaCargos());
        verify(client, times(1)).getUsuariosAaFeederPorCargo(umaListaAaIds(), umaListaCargos());
    }

    @Test
    public void getAaFeederPorCargo_deveLancarIntegracaoException_seApiIndisponivel() {
        when(client.getUsuariosAaFeederPorCargo(umaListaAaIds(), umaListaCargos()))
            .thenThrow(new RetryableException("Connection refused (Connection refused) executing "
                + "GET http://localhost:8300/api/colaboradores-vendas/cargos", new Date()));

        assertThatExceptionOfType(IntegracaoException.class)
            .isThrownBy(() -> service.getUsuariosAaFeederPorCargo(umaListaAaIds(), umaListaCargos()))
            .withMessage("#045 - Desculpe, ocorreu um erro interno. Contate a administrador.");
    }

    private List<CodigoCargo> umaListaCargos() {
        return List.of(
            CodigoCargo.AGENTE_AUTORIZADO_GERENTE,
            CodigoCargo.AGENTE_AUTORIZADO_COORDENADOR);
    }

    private List<Integer> umaListaAaIds() {
        return List.of(1, 2);
    }
}
