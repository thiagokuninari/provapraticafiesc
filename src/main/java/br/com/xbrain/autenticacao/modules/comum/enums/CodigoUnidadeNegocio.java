package br.com.xbrain.autenticacao.modules.comum.enums;

import lombok.Getter;

public enum CodigoUnidadeNegocio {

    PESSOAL("Pessoal"),
    RESIDENCIAL_COMBOS("Residencial e Combos"),
    XBRAIN("X-Brain"),
    CLARO_RESIDENCIAL("Claro Residencial");

    @Getter
    private final String descricao;

    CodigoUnidadeNegocio(String descricao) {
        this.descricao = descricao;
    }

}
