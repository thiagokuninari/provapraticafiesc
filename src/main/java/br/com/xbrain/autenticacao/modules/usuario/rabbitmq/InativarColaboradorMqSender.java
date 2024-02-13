package br.com.xbrain.autenticacao.modules.usuario.rabbitmq;

import br.com.xbrain.autenticacao.modules.usuario.dto.ColaboradorInativacaoPolRequest;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class InativarColaboradorMqSender {

    @Value("${app-config.queue.inativar-colaborador-pol}")
    private String inativarColaboradorPolQueue;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void sendSuccess(ColaboradorInativacaoPolRequest colaboradorInativacao) {
        rabbitTemplate.convertAndSend(inativarColaboradorPolQueue, colaboradorInativacao);
    }
}
