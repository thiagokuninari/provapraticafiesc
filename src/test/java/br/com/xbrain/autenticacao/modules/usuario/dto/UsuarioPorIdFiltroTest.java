package br.com.xbrain.autenticacao.modules.usuario.dto;

import br.com.xbrain.autenticacao.modules.comum.enums.Eboolean;
import org.junit.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class UsuarioPorIdFiltroTest {

    @Test
    public void toPredicate_deveRetornarUsuarioPorIdPredicate_quandoSolicitado() {
        var filtro = new UsuarioPorIdFiltro();
        filtro.setUsuariosIds(List.of(1, 2));
        filtro.setApenasAtivos(Eboolean.V);

        assertThat(filtro.toPredicate().build().toString())
            .hasToString("usuario.situacao = A && usuario.id in [1, 2]");

    }
}
