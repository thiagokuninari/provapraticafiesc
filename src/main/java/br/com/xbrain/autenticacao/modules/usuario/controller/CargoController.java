package br.com.xbrain.autenticacao.modules.usuario.controller;

import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.usuario.model.Cargo;
import br.com.xbrain.autenticacao.modules.usuario.repository.CargoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "api/cargos")
public class CargoController {

    @Autowired
    private CargoRepository repository;

    @RequestMapping(method = RequestMethod.GET)
    public Iterable<Cargo> getAll(Integer nivelId) {
        return repository.findBySituacaoAndNivelId(ESituacao.A, nivelId);
    }
}
