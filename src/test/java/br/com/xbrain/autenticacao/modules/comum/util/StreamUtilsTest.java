package br.com.xbrain.autenticacao.modules.comum.util;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class StreamUtilsTest {

    @Test
    public void mapNull_deveRetornarValorDefault_quandoValueForNull() {
        var resultado = StreamUtils.mapNull(null, (String texto) -> texto.toUpperCase(), "default");
        assertEquals("default", resultado);
    }

    @Test
    public void mapNull_deveRetornarAplicarAFuncao_quandoValueNaoForNull() {
        var resultado = StreamUtils.mapNull("xuxu", (String texto) -> texto.toUpperCase(), "backup");
        assertEquals("XUXU", resultado);
    }
}
