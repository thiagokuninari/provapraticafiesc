package br.com.xbrain.autenticacao.modules.usuario.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public enum CodigoDepartamento {

    AGENTE_AUTORIZADO,
    COMERCIAL(3),
    HELP_DESK,
    ADMINISTRADOR,
    VAREJO,
    ATENDIMENTO_JEC,
    FEEDER,
    OUVIDORIA,
    INTEGRACAO,
    TREINAMENTO;

    @Getter
    int codigo;
}
