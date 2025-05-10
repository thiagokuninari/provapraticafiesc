package com.provapratica.api.dto;

import com.provapratica.api.domain.Nivel;
import com.provapratica.api.enums.ENivelUsuario;
import com.provapratica.api.enums.EStatus;
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
    private String codigo;

    public static NivelResponse of(Nivel nivel) {
        var nivelResponse = new NivelResponse();
        if (nivel != null) {
            BeanUtils.copyProperties(nivel, nivelResponse);
            nivelResponse.setNome(nivel.getNome().toUpperCase());
            nivelResponse.setCodigo(nivel.getCodigo().name());
        }
        return nivelResponse;
    }
}
