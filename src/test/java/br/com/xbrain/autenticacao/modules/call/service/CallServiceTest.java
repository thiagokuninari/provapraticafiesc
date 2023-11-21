package br.com.xbrain.autenticacao.modules.call.service;

import br.com.xbrain.autenticacao.modules.comum.exception.IntegracaoException;
import com.netflix.hystrix.exception.HystrixBadRequestException;
import feign.RetryableException;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CallServiceTest {

    @InjectMocks
    private CallService callService;
    @Mock
    private CallClient callClient;

    @Test
    public void desvincularDiscadoraERamaisSuporteVendas_deveDesvincularDiscadoraERamais_quandoNenhumErroComClient() {
        doNothing().when(callClient).desvicularDiscadoraSuporteVendas(1);

        assertThatCode(() -> callService.desvincularDiscadoraERamaisSuporteVendas(1))
            .doesNotThrowAnyException();

        verify(callClient).desvicularDiscadoraSuporteVendas(1);
    }

    @Test
    public void desvincularDiscadoraERamaisSuporteVendas_deveLancarException_quandoErroConexaoComClient() {
        doThrow(RetryableException.class).when(callClient).desvicularDiscadoraSuporteVendas(1);

        assertThatExceptionOfType(IntegracaoException.class)
            .isThrownBy(() -> callService.desvincularDiscadoraERamaisSuporteVendas(1));

        verify(callClient).desvicularDiscadoraSuporteVendas(1);
    }

    @Test
    public void desvincularDiscadoraERamaisSuporteVendas_deveLancarException_quandoErroRequisicao() {
        doThrow(HystrixBadRequestException.class).when(callClient).desvicularDiscadoraSuporteVendas(1);

        assertThatExceptionOfType(IntegracaoException.class)
            .isThrownBy(() -> callService.desvincularDiscadoraERamaisSuporteVendas(1));

        verify(callClient).desvicularDiscadoraSuporteVendas(1);
    }

    @Test
    public void ativarConfiguracaoSuporteVendas_deveDesvincularAtivarConfiguracao_quandoNenhumErroComClient() {
        doNothing().when(callClient).ativarConfiguracaoSuporteVendas(1);

        assertThatCode(() -> callService.ativarConfiguracaoSuporteVendas(1))
            .doesNotThrowAnyException();

        verify(callClient).ativarConfiguracaoSuporteVendas(1);
    }

    @Test
    public void ativarConfiguracaoSuporteVendass_deveLancarException_quandoErroConexaoComClient() {
        doThrow(RetryableException.class).when(callClient).ativarConfiguracaoSuporteVendas(1);

        assertThatExceptionOfType(IntegracaoException.class)
            .isThrownBy(() -> callService.ativarConfiguracaoSuporteVendas(1));

        verify(callClient).ativarConfiguracaoSuporteVendas(1);
    }

    @Test
    public void ativarConfiguracaoSuporteVendass_deveLancarException_quandoErroRequisicao() {
        doThrow(RetryableException.class).when(callClient).ativarConfiguracaoSuporteVendas(1);

        assertThatExceptionOfType(IntegracaoException.class)
            .isThrownBy(() -> callService.ativarConfiguracaoSuporteVendas(1));

        verify(callClient).ativarConfiguracaoSuporteVendas(1);
    }
}
