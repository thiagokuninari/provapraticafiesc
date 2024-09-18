package br.com.xbrain.autenticacao.modules.agendador.rabbit;

import br.com.xbrain.autenticacao.modules.agendador.dto.AgendadorMqDto;
import br.com.xbrain.autenticacao.modules.agendador.service.AgendadorService;
import br.com.xbrain.autenticacao.modules.comum.exception.NotFoundException;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import lombok.SneakyThrows;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Java6Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class AgendadorMqListenerTest {

    @InjectMocks
    private AgendadorMqListener listener;
    @Mock
    private AgendadorService service;
    @Mock
    private AgendadorSender agendadorSender;

    @Test
    @SneakyThrows
    public void executarAgendador_deveExecutarMetodo_quandoSolicitado() {
        var agendadorMqDto = new AgendadorMqDto();
        agendadorMqDto.setJobName("AUT_22789_01");

        assertThatCode(() -> listener.executarAgendador(agendadorMqDto))
            .doesNotThrowAnyException();
    }

    @Test
    @SneakyThrows
    public void executarAgendador_deveLancarException_quandoNaoEncontrarMetodoParaExecutar() {
        var logger = (Logger) LoggerFactory.getLogger(AgendadorMqListener.class);
        ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
        listAppender.start();
        logger.addAppender(listAppender);

        var agendadorMqDto = new AgendadorMqDto();
        agendadorMqDto.setJobName("AGD_01");

        listener.executarAgendador(agendadorMqDto);

        assertThat(listAppender.list.get(0).getFormattedMessage())
            .isEqualTo("Inicia execução do agendador: AGD_01.");
        assertThat(listAppender.list.get(1).getFormattedMessage())
            .isEqualTo("Erro ao executar agendador");

        verify(service).setarErroEEnviarParaFila(any(NotFoundException.class), eq(agendadorMqDto));
    }
}

