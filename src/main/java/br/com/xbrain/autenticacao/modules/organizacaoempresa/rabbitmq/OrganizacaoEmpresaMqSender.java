package br.com.xbrain.autenticacao.modules.organizacaoempresa.rabbitmq;

import br.com.xbrain.autenticacao.modules.organizacaoempresa.dto.OrganizacaoEmpresaUpdateDto;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class OrganizacaoEmpresaMqSender {

    @Value("${app-config.queue.organizacao-empresa-atualizacao-nome}")
    private String organizacaoEmpresaAtualizacaoNomeQueue;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void sendUpdateNomeSucess(OrganizacaoEmpresaUpdateDto organizacaoEmpresaDto) {
        rabbitTemplate.convertAndSend(organizacaoEmpresaAtualizacaoNomeQueue, organizacaoEmpresaDto);
    }
}
