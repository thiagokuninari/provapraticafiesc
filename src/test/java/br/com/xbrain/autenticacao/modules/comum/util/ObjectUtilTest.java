package br.com.xbrain.autenticacao.modules.comum.util;

import org.junit.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

public class ObjectUtilTest {

    @Test
    public void toInteger_null_seObjectNull() {
        assertThat(ObjectUtil.toInteger(null))
            .isNull();
    }

    @Test
    public void toInteger_integer_seObjectString() {
        Object object = "10";

        assertThat(ObjectUtil.toInteger(object))
            .isEqualTo(10);
    }

    @Test
    public void toInteger_integer_seObjectInteger() {
        Object object = Integer.parseInt("10");

        assertThat(ObjectUtil.toInteger(object))
            .isEqualTo(10);
    }

    @Test
    public void toInteger_integer_seObjectBigDecimal() {
        Object object = BigDecimal.valueOf(10);

        assertThat(ObjectUtil.toInteger(object))
            .isEqualTo(10);
    }

    @Test
    public void toString_null_seObjectNull() {
        assertThat(ObjectUtil.toString(null))
            .isNull();
    }

    @Test
    public void toString_string_seObjectString() {
        Object object = "OI";

        assertThat(ObjectUtil.toString(object))
            .isEqualTo("OI");
    }

    @Test
    public void toString_string_seObjectInteger() {
        Object object = Integer.parseInt("10");

        assertThat(ObjectUtil.toString(object))
            .isEqualTo("10");
    }

    @Test
    public void toString_string_seObjectBigDecimal() {
        Object object = BigDecimal.valueOf(10);

        assertThat(ObjectUtil.toString(object))
            .isEqualTo("10");
    }
}
