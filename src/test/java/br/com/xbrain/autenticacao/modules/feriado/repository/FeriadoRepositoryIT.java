package br.com.xbrain.autenticacao.modules.feriado.repository;

import br.com.xbrain.autenticacao.modules.feriado.dto.FeriadoCidadeEstadoResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@DataJpaTest
@Sql("classpath:/feriado-repository-test.sql")
public class FeriadoRepositoryIT {

    @Autowired
    private FeriadoRepository feriadoRepository;

    @Test
    public void hasFeriadoNacionalOuRegional_deveRetornarTrue_casoSejaFeriadoNacional() {

        var isFeriado = feriadoRepository.hasFeriadoNacionalOuRegional(LocalDate.of(2019, 7, 30), "LONDRINA", "PR");

        assertThat(isFeriado).isTrue();
    }

    @Test
    public void hasFeriadoNacionalOuRegional_deveRetornarTrue_casoSejaFeriadoRegional() {

        var isFeriado = feriadoRepository.hasFeriadoNacionalOuRegional(LocalDate.of(2019, 7, 28), "LONDRINA", "PR");

        assertThat(isFeriado).isTrue();
    }

    @Test
    public void hasFeriadoNacionalOuRegional_deveRetornarFalse_casoNaoSejaFeriadoRegional() {

        var isFeriado = feriadoRepository.hasFeriadoNacionalOuRegional(LocalDate.of(2019, 7, 28), "ARAPONGAS", "PR");

        assertThat(isFeriado).isFalse();
    }

    @Test
    public void hasFeriadoNacionalOuRegional_deveRetornarFalse_casoNaoSejaFeriado() {

        var isFeriado = feriadoRepository.hasFeriadoNacionalOuRegional(LocalDate.of(2019, 7, 19), "LONDRINA", "PR");

        assertThat(isFeriado).isFalse();
    }

    @Test
    public void hasFeriadoNacionalOuRegional_deveRetornarTrue_seEncontrarFeriadoParaUfComNomePorExtenso() {
        var isFeriado = feriadoRepository.hasFeriadoNacionalOuRegional(LocalDate.of(2019, 7, 28), "Londrina", "Parana");

        assertThat(isFeriado).isTrue();
    }

    @Test
    public void findAllDataFeriadoByCidadeId_deveRetornarListaDeDatasFeriadosNacionais_quandoCidadeNaoTiverFeriadoRegional() {
        assertThat(feriadoRepository.findAllDataFeriadoByCidadeId(1111))
            .hasSize(10)
            .contains(LocalDate.of(2019, 7, 30))
            .doesNotContain(LocalDate.of(2019, 7, 28), LocalDate.of(2019, 7, 29));
    }

    @Test
    public void findAllDataFeriadoByCidadeId_deveRetornarDatasFeriadosNacionaisELocais_quandoCidadeTiverFeriadoRegional() {
        assertThat(feriadoRepository.findAllDataFeriadoByCidadeId(5578))
            .hasSize(11)
            .contains(LocalDate.of(2019, 7, 30), LocalDate.of(2019, 7, 28))
            .doesNotContain(LocalDate.of(2019, 7, 29));
    }

    @Test
    public void buscarTotalDeFeriadosPorMesAno_deveRetornarTotalFeriadosAgrupadoPorAnoMes_quandoSolicitado() {
        assertThat(feriadoRepository.buscarTotalDeFeriadosPorMesAno())
            .extracting("ano", "mes", "qtdFeriadosNacionais")
            .containsExactly(
                tuple(2018, 1, 1L),
                tuple(2018, 3, 1L),
                tuple(2018, 4, 1L),
                tuple(2018, 5, 1L),
                tuple(2018, 9, 1L),
                tuple(2018, 10, 1L),
                tuple(2018, 11, 2L),
                tuple(2018, 12, 1L),
                tuple(2019, 7, 1L)
            );
    }

    @Test
    public void buscarFeriadoNacional_deveRetornarBoolean_quandoSolicitado() {
        assertThat(feriadoRepository.buscarEstadosFeriadosEstaduaisPorData(LocalDate.of(2019, 9, 23)))
            .containsExactlyInAnyOrderElementsOf(List.of("SC", "PR"));
    }

    @Test
    public void buscarFeriadoMunicipal_deveRetornarDto_quandoSolicitado() {
        assertThat(feriadoRepository.buscarFeriadosMunicipaisPorData(LocalDate.of(2019, 9, 23)))
            .extracting("cidade", "estado")
            .containsExactlyInAnyOrder(
                tuple("MARINGA", "PR"),
                tuple("LONDRINA", "PR")
            );
    }
}
