package br.com.xbrain.autenticacao.modules.permissao.predicate;

import br.com.xbrain.autenticacao.modules.permissao.model.QCargoDepartamentoFuncionalidade;
import com.querydsl.core.BooleanBuilder;

public class FuncionalidadePredicate {

    private QCargoDepartamentoFuncionalidade cargoDepartamentoFuncionalidade
            = QCargoDepartamentoFuncionalidade.cargoDepartamentoFuncionalidade;

    private BooleanBuilder builder;

    public FuncionalidadePredicate() {
        this.builder = new BooleanBuilder();
    }

    public FuncionalidadePredicate comDepartamento(Integer departamentoId) {
        if (departamentoId != null) {
            builder.and(cargoDepartamentoFuncionalidade.departamento.id.eq(departamentoId));
        }
        return this;
    }

    public FuncionalidadePredicate comCargo(Integer cargoId) {
        if (cargoId != null) {
            builder.and(cargoDepartamentoFuncionalidade.cargo.id.eq(cargoId));
        }
        return this;
    }

    public BooleanBuilder build() {
        return this.builder;
    }

}
