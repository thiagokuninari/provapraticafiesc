package br.com.xbrain.autenticacao.modules.comum.util;

import br.com.xbrain.autenticacao.modules.comum.exception.ValidacaoException;
import org.junit.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

public class StringUtilTest {

    @Test
    public void getOnlyNumbers_deveRetornarStringVazia_quandoStringNull() {
        assertThat(StringUtil.getOnlyNumbers(null))
            .isEqualTo("");
    }

    @Test
    public void getOnlyNumbers_deveRetornarStringVazia_quandoStringVazia() {
        assertThat(StringUtil.getOnlyNumbers(""))
            .isEqualTo("");
    }

    @Test
    public void getOnlyNumbers_deveRetornarSomenteNumeros_quandoValorForFornecido() {
        assertThat(StringUtil.getOnlyNumbers("2345Meia78 Tá na hora de tomar sorvete"))
            .isEqualTo("234578");
    }

    @Test
    public void getStringFormatadaCsv_deveRetornarStringFormatadaParaCsv_quandoTipoAtributoForString() {
        assertThat(StringUtil.getStringFormatadaCsv(".|texto;"))
            .isEqualTo(".texto");
    }

    @Test
    public void getStringFormatadaCsv_deveRetornarStringFormatadaParaCsv_quandoTipoAtributoForBigDecimal() {
        var bigDecimal = new BigDecimal("12345.09");
        assertThat(StringUtil.getStringFormatadaCsv(bigDecimal))
            .isEqualTo("12345.09");
    }

    @Test
    public void getStringFormatadaCsv_deveLancarValidacaoException_quandoSerializableForTipoNaoSuportado() {
        assertThatCode(() -> StringUtil.getStringFormatadaCsv(123))
            .isInstanceOf(ValidacaoException.class)
            .hasMessageContaining("Tipo class java.lang.Integer não suportado");
    }

    @Test
    public void atualizarEmailInativo_deveRetornarValidacaoException_quandoEmailVazio() {
        assertThatCode(() -> StringUtil.atualizarEmailInativo(""))
            .isInstanceOf(ValidacaoException.class)
            .hasMessage("#050 - O e-mail cadastrado para o usuário está inválido.");
    }

    @Test
    public void atualizarEmailInativo_deveRetornarValidacaoException_quandoEmailNulo() {
        assertThatCode(() -> StringUtil.atualizarEmailInativo(null))
            .isInstanceOf(ValidacaoException.class)
            .hasMessage("#050 - O e-mail cadastrado para o usuário está inválido.");
    }

    @Test
    public void atualizarEmailInativo_deveRetornarValidacaoException_quandoEmailSemDominio() {
        assertThatCode(() -> StringUtil.atualizarEmailInativo("SOCIO.PRINCIPAL@EMPRESA"))
            .isInstanceOf(ValidacaoException.class)
            .hasMessage("#050 - O e-mail cadastrado para o usuário está inválido.");
    }

    @Test
    public void atualizarEmailInativo_deveRetornarValidacaoException_quandoEmailComMaisDeUmArroba() {
        assertThatCode(() -> StringUtil.atualizarEmailInativo("SOCIO.PRINCIP@L@EMPRESA"))
            .isInstanceOf(ValidacaoException.class)
            .hasMessage("#050 - O e-mail cadastrado para o usuário está inválido.");
    }

    @Test
    public void atualizarEmailInativo_deveAtualizarEmail_quandoEmailValido() {
        assertThat(StringUtil.atualizarEmailInativo("SOCIO.PRINCIPAL@EMPRESA.COM.BR"))
            .isNotInstanceOf(ValidacaoException.class)
            .isEqualTo("SOCIO.PRINCIPAL.INATIVO@EMPRESA.COM.BR");
    }
}
