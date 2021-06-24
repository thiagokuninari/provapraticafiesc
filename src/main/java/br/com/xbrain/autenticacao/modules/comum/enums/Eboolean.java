package br.com.xbrain.autenticacao.modules.comum.enums;

import org.springframework.util.ObjectUtils;

public enum Eboolean {

    V("Verdadeiro"), F("Falso");

    private String descricao;

    Eboolean(String descricao) {
        this.descricao = descricao;
    }

    public static Eboolean valueOf(Boolean arg) {
        return !ObjectUtils.isEmpty(arg) && arg ? Eboolean.V : Eboolean.F;
    }
}
