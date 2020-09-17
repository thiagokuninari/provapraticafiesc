package br.com.xbrain.autenticacao.modules.usuario.rabbitmq;

import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioFeederMqDto;
import br.com.xbrain.autenticacao.modules.usuario.service.UsuarioService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class UsuarioFeederCadastroMqListener {

    @Autowired
    UsuarioService usuarioService;

    @RabbitListener(queues = "${app-config.queue.cadastro-usuario-feeder}")
    public void salvarUsuarioFeeder(UsuarioFeederMqDto usuarioFeederDto) {
        try {
            usuarioService.salvarUsuarioFeeder(usuarioFeederDto);
        } catch (Exception ex) {
            log.error("Erro ao processar fila do cadastro dos usuarios Feeder", ex);
        }
    }
}
