package br.com.xbrain.autenticacao.modules.gestaocolaboradorespol.client;

import br.com.xbrain.autenticacao.config.feign.FeignSkipBadRequestsConfiguration;
import br.com.xbrain.autenticacao.modules.equipevenda.dto.EquipeVendaDto;
import br.com.xbrain.autenticacao.modules.gestaocolaboradorespol.dto.EquipeVendasSupervisionadasResponse;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;

@FeignClient(
    name = "EquipeVendasClient",
    configuration = FeignSkipBadRequestsConfiguration.class,
    url = "${app-config.services.gestao-colaborador-pol.url}"
)
public interface EquipeVendasClient {

    String API_EQUIPE_VENDAS = "api/equipe-vendas";

    @GetMapping(API_EQUIPE_VENDAS + "/obter-equipes-supervisionadas/{supervisorId}")
    List<EquipeVendasSupervisionadasResponse> getEquipesPorSupervisor(@PathVariable("supervisorId") Integer supervisorId);

    @GetMapping(API_EQUIPE_VENDAS + "/usuario/{usuarioId}")
    EquipeVendaDto getByUsuario(@PathVariable("usuarioId") Integer usuarioId);

    @PostMapping(API_EQUIPE_VENDAS + "/usuarios-equipes")
    Map<Integer, Integer> getUsuarioEEquipeByUsuarioIds(@RequestBody List<Integer> usuarioIds);
}
