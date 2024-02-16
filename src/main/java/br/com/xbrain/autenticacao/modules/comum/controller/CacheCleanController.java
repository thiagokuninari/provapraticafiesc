package br.com.xbrain.autenticacao.modules.comum.controller;

import br.com.xbrain.autenticacao.modules.agenteautorizadonovo.service.AgenteAutorizadoNovoService;
import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.usuario.enums.ETipoConfiguracao;
import br.com.xbrain.autenticacao.modules.usuario.service.ConfiguracaoAgendaRealService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/cache-clean")
public class CacheCleanController {

    private final ConfiguracaoAgendaRealService configuracaoAgendaRealService;
    private final AgenteAutorizadoNovoService agenteAutorizadoNovoService;
    private final AutenticacaoService autenticacaoService;

    @DeleteMapping("agente-autorizado/estrutura")
    public void limparCachesEstruturas() {
        autenticacaoService.getUsuarioAutenticado().validarAdministrador();
        agenteAutorizadoNovoService.flushCacheEstruturasAas();
    }

    @DeleteMapping("configuracao-agenda")
    public void limparCacheConfiguracaoAgenda() {
        autenticacaoService.getUsuarioAutenticado().validarAdministrador();
        Arrays.asList(ETipoConfiguracao.values())
            .forEach(configuracaoAgendaRealService::flushCacheByTipoConfig);
    }
}
