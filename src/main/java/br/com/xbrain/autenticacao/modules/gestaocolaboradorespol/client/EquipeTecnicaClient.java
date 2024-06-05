package br.com.xbrain.autenticacao.modules.gestaocolaboradorespol.client;

import br.com.xbrain.autenticacao.config.feign.FeignSkipBadRequestsConfiguration;
import br.com.xbrain.autenticacao.modules.gestaocolaboradorespol.dto.EquipeTecnicaSupervisionadasResponse;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(
    name = "EquipeTecnicaClient",
    configuration = FeignSkipBadRequestsConfiguration.class,
    url = "${app-config.services.gestao-colaborador-pol.url}"
)
public interface EquipeTecnicaClient {

    String API_EQUIPE_TECNICA_POL = "api/equipes-tecnicas";

    @GetMapping(API_EQUIPE_TECNICA_POL + "/obter-equipes-supervisionadas/{supervisorId}")
    List<EquipeTecnicaSupervisionadasResponse> getEquipesTecnicasPorSupervisor(
        @PathVariable("supervisorId") Integer supervisorId);
}
