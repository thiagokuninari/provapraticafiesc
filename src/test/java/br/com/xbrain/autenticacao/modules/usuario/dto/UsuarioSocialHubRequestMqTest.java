package br.com.xbrain.autenticacao.modules.usuario.dto;

import org.junit.Test;

import java.util.List;

import static br.com.xbrain.autenticacao.modules.usuario.helpers.UsuarioHelper.umUsuarioSocialHub;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class UsuarioSocialHubRequestMqTest {

    @Test
    public void from_deveCopiarPropriedadesCorretamente_quandoSolicitado() {
        var regionaisIds = List.of(1022);
        var usuario = umUsuarioSocialHub("teste@teste.com");
        var request = UsuarioSocialHubRequestMq.from(usuario, regionaisIds);

        assertEquals(usuario.getId(), request.getId());
        assertEquals(usuario.getNome(), request.getNome());
        assertEquals(usuario.getEmail(), request.getEmail());
        assertEquals(usuario.getCargoCodigo().toString(), request.getCargo());
        assertEquals(usuario.getNivelCodigo().toString(), request.getNivel());
        assertEquals(usuario.getNivelCodigo().toString(), request.getNivel());
        assertEquals(List.of(1022), regionaisIds);
    }

    @Test
    public void from_deveManusearNulosCorretamente_quandoSolicitado() {
        var usuario = umUsuarioSocialHub("teste@teste.com");
        usuario.setCargo(null);

        var request = UsuarioSocialHubRequestMq.from(usuario, List.of());

        assertEquals(usuario.getId(), request.getId());
        assertEquals(usuario.getNome(), request.getNome());
        assertEquals(usuario.getEmail(), request.getEmail());
        assertNull(request.getCargo());
        assertNull(request.getNivel());
    }
}
