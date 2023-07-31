package br.com.xbrain.autenticacao.modules.usuario.controller;

import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.usuario.model.MotivoInativacao;
import br.com.xbrain.autenticacao.modules.usuario.repository.MotivoInativacaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/motivo-inativacao")
@RequiredArgsConstructor
public class MotivoInativacaoController {

    private final MotivoInativacaoRepository repository;

    @GetMapping
    public Iterable<MotivoInativacao> getBySituacao() {
        return repository.findBySituacao(ESituacao.A);
    }

}
