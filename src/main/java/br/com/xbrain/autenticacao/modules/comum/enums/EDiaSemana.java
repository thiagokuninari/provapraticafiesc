package br.com.xbrain.autenticacao.modules.comum.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum EDiaSemana {
    
    DOMINGO(1, "DOMINGO", "Domingo"),
    SEGUNDA(2, "SEGUNDA", "Segunda-feira"),
    TERCA(3, "TERÇA", "Terça-feira"),
    QUARTA(4, "QUARTA", "Quarta-feira"),
    QUINTA(5, "QUINTA", "Quinta-feira"),
    SEXTA(6, "SEXTA", "Sexta-feira"),
    SABADO(7, "SÁBADO", "Sábado");

    private Integer codigo;
    private String descricao;
    private String nomeCompleto;
}
