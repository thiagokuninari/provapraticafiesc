package br.com.xbrain.autenticacao.modules.agenteautorizadonovo.client;

import br.com.xbrain.autenticacao.config.feign.FeignSkipBadRequestsConfiguration;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "agenteAutorizadoNovoClient",
    url = "${app-config.services.agente-autorizado.url}",
    configuration = {
        FeignSkipBadRequestsConfiguration.class
    })
public interface AgenteAutorizadoNovoClient {

    @GetMapping("api/usuarios-id-por-agente-autorizado-id")
    List<Integer> buscarUsuariosIdsPorAaId(
        @RequestParam("agenteAutorizadoId") Integer agenteAutorizadoId,
        @RequestParam("buscarInativos") Boolean buscarInativos);
}

