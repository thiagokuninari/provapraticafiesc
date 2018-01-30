package br.com.xbrain.autenticacao.modules.comum.controller;

import br.com.xbrain.autenticacao.modules.comum.model.Regional;
import br.com.xbrain.autenticacao.modules.comum.service.RegionalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "api/regionais")
public class RegionalController {

    @Autowired
    private RegionalService service;

    @RequestMapping(method = RequestMethod.GET)
    public Iterable<Regional> getAtivos() {
        return service.getAll();
    }
}