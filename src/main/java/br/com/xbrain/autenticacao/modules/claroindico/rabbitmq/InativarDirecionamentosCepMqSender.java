package br.com.xbrain.autenticacao.modules.claroindico.rabbitmq;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InativarDirecionamentosCepMqSender {

    @Value("${app-config.queue.inativar-direcionamentos-cep-vendedor-inside-sales}")
    private String inativarDirecionamentoCepQueue;

    private final RabbitTemplate rabbitTemplate;

    public void sendInativarDirecionamentoCep(Integer vendedorId) {
        rabbitTemplate.convertAndSend(inativarDirecionamentoCepQueue, vendedorId);
    }
}
