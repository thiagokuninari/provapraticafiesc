package br.com.xbrain.autenticacao.modules.usuario.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
public enum ENivel {

    RECEPTIVO(8,11),
    AGENTE_AUTORIZADO(3,12);

    @Getter
    List<Integer> ids;

    ENivel(int i, int i1) {

    }
}
