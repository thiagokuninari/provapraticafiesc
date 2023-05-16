package br.com.xbrain.autenticacao.modules.agenteautorizadonovo.rabbitmq;

import br.com.xbrain.autenticacao.modules.agenteautorizadonovo.dto.PermissaoTecnicoIndicadorDto;
import br.com.xbrain.autenticacao.modules.agenteautorizadonovo.service.PermissaoTecnicoIndicadorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PermissaoTecnicoIndicadorMqListener {

    private final PermissaoTecnicoIndicadorService service;

    @RabbitListener(queues = "${app-config.queue.atualizar-permissao-tecnico-indicador}")
    public void atualizarPermissaoTecnicoIndicador(PermissaoTecnicoIndicadorDto dto) {
        try {
            service.atualizarPermissaoTecnicoIndicador(dto);
        } catch (Exception ex) {
            log.error("Erro ao processar fila para atualizar permissão de técnico indicador", ex);
        }
    }
}
