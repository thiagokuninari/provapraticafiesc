package br.com.xbrain.autenticacao.modules.feeder.dto;

import br.com.xbrain.autenticacao.modules.comum.enums.Eboolean;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class AgenteAutorizadoPermissaoFeederDtoTest {

    @Test
    public void hasPermissaoFeeder_deveRetornarFalse_quandoAgenteAutorizadoNaoTiverPermissao() {
        var agenteAutorizadoFeederDto = AgenteAutorizadoPermissaoFeederDto.builder()
            .feeder(Eboolean.F)
            .build();

        assertFalse(agenteAutorizadoFeederDto.hasPermissaoFeeder());
    }

    @Test
    public void hasPermissaoFeeder_deveRetornarTrue_quandoAgenteAutorizadoTiverPermissao() {
        var agenteAutorizadoFeederDto = AgenteAutorizadoPermissaoFeederDto.builder()
            .feeder(Eboolean.V)
            .build();

        assertTrue(agenteAutorizadoFeederDto.hasPermissaoFeeder());
    }
}