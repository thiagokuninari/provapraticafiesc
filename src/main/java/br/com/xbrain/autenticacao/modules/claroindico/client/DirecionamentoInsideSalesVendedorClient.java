package br.com.xbrain.autenticacao.modules.claroindico.client;

import br.com.xbrain.autenticacao.config.feign.FeignSkipBadRequestsConfiguration;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

@FeignClient(name = "direcionamentoInsideSalesVendedorClient",
    url = "${app-config.services.claro-indico.url}",
    configuration = FeignSkipBadRequestsConfiguration.class)
public interface DirecionamentoInsideSalesVendedorClient {

    String URL_DIRECIONAMENTO_VENDEDOR = "api/direcionamento-inside-sales-vendedores";

    @PutMapping(URL_DIRECIONAMENTO_VENDEDOR + "inativar-direcionamentos/{usuarioVendedorId}")
    void inativarDirecionamentoPorUsuarioVendedorId(@PathVariable("usuarioVendedorId") Integer usuarioVendedorId);
}
