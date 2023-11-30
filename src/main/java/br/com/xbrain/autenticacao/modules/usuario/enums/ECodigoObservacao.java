package br.com.xbrain.autenticacao.modules.usuario.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ECodigoObservacao {

    IFA("Usuário inativado por falta de acesso"),
    ITL("Usuário inativo devido ao erro excessivo de senha");

    private final String observacao;
}
