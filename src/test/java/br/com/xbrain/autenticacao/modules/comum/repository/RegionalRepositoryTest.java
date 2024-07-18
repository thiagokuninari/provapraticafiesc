package br.com.xbrain.autenticacao.modules.comum.repository;

import br.com.xbrain.autenticacao.modules.comum.predicate.RegionalPredicate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import static br.com.xbrain.autenticacao.modules.comum.enums.ESituacao.A;
import static br.com.xbrain.autenticacao.modules.comum.enums.Eboolean.V;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@DataJpaTest
public class RegionalRepositoryTest {

    @Autowired
    RegionalRepository regionalRepository;

    @Test
    public void getAll_deveRetornarRegionais_quandoSituacaoForAtivoEForNovaRegional() {
        assertThat(regionalRepository.getAll(new RegionalPredicate().build()))
            .extracting("id", "nome", "situacao", "novaRegional")
            .containsExactly(
                tuple(1022, "RBS", A, V),
                tuple(1023, "RCO", A, V),
                tuple(1024, "RMG", A, V),
                tuple(1025, "RNE", A, V),
                tuple(1026, "RNO", A, V),
                tuple(1027, "RPS", A, V),
                tuple(1028, "RRE", A, V),
                tuple(1029, "RRS", A, V),
                tuple(1030, "RSC", A, V),
                tuple(1031, "RSI", A, V));
    }
}
