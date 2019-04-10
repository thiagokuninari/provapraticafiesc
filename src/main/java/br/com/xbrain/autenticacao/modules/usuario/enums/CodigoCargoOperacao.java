package br.com.xbrain.autenticacao.modules.usuario.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum CodigoCargoOperacao {

    SUPERVISOR_OPERACAO(10),
    ASSISTENTE_OPERACAO(2),
    VENDEDOR_OPERACAO(8);

    @Getter
    int codigo;

}
