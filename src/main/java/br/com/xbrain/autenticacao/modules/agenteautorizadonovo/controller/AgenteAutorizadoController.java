package br.com.xbrain.autenticacao.modules.agenteautorizadonovo.controller;

import br.com.xbrain.autenticacao.modules.agenteautorizadonovo.service.AgenteAutorizadoNovoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/agentes-autorizados")
public class AgenteAutorizadoController {

    private final AgenteAutorizadoNovoService service;

    @PostMapping("cache/estrutura/limpar")
    public void limparCachesEstruturas() {
        service.flushCacheEstruturasAas();
    }
}
