package br.com.xbrain.autenticacao.modules.usuario.dto;

import br.com.xbrain.autenticacao.modules.usuario.service.UsuarioService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UsuarioMqListener {

    @Autowired
    private UsuarioService service;

    @RabbitListener(queues = "${app-config.queue.usuario}")
    public void save(UsuarioMqRequest usuarioMqRequest) {
        service.saveFromQueue(usuarioMqRequest);
    }
}

