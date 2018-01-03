package br.com.xbrain.autenticacao.modules.usuario.controller;

import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.usuario.model.MotivoInativacao;
import br.com.xbrain.autenticacao.modules.usuario.repository.MotivoInativacaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "api/motivo-inativacao")
public class MotivoInativacaoController {

    @Autowired
    private MotivoInativacaoRepository repository;

    @RequestMapping(method = RequestMethod.GET)
    public Iterable<MotivoInativacao> getBySituacao() {
        return repository.findBySituacao(ESituacao.A);
    }

}
