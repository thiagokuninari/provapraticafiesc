package br.com.xbrain.autenticacao.modules.usuario.dto;

import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioSocialHubRequestMq {

    private Integer id;
    private String nome;
    private String email;
    private String nivel;
    private String cargo;
    private List<Integer> regionaisIds;
    private Integer territorioMercadoDesenvolvimentoId;
    private boolean isPermissaoAdmSocialRemovida;

    public static UsuarioSocialHubRequestMq from(Usuario usuario, List<Integer> regionaisIds, String nomeCargo,
                                                 boolean isPermissaoAdmSocialRemovida) {
        var request = new UsuarioSocialHubRequestMq();
        BeanUtils.copyProperties(usuario, request);
        request.setCargo(nomeCargo);
        request.setNivel(usuario.getNivelCodigo() != null ? usuario.getNivelCodigo().toString() : null);
        request.setRegionaisIds(regionaisIds);
        request.setTerritorioMercadoDesenvolvimentoId(usuario.getTerritorioMercadoDesenvolvimentoIdOrNull());
        request.setPermissaoAdmSocialRemovida(isPermissaoAdmSocialRemovida);
        return request;
    }

    public static UsuarioSocialHubRequestMq from(Integer usuarioId, boolean isPermissaoAdmSocialRemovida) {
        var request = new UsuarioSocialHubRequestMq();
        request.setId(usuarioId);
        request.setPermissaoAdmSocialRemovida(isPermissaoAdmSocialRemovida);
        return request;
    }
}
