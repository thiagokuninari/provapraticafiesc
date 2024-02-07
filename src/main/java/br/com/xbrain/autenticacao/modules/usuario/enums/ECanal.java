package br.com.xbrain.autenticacao.modules.usuario.enums;

import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum ECanal {

    AGENTE_AUTORIZADO("Agente autorizado", Boolean.TRUE),
    D2D_PROPRIO("D2D Próprio", Boolean.TRUE),
    ATIVO_PROPRIO("Ativo Local Próprio", Boolean.TRUE),
    ATIVO("Ativo", Boolean.FALSE),
    ATP("Atp", Boolean.FALSE),
    VAREJO("Varejo", Boolean.TRUE),
    INTERNET("Internet", Boolean.TRUE);

    @Getter
    private String descricao;
    @Getter
    private boolean ativo;

    ECanal(String descricao, Boolean ativo) {
        this.ativo = ativo;
        this.descricao = descricao;
    }

    public static List<ECanal> getCanaisAtivos() {
        return Stream.of(ECanal.values())
            .filter(ECanal::isAtivo)
            .collect(Collectors.toList());
    }
}
