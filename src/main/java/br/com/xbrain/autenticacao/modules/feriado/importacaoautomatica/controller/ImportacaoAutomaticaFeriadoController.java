package br.com.xbrain.autenticacao.modules.feriado.importacaoautomatica.controller;

import br.com.xbrain.autenticacao.modules.feriado.dto.FeriadoFiltros;
import br.com.xbrain.autenticacao.modules.feriado.dto.FeriadoRequest;
import br.com.xbrain.autenticacao.modules.feriado.dto.ImportacaoFeriadoHistoricoResponse;
import br.com.xbrain.autenticacao.modules.feriado.importacaoautomatica.service.ImportacaoAutomaticaFeriadoService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import br.com.xbrain.autenticacao.modules.comum.dto.PageRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/importacao-automatica")
@RequiredArgsConstructor
public class ImportacaoAutomaticaFeriadoController {

    private final ImportacaoAutomaticaFeriadoService service;

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

    @GetMapping("historico-importacao")
    public Page<ImportacaoFeriadoHistoricoResponse> getAllHistoricoDeImportacao(PageRequest pageRequest, FeriadoFiltros filtros) {
        return service.getAllImportacaoHistorico(pageRequest, filtros);
    }
}
