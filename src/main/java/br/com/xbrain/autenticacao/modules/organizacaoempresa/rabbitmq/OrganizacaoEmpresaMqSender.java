package br.com.xbrain.autenticacao.modules.organizacaoempresa.rabbitmq;

import br.com.xbrain.autenticacao.modules.organizacaoempresa.dto.OrganizacaoEmpresaUpdateDto;
import br.com.xbrain.autenticacao.modules.organizacaoempresa.dto.OrganizacaoFanoutDto;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrganizacaoEmpresaMqSender {

    @Value("${app-config.queue.organizacao-empresa-atualizacao-nome}")
    private String organizacaoEmpresaAtualizacaoNomeQueue;
    @Value("${app-config.fanout.organizacao-inativada}")
    private String organizacaoInativadaFanout;

    private final RabbitTemplate rabbitTemplate;

    public void sendUpdateNomeSucess(OrganizacaoEmpresaUpdateDto organizacaoEmpresaDto) {
        rabbitTemplate.convertAndSend(organizacaoEmpresaAtualizacaoNomeQueue, organizacaoEmpresaDto);
    }

    public void sendOrganizacaoInativada(OrganizacaoFanoutDto organizacaoFanoutDto) {
        rabbitTemplate.convertAndSend(organizacaoInativadaFanout, "", organizacaoFanoutDto);
    }
}
