package br.com.xbrain.autenticacao.modules.parceirosonline.service;

import br.com.xbrain.autenticacao.config.feign.FeignSkipBadRequestsConfiguration;
import br.com.xbrain.autenticacao.modules.parceirosonline.dto.AgenteAutorizadoResponse;
import br.com.xbrain.autenticacao.modules.parceirosonline.dto.UsuarioAgenteAutorizadoResponse;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@FeignClient(name = "agenteAutorizadoClient",
        url = "${app-config.services.parceiros-online.url}",
        configuration = FeignSkipBadRequestsConfiguration.class)
public interface AgenteAutorizadoClient {

    @RequestMapping(
            method = RequestMethod.GET,
            value = "api/agente-autorizado/agente-autorizado-por-cnpj")
    AgenteAutorizadoResponse getAaByCpnj(@RequestParam Map request);

    @RequestMapping(
            method = RequestMethod.GET,
            value = "api/usuarios-agente-autorizado/{agenteAutorizadoId}")
    List<UsuarioAgenteAutorizadoResponse> getUsuariosByAaId(
            @PathVariable("agenteAutorizadoId") Integer agenteAutorizadoId);
}
