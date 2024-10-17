package br.com.xbrain.autenticacao.modules.comum.dto;

import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.comum.model.Cluster;
import br.com.xbrain.autenticacao.modules.comum.model.SubCluster;
import org.junit.Test;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class SubClusterDtoTest {

    @Test
    public void of_deveRetornarUmsubClusterDto_quandoSolicitado() {
        assertThat(SubClusterDto.of(umSubCluster()))
            .extracting(SubClusterDto::getId, SubClusterDto::getNome, SubClusterDto::getCluster, SubClusterDto::getSituacao)
            .containsExactly(1, "Kaique", new ClusterDto(1, null, null, null), ESituacao.A);
    }

    @Test
    public void of_deveRetornarUmsubClusterDtoComClusterVazio_quandoClusterForNulo() {
        var subCluster = umSubCluster();
        subCluster.setCluster(null);

        assertThat(SubClusterDto.of(subCluster))
            .extracting(SubClusterDto::getId, SubClusterDto::getNome, SubClusterDto::getCluster, SubClusterDto::getSituacao)
            .containsExactly(1, "Kaique", null, ESituacao.A);
    }

    @Test
    public void of_deveRetornarUmaListaDeSubClusterDto_quandoSolicitado() {
        var esperado = SubClusterDto.of(List.of(umSubCluster()));

        assertThat(esperado).isEqualTo(List.of(SubClusterDto.of(umSubCluster())));
    }

    private SubCluster umSubCluster() {
        return SubCluster.builder()
            .id(1)
            .nome("Kaique")
            .cluster(new Cluster(1))
            .situacao(ESituacao.A)
            .build();
    }

}
