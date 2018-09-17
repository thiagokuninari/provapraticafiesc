package br.com.xbrain.autenticacao.modules.importacaousuario.util;

import java.util.regex.Pattern;

public class EmailUtil {
    public static boolean validar(String email) {
        if (email == null) {
            return false;
        }
        return Pattern
                .compile("^(.+)@(.+)$")
                .matcher(email)
                .matches();
    }
}
