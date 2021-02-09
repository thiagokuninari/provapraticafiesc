package br.com.xbrain.autenticacao.modules.usuario.helpers;

import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioResponse;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo;

public class UsuarioResponseHelper {

    public static UsuarioResponse umUsuarioResponse(Integer id, String nome, ESituacao situacao, CodigoCargo codigoCargo) {
        return UsuarioResponse
            .builder()
            .id(id)
            .nome(nome)
            .situacao(situacao)
            .codigoCargo(codigoCargo)
            .build();
    }

    public static UsuarioResponse doisUsuarioResponse(Integer id, String nome, String nomeCargo, CodigoCargo codigoCargo) {
        return UsuarioResponse
            .builder()
            .id(id)
            .nome(nome)
            .nomeCargo(nomeCargo)
            .codigoCargo(codigoCargo)
            .build();
    }
}
