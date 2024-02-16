package br.com.xbrain.autenticacao.modules.agenteautorizadonovo.controller;

import br.com.xbrain.autenticacao.modules.agenteautorizadonovo.service.AgenteAutorizadoNovoService;
import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/agentes-autorizados")
public class AgenteAutorizadoController {

    private final AgenteAutorizadoNovoService service;
    private final AutenticacaoService autenticacaoService;

    @PostMapping("cache/estrutura/limpar")
    public void limparCachesEstruturas() {
        autenticacaoService.getUsuarioAutenticado().validarAdministrador();
        service.flushCacheEstruturasAas();
    }
}
