package br.com.xbrain.autenticacao.modules.permissao.dto;

import org.junit.Test;

import java.util.List;

import static br.com.xbrain.autenticacao.modules.permissao.helper.CargoDepartamentoFuncionalidadeHelper.umCargoDepartamentoFuncionalidade;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

public class CargoDepartamentoFuncionalidadeResponseTest {

    @Test
    public void convertFrom_deveRetornarCargoDepartamentoFuncionalidadeResponse_quandoSolicitado() {
        assertThat(CargoDepartamentoFuncionalidadeResponse.convertFrom(umCargoDepartamentoFuncionalidade(1)))
            .extracting("id", "nivelNome", "cargoNome", "departamentoNome", "funcionalidadeId",
                "funcionalidadeNome", "funcionalidadeRole", "aplicacaoNome")
            .containsExactly(1, "Operação", "Vendedor Operação", "Comercial", 3024, "Relatório - Gerenciamento Operacional",
                "VDS_3024", "VENDAS");
    }

    @Test
    public void convertFrom_deveRetornarListaCargoDepartamentoFuncionalidadeResponse_quandoSolicitado() {
        assertThat(CargoDepartamentoFuncionalidadeResponse
            .convertFrom(List.of(umCargoDepartamentoFuncionalidade(1), umCargoDepartamentoFuncionalidade(2))))
            .extracting("id", "nivelNome", "cargoNome", "departamentoNome", "funcionalidadeId",
                "funcionalidadeNome", "funcionalidadeRole", "aplicacaoNome")
            .containsExactly(
                tuple(1, "Operação", "Vendedor Operação", "Comercial", 3024, "Relatório - Gerenciamento Operacional",
                    "VDS_3024", "VENDAS"),
                tuple(2, "Operação", "Vendedor Operação", "Comercial", 3024, "Relatório - Gerenciamento Operacional",
                    "VDS_3024", "VENDAS"));
    }
}
