package br.com.xbrain.autenticacao.modules.usuario.rabbitmq;

import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioMqRequest;
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
@ContextConfiguration(classes = UsuarioAaAtualizacaoMqSender.class)
public class UsuarioAaAtualizacaoMqSenderTest {

    @Autowired
    private UsuarioAaAtualizacaoMqSender mqSender;
    @MockBean
    private RabbitTemplate rabbitTemplate;

    @Test
    public void sendSuccess_deveEnviarParaFila_seNaoOcorrerErro() {
        var request = new UsuarioMqRequest();
        mqSender.sendWithFailure(request);
        verify(rabbitTemplate).convertAndSend("usuario-aa-atualizacao-failure.queue", request);
    }
}
