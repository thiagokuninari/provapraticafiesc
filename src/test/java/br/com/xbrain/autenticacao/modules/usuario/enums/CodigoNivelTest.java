package br.com.xbrain.autenticacao.modules.usuario.enums;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class CodigoNivelTest {

    @Test
    public void isNivelObrigatorioDadosNetSales_deveRetornarTrue_quandoNivelObrigatorio() {
        assertThat(CodigoNivel.isNivelObrigatorioDadosNetSales(CodigoNivel.OPERACAO)).isTrue();
    }

    @Test
    public void isNivelObrigatorioDadosNetSales_deveRetornarFalse_quandoNivelNaoObrigatorio() {
        assertThat(CodigoNivel.isNivelObrigatorioDadosNetSales(CodigoNivel.MSO)).isFalse();
    }

    @Test
    public void isNivelObrigatorioDadosNetSales_deveRetornarFalse_quandoNivelNull() {
        assertThat(CodigoNivel.isNivelObrigatorioDadosNetSales(null)).isFalse();
    }
}
