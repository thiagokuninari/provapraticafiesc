package br.com.xbrain.autenticacao.modules.usuario.rabbitmq;

import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioMqRequest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import static org.mockito.Mockito.verify;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = UsuarioRecuperacaoMqSender.class)
public class UsuarioRecuperacaoMqSenderTest {

    @Autowired
    private UsuarioRecuperacaoMqSender mqSender;
    @MockBean
    private RabbitTemplate rabbitTemplate;

    @Test
    public void sendInativar_deveEnviarParaFila_seNaoOcorrerErro() {
        var dto = new UsuarioMqRequest();
        mqSender.sendWithFailure(dto);
        verify(rabbitTemplate).convertAndSend("${app-config.queue.usuario-recuperacao-failure}", dto);
    }
}
