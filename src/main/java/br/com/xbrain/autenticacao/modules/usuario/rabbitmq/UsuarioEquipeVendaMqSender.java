package br.com.xbrain.autenticacao.modules.usuario.rabbitmq;

import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioDto;
import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioEquipeVendasDto;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class UsuarioEquipeVendaMqSender {

    @Value("${app-config.queue.inativar-usuario-equipe-venda}")
    private String equipeVendaUsuarioPolQueue;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void sendInativar(UsuarioEquipeVendasDto usuarioDto) {
        rabbitTemplate.convertAndSend(equipeVendaUsuarioPolQueue, usuarioDto);
    }
}
