package br.com.xbrain.autenticacao.modules.solicitacaoramal.controller;

import br.com.xbrain.autenticacao.modules.comum.dto.PageRequest;
import br.com.xbrain.autenticacao.modules.solicitacaoramal.dto.*;
import br.com.xbrain.autenticacao.modules.solicitacaoramal.enums.ETipoImplantacao;
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

    @GetMapping("/dados-agente-autorizado/{agenteAutorizadoId}")
    public SolicitacaoRamalDadosAdicionaisAaResponse getDadosAgenteAutorizado(@PathVariable Integer agenteAutorizadoId) {
        return solicitacaoRamalService.getDadosAgenteAutorizado(agenteAutorizadoId);
    }

    @GetMapping("/historico/{idSolicitacao}")
    public List<SolicitacaoRamalHistoricoResponse> getAllHistoricoBySolicitacaoId(@PathVariable Integer idSolicitacao) {
        return solicitacaoRamalService.getAllHistoricoBySolicitacaoId(idSolicitacao);
    }

    @GetMapping("/solicitacao/{idSolicitacao}")
    public SolicitacaoRamalResponse getSolicitacaoById(@PathVariable Integer idSolicitacao) {
        return solicitacaoRamalService.getSolicitacaoById(idSolicitacao);
    }

    @PostMapping("/gerencia/atualiza-status")
    public SolicitacaoRamalResponse atualizarSituacao(@Validated @RequestBody SolicitacaoRamalAtualizarStatusRequest request) {
        return solicitacaoRamalService.atualizarStatus(request);
    }

    @GetMapping("/gerencia")
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

    @DeleteMapping("/{solicitacaoId}")
    public void remover(@PathVariable Integer solicitacaoId) {
        solicitacaoRamalService.remover(solicitacaoId);
    }

    @GetMapping("/colaboradores/{solicitacaoId}")
    public List<SolicitacaoRamalColaboradorResponse> getColaboradoresBySolicitacaoId(@PathVariable Integer solicitacaoId) {
        return solicitacaoRamalService.getColaboradoresBySolicitacaoId(solicitacaoId);
    }

    @GetMapping("tipo-implantacao")
    public ETipoImplantacao[] getAllTipoImplantacao() {
        return ETipoImplantacao.values();
    }

    @PutMapping("calcula-data-finalizacao")
    public void calculaDataFinalizacao(SolicitacaoRamalFiltros filtros) {
        solicitacaoRamalService.calculaDataFinalizacao(filtros);
    }
}
