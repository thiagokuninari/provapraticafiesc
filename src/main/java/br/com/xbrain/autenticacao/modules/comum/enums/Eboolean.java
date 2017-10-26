package br.com.xbrain.autenticacao.modules.comum.enums;

public enum Eboolean {

    V("Verdadeiro"), F("Falso");

    private String descricao;

    Eboolean(String descricao) {
        this.descricao = descricao;
    }
}
