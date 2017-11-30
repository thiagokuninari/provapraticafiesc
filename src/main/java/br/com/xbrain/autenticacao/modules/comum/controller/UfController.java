package br.com.xbrain.autenticacao.modules.comum.controller;

import br.com.xbrain.autenticacao.modules.comum.model.Uf;
import br.com.xbrain.autenticacao.modules.comum.repository.UfRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "api/ufs")
public class UfController {

    @Autowired
    private UfRepository repository;

    @RequestMapping(method = RequestMethod.GET)
    public Iterable<Uf> getAll() {
        return repository.findAll(new Sort("nome"));
    }
}
