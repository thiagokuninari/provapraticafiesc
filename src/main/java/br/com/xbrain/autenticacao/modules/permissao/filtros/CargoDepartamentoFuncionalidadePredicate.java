package br.com.xbrain.autenticacao.modules.permissao.filtros;

import br.com.xbrain.autenticacao.modules.permissao.model.QCargoDepartamentoFuncionalidade;
import com.querydsl.core.BooleanBuilder;

public class CargoDepartamentoFuncionalidadePredicate {

    private QCargoDepartamentoFuncionalidade cargoDepartamentoFuncionalidade
            = QCargoDepartamentoFuncionalidade.cargoDepartamentoFuncionalidade;

    private BooleanBuilder builder;

    public CargoDepartamentoFuncionalidadePredicate() {
        this.builder = new BooleanBuilder();
    }

    public CargoDepartamentoFuncionalidadePredicate(BooleanBuilder builder) {
        this.builder = builder;
    }

    public CargoDepartamentoFuncionalidadePredicate comDepartamento(Integer departamentoId) {
        if (departamentoId != null) {
            builder.and(cargoDepartamentoFuncionalidade.departamento.id.eq(departamentoId));
        }
        return this;
    }

    public CargoDepartamentoFuncionalidadePredicate comCargo(Integer cargoId) {
        if (cargoId != null) {
            builder.and(cargoDepartamentoFuncionalidade.cargo.id.eq(cargoId));
        }
        return this;
    }

    public BooleanBuilder build() {
        return this.builder;
    }
}
