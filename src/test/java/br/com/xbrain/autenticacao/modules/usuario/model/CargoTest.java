package br.com.xbrain.autenticacao.modules.usuario.model;

import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo;
import br.com.xbrain.autenticacao.modules.usuario.enums.ECanal;
import org.junit.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class CargoTest {

    @Test
    public void hasPermissaoSobreOCanal_true_quandoHouverOCanalTestado() {
        var cargo = umCargoComCanais(ECanal.AGENTE_AUTORIZADO, ECanal.ATIVO_PROPRIO);
        assertThat(cargo.hasPermissaoSobreOCanal(ECanal.AGENTE_AUTORIZADO)).isTrue();
    }

    @Test
    public void hasPermissaoSobreOCanal_false_quandoNaoHouverOCanalTestado() {
        var cargo = umCargoComCanais(ECanal.D2D_PROPRIO, ECanal.ATIVO_PROPRIO);
        assertThat(cargo.hasPermissaoSobreOCanal(ECanal.AGENTE_AUTORIZADO)).isFalse();
    }

    @Test
    public void hasPermissaoSobreOCanal_true_quandoNaoHouverCanais() {
        var cargo = umCargoComCanais();
        assertThat(cargo.getCanais()).isEmpty();
        assertThat(cargo.hasPermissaoSobreOCanal(ECanal.D2D_PROPRIO)).isTrue();
    }

    @Test
    public void hasPermissaoSobreOCanal_true_quandoCanaisForNull() {
        var cargo = new Cargo();
        assertThat(cargo.getCanais()).isNull();
        assertThat(cargo.hasPermissaoSobreOCanal(ECanal.D2D_PROPRIO)).isTrue();
    }

    @Test
    public void hasPermissaoSobreOCanal_true_quandoCanalForNull() {
        var cargo = umCargoComCanais(ECanal.D2D_PROPRIO, ECanal.AGENTE_AUTORIZADO);
        assertThat(cargo.hasPermissaoSobreOCanal(null)).isTrue();

        cargo = new Cargo();
        assertThat(cargo.getCanais()).isNull();
        assertThat(cargo.hasPermissaoSobreOCanal(null)).isTrue();
    }

    @Test
    public void isDiretorOperacao_deveRetornarTrue_quandoCodigoIgualADiretorOperacao() {
        var cargo = Cargo.builder()
            .codigo(CodigoCargo.DIRETOR_OPERACAO)
            .build();

        assertTrue(cargo.isDiretorOperacao());
    }

    @Test
    public void isDiretorOperacao_deveRetornarFalse_quandoCodigoDiferenteDeDiretorOperacao() {
        var cargo = Cargo.builder()
            .codigo(CodigoCargo.GERENTE_OPERACAO)
            .build();

        assertFalse(cargo.isDiretorOperacao());
    }

    private Cargo umCargoComCanais(ECanal... canais) {
        return Cargo.builder()
            .canais(Set.of(canais))
            .build();
    }
}
