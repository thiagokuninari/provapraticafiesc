package br.com.xbrain.autenticacao.modules.claroindico.rabbitmq;

import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = InativarDirecionamentosCepMqSender.class)
public class InativarDirecionamentosCepMqSenderTest extends TestCase {

    @Autowired
    private InativarDirecionamentosCepMqSender sender;
    @MockBean
    private RabbitTemplate rabbitTemplate;

    @Test
    public void sendInativarDirecionamentoCep_deveEnviarParaFila_quandoSolicitado() {
        sender.sendInativarDirecionamentoCep(14);

        verify(rabbitTemplate)
            .convertAndSend("${app-config.queue.inativar-direcionamentos-cep-vendedor-inside-sales}", 14);
    }
}
