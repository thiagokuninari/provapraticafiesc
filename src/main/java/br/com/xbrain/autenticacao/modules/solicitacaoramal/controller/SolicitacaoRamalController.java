package br.com.xbrain.autenticacao.modules.solicitacaoramal.controller;

import br.com.xbrain.autenticacao.modules.comum.dto.PageRequest;
import br.com.xbrain.autenticacao.modules.solicitacaoramal.dto.SolicitacaoRamalFiltros;
import br.com.xbrain.autenticacao.modules.solicitacaoramal.dto.SolicitacaoRamalHistoricoResponse;
import br.com.xbrain.autenticacao.modules.solicitacaoramal.dto.SolicitacaoRamalRequest;
import br.com.xbrain.autenticacao.modules.solicitacaoramal.dto.SolicitacaoRamalResponse;
import br.com.xbrain.autenticacao.modules.solicitacaoramal.service.SolicitacaoRamalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "api/solicitacao-ramal")
public class SolicitacaoRamalController {

    @Autowired
    private SolicitacaoRamalService solicitacaoRamalService;

    @GetMapping(value = "/historico/{idSolicitacao}")
    public List<SolicitacaoRamalHistoricoResponse> getAllHistoricoBySolicitacaoId(@PathVariable Integer idSolicitacao) {
        return solicitacaoRamalService.getAllHistoricoBySolicitacaoId(idSolicitacao);
    }

    @GetMapping(value = "/solicitacao/{idSolicitacao}")
    public SolicitacaoRamalResponse getSolicitacaoById(@PathVariable Integer idSolicitacao) {
        return solicitacaoRamalService.getSolicitacaoById(idSolicitacao);
    }

    @GetMapping(value = "/gerencia")
    public PageImpl<SolicitacaoRamalResponse> getAllGerencia(PageRequest pageable, SolicitacaoRamalFiltros filtros) {
        return solicitacaoRamalService.getAllGerencia(pageable, filtros);
    }

    @GetMapping
    public PageImpl<SolicitacaoRamalResponse> getAll(PageRequest pageable, SolicitacaoRamalFiltros filtros) {
        return solicitacaoRamalService.getAll(pageable, filtros);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SolicitacaoRamalResponse save(@Validated @RequestBody SolicitacaoRamalRequest request) {
        return solicitacaoRamalService.save(request);
    }

    @PutMapping
    public SolicitacaoRamalResponse update(@Validated @RequestBody SolicitacaoRamalRequest request) {
        return solicitacaoRamalService.update(request);
    }

}
