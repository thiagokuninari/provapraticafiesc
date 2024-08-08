package br.com.xbrain.autenticacao.modules.usuario.rabbitmq;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import static org.mockito.Mockito.verify;

@SpringBootTest
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = InativarUsuarioFeederMqSender.class)
public class InativarUsuarioFeederMqSenderTest {

    @Autowired
    private InativarUsuarioFeederMqSender mqSender;
    @MockBean
    private RabbitTemplate rabbitTemplate;

    @Test
    public void sendSuccess_deveEnviarParaFila_seNaoOcorrerErro() {
        mqSender.sendSuccess("email1@xbrain.com");
        verify(rabbitTemplate).convertAndSend("inativar-usuario-feeder.queue","email1@xbrain.com");
    }
}
