package br.com.xbrain.autenticacao.modules.feriado.controller;

import br.com.xbrain.autenticacao.modules.feriado.dto.FeriadoAutomacaoFiltros;
import br.com.xbrain.autenticacao.modules.feriado.service.FeriadoAutomacaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/feriado-automacao")
public class FeriadoAutomacaoController {

    @Autowired
    private FeriadoAutomacaoService service;

    @GetMapping("municipais")
    public void importarFeriadosAutomacaoMunicipais(FeriadoAutomacaoFiltros filtros) {
        service.importarFeriadosAutomacaoMunicipais(filtros);
    }

    @GetMapping("nacionais")
    public void importarFeriadosAutomacaoNacionais(Integer ano) {
        service.importarFeriadosAutomacaoNacionais(ano);
    }

}
