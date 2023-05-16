package br.com.xbrain.autenticacao.modules.agenteautorizadonovo.rabbitmq;

import br.com.xbrain.autenticacao.modules.agenteautorizadonovo.dto.PermissaoTecnicoIndicadorDto;
import br.com.xbrain.autenticacao.modules.agenteautorizadonovo.service.PermissaoTecnicoIndicadorService;
import br.com.xbrain.autenticacao.modules.comum.enums.Eboolean;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PermissaoTecnicoIndicadorMqListener {

    private final PermissaoTecnicoIndicadorService service;

    @RabbitListener(queues = "${app-config.queue.atualizar-permissao-tecnico-indicador}")
    public void atualizarPermissaoTecnicoIndicador(PermissaoTecnicoIndicadorDto dto) {
        if (dto.getIsAdicionarPermissao() == Eboolean.V) {
            service.adicionarPermissaoTecnicoIndicador(dto);
        } else {
            service.removerPermissaoTecnicoIndicador(dto);
        }
    }
}
