package br.com.xbrain.autenticacao.modules.usuario.dto;

import br.com.xbrain.autenticacao.modules.permissao.model.Funcionalidade;
import br.com.xbrain.autenticacao.modules.permissao.model.FuncionalidadeCanal;
import br.com.xbrain.autenticacao.modules.usuario.enums.ECanal;
import org.junit.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class UsuarioPermissaoCanalTest {

    @Test
    public void of_deveRetonarUsuarioPermissaoCanal_quandoSolicitado() {
        assertThat(UsuarioPermissaoCanal.of(umaFuncionalidade()))
            .extracting("permissao", "canais")
            .containsExactly("ROLE_AUT_PERMISSAO", List.of("AGENTE_AUTORIZADO", "D2D_PROPRIO"));
    }

    private Funcionalidade umaFuncionalidade() {
        var funcionalidadeCanal1 = new FuncionalidadeCanal();
        funcionalidadeCanal1.setCanal(ECanal.AGENTE_AUTORIZADO);

        var funcionalidadeCanal2 = new FuncionalidadeCanal();
        funcionalidadeCanal2.setCanal(ECanal.D2D_PROPRIO);
        return Funcionalidade.builder()
            .role("AUT_PERMISSAO")
            .canais(List.of(funcionalidadeCanal1, funcionalidadeCanal2))
            .build();
    }
}
