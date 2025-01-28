package br.com.xbrain.autenticacao.modules.gestaocolaboradorespol.client;

import br.com.xbrain.autenticacao.config.feign.FeignSkipBadRequestsConfiguration;
import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioRemanejamentoRequest;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
    name = "colaboradorTecnicoClient",
    configuration = FeignSkipBadRequestsConfiguration.class,
    url = "${app-config.services.gestao-colaborador-pol.url}"
)
public interface ColaboradorTecnicoClient {

    String API_COLABORADOR_TECNICO = "api/colaboradores-tecnicos";

    @PutMapping(API_COLABORADOR_TECNICO + "/atualizar-usuario-remanejado")
    void atualizarUsuarioRemanejado(@RequestBody UsuarioRemanejamentoRequest request);

    @PutMapping(API_COLABORADOR_TECNICO + "/limpar-cpf")
    void limparCpfColaboradorTecnico(@RequestParam("email") String email);

}
