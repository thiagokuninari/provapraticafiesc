package br.com.xbrain.autenticacao.modules.solicitacaoramal.enums;

public enum ESituacao {
    PD("PENDENTE"),
    EA("EM ANDAMENTO"),
    CD("CONCLUIDO"),
    RJ("REJEITADO");

    private String descricao;

    ESituacao(String descricao) {
        this.descricao = descricao;
    }
}
