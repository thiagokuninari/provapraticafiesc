package br.com.xbrain.autenticacao.modules.usuario.rabbitmq;

import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioDto;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AtualizarUsuarioMqSender {

    @Value("${app-config.queue.atualizar-usuario-pol}")
    private String atualizarUsuarioPolQueue;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void sendSuccess(UsuarioDto usuarioDto) {
        rabbitTemplate.convertAndSend(atualizarUsuarioPolQueue, usuarioDto);
    }

}
