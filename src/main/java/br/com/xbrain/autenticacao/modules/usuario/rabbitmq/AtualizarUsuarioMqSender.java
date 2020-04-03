package br.com.xbrain.autenticacao.modules.usuario.rabbitmq;

import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioDto;
import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioExcessoUsoRequest;
import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioUltimoAcessoPol;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AtualizarUsuarioMqSender {

    @Value("${app-config.queue.atualizar-usuario-pol}")
    private String atualizarUsuarioPolQueue;

    @Value("${app-config.queue.usuario-ultimo-acesso-pol}")
    private String usuarioUltimoAcessoPol;

    @Value("${app-config.queue.inativar-por-excesso-uso}")
    private String inativarPorExcessoDeUso;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void sendSuccess(UsuarioDto usuarioDto) {
        rabbitTemplate.convertAndSend(atualizarUsuarioPolQueue, usuarioDto);
    }

    public void sendUltimoAcessoPol(UsuarioUltimoAcessoPol ultimoAcessoPol) {
        rabbitTemplate.convertAndSend(usuarioUltimoAcessoPol, ultimoAcessoPol);
    }

    public void inativarPorExcessoDeUso(UsuarioExcessoUsoRequest request) {
        rabbitTemplate.convertAndSend(inativarPorExcessoDeUso, request);
    }
}
