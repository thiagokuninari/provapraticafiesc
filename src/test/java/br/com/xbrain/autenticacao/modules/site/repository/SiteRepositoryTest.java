package br.com.xbrain.autenticacao.modules.site.repository;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@DataJpaTest
@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@Sql({"classpath:/tests_database.sql", "classpath:/tests_sites.sql"})
public class SiteRepositoryTest {

    @Autowired
    private SiteRepository repository;

    @Test
    public void findFirstByCidadesIdInAndIdNot_naoDeveRetornarNada_quandoNaoExistirCidadesVinculadas() {
        Assertions.assertThat(repository.findFirstByCidadesIdInAndIdNot(List.of(1, 2), 1))
            .isNotPresent();
    }

    @Test
    public void findFirstByCidadesIdInAndIdNot_naoDeveRetornarNada_quandoExistirCidadesVinculadasEIdForDiferente() {
        Assertions.assertThat(repository.findFirstByCidadesIdInAndIdNot(List.of(5578), 1))
            .isNotPresent();
    }

    @Test
    public void findFirstByCidadesIdInAndIdNot_deveRetornarUmSite_quandoExistirCidadesVinculadasNele() {
        Assertions.assertThat(repository.findFirstByCidadesIdInAndIdNot(List.of(5578), 0))
            .isPresent();
    }
}
