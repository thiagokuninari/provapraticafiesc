package br.com.xbrain.autenticacao.modules.usuarioacesso.predicate;

import com.querydsl.core.BooleanBuilder;
import org.junit.Test;

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

    private List<Integer> umIdsList(int startInclusive, int endInclusive) {
        return IntStream.rangeClosed(startInclusive, endInclusive)
            .boxed()
            .collect(Collectors.toList());
    }
}
