package br.com.xbrain.autenticacao.modules.cep.client;

import br.com.xbrain.autenticacao.modules.cep.dto.ConsultaCepResponse;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "consultaCepClient", url = "${app-config.services.consulta-cep.url}")
public interface ConsultaCepClient {

    @GetMapping("/consultar/{cep}")
    ConsultaCepResponse consultarCep(@PathVariable("cep") String cep);
}
