package br.com.xbrain.autenticacao.modules.agendador.rabbit;

import br.com.xbrain.autenticacao.modules.agendador.dto.AgendadorMqDto;
import br.com.xbrain.autenticacao.modules.agendador.enums.EAgendador;
import br.com.xbrain.autenticacao.modules.agendador.service.AgendadorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
        log.info("Inicia execução do agendador: {}.", agendadorMqDto.getJobName());

        EAgendador.convertFrom(agendadorMqDto.getJobName()).executar(service, agendadorMqDto, agendadorSender);
    }
}
