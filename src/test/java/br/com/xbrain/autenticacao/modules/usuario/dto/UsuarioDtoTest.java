package br.com.xbrain.autenticacao.modules.usuario.dto;

import org.junit.Test;

import java.util.Set;

import static br.com.xbrain.autenticacao.modules.usuario.enums.CodigoNivel.OPERACAO;
import static br.com.xbrain.autenticacao.modules.usuario.helpers.UsuarioHelper.umUsuarioOperacaoDto;
import static org.assertj.core.api.Assertions.assertThat;

public class UsuarioDtoTest {

    @Test
    public void convertFrom_deveRetornarNivelCodigo_quandoCadastrarUsuario() {
        assertThat(UsuarioDto.convertFrom(umUsuarioOperacaoDto()))
                .extracting("nome", "nivelCodigo", "subCanaisId")
                .containsExactly("VENDEDOR OPERACAO D2D", OPERACAO, Set.of(3));
    }
}
