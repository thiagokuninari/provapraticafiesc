package br.com.xbrain.autenticacao.modules.usuario.controller;

import br.com.xbrain.autenticacao.modules.usuario.model.Nivel;
import br.com.xbrain.autenticacao.modules.usuario.service.NivelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "api/niveis")
public class NivelController {

    @Autowired
    private NivelService service;

    @RequestMapping(method = RequestMethod.GET)
    public Iterable<Nivel> getAll() {
        return service.getAll();
    }

    @RequestMapping(value = "/permitidos", method = RequestMethod.GET)
    public Iterable<Nivel> getAllByPermitidos() {
        return service.getAllByPermitidos();
    }

}
