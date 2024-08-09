package br.com.xbrain.autenticacao.modules.usuario.model;

import org.junit.Test;

import java.util.Set;

import static br.com.xbrain.autenticacao.modules.permissao.helper.FuncionalidadeHelper.umaFuncionalidadeBko;
import static br.com.xbrain.autenticacao.modules.usuario.helpers.UsuarioHelper.umSubnivel;
import static org.assertj.core.api.Assertions.assertThat;

public class SubnivelTest {

    @Test
    public void getFuncionalidadesIds_deveRetornarOsIdsDasFuncionalidades_quandoSolicitado() {
        var subnivel = umSubnivel(1, "BACKOFFICE", "BACKOFFICE",
            Set.of(umaFuncionalidadeBko(1, "Teste 1")));
        assertThat(subnivel.getFuncionalidadesIds())
            .containsExactly(1);
    }
}
