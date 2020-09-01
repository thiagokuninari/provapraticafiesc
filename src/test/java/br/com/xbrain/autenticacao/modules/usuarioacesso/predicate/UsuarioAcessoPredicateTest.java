package br.com.xbrain.autenticacao.modules.usuarioacesso.predicate;

import com.querydsl.core.BooleanBuilder;
import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static br.com.xbrain.autenticacao.modules.usuarioacesso.model.QUsuarioAcesso.usuarioAcesso;
import static org.assertj.core.api.Assertions.assertThat;

public class UsuarioAcessoPredicateTest {

    @Test
    public void porUsuarioIds_deveParticionarOsIns_quandoNumeroDeIdsMaiorQueMaximoOracle() {
        var predicate = new UsuarioAcessoPredicate()
            .porUsuarioIds(umIdsList(1, 2700))
            .build();

        var expected = new BooleanBuilder(usuarioAcesso.usuario.id.in(umIdsList(1, 1000)))
            .or(usuarioAcesso.usuario.id.in(umIdsList(1001, 2000)))
            .or(usuarioAcesso.usuario.id.in(umIdsList(2001, 2700)));

        assertThat(predicate).isEqualTo(expected);
    }

    @Test
    public void porUsuarioIds_deveNaoParticionarOsIns_quandoNumeroDeIdsMenorQueMaximoOracle() {
        var predicate = new UsuarioAcessoPredicate()
            .porUsuarioIds(umIdsList(1, 600))
            .build();

        var expected = new BooleanBuilder(usuarioAcesso.usuario.id.in(umIdsList(1, 600)));

        assertThat(predicate).isEqualTo(expected);
    }

    @Test
    public void porUsuarioIds_deveNaoParticionarOsIns_quandoNumeroDeIdsIgualAoMaximoOracle() {
        var predicate = new UsuarioAcessoPredicate()
            .porUsuarioIds(umIdsList(1, 1000))
            .build();

        var expected = new BooleanBuilder(usuarioAcesso.usuario.id.in(umIdsList(1, 1000)));

        assertThat(predicate).isEqualTo(expected);
    }

    @Test
    public void porPeriodoDataCadastro_deveAplicarFiltroPorDataCadastroEntre_quandoHouverDatasInicioEFim() {
        var predicate = new UsuarioAcessoPredicate()
            .porPeriodoDataCadastro(LocalDate.of(2019, 12, 6), LocalDate.of(2019, 12, 18))
            .build();

        var expectedLocalDateFrom = LocalDateTime.of(2019, 12, 6, 0, 0, 0, 0);
        var expectedLocalDateTo = LocalDateTime.of(2019, 12, 18, 23, 59, 59, 999999999);
        var expected = new BooleanBuilder(usuarioAcesso.dataCadastro.between(expectedLocalDateFrom, expectedLocalDateTo));

        assertThat(predicate).isEqualTo(expected);
    }

    @Test
    public void porPeriodoDataCadastro_deveAplicarFiltroPorDataCadastroMaiorOuIgualADataInicio_quandoDataFimForNull() {
        var predicate = new UsuarioAcessoPredicate()
            .porPeriodoDataCadastro(LocalDate.of(2019, 12, 6), null)
            .build();

        var expectedLocalDateFrom = LocalDateTime.of(2019, 12, 6, 0, 0, 0, 0);
        var expected = new BooleanBuilder(usuarioAcesso.dataCadastro.goe(expectedLocalDateFrom));

        assertThat(predicate).isEqualTo(expected);
    }

    @Test
    public void porPeriodoDataCadastro_deveAplicarFiltroPorDataCadastroMenorQueDataFim_quandoDataInicioForNull() {
        var predicate = new UsuarioAcessoPredicate()
            .porPeriodoDataCadastro(null, LocalDate.of(2019, 12, 18))
            .build();

        var expectedLocalDateTo = LocalDateTime.of(2019, 12, 18, 23, 59, 59, 999999999);
        var expected = new BooleanBuilder(usuarioAcesso.dataCadastro.loe(expectedLocalDateTo));

        assertThat(predicate).isEqualTo(expected);
    }

    @Test
    public void porPeriodoDataCadastro_deveNaoAplicarNenhumFiltro_quandoDatasInicioEFimForemNulls() {
        var predicate = new UsuarioAcessoPredicate()
            .porPeriodoDataCadastro(null, null)
            .build();

        var expected = new BooleanBuilder();

        assertThat(predicate).isEqualTo(expected);
    }

    private List<Integer> umIdsList(int startInclusive, int endInclusive) {
        return IntStream.rangeClosed(startInclusive, endInclusive)
            .boxed()
            .collect(Collectors.toList());
    }
}
