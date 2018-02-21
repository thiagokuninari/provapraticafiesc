package br.com.xbrain.autenticacao.modules.usuario.controller;

import br.com.xbrain.autenticacao.modules.usuario.model.Departamento;
import br.com.xbrain.autenticacao.modules.usuario.service.DepartamentoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "api/departamentos")
public class DepartamentoController {

    @Autowired
    private DepartamentoService service;

    @RequestMapping(method = RequestMethod.GET)
    public Iterable<Departamento> getAll(Integer nivelId) {
        return service.getAllByNivelId(nivelId);
    }
}

