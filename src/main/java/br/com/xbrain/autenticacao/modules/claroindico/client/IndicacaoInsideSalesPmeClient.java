package br.com.xbrain.autenticacao.modules.claroindico.client;

import br.com.xbrain.autenticacao.config.feign.FeignSkipBadRequestsConfiguration;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

@FeignClient(name = "indicacaoInsideSalesPmeClient",
    url = "${app-config.services.claro-indico.url}",
    configuration = FeignSkipBadRequestsConfiguration.class)
public interface IndicacaoInsideSalesPmeClient {
    String URL_INDICACAO_INSIDE_SALES = "api/indicacoes-inside-sales-pme";

    @PutMapping(URL_INDICACAO_INSIDE_SALES + "/redistribuir-indicacoes/{usuarioVendedorId}")
    void redistribuirIndicacoesPorUsuarioVendedorId(@PathVariable("usuarioVendedorId") Integer usuarioVendedorId);
}
