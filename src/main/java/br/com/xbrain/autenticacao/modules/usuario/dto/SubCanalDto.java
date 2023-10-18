package br.com.xbrain.autenticacao.modules.usuario.dto;

import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.comum.enums.Eboolean;
import br.com.xbrain.autenticacao.modules.usuario.enums.ETipoCanal;
import br.com.xbrain.autenticacao.modules.usuario.model.SubCanal;
import com.fasterxml.jackson.annotation.JsonInclude;
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

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(NON_NULL)
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

    public static Set<SubCanalDto> of(Collection<SubCanal> subcanais, String... camposExcluidos) {
        return subcanais
            .stream()
            .map(subcanal -> of(subcanal, camposExcluidos))
            .collect(Collectors.toSet());
    }

    public static SubCanalDto of(SubCanal subcanal, String... camposExcluidos) {
        var response = new SubCanalDto();
        BeanUtils.copyProperties(subcanal, response, camposExcluidos);
        return response;
    }
}
