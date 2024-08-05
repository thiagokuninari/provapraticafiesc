package br.com.xbrain.autenticacao.modules.usuario.rabbitmq;

import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioCadastroSucessoMqDto;
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
public class UsuarioFeederCadastroSucessoMqSenderTest {

    @Autowired
    private UsuarioFeederCadastroSucessoMqSender mqSender;
    @MockBean
    private RabbitTemplate rabbitTemplate;

    @Test
    public void sendInativar_deveEnviarParaFila_seNaoOcorrerErro() {
        var dto = new UsuarioCadastroSucessoMqDto();
        mqSender.sendCadastroSuccessoMensagem(dto);
        verify(rabbitTemplate).convertAndSend("sucesso-cadastro-usuario-feeder.queue", dto);
    }
}
