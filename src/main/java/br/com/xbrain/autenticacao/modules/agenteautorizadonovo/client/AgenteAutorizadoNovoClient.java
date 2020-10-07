package br.com.xbrain.autenticacao.modules.agenteautorizadonovo.client;

import br.com.xbrain.autenticacao.config.feign.FeignSkipBadRequestsConfiguration;
import br.com.xbrain.autenticacao.modules.agenteautorizadonovo.dto.UsuarioDtoVendas;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "agenteAutorizadoNovoClient",
    url = "${app-config.services.agente-autorizado.url}",
    configuration = {
        FeignSkipBadRequestsConfiguration.class
    })
public interface AgenteAutorizadoNovoClient {

    @GetMapping("api/{agenteAutorizadoId}/{buscarInativos}")
    List<UsuarioDtoVendas> buscarUsuariosDoAgenteAutorizado(@PathVariable("agenteAutorizadoId") Integer agenteAutorizadoId,
                                                            @PathVariable("buscarInativos") Boolean buscarInativos);
}

