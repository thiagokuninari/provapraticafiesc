package br.com.xbrain.autenticacao.modules.comum.dto;

import br.com.xbrain.autenticacao.modules.comum.model.SubCluster;
import lombok.Data;
import org.springframework.beans.BeanUtils;

@Data
public class SubClusterDto {

    private Integer id;
    private String nome;

    public SubClusterDto() {
    }

    public SubClusterDto(Integer id, String nome) {
        this.id = id;
        this.nome = nome;
    }

    public static SubClusterDto objectToDto(SubCluster subCluster) {
        SubClusterDto dto = new SubClusterDto();
        BeanUtils.copyProperties(subCluster, dto);
        return dto;
    }
}
