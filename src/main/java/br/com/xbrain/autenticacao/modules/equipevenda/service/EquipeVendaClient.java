package br.com.xbrain.autenticacao.modules.equipevenda.service;

import br.com.xbrain.autenticacao.config.feign.FeignSkipBadRequestsConfiguration;
import br.com.xbrain.autenticacao.modules.equipevenda.dto.EquipeVendaDto;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@FeignClient(name = "equipeVendaClient",
        url = "${app-config.services.equipe-venda.url}",
        configuration = FeignSkipBadRequestsConfiguration.class)
public interface EquipeVendaClient {

    String EQUIPE_VENDAS_ENDPOINT = "api/equipes-vendas";
    String PAUSA_AGENDADA_ENDPOINT = "api/pausa-agendada";

    @GetMapping(PAUSA_AGENDADA_ENDPOINT + "/verificar-pausa/{username}")
    boolean verificarPausaEmAndamento(@PathVariable("username") String username);

    @GetMapping(EQUIPE_VENDAS_ENDPOINT + "/usuario")
    List<EquipeVendaDto> getUsuario(@RequestParam Map request);
}
