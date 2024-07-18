package br.com.xbrain.autenticacao.modules.comum.repository;

import br.com.xbrain.autenticacao.modules.comum.predicate.SubClusterPredicate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static br.com.xbrain.autenticacao.modules.comum.enums.ESituacao.A;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@DataJpaTest
@Sql(scripts = {"classpath:/tests_cluster.sql"})
public class SubClusterRepositoryTest {

    @Autowired
    SubClusterRepository subClusterRepository;

    @Test
    public void findAllByClusterId_deveRetornarSubCluters_quandoSituacaAtivoEClusterValido() {

        assertThat(subClusterRepository.findAllByClusterId(1009, new SubClusterPredicate().build()))
            .extracting("id", "nome", "cluster.id", "cluster.nome", "situacao")
            .containsExactly(
                tuple(1013, "A NOME SUB CLUSTER", 1009, "A NOME CLUSTER", A),
                tuple(1014, "C NOME SUB CLUSTER", 1009, "A NOME CLUSTER", A));

    }

    @Test
    public void findAllByClustersId_deveRetornarSubCluters_quandoSituacaAtivoEClustersValidos() {
        assertThat(subClusterRepository.findAllByClustersId(List.of(1009, 1010), new SubClusterPredicate().build()))
            .extracting("id", "nome", "cluster.id", "cluster.nome", "situacao")
            .containsExactly(
                tuple(1013, "A NOME SUB CLUSTER", 1009, "A NOME CLUSTER", A),
                tuple(1015, "B NOME SUB CLUSTER", 1010, "C NOME CLUSTER", A),
                tuple(1014, "C NOME SUB CLUSTER", 1009, "A NOME CLUSTER", A));
    }

    @Test
    public void findAllAtivo_deveRetornarSubCluters_quandoSituacaAtivo() {
        assertThat(subClusterRepository.findAllAtivo(new SubClusterPredicate().filtrarPermitidos(230).build()))
            .isNotNull();
    }
}
