package br.com.xbrain.autenticacao.modules.comum.model;

import br.com.xbrain.autenticacao.modules.comum.enums.EAreaAtuacao;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class GrupoTest {

    @Test
    public void getTipo_deveRetornarGrupo_quandoSolicitado() {
        assertThat(new Grupo().getTipo())
            .isEqualTo(EAreaAtuacao.GRUPO);
    }
}
