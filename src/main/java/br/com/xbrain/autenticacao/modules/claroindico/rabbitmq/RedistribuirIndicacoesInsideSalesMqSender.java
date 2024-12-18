package br.com.xbrain.autenticacao.modules.claroindico.rabbitmq;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RedistribuirIndicacoesInsideSalesMqSender {

    @Value("${app-config.queue.redistribuir-indicacoes-inside-sales-vendedor}")
    private String redistribuirIndicacoesInsideSalesQueue;

    private final RabbitTemplate rabbitTemplate;

    public void sendRedistribuirIndicacoesInsideSales(Integer vendedorId) {
        rabbitTemplate.convertAndSend(redistribuirIndicacoesInsideSalesQueue, vendedorId);
    }
}
