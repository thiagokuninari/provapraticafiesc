package br.com.xbrain.autenticacao.modules.claroindico.service;

import br.com.xbrain.autenticacao.modules.claroindico.client.IndicacaoInsideSalesPmeClient;
import br.com.xbrain.autenticacao.modules.comum.enums.EErrors;
import br.com.xbrain.autenticacao.modules.comum.exception.IntegracaoException;
import com.netflix.hystrix.exception.HystrixBadRequestException;
import feign.RetryableException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class IndicacaoInsideSalesPmeServiceTest {

    @InjectMocks
    private IndicacaoInsideSalesPmeService service;
    @Mock
    private IndicacaoInsideSalesPmeClient client;

    @Test
    public void redistribuirIndicacoesPorUsuarioVendedorId_deveNaoDeveLacarException_quandoSolicitado() {
        doNothing()
            .when(client).redistribuirIndicacoesPorUsuarioVendedorId(14);

        assertThatCode(() -> service.redistribuirIndicacoesPorUsuarioVendedorId(14))
            .doesNotThrowAnyException();
    }

    @Test
    public void redistribuirIndicacoesPorUsuarioVendedorId_deveDeveLacarException_quandoErroConexaoComClient() {
        doThrow(RetryableException.class)
            .when(client).redistribuirIndicacoesPorUsuarioVendedorId(14);

        assertThatExceptionOfType(IntegracaoException.class)
            .isThrownBy(() -> service.redistribuirIndicacoesPorUsuarioVendedorId(14))
            .withMessage(EErrors.ERRO_REDISTRIBUIR_INSIDE_SALES.getDescricao());

        verify(client).redistribuirIndicacoesPorUsuarioVendedorId(14);
    }

    @Test
    public void iredistribuirIndicacoesPorUsuarioVendedorId_deveDeveLacarException_quandoErroRequisicao() {
        doThrow(HystrixBadRequestException.class)
            .when(client).redistribuirIndicacoesPorUsuarioVendedorId(14);

        assertThatExceptionOfType(IntegracaoException.class)
            .isThrownBy(() -> service.redistribuirIndicacoesPorUsuarioVendedorId(14));

        verify(client).redistribuirIndicacoesPorUsuarioVendedorId(14);
    }

}
