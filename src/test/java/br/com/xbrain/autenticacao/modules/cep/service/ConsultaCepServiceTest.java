package br.com.xbrain.autenticacao.modules.cep.service;

import br.com.xbrain.autenticacao.modules.cep.client.ConsultaCepClient;
import br.com.xbrain.autenticacao.modules.comum.exception.IntegracaoException;
import br.com.xbrain.autenticacao.modules.usuario.service.CidadeService;
import com.netflix.hystrix.exception.HystrixBadRequestException;
import feign.RetryableException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static br.com.xbrain.autenticacao.modules.cep.helper.CepHelper.umCidadeUfResponse;
import static br.com.xbrain.autenticacao.modules.cep.helper.CepHelper.umConsultaCepResponse;
import static br.com.xbrain.autenticacao.modules.usuario.helpers.CidadeHelper.umaCidadeLondrina;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@RunWith(MockitoJUnitRunner.class)
public class ConsultaCepServiceTest {

    @InjectMocks
    private ConsultaCepService service;
    @Mock
    private CidadeService cidadeService;
    @Mock
    private ConsultaCepClient consultaCepClient;

    @Test
    public void consultarCep_deveLancarException_quandoReceberAlgumErroDoClient() {
        doThrow(RetryableException.class).when(consultaCepClient).consultarCep("86023112");

        assertThatExceptionOfType(IntegracaoException.class)
            .isThrownBy(() -> service.consultarCep("86023112"))
            .withMessageContaining("#023 - Desculpe, ocorreu um erro interno. Contate o administrador.");

        verify(consultaCepClient).consultarCep("86023112");
        verify(cidadeService, never()).findByUfNomeAndCidadeNome(any(), any());
    }

    @Test
    public void consultarCep_deveRetornarCidadeEUf_quandoEncontrarCep() {
        when(consultaCepClient.consultarCep("86023112"))
            .thenReturn(umConsultaCepResponse());
        when(cidadeService.findByUfNomeAndCidadeNome("PR", "LONDRINA")).thenReturn(umaCidadeLondrina());

        assertThat(service.consultarCep("86023112")).isEqualTo(umCidadeUfResponse());

        verify(consultaCepClient).consultarCep("86023112");
        verify(cidadeService).findByUfNomeAndCidadeNome("PR", "LONDRINA");
    }

    @Test
    public void consultarCep_deveLancarHystrixBadRequestException_quandoErroNaApi() {
        doThrow(HystrixBadRequestException.class).when(consultaCepClient).consultarCep("86023112");

        assertThatExceptionOfType(IntegracaoException.class)
            .isThrownBy(() -> service.consultarCep("86023112"));

        verify(consultaCepClient).consultarCep("86023112");
        verify(cidadeService, never()).findByUfNomeAndCidadeNome(any(), any());
    }

    @Test
    public void consultarCeps_deveRetornarListaDeCidadeEUf_quandoEncontrarCeps() {
        when(consultaCepClient.consultarCep("86023112"))
            .thenReturn(umConsultaCepResponse());

        assertThat(service.consultarCeps(List.of("86023112")))
            .isEqualTo(List.of(umConsultaCepResponse()));

        verify(consultaCepClient).consultarCep("86023112");
    }

    @Test
    public void consultarCeps_deveLancarException_quandoReceberAlgumErroDoClient() {
        doThrow(RetryableException.class).when(consultaCepClient).consultarCep("86023112");

        assertThatExceptionOfType(IntegracaoException.class)
            .isThrownBy(() -> service.consultarCeps(List.of("86023112")))
            .withMessageContaining("#023 - Desculpe, ocorreu um erro interno. Contate o administrador.");

        verify(consultaCepClient).consultarCep("86023112");
    }

    @Test
    public void consultarCeps_deveLancarHystrixBadRequestException_quandoErroNaApi() {
        doThrow(HystrixBadRequestException.class).when(consultaCepClient).consultarCep("86023112");

        assertThatExceptionOfType(IntegracaoException.class)
            .isThrownBy(() -> service.consultarCeps(List.of("86023112")));

        verify(consultaCepClient).consultarCep("86023112");
    }
}
