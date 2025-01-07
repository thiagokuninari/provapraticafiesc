package br.com.xbrain.autenticacao.modules.solicitacaoramal.controller;

import br.com.xbrain.autenticacao.modules.comum.dto.PageRequest;
import br.com.xbrain.autenticacao.modules.agenteautorizado.dto.UsuarioAgenteAutorizadoResponse;
import br.com.xbrain.autenticacao.modules.solicitacaoramal.dto.*;
import br.com.xbrain.autenticacao.modules.solicitacaoramal.enums.ETipoImplantacao;
import br.com.xbrain.autenticacao.modules.solicitacaoramal.service.SolicitacaoRamalService;
import br.com.xbrain.autenticacao.modules.solicitacaoramal.service.SolicitacaoRamalServiceAa;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/solicitacao-ramal")
public class SolicitacaoRamalController {

    private final SolicitacaoRamalService solicitacaoRamalService;
    private final SolicitacaoRamalServiceAa solicitacaoRamalServiceAa;

    @GetMapping("dados-canal")
    public SolicitacaoRamalDadosAdicionaisResponse getDadosAdicionais(SolicitacaoRamalFiltros filtros) {
        return solicitacaoRamalService.getDadosAdicionais(filtros);
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
    public Page<SolicitacaoRamalResponse> getAllGerencia(PageRequest pageable, SolicitacaoRamalFiltros filtros) {
        return solicitacaoRamalService.getAllGerencia(pageable, filtros);
    }

    @GetMapping
    public Page<SolicitacaoRamalResponse> getAll(PageRequest pageable, SolicitacaoRamalFiltros filtros) {
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

    @PutMapping("calcular-data-finalizacao")
    public void calcularDataFinalizacao(SolicitacaoRamalFiltros filtros) {
        solicitacaoRamalService.calcularDataFinalizacao(filtros);
    }

    @GetMapping("{agenteAutorizadoId}/ramais-aa-disponiveis")
    public Integer getRamaisDisponiveis(@PathVariable Integer agenteAutorizadoId) {
        return solicitacaoRamalServiceAa.getRamaisDisponiveis(agenteAutorizadoId);
    }

    @GetMapping("{agenteAutorizadoId}/colaboradores-aa-disponivel")
    public List<UsuarioAgenteAutorizadoResponse> getUsuariosByAgenteAutorizadoId(@PathVariable Integer agenteAutorizadoId) {
        return solicitacaoRamalServiceAa.getUsuariosAtivosByAgenteAutorizadoId(agenteAutorizadoId);
    }
}
