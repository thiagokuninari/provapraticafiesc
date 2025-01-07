package br.com.xbrain.autenticacao.modules.agenteautorizado.client;

import br.com.xbrain.autenticacao.config.feign.FeignSkipBadRequestsConfiguration;
import br.com.xbrain.autenticacao.modules.agenteautorizado.dto.SocioResponse;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "socioClient",
    url = "${app-config.services.agente-autorizado.url}",
    configuration = FeignSkipBadRequestsConfiguration.class)
public interface SocioClient {

    @GetMapping("api/socio/{agenteAutorizadoId}")
    SocioResponse findSocioPrincipalByAaId(@PathVariable("agenteAutorizadoId") Integer agenteAutorizadoId);

}
