package br.com.xbrain.autenticacao.modules.feriado.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ESituacaoFeriadoAutomacao {

    EM_IMPORTACAO("EM IMPORTAÇÃO"),
    IMPORTADO("IMPORTADO");

    private String descricao;
}
