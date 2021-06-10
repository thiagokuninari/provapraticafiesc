package br.com.xbrain.autenticacao.modules.usuario.enums;

import lombok.Getter;

import java.util.List;

public enum ENivel {

    RECEPTIVO(List.of(8,11)),
    AGENTE_AUTORIZADO(List.of(3,12));

    @Getter
    List<Integer> ids;

    ENivel(List<Integer> integerList) {
        this.ids = integerList;
    }
}
