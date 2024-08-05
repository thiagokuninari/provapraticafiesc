package br.com.xbrain.autenticacao.modules.usuario.rabbitmq;

import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioDto;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import static org.mockito.Mockito.verify;

@SpringBootTest
@ActiveProfiles("test")
@RunWith(SpringRunner.class)
public class AtualizarUsuarioMqSenderTest {

    @Autowired
    private AtualizarUsuarioMqSender mqSender;
    @MockBean
    private RabbitTemplate rabbitTemplate;

    @Test
    public void sendSuccess_deveEnviarParaFila_seNaoOcorrerErro() {
        var dto = new UsuarioDto(1, "email");
        mqSender.sendSuccess(dto);
        verify(rabbitTemplate).convertAndSend("atualizar-usuario-pol.queue", dto);
    }
}
