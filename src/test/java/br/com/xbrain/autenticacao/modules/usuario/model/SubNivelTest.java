package br.com.xbrain.autenticacao.modules.usuario.model;

import org.junit.Test;

import java.util.Set;

import static br.com.xbrain.autenticacao.modules.permissao.helper.FuncionalidadeHelper.umaFuncionalidadeBko;
import static br.com.xbrain.autenticacao.modules.usuario.helpers.UsuarioHelper.umSubNivel;
import static org.assertj.core.api.Assertions.assertThat;

public class SubNivelTest {

    @Test
    public void getFuncionalidadesIds_deveRetornarOsIdsDasFuncionalidades_quandoSolicitado() {
        var subNivel = umSubNivel(1, "BACKOFFICE", "BACKOFFICE",
            Set.of(umaFuncionalidadeBko(1, "Teste 1")));
        assertThat(subNivel.getFuncionalidadesIds())
            .containsExactly(1);
    }
}
