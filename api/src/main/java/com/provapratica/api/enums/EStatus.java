package com.provapratica.api.enums;

public enum EStatus {
    ATIVO("Ativo"),
    INATIVO("Inativo");

    private final String descricao;

    EStatus(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
