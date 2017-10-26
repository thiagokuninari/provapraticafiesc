package br.com.xbrain.autenticacao.modules.usuario.dto;

import br.com.xbrain.autenticacao.modules.usuario.predicate.UsuarioPredicate;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.querydsl.core.BooleanBuilder;
import lombok.Data;

@Data
public class UsuarioFiltros {

    private String nome;

    @JsonIgnore
    public BooleanBuilder toPredicate() {
        return new UsuarioPredicate()
                .comNome(nome)
                .build();
    }
}
