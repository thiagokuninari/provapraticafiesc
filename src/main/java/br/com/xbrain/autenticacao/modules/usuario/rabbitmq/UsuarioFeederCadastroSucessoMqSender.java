package br.com.xbrain.autenticacao.modules.usuario.rabbitmq;

import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioCadastroSucessoMqDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class UsuarioFeederCadastroSucessoMqSender {

    @Value("${app-config.queue.sucesso-cadastro-usuario-feeder}")
    private String usuarioFeederCadastroSuccesso;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void sendCadastroSuccessoMensagem(UsuarioCadastroSucessoMqDto usuarioCadastroSucessoMqDto) {
        try {
            rabbitTemplate.convertAndSend(usuarioFeederCadastroSuccesso, usuarioCadastroSucessoMqDto);
        } catch (Exception ex) {
            log.error("Erro ao enviar o cadastro successo mensagem do usuario " + usuarioCadastroSucessoMqDto.getUsuarioId(),
                ex);
        }
    }
}
