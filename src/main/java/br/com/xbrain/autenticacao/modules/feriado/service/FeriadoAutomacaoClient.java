package br.com.xbrain.autenticacao.modules.feriado.service;

import br.com.xbrain.autenticacao.config.feign.FeignSkipBadRequestsConfiguration;
import br.com.xbrain.autenticacao.modules.feriado.dto.FeriadoAutomacao;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "feriadoAutomacaoClient",
    url = "${app-config.services.feriado-automacao.url}",
    configuration = FeignSkipBadRequestsConfiguration.class)
public interface FeriadoAutomacaoClient {

    String FERIADOS = "/feriados";

    @Cacheable("feriadosMunicipais")
    @GetMapping(FERIADOS + "/municipal")
    List<FeriadoAutomacao> buscarFeriadosMunicipais(@RequestParam("ano") Integer ano,
                                                    @RequestParam("estado") String estado,
                                                    @RequestParam("cidade") String cidade);

    @Cacheable("feriadosEstaduais")
    @GetMapping(FERIADOS + "/estadual")
    List<FeriadoAutomacao> buscarFeriadosEstaduais(@RequestParam("ano") Integer ano,
                                                   @RequestParam("estado") String estado);

    @Cacheable("feriadosNacionais")
    @GetMapping(FERIADOS + "/nacional")
    List<FeriadoAutomacao> buscarFeriadosNacionais(@RequestParam("ano") Integer ano);

}
