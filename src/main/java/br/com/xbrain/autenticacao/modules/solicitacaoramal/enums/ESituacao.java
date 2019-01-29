package br.com.xbrain.autenticacao.modules.solicitacaoramal.enums;

public enum ESituacao {
    PD("PENDENTE"),
    EA("EM_ANDAMENTO"),
    CD("CONCLUIDO"),
    RJ("REJEITADO");

    private String descricao;

    ESituacao(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
