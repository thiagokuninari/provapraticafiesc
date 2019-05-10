package br.com.xbrain.autenticacao.modules.comum.dto;

import br.com.xbrain.autenticacao.modules.comum.model.SubCluster;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class SubClusterDto {

    private Integer id;
    private String nome;

    public static SubClusterDto of(SubCluster subCluster) {
        return SubClusterDto.builder()
                .id(subCluster.getId())
                .nome(subCluster.getNomeComMarca())
                .build();
    }
}
