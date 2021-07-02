package br.com.xbrain.autenticacao.modules.usuario.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioRequest {

    private List<Integer> usuarioIds;

    public static UsuarioRequest of(List<Integer> usuarioIds) {
        var usuarioRequest = new UsuarioRequest();
        usuarioRequest.setUsuarioIds(usuarioIds);
        return usuarioRequest;
    }

}

