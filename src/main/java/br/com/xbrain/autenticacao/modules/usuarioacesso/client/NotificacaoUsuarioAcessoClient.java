package br.com.xbrain.autenticacao.modules.usuarioacesso.client;

import br.com.xbrain.autenticacao.config.feign.FeignSkipBadRequestsConfiguration;
import br.com.xbrain.autenticacao.modules.usuarioacesso.dto.PaLogadoResponse;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@FeignClient(name = "notificacaoUsuarioAcessoClient",
    url = "${app-config.services.notificacao.url}",
    configuration = FeignSkipBadRequestsConfiguration.class)
public interface NotificacaoUsuarioAcessoClient {

    String API_USUARIOS_LOGADOS = "api/usuarios-logados";

    @GetMapping(API_USUARIOS_LOGADOS + "/por-hora")
    List<PaLogadoResponse> countUsuariosLogadosPorHora(@RequestParam Map<String, Object> filtro);
}
