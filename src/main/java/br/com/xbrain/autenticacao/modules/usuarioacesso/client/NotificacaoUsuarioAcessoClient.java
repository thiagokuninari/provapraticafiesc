package br.com.xbrain.autenticacao.modules.usuarioacesso.client;

import br.com.xbrain.autenticacao.config.feign.FeignSkipBadRequestsConfiguration;
import br.com.xbrain.autenticacao.modules.comum.dto.MongoosePage;
import br.com.xbrain.autenticacao.modules.usuarioacesso.dto.LoginLogoutCsv;
import br.com.xbrain.autenticacao.modules.usuarioacesso.dto.LoginLogoutResponse;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@FeignClient(name = "notificacaoUsuarioAcessoClient",
    url = "${app-config.services.notificacao.url}",
    configuration = FeignSkipBadRequestsConfiguration.class)
public interface NotificacaoUsuarioAcessoClient {

    @GetMapping("api/relatorio-login-logout/hoje")
    MongoosePage<LoginLogoutResponse> getLoginsLogoutsDeHoje(@RequestParam Map<String, Object> filtro);

    @GetMapping("api/relatorio-login-logout/csv")
    List<LoginLogoutCsv> getCsv(@RequestParam Map<String, Object> filtro);

    @GetMapping("api/relatorio-login-logout/usuarios/ids")
    List<Integer> getUsuariosIdsByIds(@RequestParam(value = "usuariosIds", required = false) Collection<Integer> usuariosIds);
}
