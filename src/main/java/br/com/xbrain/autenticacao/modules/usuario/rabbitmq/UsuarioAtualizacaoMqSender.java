package br.com.xbrain.autenticacao.modules.usuario.rabbitmq;

import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioMqAtualizacaoRequest;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class UsuarioAtualizacaoMqSender {

    @Value("${app-config.queue.usuario-atualizacao-failure}")
    private String usuarioAtualizacaoFailureQueue;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void sendWithFailure(UsuarioMqAtualizacaoRequest usuarioMqAtualizacaoRequest) {
        rabbitTemplate.convertAndSend(usuarioAtualizacaoFailureQueue, usuarioMqAtualizacaoRequest);
    }
}
