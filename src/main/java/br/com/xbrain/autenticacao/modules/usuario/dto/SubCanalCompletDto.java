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

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubCanalCompletDto implements Serializable {

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

    public static SubCanalCompletDto of(SubCanal subcanal) {
        var response = new SubCanalCompletDto();
        BeanUtils.copyProperties(subcanal, response);
        return response;
    }
}
