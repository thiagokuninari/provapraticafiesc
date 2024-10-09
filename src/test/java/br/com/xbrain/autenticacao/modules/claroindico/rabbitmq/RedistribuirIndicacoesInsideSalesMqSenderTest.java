package br.com.xbrain.autenticacao.modules.claroindico.rabbitmq;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = RedistribuirIndicacoesInsideSalesMqSender.class)
public class RedistribuirIndicacoesInsideSalesMqSenderTest {

    @Autowired
    private RedistribuirIndicacoesInsideSalesMqSender sender;
    @MockBean
    private RabbitTemplate rabbitTemplate;

    @Test
    public void sendRedistribuirIndicacoesInsideSales_deveEnviarParaFila_quandoSolicitado() {
        sender.sendRedistribuirIndicacoesInsideSales(14);

        verify(rabbitTemplate)
            .convertAndSend("${app-config.queue.redistribuir-indicacoes-inside-sales-vendedor}", 14);
    }
}
