package br.com.xbrain.autenticacao.modules.usuario.predicate;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.Expressions;
import org.junit.Test;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static br.com.xbrain.autenticacao.modules.usuario.model.QUsuario.usuario;
import static org.assertj.core.api.Assertions.assertThat;

public class UsuarioPredicateTest {

    @Test
    public void comIds_deveIgnorarFiltroPorIds_quandoIdsForListaVazia() {
        var predicate = new UsuarioPredicate()
            .comIds(List.of())
            .build();
        var expected = new BooleanBuilder();
        assertThat(predicate).isEqualTo(expected);
    }

    @Test
    public void comIds_deveIgnorarFiltroPorIds_quandoIdsForNull() {
        var predicate = new UsuarioPredicate()
            .comIds(null)
            .build();
        var expected = new BooleanBuilder();
        assertThat(predicate).isEqualTo(expected);
    }

    @Test
    public void comIds_deveFiltrarPorIds_quandoIdsListaComElementos() {
        var predicate = new UsuarioPredicate()
            .comIds(List.of(100, 22, 456))
            .build();
        var expected = new BooleanBuilder(usuario.id.in(100, 22, 456));
        assertThat(predicate).isEqualTo(expected);
    }

    @Test
    public void comIds_deveParticionarOsIns_quandoTamanhoDaListaDeIdsForMaiorQueMaximoOracle() {
        var ids = generateRandomIntList(new Random(789), 2700);

        var predicate = new UsuarioPredicate()
            .comIds(ids)
            .build();

        var expected = new BooleanBuilder(usuario.id.in(ids.subList(0, 1000)))
            .or(usuario.id.in(ids.subList(1000, 2000)))
            .or(usuario.id.in(ids.subList(2000, 2700)));

        assertThat(predicate).isEqualTo(expected);
    }

    @Test
    public void comIdsObrigatorio_deveFiltrarPorNenhumId_quandoIdsForListaVazia() {
        var predicate = new UsuarioPredicate()
            .comIdsObrigatorio(List.of())
            .build();
        var expected = new BooleanBuilder(Expressions.TRUE.eq(false));
        assertThat(predicate).isEqualTo(expected);
    }

    @Test
    public void comIdsObrigatorio_deveIgnorarFiltroPorIds_quandoIdsForNull() {
        var predicate = new UsuarioPredicate()
            .comIdsObrigatorio(null)
            .build();
        var expected = new BooleanBuilder();
        assertThat(predicate).isEqualTo(expected);
    }

    @Test
    public void comIdsObrigatorio_deveFiltrarPorIds_quandoIdsListaComElementos() {
        var predicate = new UsuarioPredicate()
            .comIdsObrigatorio(List.of(100, 22, 456))
            .build();
        var expected = new BooleanBuilder(usuario.id.in(100, 22, 456));
        assertThat(predicate).isEqualTo(expected);
    }

    @Test
    public void comIdsObrigatorio_deveParticionarOsIns_quandoTamanhoDaListaDeIdsForMaiorQueMaximoOracle() {
        var ids = generateRandomIntList(new Random(789), 2700);

        var predicate = new UsuarioPredicate()
            .comIdsObrigatorio(ids)
            .build();

        var expected = new BooleanBuilder(usuario.id.in(ids.subList(0, 1000)))
            .or(usuario.id.in(ids.subList(1000, 2000)))
            .or(usuario.id.in(ids.subList(2000, 2700)));

        assertThat(predicate).isEqualTo(expected);
    }

    private List<Integer> generateRandomIntList(Random random, int size) {
        return IntStream.range(0, size)
            .map(i -> random.nextInt())
            .boxed()
            .collect(Collectors.toList());
    }
}
