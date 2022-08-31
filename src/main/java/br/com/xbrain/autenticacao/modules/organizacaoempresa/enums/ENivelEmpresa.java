package br.com.xbrain.autenticacao.modules.organizacaoempresa.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ENivelEmpresa {

    VAREJO("VAREJO"),
    RECEPTIVO("RECEPTIVO");

    private String descricao;
}
