package br.com.xbrain.autenticacao.modules.usuario.dto;

import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioNomeResponse {

    private Integer id;
    private String nome;

    public static UsuarioNomeResponse of(Usuario usuario) {
        return new UsuarioNomeResponse(usuario.getId(), usuario.getNome());
    }
}
