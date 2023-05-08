package br.com.xbrain.autenticacao.modules.feriado.importacaoautomatica.controller;

import br.com.xbrain.autenticacao.modules.feriado.dto.FeriadoRequest;
import br.com.xbrain.autenticacao.modules.feriado.importacaoautomatica.service.ImportacaoAutomaticaFeriadoService;
import org.springframework.beans.factory.annotation.Autowired;
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

    @PostMapping("estaduais")
    public void importarFeriadosAutomacaoEstaduais(FeriadoRequest request) {
        service.importarFeriadosAutomacaoEstaduais(request);
    }

    @PostMapping("nacionais")
    public void importarFeriadosAutomacaoNacionais(FeriadoRequest request) {
        service.importarFeriadosAutomacaoNacionais(request);
    }
}
