package br.com.xbrain.autenticacao.modules.cep.client;

import br.com.xbrain.autenticacao.modules.cep.dto.ConsultaCepResponse;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "consultaCepClient", url = "${app-config.services.localizacao.url}")
public interface ConsultaCepClient {

    String LOCALIZACAO_ENDPOINT = "api/consulta-cep";

    @GetMapping(LOCALIZACAO_ENDPOINT)
    ConsultaCepResponse consultarCep(@RequestParam("cep") String cep);
}
