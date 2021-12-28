package br.com.xbrain.autenticacao.modules.usuario.predicate;

import br.com.xbrain.autenticacao.infra.PredicateBase;

import java.util.Optional;

import static br.com.xbrain.autenticacao.modules.usuario.model.QCidadeDbm.cidadeDbm;

public class CidadeDbmPredicate extends PredicateBase {

    public CidadeDbmPredicate comCodigoCidadeDbm(Integer codigoCidadeDbm) {
        Optional.ofNullable(codigoCidadeDbm)
            .map(cidadeDbm.codigoCidadeDbm::eq)
            .ifPresent(builder::and);

        return this;
    }

    public CidadeDbmPredicate comDdd(Integer ddd) {
        Optional.ofNullable(ddd)
            .map(cidadeDbm.ddd::eq)
            .ifPresent(builder::and);

        return this;
    }
}
