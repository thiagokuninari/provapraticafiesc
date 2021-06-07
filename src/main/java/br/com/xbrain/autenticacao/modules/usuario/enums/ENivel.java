package br.com.xbrain.autenticacao.modules.usuario.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum ENivel {

    RECEPTIVO(8),
    AGENTE_AUTORIZADO(3);

    @Getter
    int id;

}
