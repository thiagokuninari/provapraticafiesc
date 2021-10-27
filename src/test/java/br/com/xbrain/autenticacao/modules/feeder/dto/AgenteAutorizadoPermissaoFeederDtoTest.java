package br.com.xbrain.autenticacao.modules.feeder.dto;

import br.com.xbrain.autenticacao.modules.comum.enums.ETipoFeeder;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class AgenteAutorizadoPermissaoFeederDtoTest {

    @Test
    public void hasPermissaoFeeder_deveRetornarFalse_quandoAgenteAutorizadoNaoTiverPermissao() {
        var agenteAutorizadoFeederDto = AgenteAutorizadoPermissaoFeederDto.builder()
            .feeder(ETipoFeeder.NAO_FEEDER)
            .build();

        assertFalse(agenteAutorizadoFeederDto.hasPermissaoFeeder());
    }

    @Test
    public void hasPermissaoFeeder_deveRetornarTrue_quandoAgenteAutorizadoTiverPermissao() {
        var agenteAutorizadoFeederDto = AgenteAutorizadoPermissaoFeederDto.builder()
            .feeder(ETipoFeeder.RESIDENCIAL)
            .build();

        assertTrue(agenteAutorizadoFeederDto.hasPermissaoFeeder());
    }

    @Test
    public void hasPermissaoFeeder_deveRetornarTrue_quandoAgenteAutorizadoTiverPermissaoEmpresarial() {
        var agenteAutorizadoFeederDto = AgenteAutorizadoPermissaoFeederDto.builder()
            .feeder(ETipoFeeder.EMPRESARIAL)
            .build();

        assertTrue(agenteAutorizadoFeederDto.hasPermissaoFeeder());
    }
}
