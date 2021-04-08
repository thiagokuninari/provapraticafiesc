package br.com.xbrain.autenticacao.modules.feeder.rabbitmq;

import br.com.xbrain.autenticacao.modules.feeder.dto.AgenteAutorizadoPermissaoFeederDto;
import br.com.xbrain.autenticacao.modules.feeder.dto.SituacaoAlteracaoUsuarioFeederDto;
import br.com.xbrain.autenticacao.modules.feeder.service.FeederService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class FeederMqListener {

    @Autowired
    private FeederService service;

    @RabbitListener(queues = "${app-config.queue.atualizar-permissao-feeder}")
    public void atualizarPermissaoFeeder(AgenteAutorizadoPermissaoFeederDto agenteAutorizadoPermissaoFeederDto) {
        try {
            service.atualizarPermissaoFeeder(agenteAutorizadoPermissaoFeederDto);
        } catch (Exception ex) {
            log.error("Erro ao processar fila de mensagem de atualizar permissões de agente autorizado para Feeder.", ex);
        }
    }

    @RabbitListener(queues = "${app-config.queue.alterar-situacao-usuario-feeder}")
    public void alterarSituacaoUsuarioFeeder(SituacaoAlteracaoUsuarioFeederDto situacaoAlteracaoFeederDto) {
        try {
            service.alterarSituacaoUsuarioFeeder(situacaoAlteracaoFeederDto);
        } catch (Exception ex) {
            log.error("Erro ao processar fila de mensagem de alterar a situação de usuário Feeder.", ex);
        }
    }
}

