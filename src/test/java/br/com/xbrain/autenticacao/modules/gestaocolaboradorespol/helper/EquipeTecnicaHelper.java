package br.com.xbrain.autenticacao.modules.gestaocolaboradorespol.helper;

import br.com.xbrain.autenticacao.modules.gestaocolaboradorespol.dto.EquipeTecnicaSupervisionadasResponse;

import java.util.List;

public class EquipeTecnicaHelper {

    public static EquipeTecnicaSupervisionadasResponse umaEquipeTecnicaSupervisionadasResponse() {
        return EquipeTecnicaSupervisionadasResponse
            .builder()
            .id(1)
            .descricao("EQUIPE TECNICA 1")
            .build();
    }

    public static EquipeTecnicaSupervisionadasResponse outraEquipeTecnicaSupervisionadasResponse() {
        return EquipeTecnicaSupervisionadasResponse
            .builder()
            .id(2)
            .descricao("EQUIPE TECNICA 2")
            .build();
    }

    public static List<EquipeTecnicaSupervisionadasResponse> umaListaEquipeTecnicaSupervisionada() {
        return List.of(
            umaEquipeTecnicaSupervisionadasResponse(),
            outraEquipeTecnicaSupervisionadasResponse()
        );
    }
}
