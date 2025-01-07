package br.com.xbrain.autenticacao.modules.usuario.predicate;

import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo;
import br.com.xbrain.autenticacao.modules.usuario.enums.ECanal;
import com.querydsl.core.BooleanBuilder;
import org.junit.Test;

import java.util.List;

import static br.com.xbrain.autenticacao.modules.usuario.model.QCargo.cargo;
import static org.assertj.core.api.Assertions.assertThat;

public class CargoPredicateTest {

    @Test
    public void comCanal_deveMontarPredicateComCanal_QuandoSolicitado() {
        var predicate = new CargoPredicate()
            .comCanal(ECanal.INTERNET)
            .build();

        var expected = new BooleanBuilder(cargo.canais.contains(ECanal.INTERNET));
        assertThat(predicate).isEqualTo(expected);
    }

    @Test
    public void comCanal_deveIgnorarTodosOsRegistros_quandoCanalForNull() {
        var predicate = new CargoPredicate()
            .comCanal(null)
            .build();

        var expected = new BooleanBuilder();
        assertThat(predicate).isEqualTo(expected);
    }

    @Test
    public void comNome_deveMontarPredicateComNome_QuandoSolicitado() {
        var predicate = new CargoPredicate()
            .comNome("nome")
            .build();

        var expected = new BooleanBuilder(cargo.nome.likeIgnoreCase("%" + "nome" + "%"));
        assertThat(predicate).isEqualTo(expected);
    }

    @Test
    public void comNome_deveIgnorarTodosOsRegistros_quandoCanalForNull() {
        var predicate = new CargoPredicate()
            .comNome(null)
            .build();

        var expected = new BooleanBuilder();
        assertThat(predicate).isEqualTo(expected);
    }

    @Test
    public void ouComCodigos_deveMontarPredicateComCodigos_QuandoSolicitado() {
        var predicate = new CargoPredicate()
            .ouComCodigos(List.of(CodigoCargo.INTERNET_BACKOFFICE))
            .build();

        var expected = new BooleanBuilder(cargo.codigo.in(CodigoCargo.INTERNET_BACKOFFICE));
        assertThat(predicate).isEqualTo(expected);
    }

    @Test
    public void ouComCodigos_deveIgnorarTodosOsRegistros_quandoCanalForNull() {
        var predicate = new CargoPredicate()
            .ouComCodigos(null)
            .build();

        var expected = new BooleanBuilder();
        assertThat(predicate).isEqualTo(expected);
    }

    @Test
    public void comNiveis_naoDeveMontarPredicate_listaDeNiveisVazia() {
        var predicate = new CargoPredicate()
            .comNiveis(List.of())
            .build();

        var expected = new BooleanBuilder();
        assertThat(predicate).isEqualTo(expected);
    }

    @Test
    public void comNiveis_deveMontarPredicate_quandoListaDeCanaisTerIds() {
        var predicate = new CargoPredicate()
            .comNiveis(List.of(1,2,3))
            .build();

        var expected = new BooleanBuilder(
            cargo.nivel.id.in(List.of(1,2,3))
        );
        assertThat(predicate).isEqualTo(expected);
    }
}
