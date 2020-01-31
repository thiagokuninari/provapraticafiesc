package br.com.xbrain.autenticacao.modules.usuario.rabbitmq;

import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioGeradorLeadsMqDto;
import br.com.xbrain.autenticacao.modules.usuario.service.UsuarioService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Slf4j
public class UsuarioGeradorLeadsCadastroMqListener {

    @Autowired
    UsuarioService usuarioService;

    @RabbitListener(queues = "${app-config.queue.cadastro-usuario-gerador-leads}")
    @Transactional
    public void salvarUsuarioGeradorLeads(UsuarioGeradorLeadsMqDto usuarioGeradorLeadsDto) {
        try {
            usuarioService.salvaUsuarioGeradorLeads(usuarioGeradorLeadsDto);
        } catch (Exception ex) {
            log.error("Erro ao processar fila do cadastro dos usuarios Gerador de Leads", ex);
        }
    }
}
