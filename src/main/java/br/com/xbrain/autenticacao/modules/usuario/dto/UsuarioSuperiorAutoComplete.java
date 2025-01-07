package br.com.xbrain.autenticacao.modules.usuario.dto;

import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UsuarioSuperiorAutoComplete {

    private Integer id;
    private String nome;
    private String email;
    private String cargo;

    public static UsuarioSuperiorAutoComplete of(Usuario usuario) {
        var usuarioSuperiorAutoComplete = new UsuarioSuperiorAutoComplete();
        BeanUtils.copyProperties(usuario, usuarioSuperiorAutoComplete);
        usuarioSuperiorAutoComplete.setCargo(usuario.getCargo().getNome());
        return usuarioSuperiorAutoComplete;
    }
}
