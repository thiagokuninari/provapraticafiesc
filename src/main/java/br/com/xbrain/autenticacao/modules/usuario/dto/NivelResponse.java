package br.com.xbrain.autenticacao.modules.usuario.dto;

import br.com.xbrain.autenticacao.modules.usuario.model.Nivel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NivelResponse {

    private Integer id;
    private String nome;

    public static NivelResponse of(Nivel nivel) {
        var nivelResponse = new NivelResponse();
        if (nivel != null) {
            BeanUtils.copyProperties(nivel, nivelResponse);
        }
        return nivelResponse;
    }
}
