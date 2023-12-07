package br.com.xbrain.autenticacao.modules.feriado.controller;

import br.com.xbrain.autenticacao.modules.feriado.dto.FeriadoCidadeEstadoResponse;
import br.com.xbrain.autenticacao.modules.feriado.dto.FeriadoMesAnoResponse;
import br.com.xbrain.autenticacao.modules.feriado.model.Feriado;
import br.com.xbrain.autenticacao.modules.feriado.service.FeriadoService;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "api/feriado")
public class FeriadoController {

    private final FeriadoService service;

    @GetMapping("/consulta")
    public boolean consultarFeriadoNacional(@RequestParam String data) {
        return service.consulta(data);
    }

    @GetMapping("feriado-nacional")
    public boolean consultarFeriadoNacional() {
        return service.consulta();
    }

    @GetMapping("feriados-estaduais")
    public List<String> consultarFeriadoEstadualPorDataAtual() {
        return service.buscarUfsFeriadosEstaduaisPorData();
    }

    @GetMapping("feriados-municipais")
    public List<FeriadoCidadeEstadoResponse> consultarFeriadosMunicipais() {
        return service.buscarFeriadosMunicipaisPorDataAtualUfs();
    }

    @GetMapping("/consulta/{cidadeId}")
    public boolean consultaFeriadoComCidade(@RequestParam String data, @PathVariable("cidadeId") Integer cidadeId) {
        return service.consulta(data, cidadeId);
    }

    @GetMapping
    public Iterable<Feriado> findAllByAnoAtual() {
        return service.findAllByAnoAtual();
    }

    @GetMapping("cidade/{cidade}/{uf}")
    public boolean consultarFeriadoComCidadeUf(@PathVariable @NotEmpty String cidade,
                                               @PathVariable @NotEmpty String uf) {
        return service.isFeriadoHojeNaCidadeUf(cidade, uf);
    }

    @DeleteMapping("cache/clear")
    public void cacheClearFeriados() {
        service.flushCacheFeriados();
    }

    @GetMapping("mes-ano/total-feriados")
    public List<FeriadoMesAnoResponse> buscarTotalDeFeriadosPorMesAno() {
        return service.buscarTotalDeFeriadosPorMesAno();
    }

    @GetMapping("/{cidadeId}")
    public boolean isFeriadoComCidadeId(@PathVariable Integer cidadeId) {
        return service.isFeriadoComCidadeId(cidadeId);
    }
}
