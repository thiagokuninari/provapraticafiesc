package br.com.xbrain.autenticacao.modules.usuarioacesso.client;

import br.com.xbrain.autenticacao.config.feign.FeignSkipBadRequestsConfiguration;
import br.com.xbrain.autenticacao.modules.comum.dto.MongoosePage;
import br.com.xbrain.autenticacao.modules.usuarioacesso.dto.*;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@FeignClient(name = "notificacaoUsuarioAcessoClient",
    url = "${app-config.services.notificacao.url}",
    configuration = FeignSkipBadRequestsConfiguration.class)
public interface NotificacaoUsuarioAcessoClient {

    String API_USUARIOS_LOGADOS = "api/usuarios-logados";

    @PostMapping("api/relatorio-login-logout/hoje")
    MongoosePage<LoginLogoutResponse> getLoginsLogoutsDeHoje(@RequestBody GetLoginLogoutHojeRequest request);

    @PostMapping("api/relatorio-login-logout/entre-datas")
    List<LoginLogoutResponse> getLoginsLogoutsEntreDatas(@RequestBody RelatorioLoginLogoutRequest request);

    @GetMapping("api/relatorio-login-logout/csv")
    List<LoginLogoutCsv> getCsv(@RequestParam Map<String, Object> filtro);

    @GetMapping("api/relatorio-login-logout/usuarios/ids")
    List<Integer> getUsuariosIdsByIds(@RequestParam(value = "usuariosIds", required = false) Collection<Integer> usuariosIds);

    @PostMapping(API_USUARIOS_LOGADOS + "/por-periodo")
    List<PaLogadoDto> countUsuariosLogadosPorPeriodo(@RequestBody UsuarioLogadoRequest request);

    @PostMapping(API_USUARIOS_LOGADOS + "/ids")
    List<Integer> getUsuariosLogadosAtualPorIds(@RequestBody List<Integer> usuarioIds);

    @PostMapping(API_USUARIOS_LOGADOS + "/ids/com-data-entrada")
    List<UsuarioLogadoResponse> getUsuariosLogadosAtualComDataEntradaPorIds(@RequestBody List<Integer> usuariosIds);
}
