package br.com.xbrain.autenticacao.modules.suportevendas.client;

import br.com.xbrain.autenticacao.config.feign.FeignSkipBadRequestsConfiguration;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "suporteVendasClient",
    url = "${app-config.services.suporte-vendas-bko.url}",
    configuration = FeignSkipBadRequestsConfiguration.class)
public interface SuporteVendasClient {

    String URI_GRUPOS = "api/grupo";

    @GetMapping(URI_GRUPOS + "/organizacao/{organizacaoId}/verificar-grupos-em-outras-organizacoes/{usuarioId}")
    boolean existsGrupoByUsuarioAndOrganizacaoNot(@PathVariable("usuarioId") Integer usuarioId,
                                                  @PathVariable("organizacaoId") Integer organizacaoId);
}
