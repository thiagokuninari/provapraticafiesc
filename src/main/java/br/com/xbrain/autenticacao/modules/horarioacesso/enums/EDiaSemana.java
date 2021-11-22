package br.com.xbrain.autenticacao.modules.horarioacesso.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum EDiaSemana {
    
    DOMINGO(1, "DOMINGO", "Domingo"),
    SEGUNDA(2, "SEGUNDA", "Segunda-Feira"),
    TERCA(3, "TERÇA", "Terça-Feira"),
    QUARTA(4, "QUARTA", "Quarta-Feira"),
    QUINTA(5, "QUINTA", "Quinta-Feira"),
    SEXTA(6, "SEXTA", "Sexta-Feira"),
    SABADO(7, "SÁBADO", "Sábado");

    private Integer codigo;
    private String descricao;
    private String nomeCompleto;
}
