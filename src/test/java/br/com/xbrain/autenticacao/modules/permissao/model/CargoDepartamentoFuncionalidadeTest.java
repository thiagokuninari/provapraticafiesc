package br.com.xbrain.autenticacao.modules.permissao.model;

import org.junit.Test;

import static br.com.xbrain.autenticacao.modules.permissao.helper.CargoDepartamentoFuncionalidadeHelper.umCargoDepartamentoFuncionalidade;
import static org.assertj.core.api.Assertions.assertThat;

public class CargoDepartamentoFuncionalidadeTest {

    @Test
    public void getCargoId_deveRetornarCargoId_quandoSolicitado() {
        var cargoDepartamento = umCargoDepartamentoFuncionalidade(1);

        assertThat(cargoDepartamento.getCargoId()).isEqualTo(8);
    }

    @Test
    public void getDepartamentoId_deveRetornarDepartamentoId_quandoSolicitado() {
        var cargoDepartamento = umCargoDepartamentoFuncionalidade(1);

        assertThat(cargoDepartamento.getDepartamentoId()).isEqualTo(3);
    }
}
