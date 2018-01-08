package br.com.xbrain.autenticacao.modules.usuario.predicate;

import br.com.xbrain.autenticacao.modules.usuario.model.QCidade;
import com.querydsl.core.BooleanBuilder;

public class CidadePredicate {

    private QCidade cidade = QCidade.cidade;
    private BooleanBuilder builder;

    public CidadePredicate() {
        this.builder = new BooleanBuilder();
    }

    public CidadePredicate comNome(String nome) {
        if (nome != null) {
            builder.and(cidade.nome.likeIgnoreCase(nome));
        }
        return this;
    }

    public CidadePredicate comUf(String nome) {
        if (nome != null) {
            builder.and(cidade.uf.uf.likeIgnoreCase(nome));
        }
        return this;
    }

    public BooleanBuilder build() {
        return this.builder;
    }
}
