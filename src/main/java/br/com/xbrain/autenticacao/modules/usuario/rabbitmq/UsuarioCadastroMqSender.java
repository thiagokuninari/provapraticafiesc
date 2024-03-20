package br.com.xbrain.autenticacao.modules.usuario.rabbitmq;

import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioDto;
import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioMqRequest;
import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioSocialHubRequestMq;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class UsuarioCadastroMqSender {

    @Value("${app-config.queue.usuario-cadastro-success}")
    private String usuarioCadastroSuccessQueue;
    @Value("${app-config.queue.usuario-cadastro-socio-principal-success}")
    private String usuarioCadastroSocioPrincipalSuccessQueue;
    @Value("${app-config.queue.usuario-cadastro-loja-futuro-success}")
    private String usuarioCadastroLojaFuturoSuccessQueue;
    @Value("${app-config.queue.usuario-atualizar-socio-principal-success}")
    private String usuarioAtualizarSocioPrincipalSuccessMqQueue;
    @Value("${app-config.queue.usuario-cadastro-failure}")
    private String usuarioCadastroFailureQueue;
    @Value("${app-config.queue.usuario-remanejar-pol-failure}")
    private String usuarioRemanejarPolFailureMq;
    @Value("${app-config.queue.usuario-atualizacao-social-hub}")
    private String usuarioAtualizacaoSocialHubMq;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void sendSuccess(UsuarioDto usuarioDto) {
        rabbitTemplate.convertAndSend(usuarioCadastroSuccessQueue, usuarioDto);
    }

    public void sendSuccessSocioPrincipal(UsuarioDto usuarioDto) {
        rabbitTemplate.convertAndSend(usuarioCadastroSocioPrincipalSuccessQueue, usuarioDto);
    }

    public void sendSuccessLojaFuturo(UsuarioDto usuarioDto) {
        rabbitTemplate.convertAndSend(usuarioCadastroLojaFuturoSuccessQueue, usuarioDto);
    }

    public void sendSuccessAtualizarSocioPrincipal(UsuarioDto usuarioDto) {
        rabbitTemplate.convertAndSend(usuarioAtualizarSocioPrincipalSuccessMqQueue, usuarioDto);
    }

    public void sendWithFailure(UsuarioMqRequest usuarioMqRequest) {
        rabbitTemplate.convertAndSend(usuarioCadastroFailureQueue, usuarioMqRequest);
    }

    public void sendRemanejamentoWithFailure(UsuarioMqRequest usuarioMqRequest) {
        rabbitTemplate.convertAndSend(usuarioRemanejarPolFailureMq, usuarioMqRequest);
    }

    public void enviarDadosUsuarioParaSocialHub(UsuarioSocialHubRequestMq usuarioSocialHubRequestMq) {
        try {
            log.info("Enviando dados usuario para fila de atualização de dados socialHub, usuarioId: {}",
                usuarioSocialHubRequestMq.getId());
            rabbitTemplate.convertAndSend(usuarioAtualizacaoSocialHubMq, usuarioSocialHubRequestMq);
        } catch (AmqpException ex) {
            log.error(ex.getMessage(), "Erro ao enviar dados para fila usuarioSocialHub");
        }
    }
}
