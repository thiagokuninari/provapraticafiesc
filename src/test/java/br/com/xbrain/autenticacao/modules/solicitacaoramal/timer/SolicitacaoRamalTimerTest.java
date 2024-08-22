package br.com.xbrain.autenticacao.modules.solicitacaoramal.timer;

import br.com.xbrain.autenticacao.modules.solicitacaoramal.service.SolicitacaoRamalService;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class SolicitacaoRamalTimerTest {

    @Mock
    private SolicitacaoRamalService solicitacaoRamalService;

    @InjectMocks
    private SolicitacaoRamalTimer solicitacaoRamalTimer;

    private List<ILoggingEvent> logEvents = new ArrayList<>();
    private Logger logger;

    @Before
    public void setUp() {
        logger = (Logger) LoggerFactory.getLogger(SolicitacaoRamalTimer.class);
        logger.setLevel(Level.INFO);
        logger.setAdditive(false);

        AppenderBase<ILoggingEvent> appender = new AppenderBase<ILoggingEvent>() {
            @Override
            protected void append(ILoggingEvent eventObject) {
                logEvents.add(eventObject);
            }
        };
        appender.start();
        logger.addAppender(appender);
    }

    @Test
    public void testEnviarEmailDeNotificacaoParaSolicitacaoRamal() {
        solicitacaoRamalTimer.enviarEmailDeNotificacaoParaSolicitacaoRamal();
        verify(solicitacaoRamalService).enviarEmailSolicitacoesQueVaoExpirar();
        assertThat(logEvents)
            .extracting(ILoggingEvent::getLevel, ILoggingEvent::getFormattedMessage)
            .containsExactly(
                tuple(Level.INFO, "Iniciando fluxo para envio de emails de solicitações ramal que vão expirar"),
                tuple(Level.INFO, "Encerrando fluxo para envio de emails de solicitações ramal que vão expirar")
            );
    }
}
