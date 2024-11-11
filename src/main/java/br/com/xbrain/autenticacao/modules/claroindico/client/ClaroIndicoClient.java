package br.com.xbrain.autenticacao.modules.claroindico.client;

import br.com.xbrain.autenticacao.config.feign.FeignSkipBadRequestsConfiguration;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "claroIndicoClient",
    url = "${app-config.services.claro-indico.url}",
    configuration = FeignSkipBadRequestsConfiguration.class)
public interface ClaroIndicoClient {

    String URI_FILAS_TRATAMENTO = "api/filas-tratamento";

    @GetMapping(URI_FILAS_TRATAMENTO + "/usuarios-vinculados")
    List<Integer> buscarUsuariosVinculados();

    @PutMapping(URI_FILAS_TRATAMENTO + "/usuarios/{usuarioId}/desvincular")
    void desvincularUsuarioDaFilaTratamento(
        @PathVariable("usuarioId") Integer usuarioId,
        @RequestParam boolean inativacaoUsuario
    );
}
