package br.com.xbrain.autenticacao.modules.claroindico.client;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

import br.com.xbrain.autenticacao.config.feign.FeignSkipBadRequestsConfiguration;

@FeignClient(name = "claroIndicoClient",
    url = "${app-config.services.claro-indico.url}",
    configuration = FeignSkipBadRequestsConfiguration.class)
public interface ClaroIndicoClient {

    public String URI_FILAS_TRATAMENTO = "api/filas-tratamento";

    @PutMapping(URI_FILAS_TRATAMENTO + "/usuarios/{usuarioId}/desvincular")
    public void desvincularUsuarioDaFilaTratamento(@PathVariable("usuarioId") Integer usuarioId);
}
