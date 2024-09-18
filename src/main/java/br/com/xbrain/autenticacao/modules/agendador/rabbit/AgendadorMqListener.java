package br.com.xbrain.autenticacao.modules.agendador.rabbit;

import br.com.xbrain.autenticacao.modules.agendador.dto.AgendadorMqDto;
import br.com.xbrain.autenticacao.modules.agendador.enums.EAgendador;
import br.com.xbrain.autenticacao.modules.agendador.enums.EStatusAgendador;
import br.com.xbrain.autenticacao.modules.agendador.service.AgendadorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AgendadorMqListener {

    private final AgendadorService service;
    private final AgendadorSender agendadorSender;

    @RabbitListener(queues = "${app-config.queue.agendador-autenticacao-api}")
    public void executarAgendador(AgendadorMqDto agendadorMqDto) {
        try {
            log.info("Inicia execução do agendador: {}.", agendadorMqDto.getJobName());
            agendadorMqDto.setStatus(EStatusAgendador.EM_PROCESSO);
            EAgendador.convertFrom(agendadorMqDto.getJobName()).executar(service, agendadorMqDto);
        } catch (Exception ex) {
            service.setarErroEEnviarParaFila(ex, agendadorMqDto, agendadorSender);
            throw new AmqpRejectAndDontRequeueException(ex);
        }
    }
}
