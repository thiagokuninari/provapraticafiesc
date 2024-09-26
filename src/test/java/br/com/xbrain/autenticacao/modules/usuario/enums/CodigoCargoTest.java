package br.com.xbrain.autenticacao.modules.usuario.enums;

import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo.*;
import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
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
}
