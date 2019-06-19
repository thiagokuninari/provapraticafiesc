package br.com.xbrain.autenticacao.modules.comum.enums;

import lombok.Getter;

public enum ESituacao {

    A("Ativo"), I("Inativo"), P("Pendente"), R("Realocado");

    @Getter
    private String descricao;

    ESituacao(String descricao) {
        this.descricao = descricao;
    }
}
