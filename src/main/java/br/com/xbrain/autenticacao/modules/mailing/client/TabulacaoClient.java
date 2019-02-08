package br.com.xbrain.autenticacao.modules.mailing.client;

import br.com.xbrain.autenticacao.config.feign.FeignSkipBadRequestsConfiguration;
import br.com.xbrain.autenticacao.modules.mailing.dto.AgendamentoAgenteAutorizadoResponse;
import br.com.xbrain.autenticacao.modules.usuario.dto.TabulacaoDistribuicaoRequest;
import br.com.xbrain.autenticacao.modules.usuario.dto.TabulacaoDistribuicaoResponse;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

@FeignClient(name = "tabulacaoClient",
        url = "${app-config.services.mailing.url}",
        configuration = FeignSkipBadRequestsConfiguration.class)
public interface TabulacaoClient {

    @RequestMapping(
            method = RequestMethod.GET,
            value = "api/tabulacao/agendamentos/proprietarios/{usuarioId}")
    List<AgendamentoAgenteAutorizadoResponse> getQuantidadeAgendamentosProprietariosDoUsuarioPorAa(
            @PathVariable("usuarioId") Integer usuarioId);

    @RequestMapping(
            method = RequestMethod.POST,
            value = "api/tabulacao/agendamentos/proprietarios/distribuicao")
    List<TabulacaoDistribuicaoResponse> distribuirAgendamentosProprietariosDoUsuario(
            @RequestBody TabulacaoDistribuicaoRequest request);
}
