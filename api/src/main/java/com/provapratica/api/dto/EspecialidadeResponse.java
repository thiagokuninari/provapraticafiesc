package com.provapratica.api.dto;

import com.provapratica.api.domain.Especialidade;
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
public class EspecialidadeResponse {

    private Integer id;
    private String nomeEspecialidade;
    private String codigoEspecialidade;
    private EStatus statusEspecialidade;

    public static EspecialidadeResponse of(Especialidade especialidade) {
        var especialidadeResponse = new EspecialidadeResponse();
        if (especialidade != null) {
            BeanUtils.copyProperties(especialidade, especialidadeResponse);
            especialidadeResponse.setNomeEspecialidade(especialidade.getNomeEspecialidade().toUpperCase());
            especialidadeResponse.setCodigoEspecialidade(especialidade.getCodigoEspecialidade());
        }
        return especialidadeResponse;
    }
}
