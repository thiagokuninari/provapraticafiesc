package br.com.xbrain.autenticacao.modules.usuario.dto;

import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioAutoComplete {

    private Integer value;
    private String text;

    public static UsuarioAutoComplete of(Usuario usuario) {
        return UsuarioAutoComplete.builder()
            .value(usuario.getId())
            .text(usuario.getNome())
            .build();
    }
}
