package br.com.xbrain.autenticacao.modules.feriado.importacaoautomatica.model;

import br.com.xbrain.autenticacao.modules.feriado.dto.FeriadoAutomacao;
import br.com.xbrain.autenticacao.modules.feriado.enums.ETipoFeriado;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class FeriadoAutomacaoTest {

    @Test
    public void isFacultativo_deveRetornarTrue_seTipoFeriadoForFacultativo() {
        var feriado = FeriadoAutomacao.builder().tipoFeriado(ETipoFeriado.FACULTATIVO).build();

        assertThat(feriado.isFacultativo()).isTrue();
    }

    @Test
    public void isFacultativo_deveRetornarFalse_seTipoFeriadoNaoForFacultativo() {
        var feriado = FeriadoAutomacao.builder().tipoFeriado(ETipoFeriado.MUNICIPAL).build();

        assertThat(feriado.isFacultativo()).isFalse();
    }
}

