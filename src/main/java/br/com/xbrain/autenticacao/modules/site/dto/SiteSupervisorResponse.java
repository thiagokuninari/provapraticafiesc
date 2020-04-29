package br.com.xbrain.autenticacao.modules.site.dto;

import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SiteSupervisorResponse {

    private Integer id;
    private String nome;

    public static SiteSupervisorResponse of(Usuario usuarioSupervisor) {
        var supervisorResponse = new SiteSupervisorResponse();
        BeanUtils.copyProperties(usuarioSupervisor, supervisorResponse);
        return supervisorResponse;
    }
}
