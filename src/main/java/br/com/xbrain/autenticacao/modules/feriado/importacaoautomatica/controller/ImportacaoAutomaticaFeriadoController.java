package br.com.xbrain.autenticacao.modules.feriado.importacaoautomatica.controller;

import br.com.xbrain.autenticacao.modules.feriado.dto.FeriadoAutomacaoFiltros;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/importacao-automatica")
public class ImportacaoAutomaticaFeriadoController {



    @GetMapping("municipais")
    public void importarFeriadosAutomacaoMunicipais(FeriadoAutomacaoFiltros filtros) {
        //service.importarFeriadosAutomacaoMunicipais(filtros);
    }

    @GetMapping("nacionais")
    public void importarFeriadosAutomacaoNacionais(Integer ano) {
        //service.importarFeriadosAutomacaoNacionais(ano);
    }

    @GetMapping("estaduais")
    public void importarFeriadosAutomacaoEstaduais(Integer ano) {
        //service.importarFeriadosAutomacaoEstaduais(ano);
    }
}
