package br.com.xbrain.autenticacao.modules.usuario.rabbitmq;

import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioDto;
import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioMqRequest;
import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioSocialHubRequestMq;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import static org.mockito.Mockito.verify;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = UsuarioCadastroMqSender.class)
public class UsuarioCadastroMqSenderTest {

    @Autowired
    private UsuarioCadastroMqSender mqSender;
    @MockBean
    private RabbitTemplate rabbitTemplate;

    @Test
    public void sendSuccess_deveEnviarParaFila_seNaoOcorrerErro() {
        var dto = new UsuarioDto();
        mqSender.sendSuccess(dto);
        verify(rabbitTemplate).convertAndSend("${app-config.queue.usuario-cadastro-success}", dto);
    }

    @Test
    public void sendSuccessSocioPrincipal_deveEnviarParaFila_seNaoOcorrerErro() {
        var dto = new UsuarioDto();
        mqSender.sendSuccessSocioPrincipal(dto);
        verify(rabbitTemplate).convertAndSend("${app-config.queue.usuario-cadastro-socio-principal-success}", dto);
    }

    @Test
    public void sendSuccessLojaFuturo_deveEnviarParaFila_seNaoOcorrerErro() {
        var dto = new UsuarioDto();
        mqSender.sendSuccessLojaFuturo(dto);
        verify(rabbitTemplate).convertAndSend("${app-config.queue.usuario-cadastro-loja-futuro-success}", dto);
    }

    @Test
    public void sendSuccessAtualizarSocioPrincipal_deveEnviarParaFila_seNaoOcorrerErro() {
        var dto = new UsuarioDto();
        mqSender.sendSuccessAtualizarSocioPrincipal(dto);
        verify(rabbitTemplate).convertAndSend("${app-config.queue.usuario-atualizar-socio-principal-success}", dto);
    }

    @Test
    public void sendWithFailure_deveEnviarParaFila_seNaoOcorrerErro() {
        var request = new UsuarioMqRequest();
        mqSender.sendWithFailure(request);
        verify(rabbitTemplate).convertAndSend("${app-config.queue.usuario-cadastro-failure}", request);
    }

    @Test
    public void enviarDadosUsuarioParaSocialHub_deveEnviarParaFila_seNaoOcorrerErro() {
        var request = new UsuarioSocialHubRequestMq();
        mqSender.enviarDadosUsuarioParaSocialHub(request);
        verify(rabbitTemplate).convertAndSend("${app-config.queue.usuario-atualizacao-social-hub}", request);
    }

    @Test
    public void sendRemanejamentoWithFailure_deveEnviarParaFila_seNaoOcorrerErro() {
        var request = new UsuarioMqRequest();
        mqSender.sendRemanejamentoWithFailure(request);
        verify(rabbitTemplate).convertAndSend("${app-config.queue.usuario-remanejar-pol-failure}", request);
    }
}
