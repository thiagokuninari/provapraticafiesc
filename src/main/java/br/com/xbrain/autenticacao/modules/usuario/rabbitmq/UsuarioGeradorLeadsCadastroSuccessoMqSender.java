package br.com.xbrain.autenticacao.modules.usuario.rabbitmq;

import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioCadastroSuccessoMqDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class UsuarioGeradorLeadsCadastroSuccessoMqSender {

    @Value("${app-config.queue.sucesso-cadastro-usuario-gerador-leads}")
    private String usuarioGeradorLeadsCadastroSuccesso;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void sendCadastroSuccessoMensagem(UsuarioCadastroSuccessoMqDto usuarioCadastroSuccessoMqDto) {
        try {
            rabbitTemplate.convertAndSend(usuarioGeradorLeadsCadastroSuccesso, usuarioCadastroSuccessoMqDto);
        } catch (Exception ex) {
            log.error("Erro ao enviar o cadastro successo mensagem do usuario " + usuarioCadastroSuccessoMqDto.getUsuarioId(),
                ex);
        }
    }
}
