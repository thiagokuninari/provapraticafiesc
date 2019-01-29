package br.com.xbrain.autenticacao.modules.comum.enums;

public enum ESituacao {

    A("Ativo"), I("Inativo"),  P("Pendente"), R("Realocado");

    private String descricao;

    ESituacao(String descricao) {
        this.descricao = descricao;
    }
}
