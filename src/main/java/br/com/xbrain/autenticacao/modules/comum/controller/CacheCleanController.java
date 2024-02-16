package br.com.xbrain.autenticacao.modules.comum.controller;

import br.com.xbrain.autenticacao.modules.agenteautorizadonovo.service.AgenteAutorizadoNovoService;
import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/cache-clean")
public class CacheCleanController {

    private final AgenteAutorizadoNovoService service;
    private final AutenticacaoService autenticacaoService;

    @DeleteMapping("agente-autorizado/estrutura")
    public void limparCachesEstruturas() {
        autenticacaoService.getUsuarioAutenticado().validarAdministrador();
        service.flushCacheEstruturasAas();
    }
}
