package br.com.xbrain.autenticacao.modules.usuario.rabbitmq;

import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioDto;
import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioMqRequest;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class UsuarioCadastroMqSender {

    @Value("${app-config.queue.usuario-cadastro-success}")
    private String usuarioCadastroSuccessQueue;
    @Value("${app-config.queue.usuario-cadastro-failure}")
    private String usuarioCadastroFailureQueue;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void sendSuccess(UsuarioDto usuarioDto) {
        rabbitTemplate.convertAndSend(usuarioCadastroSuccessQueue, usuarioDto);
    }

    public void sendWithFailure(UsuarioMqRequest usuarioMqRequest) {
        rabbitTemplate.convertAndSend(usuarioCadastroFailureQueue, usuarioMqRequest);
    }
}
