package br.com.xbrain.autenticacao.modules.usuario.enums;

import java.util.List;
import org.junit.Test;

import static br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo.*;
import static org.junit.Assert.*;

public class CodigoCargoTest {

    @Test
    public void getCargosTecnicos_deveRetornarListaDeCargosTecnicos_quandoSolicitado() {
        var listaCargosTecnicos = List.of(
            AGENTE_AUTORIZADO_TECNICO_GERENTE,
            AGENTE_AUTORIZADO_TECNICO_VENDEDOR,
            AGENTE_AUTORIZADO_TECNICO_SEGMENTADO,
            AGENTE_AUTORIZADO_TECNICO_SUPERVISOR,
            AGENTE_AUTORIZADO_TECNICO_COORDENADOR);

        assertEquals(getCargosTecnicos(), listaCargosTecnicos);
    }

    @Test
    public void getCargosVendedorInsideSales_deveRetornarListaDeCargosVendedorInsideSales_quandoSolicirado() {
        var listaCargosVendedorInsideSales = List.of(
            OPERACAO_EXECUTIVO_VENDAS,
            VENDEDOR_OPERACAO);

        assertEquals(getCargosVendedorInsideSales(), listaCargosVendedorInsideSales);
    }

    @Test
    public void isCargoTecnico_deveRetornarTrue_quandoCargoForTecnico() {
        var resultado = CodigoCargo.isCargoTecnico(CodigoCargo.AGENTE_AUTORIZADO_TECNICO_GERENTE);

        assertTrue("O método deve retornar true para cargos técnicos.", resultado);
    }

    @Test
    public void isCargoTecnico_deveRetornarFalse_quandoCargoNaoForTecnico() {
        var resultado = CodigoCargo.isCargoTecnico(AGENTE_AUTORIZADO_VENDEDOR_TELEVENDAS);

        assertFalse("O método deve retornar false para cargos que não são técnicos.", resultado);
    }
}
