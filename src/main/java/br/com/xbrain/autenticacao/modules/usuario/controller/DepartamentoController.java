package br.com.xbrain.autenticacao.modules.usuario.controller;

import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.usuario.model.Departamento;
import br.com.xbrain.autenticacao.modules.usuario.repository.DepartamentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "api/departamentos")
public class DepartamentoController {

    @Autowired
    private DepartamentoRepository repository;

    @RequestMapping(method = RequestMethod.GET)
    public Iterable<Departamento> getAll(Integer nivelId) {
        return repository.findBySituacaoAndNivelId(ESituacao.A, nivelId);
    }
}

