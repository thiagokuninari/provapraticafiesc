package br.com.xbrain.autenticacao.modules.equipevenda.service;

import br.com.xbrain.autenticacao.config.feign.FeignSkipBadRequestsConfiguration;
import br.com.xbrain.autenticacao.modules.equipevenda.dto.EquipeVendaUsuarioResponse;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@FeignClient(name = "equipeVendaUsuarioClient",
    url = /*"${app-config.services.equipe-venda.url}"*/"http://localhost:8089",
    configuration = FeignSkipBadRequestsConfiguration.class)
public interface EquipeVendasUsuarioClient {

    String API_EQUIPE_VENDAS_USUARIO = "/api/usuarios-equipe";

    @GetMapping(API_EQUIPE_VENDAS_USUARIO + "/all")
    List<EquipeVendaUsuarioResponse> getAll(@RequestParam Map<String, Object> filtros);

    @GetMapping(API_EQUIPE_VENDAS_USUARIO + "/buscar-usuario")
    List<EquipeVendaUsuarioResponse> buscarUsuarioPorId(@RequestParam("id") Integer id);

}
