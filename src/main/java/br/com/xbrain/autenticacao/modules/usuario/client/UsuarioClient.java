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
    @PutMapping("api/agentes-autorizados-usuario/{id}/alterar-situacao")
    void alterarSituacao(@PathVariable("id") Integer id);

    @PutMapping("api/colaboradores-vendas/alterar-situacao-usuario-id/{id}")
    void alterarSituacaoColaboradorVendas(@PathVariable("id") Integer id);
}
