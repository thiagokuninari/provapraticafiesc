package br.com.xbrain.autenticacao.modules.organizacaoempresa.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum EModalidadeEmpresa {

    PAP("PAP"),
    TELEVENDAS("TELEVENDAS");

    private String descricao;
}
