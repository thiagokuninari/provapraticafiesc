package br.com.xbrain.autenticacao.modules.feeder.dto;

import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import org.junit.Test;

import java.util.List;

import static br.com.xbrain.autenticacao.modules.feeder.helper.VendedoresFeederFiltrosHelper.umVendedoresFeederFiltros;
import static br.com.xbrain.autenticacao.modules.usuario.helpers.UsuarioPredicateHelper.umVendedoresFeederPredicateComSocioPrincipalESituacaoAtiva;
import static br.com.xbrain.autenticacao.modules.usuario.helpers.UsuarioPredicateHelper.umVendedoresFeederPredicateSemSocioPrincipalESituacaoAtiva;
import static org.assertj.core.api.Assertions.assertThat;

public class VendedoresFeederFiltrosTest {

    @Test
    public void obterSituacoes_false_quandoBuscarInativosNull() {
        assertThat(umVendedoresFeederFiltros(null, null, null).obterSituacoes())
            .isEqualTo(List.of(ESituacao.A));
    }

    @Test
    public void obterSituacoes_false_quandoBuscarInativosFalse() {
        assertThat(umVendedoresFeederFiltros(null, null, false).obterSituacoes())
            .isEqualTo(List.of(ESituacao.A));
    }

    @Test
    public void obterSituacoes_true_quandoBuscarInativosTrue() {
        assertThat(umVendedoresFeederFiltros(null, null, true).obterSituacoes())
            .isEqualTo(List.of(ESituacao.A, ESituacao.I, ESituacao.R));
    }

    @Test
    public void toPredicate_usuarioPredicate_quandoSocioPrincipalTrue() {
        assertThat(umVendedoresFeederFiltros(null, true, null).toPredicate(List.of(1, 2, 3)))
            .isEqualTo(umVendedoresFeederPredicateComSocioPrincipalESituacaoAtiva(List.of(1, 2, 3)).build());
    }

    @Test
    public void toPredicate_usuarioPredicate_quandoSocioPrincipalFalse() {
        assertThat(umVendedoresFeederFiltros(null, false, null).toPredicate(List.of(1, 2, 3)))
            .isEqualTo(umVendedoresFeederPredicateSemSocioPrincipalESituacaoAtiva(List.of(1, 2, 3)).build());
    }
}
