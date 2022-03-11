package br.com.xbrain.autenticacao.modules.mailing.client;

import br.com.xbrain.autenticacao.config.feign.FeignSkipBadRequestsConfiguration;
import br.com.xbrain.autenticacao.modules.usuario.enums.ECanal;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "mailingClient",
    url = "${app-config.services.mailing.url}",
    configuration = FeignSkipBadRequestsConfiguration.class)
public interface MailingClient {

    String URL_MAILING = "api/tabulacao";
    String API_CACHE_CLEAN_FERIADOS = "api/public/feriados";

    @GetMapping(URL_MAILING + "/count/agendamentos/proprietarios/{usuarioId}")
    Long countQuantidadeAgendamentosProprietariosDoUsuario(@PathVariable("usuarioId") Integer usuarioId,
                                                           @RequestHeader("X-Usuario-Canal") ECanal canal);

    @GetMapping(API_CACHE_CLEAN_FERIADOS + "/cache-clean")
    void flushCacheFeriados();
}
