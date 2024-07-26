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

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@DataJpaTest
@Sql(scripts = {"classpath:/tests_cluster.sql"})
public class GrupoRepositoryTest {

    @Autowired
    GrupoRepository grupoRepository;

    @Test
    public void findBySituacaoAndRegionalId_deveRetonarGruposOrdenados_quandoSituacaAtivoERegionalValido() {
        assertThat(grupoRepository.findBySituacaoAndRegionalId(A, 1004, new Sort(Sort.Direction.ASC, "nome")))
            .extracting("id", "nome", "regional.nome", "regional.id", "situacao")
            .containsExactly(tuple(1006, "A GRUPO", "REGIONAL", 1004, A),
                tuple(1007, "B GRUPO", "REGIONAL", 1004, A),
                tuple(1005, "X GRUPO", "REGIONAL", 1004, A));
    }

    @Test
    public void findBySituacaoAndRegionalId_deveRetonarGruposOrdenados_quandoSituacaInativoERegionalValido() {
        assertThat(grupoRepository.findBySituacaoAndRegionalId(I, 1004, new Sort(Sort.Direction.ASC, "nome")))
            .extracting("id", "nome", "regional.nome", "regional.id", "situacao")
            .containsExactly(tuple(1008, "GRUPO", "REGIONAL", 1004, I));
    }
}
