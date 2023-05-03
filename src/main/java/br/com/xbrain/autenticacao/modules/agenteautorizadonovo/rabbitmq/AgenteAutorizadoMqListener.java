package br.com.xbrain.autenticacao.modules.agenteautorizadonovo.rabbitmq;

import br.com.xbrain.autenticacao.modules.agenteautorizadonovo.dto.PermissaoTecnicoIndicadorDto;
import br.com.xbrain.autenticacao.modules.permissao.dto.PermissaoEspecialRequest;
import br.com.xbrain.autenticacao.modules.permissao.service.PermissaoEspecialService;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AgenteAutorizadoMqListener {

    private static final Integer POL_TABULAR_TECNICO_INDICADOR_ID = 253;

    @Autowired
    private PermissaoEspecialService permissaoEspecialService;

    @RabbitListener(queues = "${app-config.queue.adicionar-permissao-tecnico-indicador}")
    public void adicionarPermissaoTecnicoIndicador(PermissaoTecnicoIndicadorDto dto) {
        try {
            dto.getUsuariosIds().forEach(usuarioId ->
                permissaoEspecialService.save(PermissaoEspecialRequest.builder()
                    .usuarioId(usuarioId)
                    .funcionalidadesIds(List.of(POL_TABULAR_TECNICO_INDICADOR_ID))
                    .build()));
        } catch (Exception ex) {
            log.error("Erro ao processar fila para adicionar permissão de técnico indicador", ex);
        }
    }

    @RabbitListener(queues = "${app-config.queue.remover-permissao-tecnico-indicador}")
    public void removerPermissaoTecnicoIndicador(PermissaoTecnicoIndicadorDto dto) {
        try {
            dto.getUsuariosIds().forEach(usuarioId ->
                permissaoEspecialService.remover(usuarioId, POL_TABULAR_TECNICO_INDICADOR_ID));
        } catch (Exception ex) {
            log.error("Erro ao processar fila para remover permissão de técnico indicador", ex);
        }
    }
}
