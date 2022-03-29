package br.com.xbrain.autenticacao.modules.usuario.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum CodigoCargoOperacao {

    SUPERVISOR_OPERACAO(10),
    ASSISTENTE_OPERACAO(2),
    VENDEDOR_OPERACAO(8),
    COORDENADOR_OPERACAO(4);

    @Getter
    int codigo;

}
