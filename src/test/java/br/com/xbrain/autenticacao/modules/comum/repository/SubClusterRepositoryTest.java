package br.com.xbrain.autenticacao.modules.comum.repository;

import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.comum.model.Cluster;
import br.com.xbrain.autenticacao.modules.comum.model.Grupo;
import br.com.xbrain.autenticacao.modules.comum.model.Regional;
import br.com.xbrain.autenticacao.modules.comum.model.SubCluster;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@DataJpaTest
public class SubClusterRepositoryTest {

    @Autowired
    private SubClusterRepository repository;

    @Test
    public void findByIdCompleto_deveRetornarSubcluster_quandoBuscarPeloSubclusterId() {
        var actual = repository.findByIdCompleto(100);

        assertThat(actual).isPresent();
        assertThat(actual.get()).isEqualToIgnoringGivenFields(umSubcluster(), "cidades", "marca");
    }

    private SubCluster umSubcluster() {
        return SubCluster.builder()
            .id(100)
            .nome("REMOTO - NORTE MG")
            .situacao(ESituacao.A)
            .cluster(Cluster.builder()
                .id(22)
                .nome("NORTE MG")
                .grupo(Grupo.builder()
                    .id(6)
                    .nome("MINAS GERAIS")
                    .regional(Regional.builder()
                        .id(1)
                        .nome("LESTE")
                        .situacao(ESituacao.A)
                        .build())
                    .situacao(ESituacao.A)
                    .build())
                .situacao(ESituacao.A)
                .build())
            .build();
    }

    @Test
    public void findByIdCompleto_deveRetornarOptionalVazio_quandoNaoEncontrarPeloSubclusterId() {
        assertThat(repository.findByIdCompleto(9999)).isEmpty();
    }
}
