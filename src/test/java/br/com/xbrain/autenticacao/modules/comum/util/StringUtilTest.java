package br.com.xbrain.autenticacao.modules.comum.util;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class StringUtilTest {

    @Test
    public void atualizarEmailInativo_deveAtualizarEmail_quandoSolicitado() {
        assertThat(StringUtil.atualizarEmailInativo("SOCIO@EMPRESA.COM.BR"))
            .isEqualTo("SOCIO.INATIVO@EMPRESA.COM.BR");
    }
}
