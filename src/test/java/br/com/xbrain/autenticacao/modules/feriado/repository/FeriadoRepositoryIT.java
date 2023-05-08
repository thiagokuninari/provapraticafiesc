package br.com.xbrain.autenticacao.modules.feriado.repository;

import br.com.xbrain.autenticacao.modules.feriado.enums.ESituacaoFeriado;
import br.com.xbrain.autenticacao.modules.feriado.enums.ETipoFeriado;
import br.com.xbrain.autenticacao.modules.feriado.predicate.FeriadoPredicate;
import br.com.xbrain.xbrainutils.DateUtils;
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

        var isFeriado = feriadoRepository.hasFeriadoNacionalOuRegional(LocalDate.of(2019, 6, 28), "ARAPONGAS", "PR");

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
            .hasSize(12)
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
    public void findAllNacional_deveRetornarTodosOsFeriadosNacionais_quandoSolicitado() {
        assertThat(feriadoRepository.findAllNacional(LocalDate.of(2019, 2, 15)))
            .hasSize(1)
            .contains(LocalDate.of(2019, 7, 30));
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

    @Test
    public void findAllDataFeriadoByCidadeUf_deveRetornarListaDeDatasFeriadosNacionais_quandoCidadeNaoTiverFeriadoRegional() {
        assertThat(feriadoRepository.findAllDataFeriadoByCidadeEUf("LONDRINA", "PR"))
            .hasSize(2)
            .contains(LocalDate.of(2019, 7, 28))
            .doesNotContain(LocalDate.of(2021, 4, 1), LocalDate.of(2021, 4, 6));
    }

    @Test
    public void findAllDataFeriadoByCidadeUf_deveRetornarDatasFeriadosNacionaisELocais_quandoCidadeTiverFeriadoRegional() {
        assertThat(feriadoRepository.findAllDataFeriadoByCidadeEUf("MARINGA", "PR"))
            .hasSize(2)
            .contains(LocalDate.of(2019, 7, 29), LocalDate.of(2019, 9, 23))
            .doesNotContain(LocalDate.of(2019, 9, 30));
    }

    @Test
    public void existsByPredicate_deveRetornarTrue_seFeriadoExistir() {
        var predicate = new FeriadoPredicate()
            .comNome("Feriado Fiorillo")
            .comTipoFeriado(ETipoFeriado.MUNICIPAL)
            .comEstado(1)
            .comCidade(5578)
            .comDataFeriado(DateUtils.parseStringToLocalDate("20/09/2023"))
            .excetoExcluidos()
            .excetoFeriadosFilhos()
            .build();

        assertThat(feriadoRepository.existsByPredicate(predicate)).isTrue();
    }

    @Test
    public void existsByPredicate_deveRetornarFalse_seFeriadoNaoExistir() {
        var predicate = new FeriadoPredicate()
            .comNome("Feriado Fiorillo")
            .comTipoFeriado(ETipoFeriado.MUNICIPAL)
            .comEstado(1)
            .comCidade(5578)
            .comDataFeriado(DateUtils.parseStringToLocalDate("20/01/2023"))
            .excetoExcluidos()
            .excetoFeriadosFilhos()
            .build();

        assertThat(feriadoRepository.existsByPredicate(predicate)).isFalse();
    }

    @Test
    public void existsByDataFeriadoAndCidadeIdOrUfId_deveRetornarTrue_seFeriadoExistir() {
        assertThat(feriadoRepository.existsByDataFeriadoAndCidadeIdOrUfId(
            LocalDate.of(2023, 9, 20), 5578, 1, ESituacaoFeriado.ATIVO))
            .isTrue();
    }

    @Test
    public void existsByDataFeriadoAndCidadeIdOrUfId_deveRetornarFalse_seFeriadoNaoExistir() {
        assertThat(feriadoRepository.existsByDataFeriadoAndCidadeIdOrUfId(
            LocalDate.of(2023, 1, 20), 5578, 1, ESituacaoFeriado.ATIVO))
            .isFalse();
    }
}
