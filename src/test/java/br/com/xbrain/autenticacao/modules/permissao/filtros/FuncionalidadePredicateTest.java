package br.com.xbrain.autenticacao.modules.permissao.filtros;

import com.querydsl.core.BooleanBuilder;
import org.junit.Test;

import static br.com.xbrain.autenticacao.modules.permissao.model.QCargoDepartamentoFuncionalidade.cargoDepartamentoFuncionalidade;
import static org.assertj.core.api.Assertions.assertThat;

public class FuncionalidadePredicateTest {

    @Test
    public void comDepartamento_deveMontarPredicate_quandoInformarDepartamentoId() {
        assertThat(new FuncionalidadePredicate().comDepartamento(1).build())
            .isEqualTo(new BooleanBuilder(cargoDepartamentoFuncionalidade.departamento.id.eq(1)));
    }

    @Test
    public void comDepartamento_naoDeveMontarPredicate_quandoDepartamentoIdNull() {
        assertThat(new FuncionalidadePredicate().comDepartamento(null).build())
            .isEqualTo(new BooleanBuilder());
    }

    @Test
    public void comCargo_deveMontarPredicate_quandoInformarCargoId() {
        assertThat(new FuncionalidadePredicate().comCargo(1).build())
            .isEqualTo(new BooleanBuilder(cargoDepartamentoFuncionalidade.cargo.id.eq(1)));
    }

    @Test
    public void comCargo_naoDeveMontarPredicate_quandoCargoIdNull() {
        assertThat(new FuncionalidadePredicate().comCargo(null).build())
            .isEqualTo(new BooleanBuilder());
    }
}
