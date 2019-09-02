package br.com.xbrain.autenticacao.modules.usuario.dto;

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

    public static UsuarioAutoComplete of(UsuarioSubordinadoDto usuarioSubordinado) {
        return UsuarioAutoComplete.builder()
                .value(usuarioSubordinado.getId())
                .text(usuarioSubordinado.getNome())
                .build();
    }

    public static UsuarioAutoComplete of(UsuarioResponse usuarioResponse) {
        return UsuarioAutoComplete.builder()
                .value(usuarioResponse.getId())
                .text(usuarioResponse.getNome())
                .build();
    }
}
