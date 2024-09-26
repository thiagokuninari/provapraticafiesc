package br.com.xbrain.autenticacao.modules.claroindico.service;

import br.com.xbrain.autenticacao.modules.claroindico.client.DirecionamentoInsideSalesVendedorClient;
import br.com.xbrain.autenticacao.modules.comum.enums.EErrors;
import br.com.xbrain.autenticacao.modules.comum.exception.IntegracaoException;
import com.netflix.hystrix.exception.HystrixBadRequestException;
import feign.RetryableException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DirecionamentoInsideSalesVendedorServiceTest {

    @InjectMocks
    private DirecionamentoInsideSalesVendedorService service;
    @Mock
    private DirecionamentoInsideSalesVendedorClient client;

    @Test
    public void inativarDirecionamentoPorUsuarioVendedorId_naoDeveLancarException_quandoSolicitado() {
        doNothing()
            .when(client).inativarDirecionamentoPorUsuarioVendedorId(14);

        assertThatCode(() -> service.inativarDirecionamentoPorUsuarioVendedorId(14))
            .doesNotThrowAnyException();
    }

    @Test
    public void inativarDirecionamentoPorUsuarioVendedorId_deveLancarException_quandoErroConexaoComClient() {
        doThrow(RetryableException.class)
            .when(client).inativarDirecionamentoPorUsuarioVendedorId(14);

        assertThatExceptionOfType(IntegracaoException.class)
            .isThrownBy(() -> service.inativarDirecionamentoPorUsuarioVendedorId(14))
            .withMessage(EErrors.ERRO_INATIVAR_DIRECIONAMENTOS.getDescricao());

        verify(client).inativarDirecionamentoPorUsuarioVendedorId(14);
    }

    @Test
    public void inativarDirecionamentoPorUsuarioVendedorId_deveLancarException_quandoErroRequisicao() {
        doThrow(HystrixBadRequestException.class)
            .when(client).inativarDirecionamentoPorUsuarioVendedorId(14);

        assertThatExceptionOfType(IntegracaoException.class)
            .isThrownBy(() -> service.inativarDirecionamentoPorUsuarioVendedorId(14));

        verify(client).inativarDirecionamentoPorUsuarioVendedorId(14);
    }
}
