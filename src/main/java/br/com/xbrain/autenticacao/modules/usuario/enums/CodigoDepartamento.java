package br.com.xbrain.autenticacao.modules.usuario.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public enum CodigoDepartamento {

    AGENTE_AUTORIZADO(40),
    COMERCIAL(3),
    HELP_DESK(51),
    ADMINISTRADOR(50),
    VAREJO(54),
    ATENDIMENTO_JEC(64),
    ATENDIMENT0_JEC_DOIS(65),
    FEEDER(68),
    OUVIDORIA(66),
    INTEGRACAO(1001),
    TREINAMENTO(12),
    TREINAMENTO_DOIS(30);


    @Getter
    int codigo;
}
