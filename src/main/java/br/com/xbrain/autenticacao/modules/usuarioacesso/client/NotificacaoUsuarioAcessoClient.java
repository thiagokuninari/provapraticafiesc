package br.com.xbrain.autenticacao.modules.usuarioacesso.client;

import br.com.xbrain.autenticacao.config.feign.FeignSkipBadRequestsConfiguration;
import br.com.xbrain.autenticacao.modules.usuarioacesso.dto.PaLogadoDto;
import br.com.xbrain.autenticacao.modules.usuarioacesso.dto.UsuarioLogadoRequest;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "notificacaoUsuarioAcessoClient",
    url = "${app-config.services.notificacao.url}",
    configuration = FeignSkipBadRequestsConfiguration.class)
public interface NotificacaoUsuarioAcessoClient {

    String API_USUARIOS_LOGADOS = "api/usuarios-logados";

    @PostMapping(API_USUARIOS_LOGADOS + "/por-periodo")
    List<PaLogadoDto> countUsuariosLogadosPorPeriodo(@RequestBody UsuarioLogadoRequest request);
}
