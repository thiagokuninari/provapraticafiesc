package br.com.xbrain.autenticacao.modules.usuario.predicate;

import br.com.xbrain.autenticacao.modules.usuario.model.QUsuario;
import com.querydsl.core.BooleanBuilder;

public class UsuarioPredicate {

    private QUsuario usuario = QUsuario.usuario;
    private BooleanBuilder builder;

    public UsuarioPredicate() {
        this.builder = new BooleanBuilder();
    }

    public UsuarioPredicate comNome(String nome) {
        if (nome != null) {
            builder.and(usuario.nome.likeIgnoreCase(nome));
        }
        return this;
    }

    public BooleanBuilder build() {
        return this.builder;
    }
}
