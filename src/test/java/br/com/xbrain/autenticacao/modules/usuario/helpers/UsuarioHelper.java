package br.com.xbrain.autenticacao.modules.usuario.helpers;

import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;

public class UsuarioHelper {

    public static Usuario doisUsuario(Integer id, String nome) {
        return Usuario
            .builder()
            .id(id)
            .nome(nome)
            .build();
    }
}
