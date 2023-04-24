package br.com.xbrain.autenticacao.modules.feriado.importacaoautomatica.controller;

import br.com.xbrain.autenticacao.modules.feriado.dto.FeriadoRequest;
import br.com.xbrain.autenticacao.modules.feriado.importacaoautomatica.service.ImportacaoAutomaticaFeriadoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/importacao-automatica")
public class ImportacaoAutomaticaFeriadoController {

    @Autowired
    private ImportacaoAutomaticaFeriadoService service;

    @PostMapping("municipais")
    public void importarFeriadosAutomacaoMunicipais(FeriadoRequest request) {
        service.importarFeriadosAutomacaoMunicipais(request);
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
