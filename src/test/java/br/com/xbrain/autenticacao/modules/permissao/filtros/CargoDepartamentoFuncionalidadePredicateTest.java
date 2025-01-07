package br.com.xbrain.autenticacao.modules.permissao.filtros;

import com.querydsl.core.BooleanBuilder;
import org.junit.Test;

import static br.com.xbrain.autenticacao.modules.permissao.model.QCargoDepartamentoFuncionalidade.cargoDepartamentoFuncionalidade;
import static org.assertj.core.api.Assertions.assertThat;

public class CargoDepartamentoFuncionalidadePredicateTest {

    @Test
    public void comDepartamento_deveMontarPredicate_quandoInformarDepartamentoId() {
        assertThat(new CargoDepartamentoFuncionalidadePredicate().comDepartamento(1).build())
            .isEqualTo(new BooleanBuilder(cargoDepartamentoFuncionalidade.departamento.id.eq(1)));
    }

    @Test
    public void comDepartamento_naoDeveMontarPredicate_quandoDepartamentoIdNull() {
        assertThat(new CargoDepartamentoFuncionalidadePredicate().comDepartamento(null).build())
            .isEqualTo(new BooleanBuilder());
    }

    @Test
    public void comCargo_deveMontarPredicate_quandoInformarCargoId() {
        assertThat(new CargoDepartamentoFuncionalidadePredicate().comCargo(1).build())
            .isEqualTo(new BooleanBuilder(cargoDepartamentoFuncionalidade.cargo.id.eq(1)));
    }

    @Test
    public void comCargo_naoDeveMontarPredicate_quandoCargoIdNull() {
        assertThat(new CargoDepartamentoFuncionalidadePredicate().comCargo(null).build())
            .isEqualTo(new BooleanBuilder());
    }

    @Test
    public void comFuncionalidadeNome_deveMontarPredicate_quandoInformarFuncionalidadeNome() {
        assertThat(new CargoDepartamentoFuncionalidadePredicate().comFuncionalidadeNome("funcionalidade").build())
            .isEqualTo(new BooleanBuilder(cargoDepartamentoFuncionalidade.funcionalidade.nome
                .likeIgnoreCase("%funcionalidade%")));
    }

    @Test
    public void comFuncionalidadeNome_naoDeveMontarPredicate_quandoFuncionalidadeNomeNull() {
        assertThat(new CargoDepartamentoFuncionalidadePredicate().comFuncionalidadeNome(null).build())
            .isEqualTo(new BooleanBuilder());
    }

    @Test
    public void comAplicacaoNome_deveMontarPredicate_quandoInformarAplicacaoNome() {
        assertThat(new CargoDepartamentoFuncionalidadePredicate().comAplicacaoNome("aplicacao").build())
            .isEqualTo(new BooleanBuilder(cargoDepartamentoFuncionalidade.funcionalidade.aplicacao.nome
                .likeIgnoreCase("%aplicacao%")));
    }

    @Test
    public void comAplicacaoNome_naoDeveMontarPredicate_quandoAplicacaoNomeNull() {
        assertThat(new CargoDepartamentoFuncionalidadePredicate().comAplicacaoNome(null).build())
            .isEqualTo(new BooleanBuilder());
    }
}
