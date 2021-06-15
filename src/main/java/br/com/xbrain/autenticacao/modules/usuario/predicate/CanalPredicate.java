package br.com.xbrain.autenticacao.modules.usuario.predicate;

import com.google.common.collect.Lists;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.ExpressionUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

import static br.com.xbrain.autenticacao.modules.comum.util.Constantes.QTD_MAX_IN_NO_ORACLE;
import static br.com.xbrain.autenticacao.modules.usuario.model.QCanal.canal;

public class CanalPredicate {

    private BooleanBuilder builder;

    public CanalPredicate() {
        this.builder = new BooleanBuilder();
    }

    /*public CanalPredicate comIds(List<Integer> usuarioIds) {
        if (!StringUtils.isEmpty(usuarioIds)) {
            builder.and(ExpressionUtils.anyOf(
                Lists.partition(usuarioIds,QTD_MAX_IN_NO_ORACLE)
                    .stream()
                    .map(canal.usuario.id::in)
                    .collect(Collectors.toList())
            ));
        }
        return this;
    }*/

    public BooleanBuilder build() {
        return this.builder;
    }
}
