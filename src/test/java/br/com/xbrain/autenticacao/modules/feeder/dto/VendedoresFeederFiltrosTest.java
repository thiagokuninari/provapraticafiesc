package br.com.xbrain.autenticacao.modules.feeder.dto;

import org.junit.Test;

import java.util.List;

import static br.com.xbrain.autenticacao.modules.usuario.helpers.UsuarioPredicateHelper.umVendedoresFeederPredicateComSocioPrincipal;
import static br.com.xbrain.autenticacao.modules.usuario.helpers.UsuarioPredicateHelper.umVendedoresFeederPredicateSemSocioPrincipal;
import static br.com.xbrain.autenticacao.modules.feeder.helper.VendedoresFeederFiltrosHelper.umVendedoresFeederFiltros;
import static org.assertj.core.api.Assertions.assertThat;

public class VendedoresFeederFiltrosTest {

    @Test
    public void obterBuscarInativos_false_quandoBuscarInativosNull() {
        assertThat(umVendedoresFeederFiltros(null, null, null).obterBuscarInativos())
            .isEqualTo(false);
    }

    @Test
    public void obterBuscarInativos_false_quandoBuscarInativosFalse() {
        assertThat(umVendedoresFeederFiltros(null, null, false).obterBuscarInativos())
            .isEqualTo(false);
    }

    @Test
    public void obterBuscarInativos_true_quandoBuscarInativosTrue() {
        assertThat(umVendedoresFeederFiltros(null, null, true).obterBuscarInativos())
            .isEqualTo(true);
    }

    @Test
    public void toPredicate_usuarioPredicate_quandoSocioPrincipalTrue() {
        assertThat(umVendedoresFeederFiltros(null, true, null).toPredicate(List.of(1, 2, 3)))
            .isEqualTo(umVendedoresFeederPredicateComSocioPrincipal(List.of(1, 2, 3)).build());
    }

    @Test
    public void toPredicate_usuarioPredicate_quandoSocioPrincipalFalse() {
        assertThat(umVendedoresFeederFiltros(null, false, null).toPredicate(List.of(1, 2, 3)))
            .isEqualTo(umVendedoresFeederPredicateSemSocioPrincipal(List.of(1, 2, 3)).build());
    }
}
