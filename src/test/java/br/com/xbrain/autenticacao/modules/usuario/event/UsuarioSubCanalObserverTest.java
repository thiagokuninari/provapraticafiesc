package br.com.xbrain.autenticacao.modules.usuario.event;

import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioSubCanalId;
import org.junit.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class UsuarioSubCanalObserverTest {

    @Test
    public void onApplicationEvent_deveAdicionarUsuarioSubCanalEvent_quandoSolicitado() {
        var usuario = new UsuarioSubCanalObserver();
        usuario.onApplicationEvent(umUsuarioSubCanalEvent());
        assertThat(usuario.getUsuariosComSubCanais().size())
            .isEqualTo(2);
    }

    private UsuarioSubCanalEvent umUsuarioSubCanalEvent() {
        var usuario = new UsuarioSubCanalEvent(new Object(), List.of(umUsuarioSubCanalId(1), umUsuarioSubCanalId(2)));
        return usuario;
    }

    private UsuarioSubCanalId umUsuarioSubCanalId(Integer id) {
        var usuario = new UsuarioSubCanalId();
        usuario.setId(id);
        usuario.setSubCanalId(1);
        usuario.setNome("nome");
        return usuario;
    }
}
