package br.com.xbrain.autenticacao.modules.comum.dto;

import br.com.xbrain.autenticacao.modules.comum.model.Cluster;
import lombok.Data;
import org.springframework.beans.BeanUtils;

@Data
public class ClusterDto {

    private Integer id;
    private String nome;

    public ClusterDto() {
    }

    public ClusterDto(Integer id, String nome) {
        this.id = id;
        this.nome = nome;
    }

    public static ClusterDto objectToDto(Cluster cluster) {
        ClusterDto dto = new ClusterDto();
        BeanUtils.copyProperties(cluster, dto);
        return dto;
    }

}
