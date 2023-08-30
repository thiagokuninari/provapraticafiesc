package br.com.xbrain.autenticacao.modules.usuario.dto;

import br.com.xbrain.autenticacao.modules.comum.enums.ETipoFeeder;
import org.junit.Test;

import java.util.List;

import static br.com.xbrain.autenticacao.modules.usuario.helpers.UsuarioHelper.*;
import static org.assertj.core.api.Assertions.assertThat;

public class UsuarioAaTipoFeederDtoTest {

    @Test
    public void of_converterParaUsuarioAaTipoFeederDto_quandoPassarUsuario() {
        var usuario = umUsuario();
        usuario.setUsuarioCadastro(umUsuarioCadastro());

        assertThat(UsuarioAaTipoFeederDto.of(usuario, ETipoFeeder.RESIDENCIAL))
            .isEqualTo(umUsuarioAaTipoFeederDto());
    }

    @Test
    public void of_converterParaUsuarioAaTipoFeederDto_quandoPassarUsuarioMqRequest() {
        var usuario = umUsuarioAaTipoFeederDto();
        usuario.setUsuariosIds(List.of(1));
        usuario.setUsuarioCadastroId(5);
        usuario.setTipoFeeder(ETipoFeeder.RESIDENCIAL);

        assertThat(UsuarioAaTipoFeederDto.of(umUsuarioMqRequest()))
            .isEqualTo(usuario);
    }
}
