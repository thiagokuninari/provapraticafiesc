package br.com.xbrain.autenticacao.modules.organizacaoempresa.rabbitmq;

import br.com.xbrain.autenticacao.modules.organizacaoempresa.dto.OrganizacaoEmpresaDto;
import br.com.xbrain.autenticacao.modules.organizacaoempresa.dto.OrganizacaoEmpresaUpdateDto;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class OrganizacaoEmpresaMqSender {

    @Value("${app-config.queue.organizacao-empresa-cadastro-success}")
    private String organizacaoEmpresaCadastroSuccessQueue;

    @Value("${app-config.queue.organizacao-empresa-atualizacao-success}")
    private String organizacaoEmpresaAtualizacaoSuccessQueue;

    @Value("${app-config.queue.organizacao-empresa-inativar-situacao-success}")
    private String organizacaoEmpresaInativarSituacaoSuccessQueue;

    @Value("${app-config.queue.organizacao-empresa-ativar-situacao-success}")
    private String organizacaoEmpresaAtivarSituacaoSuccessQueue;

    @Value("${app-config.queue.organizacao-empresa-atualizacao-nome}")
    private String organizacaoEmpresaAtualizacaoNomeQueue;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void sendSuccess(OrganizacaoEmpresaDto organizacaoEmpresaDto) {
        rabbitTemplate.convertAndSend(organizacaoEmpresaCadastroSuccessQueue, organizacaoEmpresaDto);
    }

    public void sendUpdateSuccess(OrganizacaoEmpresaDto organizacaoEmpresaDto) {
        rabbitTemplate.convertAndSend(organizacaoEmpresaAtualizacaoSuccessQueue, organizacaoEmpresaDto);
    }

    public void sendInativarSituacaoSuccess(OrganizacaoEmpresaDto organizacaoEmpresaDto) {
        rabbitTemplate.convertAndSend(organizacaoEmpresaInativarSituacaoSuccessQueue, organizacaoEmpresaDto);
    }

    public void sendAtivarSituacaoSuccess(OrganizacaoEmpresaDto organizacaoEmpresaDto) {
        rabbitTemplate.convertAndSend(organizacaoEmpresaAtivarSituacaoSuccessQueue, organizacaoEmpresaDto);
    }

    public void sendUpdateNomeSucess(OrganizacaoEmpresaUpdateDto organizacaoEmpresaDto) {
        rabbitTemplate.convertAndSend(organizacaoEmpresaAtualizacaoNomeQueue, organizacaoEmpresaDto);
    }
}
