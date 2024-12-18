package br.com.xbrain.autenticacao.modules.feriado.importacaoautomatica.service;

import br.com.xbrain.autenticacao.modules.comum.exception.IntegracaoException;
import br.com.xbrain.autenticacao.modules.feriado.service.FeriadoAutomacaoClient;
import com.netflix.hystrix.exception.HystrixBadRequestException;
import feign.RetryableException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Date;

import static br.com.xbrain.autenticacao.modules.feriado.enums.ETipoFeriado.MUNICIPAL;
import static br.com.xbrain.autenticacao.modules.feriado.helper.FeriadoHelper.umaListFeriadoAutomacao;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.AssertionsForClassTypes.tuple;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class FeriadoAutomacaoServiceTest {

    @InjectMocks
    private FeriadoAutomacaoService feriadoAutomacaoService;
    @Mock
    private FeriadoAutomacaoClient feriadoAutomacaoClient;

    @Test
    public void consultarFeriadosMunicipais_deveLancarIntegracaoException_quandoClientRetornarErro() {
        doThrow(new HystrixBadRequestException("Erro"))
            .when(feriadoAutomacaoClient).buscarFeriadosMunicipais(2024, "PR", "Londrina");

        assertThatExceptionOfType(IntegracaoException.class)
            .isThrownBy(() -> feriadoAutomacaoService.consultarFeriadosMunicipais(2024, "PR", "Londrina"))
            .withMessage("#062 - Desculpe, ocorreu um erro interno. Contate o administrador.");

        verify(feriadoAutomacaoClient, times(1)).buscarFeriadosMunicipais(2024, "PR", "Londrina");
    }

    @Test
    public void consultarFeriadosMunicipais_deveLancarIntegracaoException_quandoApiNaoDisponivel() {
        when(feriadoAutomacaoClient.buscarFeriadosMunicipais(2024, "PR", "Londrina"))
            .thenThrow(new RetryableException("Connection refused (Connection refused)", new Date()));

        assertThatExceptionOfType(IntegracaoException.class)
            .isThrownBy(() -> feriadoAutomacaoService.consultarFeriadosMunicipais(2024, "PR", "Londrina"))
            .withMessage("#062 - Desculpe, ocorreu um erro interno. Contate o administrador.");

        verify(feriadoAutomacaoClient, times(1)).buscarFeriadosMunicipais(2024, "PR", "Londrina");
    }

    @Test
    public void consultarFeriadosMunicipais_deveRetornarListaSubCanaisId_quandoClientRetornarSemErro() {
        when(feriadoAutomacaoClient.buscarFeriadosMunicipais(2024, "PR", "Londrina"))
            .thenReturn(umaListFeriadoAutomacao());

        assertThat(feriadoAutomacaoService.consultarFeriadosMunicipais(2024, "PR", "Londrina"))
            .extracting("nome", "tipoFeriado")
            .containsExactly(
                tuple("feriado teste", MUNICIPAL),
                tuple("feriado teste", MUNICIPAL));

        verify(feriadoAutomacaoClient, times(1)).buscarFeriadosMunicipais(2024, "PR", "Londrina");
    }

    @Test
    public void consultarFeriadosEstaduais_deveLancarIntegracaoException_quandoClientRetornarErro() {
        doThrow(new HystrixBadRequestException("Erro"))
            .when(feriadoAutomacaoClient).buscarFeriadosEstaduais(2024, "PR");

        assertThatExceptionOfType(IntegracaoException.class)
            .isThrownBy(() -> feriadoAutomacaoService.consultarFeriadosEstaduais(2024, "PR"))
            .withMessage("#062 - Desculpe, ocorreu um erro interno. Contate o administrador.");

        verify(feriadoAutomacaoClient, times(1)).buscarFeriadosEstaduais(2024, "PR");
    }

    @Test
    public void consultarFeriadosEstaduais_deveLancarIntegracaoException_quandoApiNaoDisponivel() {
        when(feriadoAutomacaoClient.buscarFeriadosEstaduais(2024, "PR"))
            .thenThrow(new RetryableException("Connection refused (Connection refused)", new Date()));

        assertThatExceptionOfType(IntegracaoException.class)
            .isThrownBy(() -> feriadoAutomacaoService.consultarFeriadosEstaduais(2024, "PR"))
            .withMessage("#062 - Desculpe, ocorreu um erro interno. Contate o administrador.");

        verify(feriadoAutomacaoClient, times(1)).buscarFeriadosEstaduais(2024, "PR");
    }

    @Test
    public void consultarFeriadosEstaduais_deveRetornarFeriados_quandoClientRetornarSemErro() {
        when(feriadoAutomacaoClient.buscarFeriadosEstaduais(2024, "PR"))
            .thenReturn(umaListFeriadoAutomacao());

        assertThat(feriadoAutomacaoService.consultarFeriadosEstaduais(2024, "PR"))
            .extracting("nome", "tipoFeriado")
            .containsExactly(
                tuple("feriado teste", MUNICIPAL),
                tuple("feriado teste", MUNICIPAL));

        verify(feriadoAutomacaoClient, times(1)).buscarFeriadosEstaduais(2024, "PR");
    }

    @Test
    public void consultarFeriadosNacionais_deveLancarIntegracaoException_quandoClientRetornarErro() {
        doThrow(new HystrixBadRequestException("Erro"))
            .when(feriadoAutomacaoClient).buscarFeriadosNacionais(2024);

        assertThatExceptionOfType(IntegracaoException.class)
            .isThrownBy(() -> feriadoAutomacaoService.consultarFeriadosNacionais(2024))
            .withMessage("#062 - Desculpe, ocorreu um erro interno. Contate o administrador.");

        verify(feriadoAutomacaoClient, times(1)).buscarFeriadosNacionais(2024);
    }

    @Test
    public void consultarFeriadosNacionais_deveLancarIntegracaoException_quandoApiNaoDisponivel() {
        when(feriadoAutomacaoClient.buscarFeriadosNacionais(2024))
            .thenThrow(new RetryableException("Connection refused (Connection refused)", new Date()));

        assertThatExceptionOfType(IntegracaoException.class)
            .isThrownBy(() -> feriadoAutomacaoService.consultarFeriadosNacionais(2024))
            .withMessage("#062 - Desculpe, ocorreu um erro interno. Contate o administrador.");

        verify(feriadoAutomacaoClient, times(1)).buscarFeriadosNacionais(2024);
    }

    @Test
    public void consultarFeriadosNacionais_deveRetornarFeriados_quandoClientRetornarSemErro() {
        when(feriadoAutomacaoClient.buscarFeriadosNacionais(2024))
            .thenReturn(umaListFeriadoAutomacao());

        assertThat(feriadoAutomacaoService.consultarFeriadosNacionais(2024))
            .extracting("nome", "tipoFeriado")
            .containsExactly(
                tuple("feriado teste", MUNICIPAL),
                tuple("feriado teste", MUNICIPAL));

        verify(feriadoAutomacaoClient, times(1)).buscarFeriadosNacionais(2024);
    }
}
