package br.com.xbrain.autenticacao.modules.usuario.dto;

import br.com.xbrain.autenticacao.modules.usuario.predicate.CargoPredicate;
import com.querydsl.core.BooleanBuilder;
import lombok.Data;

@Data
public class CargoFiltros {

    private String nome;
    private Integer nivelId;

    public BooleanBuilder toPredicate() {
        return new CargoPredicate()
                .comNome(nome)
                .comNivel(nivelId)
                .build();
    }
}
