package br.com.xbrain.autenticacao.modules.gestaocolaboradorespol.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EquipeTecnicaSupervisionadasResponse {

    private Integer id;
    private String descricao;
}
