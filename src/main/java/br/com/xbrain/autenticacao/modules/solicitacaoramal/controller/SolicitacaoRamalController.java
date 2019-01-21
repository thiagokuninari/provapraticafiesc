package br.com.xbrain.autenticacao.modules.solicitacaoramal.controller;

import br.com.xbrain.autenticacao.modules.solicitacaoramal.dto.SolicitacaoRamalRequest;
import br.com.xbrain.autenticacao.modules.solicitacaoramal.dto.SolicitacaoRamalResponse;
import br.com.xbrain.autenticacao.modules.solicitacaoramal.service.SolicitacaoRamalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "api/solicitacao-ramal")
public class SolicitacaoRamalController {

    @Autowired
    private SolicitacaoRamalService service;

    @GetMapping
    public List<SolicitacaoRamalResponse> getAll() {
        return service.getAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SolicitacaoRamalResponse save(@Validated @RequestBody SolicitacaoRamalRequest request) {
        return SolicitacaoRamalResponse.convertFrom(service.save(SolicitacaoRamalRequest.convertFrom(request)));
    }

    @PutMapping
    public SolicitacaoRamalResponse update(@Validated @RequestBody SolicitacaoRamalRequest request) {
        return SolicitacaoRamalResponse.convertFrom(service.update(SolicitacaoRamalRequest.convertFrom(request)));
    }

    @GetMapping(value = "/{agenteAutorizadoId}")
    @ResponseStatus(HttpStatus.OK)
    public void verificaPermissaoSobreOAgenteAutorizado(@PathVariable Integer agenteAutorizadoId) {
        service.verificaPermissaoSobreOAgenteAutorizado(agenteAutorizadoId);
    }
}
