package br.com.xbrain.autenticacao.modules.usuario.helpers;

import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoNivel;
import br.com.xbrain.autenticacao.modules.usuario.model.Cargo;
import br.com.xbrain.autenticacao.modules.usuario.model.Nivel;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;

public class UsuarioHelper {

    public static Usuario umUsuario(Integer id, String nome, ESituacao situacao,
                                    CodigoCargo codigoCargo, CodigoNivel codigoNivel) {
        return Usuario
            .builder()
            .id(id)
            .nome(nome)
            .situacao(situacao)
            .cargo(Cargo
                .builder()
                .codigo(codigoCargo)
                .nivel(Nivel
                    .builder()
                    .codigo(codigoNivel)
                    .build())
                .build())
            .build();
    }

    public static Usuario doisUsuario(Integer id, String nome, ESituacao situacao) {
        return Usuario
            .builder()
            .id(id)
            .nome(nome)
            .situacao(situacao)
            .build();
    }
}
