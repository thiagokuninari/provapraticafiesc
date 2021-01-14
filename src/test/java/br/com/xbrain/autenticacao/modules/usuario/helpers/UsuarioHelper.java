package br.com.xbrain.autenticacao.modules.usuario.helpers;

import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo;

import java.math.BigDecimal;

public class UsuarioHelper {

    private static final int COLUNA_USUARIO_ID = 0;
    private static final int COLUNA_USUARIO_NOME = 1;
    private static final int COLUNA_USUARIO_EMAIL = 2;
    private static final int COLUNA_CARGO_NOME = 3;
    private static final int COLUNA_CARGO_CODIGO = 4;
    private static final int QTDE_COLUNAS = 5;

    public static Object[] umUsuarioObjectArray(BigDecimal id, String email, String nome,
                                                String cargoNome, CodigoCargo codigoCargo) {
        var usuario = new Object[QTDE_COLUNAS];

        usuario[COLUNA_USUARIO_ID] = id;
        usuario[COLUNA_USUARIO_NOME] = nome;
        usuario[COLUNA_USUARIO_EMAIL] = email;
        usuario[COLUNA_CARGO_NOME] = cargoNome;
        usuario[COLUNA_CARGO_CODIGO] = codigoCargo.name();

        return usuario;
    }

    public static Object[] doisUsuarioObjectArray(Integer id, String nome, CodigoCargo codigoCargo) {
        var usuario = new Object[QTDE_COLUNAS];

        usuario[COLUNA_USUARIO_ID] = id;
        usuario[COLUNA_USUARIO_NOME] = nome;
        usuario[COLUNA_CARGO_CODIGO] = codigoCargo.name();

        return usuario;
    }
}
