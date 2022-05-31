package br.com.xbrain.autenticacao.modules.usuario.dto;

import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.usuario.enums.ETipoCanal;
import br.com.xbrain.autenticacao.modules.usuario.model.SubCanal;
import lombok.*;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubCanalDto implements Serializable {

    private Integer id;
    private ETipoCanal codigo;
    private String nome;
    private ESituacao situacao;

    public static List<SubCanalDto> of(List<SubCanal> subcanais) {
        return subcanais.stream()
            .map(subcanal -> SubCanalDto.of(subcanal))
            .collect(Collectors.toList());
    }

    public static SubCanalDto of(SubCanal subcanal) {
        var response = new SubCanalDto();
        BeanUtils.copyProperties(subcanal, response);
        return response;
    }
}
