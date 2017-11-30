package br.com.xbrain.autenticacao.modules.comum.controller;

import br.com.xbrain.autenticacao.modules.comum.EmpresaPredicate;
import br.com.xbrain.autenticacao.modules.comum.model.Empresa;
import br.com.xbrain.autenticacao.modules.comum.repository.EmpresaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.data.domain.Sort.Direction.ASC;

@RestController
@RequestMapping(value = "api/empresas")
public class EmpresaController {

    @Autowired
    private EmpresaRepository repository;

    @RequestMapping(method = RequestMethod.GET)
    public Iterable<Empresa> getAll(boolean ignorarXbrain, Integer unidadeNegocioId) {
        return repository.findAll(
                new EmpresaPredicate()
                        .daUnidadeDeNegocio(unidadeNegocioId)
                        .ignorarXbrain(ignorarXbrain)
                        .build(),
                new Sort(ASC, "nome"));
    }
}