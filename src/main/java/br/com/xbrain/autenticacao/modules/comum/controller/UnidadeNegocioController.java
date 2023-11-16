package br.com.xbrain.autenticacao.modules.comum.controller;

import br.com.xbrain.autenticacao.modules.comum.dto.SelectResponse;
import br.com.xbrain.autenticacao.modules.comum.model.UnidadeNegocio;
import br.com.xbrain.autenticacao.modules.comum.service.UnidadeNegocioService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "api/unidades-negocio")
@RequiredArgsConstructor
public class UnidadeNegocioController {

    private final UnidadeNegocioService service;

    @GetMapping
    public Iterable<UnidadeNegocio> getAll() {
        return service.getAll();
    }

    @GetMapping("obter-sem-xbrain")
    public List<SelectResponse> findWithoutXbrain() {
        return service.findWithoutXbrain();
    }
}
