package br.com.xbrain.autenticacao.modules.usuario.enums;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

public class CanalNetSalesTest {

    @Test
    public void canalNetSales_deveRetornarValorEDescricao_quandoSolicitado() {
        assertThat(ECanalNetSales.values())
            .extracting(ECanalNetSales::name, ECanalNetSales::getDescricao)
            .containsExactly(
                tuple("D2D_ACOES_ESPECIAIS", "D2D-ACOES ESPECIAIS"),
                tuple("D2D_CLARO_PESSOAL", "D2D-CLARO PESSOAL"),
                tuple("D2D_CONDOMINIO", "D2D-CONDOMINIO"),
                tuple("D2D_CONVERSAO_MDU", "D2D-CONVERSAO MDU"),
                tuple("D2D_ESPECIALISTA", "D2D-ESPECIALISTA"),
                tuple("D2D_INDIRETO", "D2D-INDIRETO"),
                tuple("D2D_PESSOA_JURIDICA", "D2D-PESSOA JURIDICA"),
                tuple("D2D_PME", "D2D-PME"),
                tuple("D2D_TECNICO", "D2D-TECNICO"),
                tuple("D2D_VENDAS_PESSOAIS", "D2D-VENDAS PESSOAIS"),
                tuple("PROP_TECNICO", "PROP-TECNICO"),
                tuple("PROP_CONDOMINIO_NET_CURITIBA", "PROP-CONDOMINIO-NET CURITIBA"),
                tuple("PROP_VENDAS_MDU_NET_FLO", "PROP-VENDAS MDU-NET FLO"),
                tuple("PROP_VENDAS_PESSOAIS_NET_ANA", "PROP-VENDAS PESSOAIS-NET ANA"),
                tuple("PROP_VENDAS_PESSOAIS_NET_BLU", "PROP-VENDAS PESSOAIS-NET BLU"),
                tuple("PROP_VENDAS_PESSOAIS_NET_CGR", "PROP-VENDAS PESSOAIS-NET CGR"),
                tuple("PROP_VENDAS_PESSOAIS_NET_RIB", "PROP-VENDAS PESSOAIS-NET RIB"),
                tuple("PROP_VENDAS_PESSOAIS_NET_RIO", "PROP-VENDAS PESSOAIS-NET RIO"),
                tuple("PROP_VENDAS_PESSOAIS_NET_SAN", "PROP-VENDAS PESSOAIS-NET SAN"),
                tuple("VENDAS_CORPORATIVAS", "VENDAS CORPORATIVAS"),
                tuple("VENDAS_PARA_FUNCIONARIO", "VENDAS PARA FUNCIONARIO"),
                tuple("VENDAS_PDV", "VENDAS PDV"),
                tuple("VENDAS_POR_FORNECEDOR", "VENDAS POR FORNECEDOR"),
                tuple("VENDAS_POR_PARCEIROS", "VENDAS POR PARCEIROS"),
                tuple("VENDAS_PORTA_A_PORTA", "VENDAS PORTA A PORTA"),
                tuple("VENDAS_VIA_NOVITECH", "VENDAS VIA NOVITECH"),
                tuple("VENDAS_VIA_PORTAL", "VENDAS VIA PORTAL")
            );
    }
}
