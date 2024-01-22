package br.com.xbrain.autenticacao.modules.usuario.dto;

import org.junit.Test;

import static br.com.xbrain.autenticacao.modules.usuario.helpers.UsuarioHelper.umUsuarioSocialHub;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class UsuarioSocialHubRequestMqTest {

    @Test
    public void from_deveCopiarPropriedadesCorretamente_quandoSolicitado() {
        var usuario = umUsuarioSocialHub("teste@teste.com");
        var request = UsuarioSocialHubRequestMq.from(usuario);

        assertEquals(usuario.getId(), request.getId());
        assertEquals(usuario.getNome(), request.getNome());
        assertEquals(usuario.getEmail(), request.getEmail());
        assertEquals(usuario.getCargoCodigo().toString(), request.getCargo());
        assertEquals(usuario.getCargo().getNivel().getCodigo().toString(), request.getNivel());
    }

    @Test
    public void from_deveManusearNulosCorretamente_quandoSolicitado() {
        var usuario = umUsuarioSocialHub("teste@teste.com");
        usuario.setCargo(null);

        var request = UsuarioSocialHubRequestMq.from(usuario);

        assertEquals(usuario.getId(), request.getId());
        assertEquals(usuario.getNome(), request.getNome());
        assertEquals(usuario.getEmail(), request.getEmail());
        assertNull(request.getCargo());
        assertNull(request.getNivel());
    }
}
