package br.com.xbrain.autenticacao.modules.solicitacaoramal.controller;

import br.com.xbrain.autenticacao.modules.comum.dto.PageRequest;
import br.com.xbrain.autenticacao.modules.solicitacaoramal.dto.SolicitacaoRamalRequest;
import br.com.xbrain.autenticacao.modules.solicitacaoramal.dto.SolicitacaoRamalResponse;
import br.com.xbrain.autenticacao.modules.solicitacaoramal.service.SolicitacaoRamalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "api/solicitacao-ramal")
public class SolicitacaoRamalController {

    @Autowired
    private SolicitacaoRamalService service;

    @GetMapping
    public PageImpl<SolicitacaoRamalResponse> getAll(PageRequest pageable) {
        return service.getAll(pageable);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SolicitacaoRamalResponse save(@Validated @RequestBody SolicitacaoRamalRequest request) {
        return service.save(request);
    }

    @PutMapping
    public SolicitacaoRamalResponse update(@Validated @RequestBody SolicitacaoRamalRequest request) {
        return service.update(request);
    }

    @GetMapping(value = "/{agenteAutorizadoId}")
    @ResponseStatus(HttpStatus.OK)
    public void verificaPermissaoSobreOAgenteAutorizado(@PathVariable Integer agenteAutorizadoId) {
        service.verificaPermissaoSobreOAgenteAutorizado(agenteAutorizadoId);
    }
}
