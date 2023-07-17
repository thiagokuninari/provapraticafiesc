package br.com.xbrain.autenticacao.modules.organizacaoempresa.rabbit;

import br.com.xbrain.autenticacao.modules.organizacaoempresa.dto.OrganizacaoEmpresaUpdateDto;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrganizacaoEmpresaMqSender {

    @Value("${app-config.queue.organizacao-empresa-atualizacao-nome}")
    private String organizacaoEmpresaAtualizacaoNomeQueue;

    private final RabbitTemplate rabbitTemplate;

    public void sendUpdateNomeSucess(OrganizacaoEmpresaUpdateDto organizacaoEmpresaDto) {
        rabbitTemplate.convertAndSend(organizacaoEmpresaAtualizacaoNomeQueue, organizacaoEmpresaDto);
    }

}
