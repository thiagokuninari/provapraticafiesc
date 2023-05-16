package br.com.xbrain.autenticacao.modules.parceirosonline.service;

import br.com.xbrain.autenticacao.config.feign.FeignSkipBadRequestsConfiguration;
import br.com.xbrain.autenticacao.modules.equipevenda.dto.EquipeVendaDto;
import br.com.xbrain.autenticacao.modules.parceirosonline.dto.EquipeVendasSupervisionadasResponse;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@FeignClient(name = "equipeVendaClient",
        url = "${app-config.services.parceiros-online.url}",
        configuration = FeignSkipBadRequestsConfiguration.class)
public interface EquipeVendasClient {

    @GetMapping("api/equipe-vendas/obter-equipes-supervisionadas/{supervisorId}")
    List<EquipeVendasSupervisionadasResponse> getEquipesPorSupervisor(@PathVariable("supervisorId") Integer supervisorId);

    @GetMapping("api/equipe-vendas/usuario/{usuarioId}")
    EquipeVendaDto getByUsuario(@PathVariable("usuarioId") Integer usuarioId);

    @GetMapping("api/equipe-vendas/usuarios-equipes")
    Map<Integer, Integer> getUsuarioEEquipeByUsuarioIds(@RequestParam(name = "usuarioIds") List<Integer> usuarioIds);
}
