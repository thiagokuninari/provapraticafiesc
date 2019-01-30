package br.com.xbrain.autenticacao.modules.comum.util;

public class CnpjUtil {

    public static String formataCnpj(String cnpj) {
        if (cnpj == null) {
            return "";
        }
        if (cnpj.length() == NumberUtil.OITO) {
            return cnpj.replaceFirst("(\\d{2})(\\d{3})(\\d{3})", "$1.$2.$3");

        } else {
            return cnpj.replaceFirst("(\\d{2})(\\d{3})(\\d{3})(\\d{4})(\\d{2})", "$1.$2.$3/$4-$5");
        }
    }
}
