package br.com.xbrain.autenticacao.modules.notificacaoapi.service;

import br.com.xbrain.autenticacao.modules.comum.exception.IntegracaoException;
import feign.RetryableException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class NotificacaoApiServiceTest {

    @InjectMocks
    private NotificacaoApiService service;

    @Mock
    private NotificacaoClient notificacaoClient;

    @Test
    public void consultarStatusTabulacaoByUsuario_deveRetornarTrue_quandoRespostaForTrue() {
        when(notificacaoClient.consultarStatusTabulacaoByUsuario(1))
            .thenReturn(true);

        assertThat(service.consultarStatusTabulacaoByUsuario(1))
            .isTrue();

        verify(notificacaoClient).consultarStatusTabulacaoByUsuario(1);
    }

    @Test
    public void consultarStatusTabulacaoByUsuario_deveRetornarFalse_quandoRespostaForFalse() {
        when(notificacaoClient.consultarStatusTabulacaoByUsuario(1))
            .thenReturn(false);

        assertThat(service.consultarStatusTabulacaoByUsuario(1))
            .isFalse();

        verify(notificacaoClient).consultarStatusTabulacaoByUsuario(1);
    }

    @Test
    public void consultarStatusTabulacaoByUsuario_deveLancarIntegracaoException_quandoApiIndisponivel() {
        doThrow(RetryableException.class)
            .when(notificacaoClient)
            .consultarStatusTabulacaoByUsuario(1);

        assertThatThrownBy(() -> service.consultarStatusTabulacaoByUsuario(1))
            .isInstanceOf(IntegracaoException.class)
            .hasMessage("#035 - Desculpe, ocorreu um erro interno. Contate o administrador.");

        verify(notificacaoClient).consultarStatusTabulacaoByUsuario(1);
    }
}
