package br.com.xbrain.autenticacao.modules.call.service;

import br.com.xbrain.autenticacao.config.feign.FeignSkipBadRequestsConfiguration;
import br.com.xbrain.autenticacao.modules.call.dto.RamalResponse;
import br.com.xbrain.autenticacao.modules.call.dto.TelefoniaResponse;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "callClient",
        url = "${app-config.services.call.url}",
        configuration = FeignSkipBadRequestsConfiguration.class)
public interface CallClient {

    String API_CONFIGURACAO_TELEFONIA = "api/configuracao-telefonia";
    String URL_RAMAL = "api/ramal";

    @GetMapping(API_CONFIGURACAO_TELEFONIA + "/obter-nome-telefonia-por-id/{id}")
    TelefoniaResponse obterNomeTelefoniaPorId(@PathVariable("id") Integer id);

    @GetMapping(URL_RAMAL + "/vinculado/AGENTE_AUTORIZADO/{id}")
    List<RamalResponse> obterRamaisParaAgenteAutorizado(@PathVariable("id") Integer id);

}
