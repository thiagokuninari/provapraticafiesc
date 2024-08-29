package br.com.xbrain.autenticacao.modules.feriado.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ETipoFeriado {
    NACIONAL("NACIONAL", "Nacionais"),
    ESTADUAL("ESTADUAL", "Estaduais"),
    MUNICIPAL("MUNICIPAL", "Municipais"),
    FACULTATIVO("FACULTATIVO", "Facultativo");

    private String descricao;
    private String plural;
}
