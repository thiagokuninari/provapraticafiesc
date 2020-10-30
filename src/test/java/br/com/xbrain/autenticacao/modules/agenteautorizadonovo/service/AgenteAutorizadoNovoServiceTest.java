package br.com.xbrain.autenticacao.modules.agenteautorizadonovo.service;

import br.com.xbrain.autenticacao.modules.agenteautorizadonovo.client.AgenteAutorizadoNovoClient;
import br.com.xbrain.autenticacao.modules.comum.exception.IntegracaoException;
import com.netflix.hystrix.exception.HystrixBadRequestException;
import feign.RetryableException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

import static br.com.xbrain.autenticacao.modules.agenteautorizadonovo.helper.UsuarioDtoVendasHelper.umUsuarioDtoVendas;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AgenteAutorizadoNovoServiceTest {

    @InjectMocks
    private AgenteAutorizadoNovoService service;
    @Mock
    private AgenteAutorizadoNovoClient client;

    @Test
    public void buscarUsuariosDoAgenteAutorizado_deveDispararException_quandoErroNaIntegracao() {
        when(client.buscarUsuariosDoAgenteAutorizado(1, false)).thenThrow(RetryableException.class);

        assertThatExceptionOfType(IntegracaoException.class)
            .isThrownBy(() -> service.buscarUsuariosDoAgenteAutorizado(1, false))
            .withMessage("#030 - Desculpe, ocorreu um erro interno. Contate a administrador.");
    }

    @Test
    public void buscarUsuariosDoAgenteAutorizado_deveDispararException_quandoErroNaApi() {
        when(client.buscarUsuariosDoAgenteAutorizado(1, false)).thenThrow(HystrixBadRequestException.class);

        assertThatExceptionOfType(IntegracaoException.class)
            .isThrownBy(() -> service.buscarUsuariosDoAgenteAutorizado(1, false));
    }

    @Test
    public void buscarUsuariosDoAgenteAutorizado_deveRetornarUsuarioDtoVendas_quandoSolicitado() {
        when(client.buscarUsuariosDoAgenteAutorizado(1, false)).thenReturn(List.of(umUsuarioDtoVendas(1)));

        assertThat(service.buscarUsuariosDoAgenteAutorizado(1, false))
            .extracting("id")
            .containsExactly(1);
    }
}
