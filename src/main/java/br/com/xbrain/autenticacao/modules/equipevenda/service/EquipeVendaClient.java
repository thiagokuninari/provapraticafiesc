package br.com.xbrain.autenticacao.modules.equipevenda.service;

import br.com.xbrain.autenticacao.config.feign.FeignSkipBadRequestsConfiguration;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

@FeignClient(name = "equipeVendaClient",
        url = "${app-config.services.equipe-venda.url}",
        configuration = FeignSkipBadRequestsConfiguration.class)
public interface EquipeVendaClient {

    @GetMapping("api/pausa-agendada/verificar-pausa/{username}")
    boolean verificarPausaEmAndamento(@PathVariable("username") String username);

    @PutMapping("api/equipes-vendas/inativar-supervisor/{id}")
    void inativarSupervidor(@PathVariable("id") Integer supervisorId);

    @PutMapping("api/usuarios-equipe/inativar/{id}")
    void inativarUsuarioEquipe(@PathVariable("id") Integer usuariostatusId);
}
