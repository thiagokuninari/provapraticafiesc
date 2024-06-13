package br.com.xbrain.autenticacao.modules.usuario.enums;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class CodigoNivelTest {

    @Test
    public void isNivelObirgatorioDadosNetSales_deveRetornarTrue_quandoNivelObrigatorio() {
        assertThat(CodigoNivel.isNivelObirgatorioDadosNetSales(CodigoNivel.OPERACAO)).isTrue();
    }

    @Test
    public void isNivelObirgatorioDadosNetSales_deveRetornarFalse_quandoNivelNaoObrigatorio() {
        assertThat(CodigoNivel.isNivelObirgatorioDadosNetSales(CodigoNivel.MSO)).isTrue();
    }
}
