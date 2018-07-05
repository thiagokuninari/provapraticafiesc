package br.com.xbrain.autenticacao.modules.notificacao.controller;

import br.com.xbrain.autenticacao.modules.notificacao.dto.BoaVindaAgenteAutorizadoRequest;
import br.com.xbrain.autenticacao.modules.notificacao.service.NotificacaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "api/notificacao")
public class NotificacaoController {

    @Autowired
    private NotificacaoService notificacaoService;

    @PostMapping(value = "/boas-vindas-agente-autorizado")
    public void save(@Validated @RequestBody BoaVindaAgenteAutorizadoRequest request) {
        notificacaoService.enviarEmailBoaVindaAgenteAutorizado(request);
    }

}
