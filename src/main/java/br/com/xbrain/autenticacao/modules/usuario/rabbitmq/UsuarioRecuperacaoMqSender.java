package br.com.xbrain.autenticacao.modules.usuario.rabbitmq;

import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioMqRequest;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class UsuarioRecuperacaoMqSender {

    @Value("${app-config.queue.usuario-recuperacao-failure}")
    private String usuarioRecuperacaoFailureQueue;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void sendWithFailure(UsuarioMqRequest usuarioMqRequest) {
        rabbitTemplate.convertAndSend(usuarioRecuperacaoFailureQueue, usuarioMqRequest);
    }
}
