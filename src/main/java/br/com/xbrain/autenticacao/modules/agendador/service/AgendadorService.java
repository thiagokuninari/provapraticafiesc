package br.com.xbrain.autenticacao.modules.agendador.service;

import br.com.xbrain.autenticacao.modules.agenteautorizado.service.AgenteAutorizadoService;
import br.com.xbrain.autenticacao.modules.feriado.service.FeriadoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AgendadorService {

    private final AgenteAutorizadoService aaService;
    private final FeriadoService feriadoService;

    public void flushCacheEstruturasAas() {
        log.info("Removendo caches de estrutura por agente autorizado.");
        aaService.flushCacheEstruturasAas();
        log.info("Finaliza processo de remoção de caches de estrutura por agente autorizado.");
    }

    public void clearCacheFeriados() {
        log.info("Removendo cache de feriados");
        feriadoService.flushCacheFeriados();
        log.info("Finaliza processo de remoção de cache de feriados");
    }
}
