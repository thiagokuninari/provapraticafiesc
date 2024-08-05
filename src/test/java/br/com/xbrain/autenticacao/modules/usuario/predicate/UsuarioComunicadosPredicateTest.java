package br.com.xbrain.autenticacao.modules.usuario.predicate;

import br.com.xbrain.autenticacao.modules.usuario.dto.PublicoAlvoComunicadoFiltros;
import com.querydsl.core.BooleanBuilder;
import org.junit.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class UsuarioComunicadosPredicateTest {

    @Test
    public void comFiltroCidadeParceiros_deveMontarPredicate_quandoUfIdForFornecido() {
        var filtro = PublicoAlvoComunicadoFiltros.builder()
            .ufId(1)
            .usuariosFiltradosPorCidadePol(List.of(1, 2))
            .build();
        assertThat(new UsuarioComunicadosPredicate().comFiltroCidadeParceiros(filtro, new BooleanBuilder()).build().toString())
            .isEqualTo("uf1.id = 1");
    }

    @Test
    public void comFiltroCidadeParceiros_deveMontarPredicate_quandoRegionalIdForFornecido() {
        var filtro = PublicoAlvoComunicadoFiltros.builder()
            .regionalId(45)
            .usuariosFiltradosPorCidadePol(List.of(1, 2))
            .build();
        assertThat(new UsuarioComunicadosPredicate().comFiltroCidadeParceiros(filtro, new BooleanBuilder()).build().toString())
            .isEqualTo("regional.id = 45");
    }

}
