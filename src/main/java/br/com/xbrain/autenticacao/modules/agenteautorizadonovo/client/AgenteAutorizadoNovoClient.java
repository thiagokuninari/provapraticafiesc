package br.com.xbrain.autenticacao.modules.agenteautorizadonovo.client;

import br.com.xbrain.autenticacao.config.feign.FeignSkipBadRequestsConfiguration;
import br.com.xbrain.autenticacao.config.feign.FeignTimeoutConfiguration;
import br.com.xbrain.autenticacao.modules.agenteautorizadonovo.dto.UsuarioDtoVendas;
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

    String URL_AGENTE_AUTORIZADO = "api";

    @GetMapping(URL_AGENTE_AUTORIZADO + "/todos-usuarios-dos-aas")
    List<UsuarioDtoVendas> buscarTodosUsuariosDosAas(@RequestParam("aasIds") List<Integer> aasIds,
                                                     @RequestParam("buscarInativos") Boolean buscarInativos);
}

