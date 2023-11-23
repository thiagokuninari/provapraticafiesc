package br.com.xbrain.autenticacao.modules.horarioacesso.predicate;

import com.querydsl.core.BooleanBuilder;
import org.junit.Test;

import static br.com.xbrain.autenticacao.modules.horarioacesso.model.QHorarioAcesso.horarioAcesso;
import static org.assertj.core.api.Assertions.assertThat;

public class HorarioAcessoPredicateTest {

    @Test
    public void comSite_deveMontarPredicate_quandoInformarId() {
        assertThat(new HorarioAcessoPredicate().comSite(1).build())
            .isEqualTo(new BooleanBuilder(horarioAcesso.site.id.eq(1)));
    }

    @Test
    public void comSite_naoDeveMontarPredicate_quandoNaoInformarId() {
        assertThat(new HorarioAcessoPredicate().comSite(null).build())
            .isEqualTo(new BooleanBuilder());
    }
}
