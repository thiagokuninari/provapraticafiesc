package br.com.xbrain.autenticacao.modules.comum.controller;

import br.com.xbrain.autenticacao.modules.comum.model.UnidadeNegocio;
import br.com.xbrain.autenticacao.modules.comum.service.UnidadeNegocioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.data.domain.Sort.Direction.ASC;

@RestController
@RequestMapping(value = "api/unidades-negocio")
public class UnidadeNegocioController {

    @Autowired
    private UnidadeNegocioService service;

    @RequestMapping(method = RequestMethod.GET)
    public Iterable<UnidadeNegocio> getAll(boolean ignorarXbrain) {
        if (ignorarXbrain) {
            return service.findWithoutXbrain();
        } else {
            return service.getRepository().findAll(new Sort(ASC, "nome"));
        }
    }
}