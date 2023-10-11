package br.com.xbrain.autenticacao.modules.usuario.dto;

import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.comum.enums.Eboolean;
import br.com.xbrain.autenticacao.modules.usuario.enums.ETipoCanal;
import br.com.xbrain.autenticacao.modules.usuario.model.SubCanal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.beans.BeanUtils;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubCanalDto implements Serializable {

    @NotNull
    private Integer id;
    @NotNull
    private ETipoCanal codigo;
    @NotBlank
    private String nome;
    @NotNull
    private ESituacao situacao;
    @NotNull
    private Eboolean novaChecagemCredito;

    public static Set<SubCanalDto> of(Collection<SubCanal> subcanais) {
        return subcanais
            .stream()
            .map(SubCanalDto::of)
            .collect(Collectors.toSet());
    }

    public static SubCanalDto of(SubCanal subcanal) {
        var response = new SubCanalDto();
        BeanUtils.copyProperties(subcanal, response);
        return response;
    }
}
