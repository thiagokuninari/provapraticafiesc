package br.com.xbrain.autenticacao.modules.usuario.client;

import br.com.xbrain.autenticacao.config.feign.FeignSkipBadRequestsConfiguration;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

@FeignClient(
    name = "usuarioPolClient",
    configuration = FeignSkipBadRequestsConfiguration.class,
    url = "${app-config.services.parceiros-online.url}"
)
public interface UsuarioClient {
    @PutMapping("api/agentes-autorizados-usuario/{usuarioId}/{aaId}/alterar-situacao")
    void alterarSituacao(@PathVariable("usuarioId") Integer usuarioId,
                         @PathVariable("aaId") Integer aaId);

    @PutMapping("api/colaboradores-vendas/alterar-situacao-usuario-id/{id}")
    void alterarSituacaoColaboradorVendas(@PathVariable("id") Integer id);
}
