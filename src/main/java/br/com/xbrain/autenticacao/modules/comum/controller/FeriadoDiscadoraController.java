package br.com.xbrain.autenticacao.modules.comum.controller;

import br.com.xbrain.autenticacao.modules.feriado.service.FeriadoService;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/public/discadora/feriados")
public class FeriadoDiscadoraController {

    @Autowired
    private FeriadoService feriadoService;

    @GetMapping("cidade/{cidade}/{uf}")
    public boolean consultarFeriadoComCidadeUf(@PathVariable @NotEmpty String cidade,
                                               @PathVariable @NotEmpty String uf) {
        return feriadoService.isFeriadoHojeNaCidadeUf(cidade, uf);
    }
}
