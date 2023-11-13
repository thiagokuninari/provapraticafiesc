package br.com.xbrain.autenticacao.modules.usuario.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum EObservacaoHistorico {

    ALTERACAO_CADASTRO("Alteração nos dados de cadastro do usuário."),
    ATIVACAO_POL("Ativação do usuário pelo Parceiros Online."),
    ALTERACAO_CPF("Alteração de CPF do usuário."),
    REMANEJAMENTO("Remanejamento de usuário para outro Agente Autorizado."),
    INATIVACAO_AA("Inativado pelo Agente Autorizado."),
    INATIVACAO_ORGANIZACAO("Inativado pela organização inativa.");

    @Getter
    private String observacao;
}
