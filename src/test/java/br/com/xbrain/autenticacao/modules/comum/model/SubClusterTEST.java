package br.com.xbrain.autenticacao.modules.comum.model;

import br.com.xbrain.autenticacao.modules.comum.enums.EAreaAtuacao;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class SubClusterTEST {

    @Test
    public void getTipo_deveRetornarSubCluster_quandoSolicitado() {
        assertThat(umSubCluster().getTipo())
            .isEqualTo(EAreaAtuacao.SUBCLUSTER);
    }

    @Test
    public void getNomeComMarca_deveRetornarNomeSubClusterENomeMarcaSeparadosPorHifen_quandoSolicitado() {
        assertThat(umSubCluster().getNomeComMarca())
            .isEqualTo("nome subCluster - nome marca");
    }

    private SubCluster umSubCluster() {
        return SubCluster.builder()
            .nome("nome subCluster")
            .marca(umaMarca())
            .build();
    }

    private Marca umaMarca() {
        var marca = new Marca();
        marca.setNome("nome marca");
        return marca;
    }
}
