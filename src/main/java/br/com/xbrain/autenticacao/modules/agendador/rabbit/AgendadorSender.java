package br.com.xbrain.autenticacao.modules.agendador.rabbit;

import br.com.xbrain.autenticacao.modules.agendador.dto.AgendadorMqDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AgendadorSender {

    private final RabbitTemplate rabbitTemplate;
    @Value("${app-config.queue.agendador-success}")
    private String agendadorSuccessQueue;

    public void send(AgendadorMqDto agendadorMqDto) {
        try {
            rabbitTemplate.convertAndSend(agendadorSuccessQueue, agendadorMqDto);
        } catch (AmqpException ex) {
            log.error(String.format("Erro ao enviar processamento do job: %s para fila.", agendadorMqDto.getJobName()));
            log.error(ex.getMessage());
        }
    }
}
