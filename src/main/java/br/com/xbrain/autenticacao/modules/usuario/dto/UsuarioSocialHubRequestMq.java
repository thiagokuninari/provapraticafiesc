package br.com.xbrain.autenticacao.modules.usuario.dto;

import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import lombok.Data;
import org.springframework.beans.BeanUtils;

@Data
public class UsuarioSocialHubRequestMq {

    private Integer id;
    private String nome;
    private String email;
    private String nivel;
    private String cargo;

    public static UsuarioSocialHubRequestMq from(Usuario usuario) {
        var request = new UsuarioSocialHubRequestMq();
        BeanUtils.copyProperties(usuario, request);
        request.setCargo(usuario.getCargoCodigo() != null ? usuario.getCargoCodigo().toString() : null);
        request.setNivel(usuario.getNivelCodigo() != null ? usuario.getNivelCodigo().toString() : null);
        return request;
    }
}
