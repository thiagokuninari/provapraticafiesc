package br.com.xbrain.autenticacao.modules.usuario.controller;

import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.comum.enums.Eboolean;
import br.com.xbrain.autenticacao.modules.usuario.model.Nivel;
import br.com.xbrain.autenticacao.modules.usuario.repository.NivelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "api/niveis")
public class NivelController {

    @Autowired
    private NivelRepository repository;

    @RequestMapping(method = RequestMethod.GET)
    public Iterable<Nivel> getAll(Integer nivelId) {
        return repository.findBySituacaoAndExibirCadastroUsuario(ESituacao.A, Eboolean.V);
    }
}
