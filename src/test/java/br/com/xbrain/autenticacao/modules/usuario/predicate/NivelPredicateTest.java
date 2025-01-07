package br.com.xbrain.autenticacao.modules.usuario.predicate;

import br.com.xbrain.autenticacao.modules.comum.enums.Eboolean;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoNivel;
import com.querydsl.core.BooleanBuilder;
import org.junit.Test;

import static br.com.xbrain.autenticacao.modules.usuario.model.QNivel.nivel;
import static org.assertj.core.api.Assertions.assertThat;

public class NivelPredicateTest {

    @Test
    public void semCodigoNivel_deveMontarPredicate_quandoPassarCodigoNivel() {
        assertThat(new NivelPredicate().semCodigoNivel(CodigoNivel.OPERACAO).build())
            .isEqualTo(new BooleanBuilder(nivel.codigo.ne(CodigoNivel.OPERACAO)));
    }

    @Test
    public void comTipoConfiguracao_naoDeveMontarPredicate_quandoNaoPassarCodigoNivel() {
        assertThat(new NivelPredicate().semCodigoNivel(null).build())
            .isEqualTo(new BooleanBuilder());
    }

    @Test
    public void exibeSomenteParaCadastro_deveMontarPredicate_quandoSeEhCadastroForTrue() {
        assertThat(new NivelPredicate().exibeSomenteParaCadastro(true).build())
            .isEqualTo(new BooleanBuilder(nivel.exibirCadastroUsuario.eq(Eboolean.V)));
    }

    @Test
    public void exibeSomenteParaCadastro_naoDeveMontarPredicate_quandoSeEhCadastroForFalse() {
        assertThat(new NivelPredicate().exibeSomenteParaCadastro(false).build())
            .isEqualTo(new BooleanBuilder());
    }
}
