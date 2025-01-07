package br.com.xbrain.autenticacao.modules.importacaousuario.util;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class EmailUtilTest {

    @Test
    public void validar_deveRetornarFalse_quandoEmailNulo() {
        assertThat(EmailUtil.validar(null))
            .isFalse();
    }

    @Test
    public void validar_deveRetornarTrue_quandoEmailValido() {
        assertThat(EmailUtil.validar("kaique@gmail.com"))
            .isTrue();
    }

    @Test
    public void validarEmail_deveRetornarFalse_quandoEmailVazio() {
        assertThat(EmailUtil.validarEmail(""))
            .isFalse();
    }

    @Test
    public void validarEmail_deveRetornarFalse_quandoEmailNulo() {
        assertThat(EmailUtil.validarEmail(null))
            .isFalse();
    }

    @Test
    public void validarEmail_deveRetornarFalse_quandoEmailSemDominio() {
        assertThat(EmailUtil.validarEmail("SOCIO.PRINCIPAL@EMPRESA"))
            .isFalse();
    }

    @Test
    public void validarEmail_deveRetornarFalse_quandoEmailComMaisDeUmArroba() {
        assertThat(EmailUtil.validarEmail("SOCIO.PRINCIP@L@EMPRESA.COM.BR"))
            .isFalse();
    }

    @Test
    public void validarEmail_deveRetornarTrue_quandoEmailValido() {
        assertThat(EmailUtil.validarEmail("SOCIO.PRINCIPAL@EMPRESA.COM.BR"))
            .isTrue();
    }
}
