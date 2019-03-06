package br.com.xbrain.autenticacao.modules.usuario.enums;

public enum CodigoHierarquia {


    REGIONAL("Regional"),
    GRUPO("Grupo"),
    CLUSTER("Cluster"),
    SUBCLUSTER("Subcluster"),
    CIDADE("Cidade");

    private String hierarquia;

    CodigoHierarquia(String hierarquia) {
        this.hierarquia = hierarquia;
    }
}
