package br.com.xbrain.autenticacao.modules.permissao.dto;

import org.junit.Test;

import java.util.List;

import static br.com.xbrain.autenticacao.modules.permissao.helper.FuncionalidadeHelper.funcionalidadeRelatorioGerenciamentoOperacional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

public class FuncionalidadeResponseTest {

    @Test
    public void convertFrom_deveRetornarFuncionalidadeResponse_quandoSolicitado() {
        assertThat(FuncionalidadeResponse.convertFrom(funcionalidadeRelatorioGerenciamentoOperacional()))
            .extracting("id", "funcionalidadeId", "nome", "role", "aplicacao", "especial")
            .containsExactly(3024, 3024, "Relatório - Gerenciamento Operacional", "VDS_3024", "VENDAS", false);
    }

    @Test
    public void convertFrom_deveRetornarListaCargoDepartamentoFuncionalidadeResponse_quandoSolicitado() {
        assertThat(FuncionalidadeResponse
            .convertFrom(List.of(funcionalidadeRelatorioGerenciamentoOperacional())))
            .extracting("id", "funcionalidadeId", "nome", "role", "aplicacao", "especial")
            .containsExactly(
                tuple(3024, 3024, "Relatório - Gerenciamento Operacional", "VDS_3024", "VENDAS", false));
    }
}
