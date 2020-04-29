package br.com.xbrain.autenticacao.modules.site.dto;

import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import lombok.Data;
import org.springframework.beans.BeanUtils;

@Data
public class SiteSupervisorResponse {

    private Integer id;
    private String nome;

    // todo test
    public static SiteSupervisorResponse of(Usuario usuarioSupervisor) {
        var supervisorResponse = new SiteSupervisorResponse();
        BeanUtils.copyProperties(usuarioSupervisor, supervisorResponse);
        return supervisorResponse;
    }
}
