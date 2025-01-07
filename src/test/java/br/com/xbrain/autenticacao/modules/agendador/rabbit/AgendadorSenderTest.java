package br.com.xbrain.autenticacao.modules.agendador.rabbit;

import br.com.xbrain.autenticacao.modules.agendador.dto.AgendadorMqDto;
import nl.altindag.log.LogCaptor;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class AgendadorSenderTest {

    @InjectMocks
    private AgendadorSender sender;
    @Mock
    private RabbitTemplate rabbitTemplate;

    @Before
    public void setup() {
        ReflectionTestUtils.setField(sender, "agendadorSuccessQueue", "agendador");
    }

    @Test
    public void send_deveEnviarStatusDoProcessamentoParaFila_quandoSolicitado() {
        var agendadorMqDto = new AgendadorMqDto();
        agendadorMqDto.setJobName("limparCache");
        agendadorMqDto.setGroupName("quality-call-api");

        assertThatCode(() -> sender.send(agendadorMqDto))
            .doesNotThrowAnyException();

        verify(rabbitTemplate).convertAndSend("agendador", agendadorMqDto);
    }

    @Test
    public void send_deveLancarException_quandoHouverErroAoEnviarParaFila() {
        var agendadorMqDto = new AgendadorMqDto();
        agendadorMqDto.setJobName("limparCache");

        doThrow(new AmqpException("Erro ao processar job."))
            .when(rabbitTemplate).convertAndSend("agendador", agendadorMqDto);

        var logger = LogCaptor.forClass(AgendadorSender.class);
        sender.send(agendadorMqDto);

        assertThat(logger.getLogs().get(0))
            .isEqualTo("Erro ao enviar processamento do job: limparCache para fila.");

        verify(rabbitTemplate).convertAndSend("agendador", agendadorMqDto);
    }
}

