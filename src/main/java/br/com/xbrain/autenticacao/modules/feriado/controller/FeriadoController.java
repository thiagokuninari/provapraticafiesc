package br.com.xbrain.autenticacao.modules.feriado.controller;

import br.com.xbrain.autenticacao.modules.feriado.dto.FeriadoRequest;
import br.com.xbrain.autenticacao.modules.feriado.dto.FeriadoResponse;
import br.com.xbrain.autenticacao.modules.feriado.model.Feriado;
import br.com.xbrain.autenticacao.modules.feriado.service.FeriadoService;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Autowired;
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

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FeriadoResponse save(@Validated @RequestBody FeriadoRequest request) {
        return FeriadoResponse.convertFrom(service.save(request));
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
}
