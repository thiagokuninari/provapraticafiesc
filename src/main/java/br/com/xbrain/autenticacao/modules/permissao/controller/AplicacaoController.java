package br.com.xbrain.autenticacao.modules.permissao.controller;

import br.com.xbrain.autenticacao.modules.permissao.model.Aplicacao;
import br.com.xbrain.autenticacao.modules.permissao.service.AplicacaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "api/aplicacoes")
public class AplicacaoController {
    @Autowired
    private AplicacaoService service;

    @GetMapping
    public List<Aplicacao> getAll() {
        return service.getAll();
    }
}
