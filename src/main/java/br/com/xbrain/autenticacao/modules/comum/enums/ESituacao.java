package br.com.xbrain.autenticacao.modules.comum.enums;

import lombok.Getter;

import java.util.List;

public enum ESituacao {

    A("Ativo"), I("Inativo"), P("Pendente"), R("Realocado");

    @Getter
    private String descricao;

    ESituacao(String descricao) {
        this.descricao = descricao;
    }

    public static List<ESituacao> getOnlyAtivoInativo() {
        return List.of(A, I);
    }
}
