package br.com.xbrain.autenticacao.modules.comum.repository;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import static br.com.xbrain.autenticacao.modules.comum.enums.ESituacao.A;
import static br.com.xbrain.autenticacao.modules.comum.enums.ESituacao.I;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;
import static org.springframework.data.domain.Sort.Direction.ASC;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@DataJpaTest
@Sql(scripts = {"classpath:/tests_cluster.sql"})
public class ClusterRepositoryTest {

    @Autowired
    ClusterRepository clusterRepository;

    @Test
    public void findBySituacaoAndGrupoId_deveRetonarClustersOrdenados_quandoSituacaAtivoEGrupoValido() {
        assertThat(clusterRepository.findBySituacaoAndGrupoId(A, 1008, new Sort(ASC, "nome")))
            .extracting("id", "nome", "grupo.id", "situacao")
            .containsExactly(tuple(1009, "A NOME CLUSTER", 1008, A),
                tuple(1011, "B NOME CLUSTER", 1008, A),
                tuple(1010, "C NOME CLUSTER", 1008, A));

    }

    @Test
    public void findBySituacaoAndGrupoId_deveRetonarClustersOrdenados_quandoSituacaInativoEGrupoValido() {
        assertThat(clusterRepository.findBySituacaoAndGrupoId(I, 1008, new Sort(ASC, "nome")))
            .extracting("id", "nome", "grupo.id", "situacao")
            .containsExactly(tuple(1012, "D NOME CLUSTER", 1008, I));

    }
}
