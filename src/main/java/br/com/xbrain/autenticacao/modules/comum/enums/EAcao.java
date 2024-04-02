package br.com.xbrain.autenticacao.modules.comum.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum EAcao {
    ATIVACAO("Ativo"),
    INATIVACAO("Inativo"),
    CADASTRO("Cadastrado"),
    ATUALIZACAO("Atualizado");

    private final String descricao;
}
