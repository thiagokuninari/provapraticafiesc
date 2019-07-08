package br.com.xbrain.autenticacao.modules.comum.dto;

import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.comum.model.Regional;
import lombok.Data;
import org.springframework.beans.BeanUtils;

@Data
public class RegionalDto {

    private Integer id;
    private String nome;
    private ESituacao situacao;

    public static RegionalDto of(Regional regional) {
        RegionalDto regionalDto = new RegionalDto();
        BeanUtils.copyProperties(regional, regionalDto);
        return regionalDto;
    }

}
