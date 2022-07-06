package br.com.xbrain.autenticacao.modules.usuario.dto;

import br.com.xbrain.autenticacao.modules.comum.model.Uf;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UfResponse {

    private Integer id;
    private String nome;
    private String uf;

    public static UfResponse parse(Uf request) {
        UfResponse response = new UfResponse();
        BeanUtils.copyProperties(request, response);
        return response;
    }
}
