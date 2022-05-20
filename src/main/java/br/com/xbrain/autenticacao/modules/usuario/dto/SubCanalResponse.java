package br.com.xbrain.autenticacao.modules.usuario.dto;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;

import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.usuario.model.SubCanal;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubCanalResponse {
    
    private Integer id;
    private String codigo;
    private String nome;
    private ESituacao situacao;

    public static List<SubCanalResponse> of(List<SubCanal> subcanais) {
        return subcanais.stream()
            .map(subcanal -> SubCanalResponse.of(subcanal))
            .collect(Collectors.toList());
    }

    public static SubCanalResponse of(SubCanal subcanal) {
        var response = new SubCanalResponse();
        BeanUtils.copyProperties(subcanal, response);
        return response;
    }
}
