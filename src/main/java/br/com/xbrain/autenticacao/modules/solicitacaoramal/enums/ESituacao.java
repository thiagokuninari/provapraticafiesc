package br.com.xbrain.autenticacao.modules.solicitacaoramal.enums;

public enum ESituacao {
    PD("Pendente"),
    EA("Em Andamento"),
    CD("Concluido"),
    RJ("Rejeitado");

    private String descricao;

    ESituacao(String descricao) {
        this.descricao = descricao;
    }
}
