package br.com.xbrain.autenticacao.modules.comum.util;

import org.springframework.util.ObjectUtils;

public class CnpjUtil {

    private static final int CNPJ_8_LENGTH = 8;

    public static String formataCnpj(String cnpj) {
        if (ObjectUtils.isEmpty(cnpj)) {
            return "";
        }
        if (cnpj.length() == CNPJ_8_LENGTH) {
            return cnpj.replaceFirst("(\\d{2})(\\d{3})(\\d{3})", "$1.$2.$3");

        } else {
            return cnpj.replaceFirst("(\\d{2})(\\d{3})(\\d{3})(\\d{4})(\\d{2})", "$1.$2.$3/$4-$5");
        }
    }
}
