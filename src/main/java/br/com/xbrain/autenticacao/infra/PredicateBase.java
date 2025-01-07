package br.com.xbrain.autenticacao.infra;

import com.google.common.collect.Lists;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.SimpleExpression;
import org.springframework.util.ObjectUtils;

import java.util.Collection;
import java.util.stream.Collectors;

import static br.com.xbrain.autenticacao.modules.comum.util.Constantes.QTD_MAX_IN_NO_ORACLE;

public class PredicateBase {

    protected BooleanBuilder builder;

    public PredicateBase() {
        this.builder = new BooleanBuilder();
    }

    public BooleanBuilder build() {
        return this.builder;
    }

    protected boolean isEmpty(Object object) {
        return ObjectUtils.isEmpty(object);
    }

    public static <T> Predicate getPartitionPredicate(Collection<? extends T> values,
                                                      SimpleExpression<? super T> expression) {
        return ExpressionUtils.anyOf(
            Lists.partition(Lists.newArrayList(values), QTD_MAX_IN_NO_ORACLE)
                .stream()
                .map(expression::in)
                .collect(Collectors.toList())
        );
    }
}
