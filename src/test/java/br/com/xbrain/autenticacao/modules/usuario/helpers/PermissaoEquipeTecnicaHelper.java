package br.com.xbrain.autenticacao.modules.usuario.helpers;

import br.com.xbrain.autenticacao.modules.usuario.dto.PermissaoEquipeTecnicaDto;

import java.util.Collections;
import java.util.List;

public class PermissaoEquipeTecnicaHelper {

    public static PermissaoEquipeTecnicaDto permissaoEquipeTecnicaDto(boolean hasEquipeTecnica,
                                                                      List<Integer> sociosSecundariosIds) {
        return PermissaoEquipeTecnicaDto.builder()
            .agenteAutorizadoId(1)
            .usuarioProprietarioId(100)
            .sociosSecundariosIds(sociosSecundariosIds == null
                ? Collections.emptyList()
                : sociosSecundariosIds)
            .usuarioCadastroId(105)
            .hasEquipeTecnica(hasEquipeTecnica)
            .socioDeOutroAaComPermissaoEquipeTecnica(false)
            .build();
    }
}
