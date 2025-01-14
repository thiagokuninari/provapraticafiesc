package br.com.xbrain.autenticacao.modules.usuario.dto;

import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoNivel;
import org.junit.Test;

import java.util.List;

import static br.com.xbrain.autenticacao.modules.usuario.helpers.UsuarioHelper.umUsuarioSocialHub;
import static org.junit.Assert.*;

public class UsuarioSocialHubRequestMqTest {

    @Test
    public void from_deveCopiarPropriedadesCorretamente_quandoSolicitado() {
        var regionaisIds = List.of(1022);
        var usuario = umUsuarioSocialHub("teste@teste.com", 1, CodigoNivel.XBRAIN);
        var request = UsuarioSocialHubRequestMq.from(usuario, regionaisIds, "Diretor", false);

        assertEquals(usuario.getId(), request.getId());
        assertEquals(usuario.getTerritorioMercadoDesenvolvimentoId(), request.getTerritorioMercadoDesenvolvimentoId());
        assertEquals(usuario.getNome(), request.getNome());
        assertEquals(usuario.getEmail(), request.getEmail());
        assertEquals("Diretor", request.getCargo());
        assertEquals(usuario.getNivelCodigo().toString(), request.getNivel());
        assertEquals(List.of(1022), regionaisIds);
        assertFalse(request.isPermissaoAdmSocialRemovida());
        assertNull(request.getSituacao());
    }

    @Test
    public void from_deveManusearNulosCorretamente_quandoSolicitado() {
        var usuario = umUsuarioSocialHub("teste@teste.com", null, CodigoNivel.XBRAIN);
        usuario.setCargo(null);

        var request = UsuarioSocialHubRequestMq.from(usuario, List.of(), null, false);

        assertEquals(usuario.getId(), request.getId());
        assertEquals(usuario.getNome(), request.getNome());
        assertEquals(usuario.getEmail(), request.getEmail());
        assertNull(request.getCargo());
        assertNull(request.getTerritorioMercadoDesenvolvimentoId());
        assertNull(request.getNivel());
        assertFalse(request.isPermissaoAdmSocialRemovida());
        assertNull(request.getSituacao());
    }

    @Test
    public void from_deveCopiarPropriedadesCorretamente_quandoRequestComPermissaoAdmSocialSolicitado() {
        var regionaisIds = List.of(1022);
        var usuario = umUsuarioSocialHub("teste@teste.com", 1, CodigoNivel.XBRAIN);
        var request = UsuarioSocialHubRequestMq.from(usuario, regionaisIds, "Diretor", true);

        assertEquals(usuario.getId(), request.getId());
        assertTrue(request.isPermissaoAdmSocialRemovida());
    }

    @Test
    public void from_deveCopiarPropriedadesCorretamente_quandoRequestComSituacao() {
        var usuario = umUsuarioSocialHub("teste@teste.com", 1, CodigoNivel.XBRAIN);
        var request = UsuarioSocialHubRequestMq.from(usuario.getId(), ESituacao.A);

        assertEquals(usuario.getId(), request.getId());
        assertEquals(ESituacao.A, request.getSituacao());
    }
}
