package br.com.xbrain.autenticacao.modules.feriado.importacaoautomatica.model;

import br.com.xbrain.autenticacao.modules.autenticacao.dto.UsuarioAutenticado;
import br.com.xbrain.autenticacao.modules.feriado.enums.ESituacaoFeriadoAutomacao;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ImportacaoFeriadoTest {

    @Test
    public void of_deveRetornarFeriadoAutomacao_quandoSolicitado() {
        assertThat(ImportacaoFeriado.of(ESituacaoFeriadoAutomacao.IMPORTADO, umUsuarioAutenticado()))
            .extracting("id", "situacaoFeriadoAutomacao", "usuarioCadastroId",
                "usuarioCadastroNome")
            .containsExactlyInAnyOrder(null, ESituacaoFeriadoAutomacao.IMPORTADO, 1, "teste nome");
    }

    private UsuarioAutenticado umUsuarioAutenticado() {
        return UsuarioAutenticado.builder()
            .id(1)
            .nome("teste nome")
            .build();
    }
}
