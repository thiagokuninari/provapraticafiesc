package br.com.xbrain.autenticacao.modules.usuario.predicate;

import com.querydsl.core.BooleanBuilder;
import org.junit.Test;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static br.com.xbrain.autenticacao.modules.usuario.model.QCidade.cidade;
import static org.assertj.core.api.Assertions.assertThat;

public class CidadePredicateTest {

    @Test
    public void comCidadesId_deveFiltrarPorIds_quandoIdsListaComElementos() {
        var predicate = new CidadePredicate()
            .comCidadesId(List.of(230, 100, 340))
            .build();
        var expected = new BooleanBuilder(cidade.id.in(230, 100, 340));
        assertThat(predicate).isEqualTo(expected);
    }

    @Test
    public void comCidadesId_deveParticionarOsIns_quandoTamanhoDaListaDeIdsForMaiorQueMaximoOracle() {
        var cidadesId = generateRandomIntList(new Random(789), 2700);

        var predicate = new CidadePredicate()
            .comCidadesId(cidadesId)
            .build();

        var expected = new BooleanBuilder(cidade.id.in(cidadesId.subList(0, 1000)))
            .or(cidade.id.in(cidadesId.subList(1000, 2000)))
            .or(cidade.id.in(cidadesId.subList(2000, 2700)));

        assertThat(predicate).isEqualTo(expected);
    }

    private List<Integer> generateRandomIntList(Random random, int size) {
        return IntStream.range(0, size)
            .map(i -> random.nextInt())
            .boxed()
            .collect(Collectors.toList());
    }
}
