package br.com.xbrain.autenticacao.modules.parceirosonline.service;

import br.com.xbrain.autenticacao.config.feign.FeignSkipBadRequestsConfiguration;
import br.com.xbrain.autenticacao.modules.parceirosonline.dto.EquipeVendasSupervisorResponse;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(name = "colaboradorVendasClient",
        url = "${app-config.services.parceiros-online.url}",
        configuration = FeignSkipBadRequestsConfiguration.class)
public interface ColaboradorVendasClient {

    @RequestMapping(
            method = RequestMethod.GET,
            path = "api/colaboradores-vendas/equipe-vendas/usuario/{usuarioId}"
    )
    EquipeVendasSupervisorResponse getEquipeVendasSupervisorDoUsuarioId(@PathVariable("usuarioId") Integer usuarioId);
}
