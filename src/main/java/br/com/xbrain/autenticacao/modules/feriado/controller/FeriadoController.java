package br.com.xbrain.autenticacao.modules.feriado.controller;

import br.com.xbrain.autenticacao.modules.comum.dto.PageRequest;
import br.com.xbrain.autenticacao.modules.feriado.dto.FeriadoFiltros;
import br.com.xbrain.autenticacao.modules.feriado.dto.FeriadoRequest;
import br.com.xbrain.autenticacao.modules.feriado.dto.FeriadoResponse;
import br.com.xbrain.autenticacao.modules.feriado.model.Feriado;
import br.com.xbrain.autenticacao.modules.feriado.service.FeriadoService;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "api/feriado")
public class FeriadoController {

    @Autowired
    private FeriadoService service;

    @GetMapping("/consulta")
    public boolean consultaFeriadoNacional(@RequestParam String data) {
        return service.consulta(data);
    }

    @GetMapping("/consulta/{cidadeId}")
    public boolean consultaFeriadoComCidade(@RequestParam String data, @PathVariable("cidadeId") Integer cidadeId) {
        return service.consulta(data, cidadeId);
    }

    @GetMapping("obter-feriados")
    public Page<FeriadoResponse> obterFeriadosByFiltros(PageRequest pageRequest, FeriadoFiltros filtros) {
        return service.obterFeriadosByFiltros(pageRequest, filtros);
    }

    @GetMapping("{id}")
    public FeriadoResponse obterFeriadoPorId(@PathVariable("id") Integer id) {
        return service.getFeriadoById(id);
    }

    @PostMapping("salvar")
    @ResponseStatus(HttpStatus.CREATED)
    public FeriadoResponse salvar(@RequestBody @Validated FeriadoRequest request) {
        return service.salvarFeriado(request);
    }

    @PutMapping("editar")
    public FeriadoResponse editar(@RequestBody @Validated FeriadoRequest request) {
        return service.editarFeriado(request);
    }

    @PutMapping("excluir/{id}")
    public void excluirFeriado(@PathVariable Integer id) {
        service.excluirFeriado(id);
    }

    //    @PostMapping
    //    @ResponseStatus(HttpStatus.CREATED)
    //    public FeriadoResponse save(@Validated @RequestBody FeriadoRequest request) {
    //        return FeriadoResponse.convertFrom(service.save(request));
    //    }

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
}
