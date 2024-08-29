package br.com.xbrain.autenticacao.modules.feriado.importacaoautomatica.controller;

import br.com.xbrain.autenticacao.modules.feriado.dto.FeriadoFiltros;
import br.com.xbrain.autenticacao.modules.feriado.dto.ImportacaoFeriadoHistoricoResponse;
import br.com.xbrain.autenticacao.modules.feriado.importacaoautomatica.service.ImportacaoAutomaticaFeriadoService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import br.com.xbrain.autenticacao.modules.comum.dto.PageRequest;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/importacao-automatica")
@RequiredArgsConstructor
public class ImportacaoAutomaticaFeriadoController {

    private final ImportacaoAutomaticaFeriadoService service;

    @PostMapping("importar-todos/{ano}")
    public void importarTodosOsFeriadosAnuais(@PathVariable Integer ano) {
        service.importarTodosOsFeriadoAnuais(ano);
    }

    @GetMapping("historico-importacao")
    public Page<ImportacaoFeriadoHistoricoResponse> getAllHistoricoDeImportacao(PageRequest pageRequest, FeriadoFiltros filtros) {
        return service.getAllImportacaoHistorico(pageRequest, filtros);
    }
}
