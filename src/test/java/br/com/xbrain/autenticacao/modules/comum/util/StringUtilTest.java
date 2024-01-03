package br.com.xbrain.autenticacao.modules.comum.util;

import br.com.xbrain.autenticacao.modules.comum.exception.ValidacaoException;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

public class StringUtilTest {

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
