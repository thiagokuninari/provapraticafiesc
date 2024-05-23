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
    public void getSubCanaisDaEquipeVendaD2dByUsuarioId_deveLancarIntegracaoException_quandoClientRetornarErro() {
        doThrow(new HystrixBadRequestException("Erro"))
            .when(feriadoAutomacaoClient).buscarFeriadosMunicipais(2024, "PR", "Londrina");

        assertThatExceptionOfType(IntegracaoException.class)
            .isThrownBy(() -> feriadoAutomacaoService.consultarFeriadosMunicipais(2024, "PR", "Londrina"))
            .withMessage("#050 - Desculpe, ocorreu um erro interno. Contate o administrador.");

        verify(feriadoAutomacaoClient, times(1)).buscarFeriadosMunicipais(2024, "PR", "Londrina");
    }

    @Test
    public void getSubCanaisDaEquipeVendaD2dByUsuarioId_deveLancarIntegracaoException_quandoApiNaoDisponivel() {
        when(feriadoAutomacaoClient.buscarFeriadosMunicipais(2024, "PR", "Londrina"))
            .thenThrow(new RetryableException("Connection refused (Connection refused)", new Date()));

        assertThatExceptionOfType(IntegracaoException.class)
            .isThrownBy(() -> feriadoAutomacaoService.consultarFeriadosMunicipais(2024, "PR", "Londrina"))
            .withMessage("#050 - Desculpe, ocorreu um erro interno. Contate o administrador.");

        verify(feriadoAutomacaoClient, times(1)).buscarFeriadosMunicipais(2024, "PR", "Londrina");
    }

    @Test
    public void getSubCanaisDaEquipeVendaD2dByUsuarioId_deveRetornarListaSubCanaisId_quandoClientRetornarSemErro() {
        when(feriadoAutomacaoClient.buscarFeriadosMunicipais(2024, "PR", "Londrina"))
            .thenReturn(umaListFeriadoAutomacao());

        assertThat(feriadoAutomacaoService.consultarFeriadosMunicipais(2024, "PR", "Londrina"))
            .extracting("nome", "tipoFeriado")
            .containsExactly(
                tuple("feriado teste", MUNICIPAL),
                tuple("feriado teste", MUNICIPAL));

        verify(feriadoAutomacaoClient, times(1)).buscarFeriadosMunicipais(2024, "PR", "Londrina");
    }
}
