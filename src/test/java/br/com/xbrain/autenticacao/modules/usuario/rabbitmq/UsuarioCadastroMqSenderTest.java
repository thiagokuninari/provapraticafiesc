package br.com.xbrain.autenticacao.modules.usuario.rabbitmq;

import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioDto;
import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioMqRequest;
import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioSocialHubRequestMq;
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
public class UsuarioCadastroMqSenderTest {

    @Autowired
    private UsuarioCadastroMqSender mqSender;
    @MockBean
    private RabbitTemplate rabbitTemplate;

    @Test
    public void sendSuccess_deveEnviarParaFila_seNaoOcorrerErro() {
        var dto = new UsuarioDto();
        mqSender.sendSuccess(dto);
        verify(rabbitTemplate).convertAndSend("usuario-cadastro-success.queue", dto);
    }

    @Test
    public void sendSuccessSocioPrincipal_deveEnviarParaFila_seNaoOcorrerErro() {
        var dto = new UsuarioDto();
        mqSender.sendSuccessSocioPrincipal(dto);
        verify(rabbitTemplate).convertAndSend("usuario-cadastro-socio-principal-success.queue", dto);
    }

    @Test
    public void sendSuccessLojaFuturo_deveEnviarParaFila_seNaoOcorrerErro() {
        var dto = new UsuarioDto();
        mqSender.sendSuccessLojaFuturo(dto);
        verify(rabbitTemplate).convertAndSend("usuario-cadastro-loja-futuro-success.queue", dto);
    }

    @Test
    public void sendSuccessAtualizarSocioPrincipal_deveEnviarParaFila_seNaoOcorrerErro() {
        var dto = new UsuarioDto();
        mqSender.sendSuccessAtualizarSocioPrincipal(dto);
        verify(rabbitTemplate).convertAndSend("usuario-atualizar-socio-principal-success.queue", dto);
    }

    @Test
    public void sendWithFailure_deveEnviarParaFila_seNaoOcorrerErro() {
        var request = new UsuarioMqRequest();
        mqSender.sendWithFailure(request);
        verify(rabbitTemplate).convertAndSend("usuario-cadastro-failure.queue", request);
    }

    @Test
    public void enviarDadosUsuarioParaSocialHub_deveEnviarParaFila_seNaoOcorrerErro() {
        var request = new UsuarioSocialHubRequestMq();
        mqSender.enviarDadosUsuarioParaSocialHub(request);
        verify(rabbitTemplate).convertAndSend("usuario-atualizacao-social-hub.queue", request);
    }
}
