package br.com.xbrain.autenticacao.modules.usuario.dto;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class UsuarioMqSender {

    @Value("${app-config.queue.usuario-aut}")
    private String usuarioAutQueue;
    @Value("${app-config.queue.usuario-aut-bug}")
    private String usuarioAutBugQueue;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void send(UsuarioDto usuarioDto) {
        rabbitTemplate.convertAndSend(usuarioAutQueue, usuarioDto);
    }

    public void sendWithBug(UsuarioMqRequest usuarioMqRequest) {
        rabbitTemplate.convertAndSend(usuarioAutBugQueue, usuarioMqRequest);
    }
}
