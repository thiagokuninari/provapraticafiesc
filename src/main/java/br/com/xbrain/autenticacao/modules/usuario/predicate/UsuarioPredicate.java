package br.com.xbrain.autenticacao.modules.usuario.predicate;

import br.com.xbrain.autenticacao.modules.comum.util.StringUtil;
import br.com.xbrain.autenticacao.modules.usuario.model.QUsuario;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.Expressions;

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

    public UsuarioPredicate comCpf(String cpf) {
        if (cpf != null) {
            builder.and(
                    Expressions.stringTemplate("REGEXP_REPLACE({0}, '[^0-9]+', '')", usuario.cpf)
                            .like("%" + StringUtil.getOnlyNumbers(cpf) + "%"));
        }
        return this;
    }

    public BooleanBuilder build() {
        return this.builder;
    }
}
