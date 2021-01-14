package br.com.xbrain.autenticacao.modules.comum.util;

import java.util.Optional;

public class ObjectUtil {

    public static Integer toInteger(Object valor) {
        return Optional.ofNullable(valor)
            .map(Object::toString)
            .map(Integer::parseInt)
            .orElse(null);
    }

    public static String toString(Object valor) {
        return Optional.ofNullable(valor)
            .map(Object::toString)
            .orElse(null);
    }
}
