package br.com.xbrain.autenticacao.modules.importacaousuario.util;

import org.springframework.util.StringUtils;

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

    public static Boolean validarEmail(String email) {
        if (!StringUtils.isEmpty(email)) {
            var pattern = Pattern.compile("^[\\w-]+(\\.[\\w-]+)*@([\\w-]+\\.)+[a-zA-Z]{2,3}$");
            var matcher = pattern.matcher(email.trim());

            return matcher.find();
        }
        return false;
    }
}
