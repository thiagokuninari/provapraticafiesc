package br.com.xbrain.autenticacao.modules.comum.util;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class CnpjUtilTest {

    @Test
    public void formataCnpj_deveFormartarCnpj_quandoCnpjCompletoFornecido() {
        assertThat(CnpjUtil.formataCnpj("01179678000167"))
            .isEqualTo("01.179.678/0001-67");
    }

    @Test
    public void formataCnpj_deveFormartarCnpj_quandoCnpjOitoFornecido() {
        assertThat(CnpjUtil.formataCnpj("01179678"))
            .isEqualTo("01.179.678");
    }

    @Test
    public void formataCnpj_deveRetonarVazio_quandoCnpjForNull() {
        assertThat(CnpjUtil.formataCnpj(null))
            .isEqualTo("");
    }

    @Test
    public void formataCnpj_deveRetonarVazio_quandoCnpjForVazio() {
        assertThat(CnpjUtil.formataCnpj(""))
            .isEqualTo("");
    }
}
