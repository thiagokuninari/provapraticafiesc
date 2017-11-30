package br.com.xbrain.autenticacao.modules.comum.controller;

import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.comum.model.Regional;
import br.com.xbrain.autenticacao.modules.comum.repository.RegionalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "api/regionais")
public class RegionalController {

    @Autowired
    private RegionalRepository repository;

    @RequestMapping(method = RequestMethod.GET)
    public Iterable<Regional> getAtivos() {
        return repository.findBySituacao(ESituacao.A);
    }
}