package br.com.xbrain.autenticacao.modules.usuario.helpers;

import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;

public class UsuarioHelper {

    public static Usuario doisUsuario(Integer id, String nome, ESituacao situacao) {
        return Usuario
            .builder()
            .id(id)
            .nome(nome)
            .situacao(situacao)
            .build();
    }
}
