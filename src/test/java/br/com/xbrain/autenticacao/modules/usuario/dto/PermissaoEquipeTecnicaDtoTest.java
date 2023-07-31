package br.com.xbrain.autenticacao.modules.usuario.dto;

import org.junit.Test;

import static br.com.xbrain.autenticacao.modules.usuario.helpers.PermissaoEquipeTecnicaHelper.permissaoEquipeTecnicaDto;
import static org.assertj.core.api.Assertions.assertThat;

public class PermissaoEquipeTecnicaDtoTest {

    @Test
    public void hasEquipeTecnica_deveRetornarTrue_quandoDtoPossuirFlagEquipeTecnicaTrue() {
        assertThat(permissaoEquipeTecnicaDto(true, null).hasEquipeTecnica())
            .isTrue();
    }

    @Test
    public void hasEquipeTecnica_deveRetornarFalse_quandoDtoNaoPossuirFlagEquipeTecnicaTrue() {
        assertThat(permissaoEquipeTecnicaDto(false, null).hasEquipeTecnica())
            .isFalse();
    }
}