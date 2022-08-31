package br.com.xbrain.autenticacao.modules.organizacaoempresa.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ESituacaoOrganizacaoEmpresa {

    A("Ativo"), I("Inativo");

    private String descricao;
}
