package br.com.xbrain.autenticacao.modules.gestaocolaboradorespol.client;

import br.com.xbrain.autenticacao.config.feign.FeignSkipBadRequestsConfiguration;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(
    name = "colaboradorVendasClient",
    configuration = FeignSkipBadRequestsConfiguration.class,
    url = "${app-config.services.gestao-colaborador-pol.url}"
)
public interface ColaboradorVendasClient {

    String API_COLABORADOR_VENDAS = "api/colaboradores-vendas";

    @PutMapping(API_COLABORADOR_VENDAS + "/limpar-cpf")
    void limparCpfColaboradorVendas(@RequestParam("email") String email);

    @GetMapping(API_COLABORADOR_VENDAS + "/cargos")
    List<Integer> getUsuariosAaFeederPorCargo(@RequestParam("aaIds") List<Integer> aaIds,
                                              @RequestParam("cargos") List<CodigoCargo> cargos);
}
