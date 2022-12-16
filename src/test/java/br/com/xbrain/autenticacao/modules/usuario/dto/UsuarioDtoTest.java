package br.com.xbrain.autenticacao.modules.usuario.dto;

import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo;
import br.com.xbrain.autenticacao.modules.usuario.enums.ECanal;
import org.junit.Test;

import java.util.Set;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class UsuarioDtoTest {

    @Test
    public void hasCanalD2dProprio_deveRetornarFalse_quandoUsuarioNaoPossuirCanalD2dProprio() {
        var usuarioDto = UsuarioDto.builder()
            .canais(Set.of(ECanal.ATIVO_PROPRIO))
            .build();

        assertFalse(usuarioDto.hasCanalD2dProprio());
    }

    @Test
    public void hasCanalD2dProprio_deveRetornarTrue_quandoUsuarioPossuirCanalD2dProprio() {
        var usuarioDto = UsuarioDto.builder()
            .canais(Set.of(ECanal.D2D_PROPRIO))
            .build();

        assertTrue(usuarioDto.hasCanalD2dProprio());
    }

    @Test
    public void hasIdAndCargoCodigo_deveRetornarFalse_quandoIdECargoCodigoForemNull() {
        var usuarioDto = UsuarioDto.builder()
            .id(null)
            .cargoCodigo(null)
            .build();

        assertFalse(usuarioDto.hasIdAndCargoCodigo());
    }

    @Test
    public void hasIdAndCargoCodigo_deveRetornarFalse_quandoCargoCodigoForNull() {
        var usuarioDto = UsuarioDto.builder()
            .id(1)
            .cargoCodigo(null)
            .build();

        assertFalse(usuarioDto.hasIdAndCargoCodigo());
    }

    @Test
    public void hasIdAndCargoCodigo_deveRetornarFalse_quandoIdForNull() {
        var usuarioDto = UsuarioDto.builder()
            .id(null)
            .cargoCodigo(CodigoCargo.VENDEDOR_OPERACAO)
            .build();

        assertFalse(usuarioDto.hasIdAndCargoCodigo());
    }

    @Test
    public void hasIdAndCargoCodigo_deveRetornarTrue_quandoUsuarioPossuirSubCanaisId() {
        var usuarioDto = UsuarioDto.builder()
            .id(1)
            .cargoCodigo(CodigoCargo.VENDEDOR_OPERACAO)
            .build();

        assertTrue(usuarioDto.hasIdAndCargoCodigo());
    }

    @Test
    public void hasSubCanaisId_deveRetornarFalse_quandoUsuarioNaoPossuirSubCanaisId() {
        var usuarioDto = UsuarioDto.builder()
            .subCanaisId(Set.of())
            .build();

        assertFalse(usuarioDto.hasSubCanaisId());
    }

    @Test
    public void hasSubCanaisId_deveRetornarTrue_quandoUsuarioPossuirSubCanaisId() {
        var usuarioDto = UsuarioDto.builder()
            .subCanaisId(Set.of(1, 2, 3, 4))
            .build();

        assertTrue(usuarioDto.hasSubCanaisId());
    }
}
