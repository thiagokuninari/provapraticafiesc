package br.com.xbrain.autenticacao.modules.comum.dto;

import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.comum.model.SubCluster;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class SubClusterDto {

    private Integer id;
    private String nome;
    private ClusterDto cluster;
    private ESituacao situacao;

    public static SubClusterDto of(SubCluster subCluster) {
        return SubClusterDto.builder()
            .id(subCluster.getId())
            .nome(subCluster.getNomeComMarca())
            .cluster(getCluster(subCluster))
            .situacao(subCluster.getSituacao())
            .build();
    }

    public static List<SubClusterDto> of(List<SubCluster> subclusters) {
        return subclusters
                .stream()
                .map(SubClusterDto::of)
                .collect(Collectors.toList());
    }

    private static ClusterDto getCluster(SubCluster subCluster) {
        return Objects.nonNull(subCluster.getCluster())
            ? ClusterDto.of(subCluster.getCluster())
            : null;
    }
}
