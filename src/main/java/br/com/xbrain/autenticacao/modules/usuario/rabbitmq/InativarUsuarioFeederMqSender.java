package br.com.xbrain.autenticacao.modules.usuario.rabbitmq;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class InativarUsuarioFeederMqSender {

    @Value("${app-config.queue.inativar-usuario-feeder}")
    private String inativarUsuarioFeederQueue;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void sendSuccess(String email) {
        rabbitTemplate.convertAndSend(inativarUsuarioFeederQueue, email);
    }
}
