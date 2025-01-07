package br.com.xbrain.autenticacao.modules.usuario.repository;

import br.com.xbrain.autenticacao.modules.usuario.predicate.NivelPredicate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@RunWith(SpringRunner.class)
public class NivelRepositoryTest {

    @Autowired
    private NivelRepository repository;

    @Test
    public void findAll_deveRetornarNiveis_quandoPredicateInformado() {
        assertThat(repository.getAll(new NivelPredicate().build()).size())
            .isEqualTo(22);
    }
}
