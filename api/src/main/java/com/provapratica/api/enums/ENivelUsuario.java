package com.provapratica.api.enums;

public enum ENivelUsuario {

    ADMIN("Administrador"),
    PROFESSOR("Professor"),
    ESTUDANTE("Estudante");

    private final String descricao;

    ENivelUsuario(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
