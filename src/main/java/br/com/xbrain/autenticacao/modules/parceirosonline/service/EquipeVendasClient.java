package br.com.xbrain.autenticacao.modules.parceirosonline.service;

import br.com.xbrain.autenticacao.config.feign.FeignSkipBadRequestsConfiguration;
import br.com.xbrain.autenticacao.modules.parceirosonline.dto.EquipeVendasSupervisionadasResponse;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

@FeignClient(name = "equipeVendaClient",
        url = "${app-config.services.parceiros-online.url}",
        configuration = FeignSkipBadRequestsConfiguration.class)
public interface EquipeVendasClient {

    @RequestMapping(
            method = RequestMethod.GET,
            value = "api/equipe-vendas/obter-equipes-supervisionadas/{supervisorId}")
    List<EquipeVendasSupervisionadasResponse> getEquipesPorSupervisor(@PathVariable("supervisorId") Integer supervisorId);

}