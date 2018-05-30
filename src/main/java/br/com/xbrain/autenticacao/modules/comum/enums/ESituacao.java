package br.com.xbrain.autenticacao.modules.comum.enums;

public enum ESituacao {

    A("Ativo"), I("Inativo"),  P("Pendente");

    private String descricao;

    ESituacao(String descricao) {
        this.descricao = descricao;
    }
}
