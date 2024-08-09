package br.com.xbrain.autenticacao.modules.usuario.rabbitmq;

import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioMqAtualizacaoRequest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import static org.mockito.Mockito.verify;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = UsuarioAtualizacaoMqSender.class)
public class UsuarioAtualizacaoMqSenderTest {

    @Autowired
    private UsuarioAtualizacaoMqSender mqSender;
    @MockBean
    private RabbitTemplate rabbitTemplate;

    @Test
    public void sendSuccess_deveEnviarParaFila_seNaoOcorrerErro() {
        var request = new UsuarioMqAtualizacaoRequest();
        mqSender.sendWithFailure(request);
        verify(rabbitTemplate).convertAndSend("${app-config.queue.usuario-atualizacao-failure}", request);
    }
}
