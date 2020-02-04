package br.com.xbrain.autenticacao.modules.geradorlead.rabbitmq;

import br.com.xbrain.autenticacao.modules.geradorlead.dto.AgenteAutorizadoGeradorLeadDto;
import br.com.xbrain.autenticacao.modules.geradorlead.service.GeradorLeadService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GeradorLeadMqListener {

    @Autowired
    private GeradorLeadService service;

    @RabbitListener(queues = "${app-config.queue.atualizar-permissao-gerador-lead}")
    public void atualizarPermissaoGeradorLead(AgenteAutorizadoGeradorLeadDto agenteAutorizadoGeradorLeadDto) {
        service.atualizarPermissaoGeradorLead(agenteAutorizadoGeradorLeadDto);
    }
}

