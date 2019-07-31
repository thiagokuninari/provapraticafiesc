package br.com.xbrain.autenticacao.modules.feriado.repository;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@DataJpaTest
@Sql("classpath:/feriado-repository-test.sql")
public class FeriadoRepositoryTestIT {

    @Autowired
    private FeriadoRepository feriadoRepository;

    @Test
    public void hasFeriadoNacionalOuRegional_deveRetornarTrue_casoSejaFeriadoNacional() {

        var isFeriado = feriadoRepository.hasFeriadoNacionalOuRegional(LocalDate.of(2019, 9, 30), "Londrina", "PR");

        assertThat(isFeriado).isTrue();
    }

    @Test
    public void hasFeriadoNacionalOuRegional_deveRetornarTrue_casoSejaFeriadoRegional() {

        var isFeriado = feriadoRepository.hasFeriadoNacionalOuRegional(LocalDate.of(2019, 9, 28), "Londrina", "PR");

        assertThat(isFeriado).isTrue();
    }

    @Test
    public void hasFeriadoNacionalOuRegional_deveRetornarFalse_casoNaoSejaFeriadoRegional() {

        var isFeriado = feriadoRepository.hasFeriadoNacionalOuRegional(LocalDate.of(2019, 9, 28), "Arapongas", "PR");

        assertThat(isFeriado).isFalse();
    }

    @Test
    public void hasFeriadoNacionalOuRegional_deveRetornarFalse_casoNaoSejaFeriado() {

        var isFeriado = feriadoRepository.hasFeriadoNacionalOuRegional(LocalDate.of(2019, 9, 19), "Londrina", "PR");

        assertThat(isFeriado).isFalse();
    }
}