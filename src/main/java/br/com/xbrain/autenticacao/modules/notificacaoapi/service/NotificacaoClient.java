package br.com.xbrain.autenticacao.modules.notificacaoapi.service;

import br.com.xbrain.autenticacao.config.feign.FeignSkipBadRequestsConfiguration;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "notificacaoClient",
        url = "${app-config.services.notificacao.url}",
        configuration = FeignSkipBadRequestsConfiguration.class)
public interface NotificacaoClient {

    String API_USUARIO_TABULACAO = "api/usuario-tabulacao";

    @GetMapping(API_USUARIO_TABULACAO + "/{id}/status")
    boolean consultarStatusTabulacaoByUsuario(@PathVariable("id") Integer id);

}
