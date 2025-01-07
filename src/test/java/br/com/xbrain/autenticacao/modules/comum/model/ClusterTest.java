package br.com.xbrain.autenticacao.modules.comum.model;

import br.com.xbrain.autenticacao.modules.comum.enums.EAreaAtuacao;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ClusterTest {

    @Test
    public void getTipo_deveRetornarCluster_quandoSolicitado() {
        assertThat(new Cluster().getTipo())
            .isEqualTo(EAreaAtuacao.CLUSTER);
    }
}
