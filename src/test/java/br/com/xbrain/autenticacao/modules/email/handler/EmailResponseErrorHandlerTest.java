package br.com.xbrain.autenticacao.modules.email.handler;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import lombok.SneakyThrows;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.slf4j.LoggerFactory.getLogger;

public class EmailResponseErrorHandlerTest {

    private Logger logger;
    private ListAppender<ILoggingEvent> listAppender;

    @Before
    public void setUp() {
        logger = (Logger) getLogger(EmailResponseErrorHandler.class);
        listAppender = new ListAppender<>();
        listAppender.start();
        logger.addAppender(listAppender);
    }

    @After
    public void cleanUp() {
        logger.detachAppender(listAppender);
        listAppender.stop();
    }

    @Test
    @SneakyThrows
    public void hasError_deveRetornarTrue_quandoStatusForDiferenteDeOk() {
        var email = new EmailResponseErrorHandler();

        var response = mock(ClientHttpResponse.class);
        when(response.getStatusCode()).thenReturn(HttpStatus.BAD_REQUEST);

        assertThat(email.hasError(response)).isTrue();
        assertEquals("Erro ao enviar email Status code: 400",
            listAppender.list.get(0).getMessage());
    }

    @Test
    @SneakyThrows
    public void hasError_deveRetornarFalse_quandoStatusOk() {
        var email = new EmailResponseErrorHandler();

        var response = mock(ClientHttpResponse.class);
        when(response.getStatusCode()).thenReturn(HttpStatus.OK);

        assertThat(email.hasError(response)).isFalse();
    }

    @Test
    @SneakyThrows
    public void handleError_deveRetornarLogDeErro_quandoStatusForDiferenteDeOk() {
        var email = new EmailResponseErrorHandler();

        var response = mock(ClientHttpResponse.class);
        when(response.getStatusCode()).thenReturn(HttpStatus.BAD_REQUEST);
        email.handleError(response);

        assertEquals("Erro ao enviar email Status code: 400",
            listAppender.list.get(0).getMessage());
    }
}
