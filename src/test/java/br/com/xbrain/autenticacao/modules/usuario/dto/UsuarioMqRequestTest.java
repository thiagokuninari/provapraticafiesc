package br.com.xbrain.autenticacao.modules.usuario.dto;

import org.junit.Test;

import static br.com.xbrain.autenticacao.modules.usuario.helpers.UsuarioHelper.umUsuarioMqRequestSocioPrincipal;
import static br.com.xbrain.autenticacao.modules.usuario.helpers.UsuarioHelper.umUsuarioMqRequestSocioSecundario;
import static org.assertj.core.api.Assertions.assertThat;

public class UsuarioMqRequestTest {

    @Test
    public void isNovoCadastroSocioPrincipal_deveRetornarTrue_quandoForCadastroDeNovoSocioPrincipal() {
        assertThat(umUsuarioMqRequestSocioPrincipal().isNovoCadastroSocioPrincipal())
            .isTrue();
    }

    @Test
    public void isNovoCadastroSocioPrincipal_deveRetornarFalse_quandoNaoForCadastroDeNovoSocioPrincipal() {
        assertThat(umUsuarioMqRequestSocioSecundario().isNovoCadastroSocioPrincipal())
            .isFalse();
    }

    @Test
    public void isNovoCadastroSocioSecundario_deveRetornarTrue_quandoForCadastroDeNovoSocioSecundario() {
        assertThat(umUsuarioMqRequestSocioSecundario().isNovoCadastroSocioSecundario())
            .isTrue();
    }

    @Test
    public void isNovoCadastroSocioSecundario_deveRetornarFalse_quandoNaoForCadastroDeNovoSocioSecundario() {
        assertThat(umUsuarioMqRequestSocioPrincipal().isNovoCadastroSocioSecundario())
            .isFalse();
    }
}