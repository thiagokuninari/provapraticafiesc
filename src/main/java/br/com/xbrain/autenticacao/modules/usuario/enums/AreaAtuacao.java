package br.com.xbrain.autenticacao.modules.usuario.enums;

import br.com.xbrain.autenticacao.modules.comum.model.QRegional;
import br.com.xbrain.autenticacao.modules.comum.model.QUf;
import br.com.xbrain.autenticacao.modules.usuario.model.QCidade;
import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.Getter;

import java.util.List;
import java.util.function.Function;

public enum AreaAtuacao {

    REGIONAL(QRegional.regional.id::in),
    UF(QUf.uf1.id::in),
    CIDADE(QCidade.cidade.id::in);

    @Getter
    private Function<List<Integer>, BooleanExpression> predicate;

    AreaAtuacao(Function<List<Integer>, BooleanExpression> predicate) {
        this.predicate = predicate;
    }
}
