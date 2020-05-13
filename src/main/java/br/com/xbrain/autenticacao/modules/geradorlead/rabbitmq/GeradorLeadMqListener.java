package br.com.xbrain.autenticacao.modules.geradorlead.rabbitmq;

import br.com.xbrain.autenticacao.modules.geradorlead.dto.AgenteAutorizadoGeradorLeadDto;
import br.com.xbrain.autenticacao.modules.geradorlead.dto.SituacaoAlteracaoGeradorLeadsDto;
import br.com.xbrain.autenticacao.modules.geradorlead.service.GeradorLeadService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class GeradorLeadMqListener {

    @Autowired
    private GeradorLeadService service;

    @RabbitListener(queues = "${app-config.queue.atualizar-permissao-gerador-lead}")
    public void atualizarPermissaoGeradorLead(AgenteAutorizadoGeradorLeadDto agenteAutorizadoGeradorLeadDto) {
        try {
            service.atualizarPermissaoGeradorLead(agenteAutorizadoGeradorLeadDto);
        } catch (Exception ex) {
            log.error("Erro ao processar fila de mensagem de atualizar permissões de gerador de leads", ex);
        }
    }

    @RabbitListener(queues = "${app-config.queue.alterar-situacao-gerador-leads}")
    public void alterarSituacaoGeradorLeads(SituacaoAlteracaoGeradorLeadsDto situacaoAlteracaoGeradorLeadDto) {
        try {
            service.alterarSituacaoGeradorLeads(situacaoAlteracaoGeradorLeadDto);
        } catch (Exception ex) {
            log.error("Erro ao processar fila de mensagem de alterar a situação de gerador de leads", ex);
        }
    }
}

