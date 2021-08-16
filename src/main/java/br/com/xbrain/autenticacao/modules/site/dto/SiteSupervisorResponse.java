package br.com.xbrain.autenticacao.modules.site.dto;

import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioSubordinadoDto;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SiteSupervisorResponse {

    private Integer id;
    private String nome;
    private ESituacao situacao;
    private List<Integer> coordenadoresIds;

    public static SiteSupervisorResponse of(Usuario usuarioSupervisor) {
        var supervisorResponse = new SiteSupervisorResponse();
        BeanUtils.copyProperties(usuarioSupervisor, supervisorResponse);
        return supervisorResponse;
    }

    public static SiteSupervisorResponse of(UsuarioSubordinadoDto usuario) {
        var supervisorResponse = new SiteSupervisorResponse();
        BeanUtils.copyProperties(usuario, supervisorResponse);
        return supervisorResponse;
    }

    public static SiteSupervisorResponse of(Usuario usuarioSupervisor, List<Integer> coordenadoresIds) {
        var supervisorResponse = of(usuarioSupervisor);
        supervisorResponse.setCoordenadoresIds(coordenadoresIds);
        return supervisorResponse;
    }
}
