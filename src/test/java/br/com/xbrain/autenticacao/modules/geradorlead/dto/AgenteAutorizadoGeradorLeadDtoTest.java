package br.com.xbrain.autenticacao.modules.geradorlead.dto;

import br.com.xbrain.autenticacao.modules.comum.enums.Eboolean;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class AgenteAutorizadoGeradorLeadDtoTest {

    @Test
    public void isGeradorLead_deveRetornarFalse_quandoAgenteAutorizadoNaoForGeradorDeLeads() {
        var agenteAutorizadoGeradorLeadDto = AgenteAutorizadoGeradorLeadDto.builder()
            .geradorLead(Eboolean.F)
            .build();

        assertFalse(agenteAutorizadoGeradorLeadDto.isGeradorLead());
    }

    @Test
    public void isGeradorLead_deveRetornarTrue_quandoAgenteAutorizadoForGeradorDeLeads() {
        var agenteAutorizadoGeradorLeadDto = AgenteAutorizadoGeradorLeadDto.builder()
            .geradorLead(Eboolean.V)
            .build();

        assertTrue(agenteAutorizadoGeradorLeadDto.isGeradorLead());
    }
}