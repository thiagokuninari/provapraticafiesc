package br.com.xbrain.autenticacao.modules.call.service;

import br.com.xbrain.autenticacao.config.feign.FeignSkipBadRequestsConfiguration;
import br.com.xbrain.autenticacao.modules.call.dto.ConfiguracaoTelefoniaResponse;
import br.com.xbrain.autenticacao.modules.call.dto.RamalResponse;
import br.com.xbrain.autenticacao.modules.call.dto.SuporteVendasBkoRequest;
import br.com.xbrain.autenticacao.modules.call.dto.TelefoniaResponse;
import br.com.xbrain.autenticacao.modules.usuario.enums.ECanal;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "callClient",
        url = "${app-config.services.call.url}",
        configuration = FeignSkipBadRequestsConfiguration.class)
public interface CallClient {

    String API_CONFIGURACAO_TELEFONIA = "api/configuracao-telefonia";
    String API_DISCAGEM_TELEFONIA = "api/discagem-telefonia";
    String URL_RAMAL = "api/ramal";
    String API_CACHE_CLEAN_FERIADOS = "/api/public/cache-clean/feriados";
    String API_SUPORTE_VENDAS_BKO_CONFIGURACAO = "api/suporte-vendas-bko/configuracoes";

    @GetMapping(API_CONFIGURACAO_TELEFONIA + "/obter-nome-telefonia-por-id/{id}")
    TelefoniaResponse obterNomeTelefoniaPorId(@PathVariable("id") Integer id);

    @GetMapping(URL_RAMAL + "/vinculado/{canalTelefonia}/{canalTelefoniaId}")
    List<RamalResponse> obterRamaisParaCanal(@PathVariable("canalTelefonia") ECanal canalTelefonia,
                                             @PathVariable("canalTelefoniaId") Integer canalTelefoniaId);

    @DeleteMapping(API_CACHE_CLEAN_FERIADOS)
    void cleanCacheFeriadosTelefonia();

    @PostMapping(URL_RAMAL + "/desvincular-ramais/ATIVO_PROPRIO/{siteId}/discadora/{discadoraId}")
    void desvincularRamaisDaDiscadoraAtivoProprio(@PathVariable("siteId") Integer siteId,
                                                  @PathVariable("discadoraId") Integer discadoraId);

    @GetMapping(value = "api/public/cache-clean/ativo-proprio")
    String cleanCacheableSiteAtivoProprio();

    @GetMapping(API_DISCAGEM_TELEFONIA + "/status")
    boolean consultarStatusUsoRamalByUsuarioAutenticado();

    @PostMapping(URL_RAMAL + "/liberar-ramal")
    void liberarRamalUsuarioAutenticado();

    @GetMapping("api/configuracao-telefonia/todas-discadoras")
    List<ConfiguracaoTelefoniaResponse> getDiscadoras();

    @PostMapping(API_SUPORTE_VENDAS_BKO_CONFIGURACAO)
    void salvarConfiguracaoSuporteVendas(@RequestBody SuporteVendasBkoRequest request);

    @PutMapping(API_SUPORTE_VENDAS_BKO_CONFIGURACAO + "/{fornecedorId}/atualizar")
    void atualizarConfiguracaoSuporteVendas(@PathVariable("fornecedorId") Integer fornecedorId,
                                            @RequestBody SuporteVendasBkoRequest request);

    @PutMapping(API_SUPORTE_VENDAS_BKO_CONFIGURACAO + "/{fornecedorId}/desvincular-discadora-ramais")
    void desvicularDiscadoraSuporteVendas(@PathVariable("fornecedorId") Integer fornecedorId);

    @PutMapping(API_SUPORTE_VENDAS_BKO_CONFIGURACAO + "/{fornecedorId}/ativar")
    void ativarConfiguracaoSuporteVendas(@PathVariable("fornecedorId") Integer fornecedorId);
}
