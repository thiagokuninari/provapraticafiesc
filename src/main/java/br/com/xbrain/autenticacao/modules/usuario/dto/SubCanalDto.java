package br.com.xbrain.autenticacao.modules.usuario.dto;

import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.usuario.enums.ETipoCanal;
import br.com.xbrain.autenticacao.modules.usuario.model.SubCanal;
import lombok.*;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubCanalDto implements Serializable {

    private static final long serialVersionUID = 1823163552545794339L;

    private Integer id;
    private ETipoCanal codigo;
    private String nome;
    private ESituacao situacao;

    public static Set<SubCanalDto> of(Collection<SubCanal> subcanais) {
        return subcanais.stream().map(SubCanalDto::of).collect(Collectors.toSet());
    }

    public static SubCanalDto of(SubCanal subcanal) {
        var response = new SubCanalDto();
        BeanUtils.copyProperties(subcanal, response);
        return response;
    }
}
