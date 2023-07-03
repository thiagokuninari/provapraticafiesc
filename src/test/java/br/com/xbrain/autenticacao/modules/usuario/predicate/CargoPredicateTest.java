package br.com.xbrain.autenticacao.modules.usuario.predicate;

import br.com.xbrain.autenticacao.modules.usuario.enums.ECanal;
import com.querydsl.core.BooleanBuilder;
import org.junit.Test;

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
}
