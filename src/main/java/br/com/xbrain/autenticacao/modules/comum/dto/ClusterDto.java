package br.com.xbrain.autenticacao.modules.comum.dto;

import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.comum.model.Cluster;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClusterDto {

    private Integer id;
    private String nome;
    private GrupoDto grupo;
    private ESituacao situacao;

    public static ClusterDto of(Cluster cluster) {
        ClusterDto dto = new ClusterDto();
        BeanUtils.copyProperties(cluster, dto);
        dto.setGrupo(getGrupo(cluster));
        return dto;
    }

    private static GrupoDto getGrupo(Cluster cluster) {
        return Objects.nonNull(cluster.getGrupo())
            ? GrupoDto.of(cluster.getGrupo())
            : null;
    }
}
