package br.com.xbrain.autenticacao.modules.usuario.dto;

import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import lombok.*;
import org.springframework.beans.BeanUtils;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
        request.setNivel(usuario.getCargo() != null
            && usuario.getCargo().getNivel() != null
            && usuario.getCargo().getNivel().getCodigo() != null
            ? usuario.getCargo().getNivel().getCodigo().toString()
            : null);
        return request;
    }
}
