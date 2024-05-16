package br.com.xbrain.autenticacao.modules.feriado.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ETipoFeriado {
    NACIONAL("NACIONAL"),
    ESTADUAL("ESTADUAL"),
    MUNICIPAL("MUNICIPAL"),
    FACULTATIVO("FACULTATIVO");

    private String descricao;
}
