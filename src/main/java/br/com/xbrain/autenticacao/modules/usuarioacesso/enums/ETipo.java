package br.com.xbrain.autenticacao.modules.usuarioacesso.enums;

import lombok.Getter;

@Getter
public enum ETipo {

    LOGIN("LOGIN"),
    LOGOUT("LOGOUT");

    private String descricao;

    ETipo(String descricao) {
        this.descricao = descricao;
    }

}
