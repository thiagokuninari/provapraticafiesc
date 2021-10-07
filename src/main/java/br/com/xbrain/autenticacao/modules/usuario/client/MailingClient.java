package br.com.xbrain.autenticacao.modules.usuario.client;

import br.com.xbrain.autenticacao.config.feign.FeignSkipBadRequestsConfiguration;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "mailingClient",
    url = "${app-config.services.mailing.url}",
    configuration = FeignSkipBadRequestsConfiguration.class)
public interface MailingClient {

    String URL_MAILING = "api/tabulacao";

    @GetMapping(URL_MAILING + "count/agendamentos/proprietarios/{usuarioId}")
    Long countQuantidadeAgendamentosProprietariosDoUsuario(@PathVariable("usuarioId") Integer usuarioId);
}
