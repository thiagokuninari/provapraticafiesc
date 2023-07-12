package br.com.xbrain.autenticacao.modules.usuario.dto;

import br.com.xbrain.autenticacao.modules.usuario.enums.ETipoCanal;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UsuarioSubCanalDto {

    private String nomeUsuario;
    private ETipoCanal subCanal;

    public static UsuarioSubCanalDto of(String nomeUsuario, ETipoCanal subCanal) {
        return new UsuarioSubCanalDto(nomeUsuario, subCanal);
    }
}
