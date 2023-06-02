package br.com.xbrain.autenticacao.modules.usuario.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.validation.constraints.NotNull;

@Getter
@AllArgsConstructor
public enum ETipoCanal {
    PAP("PAP"),
    PAP_PME("PAP PME"),
    PAP_PREMIUM("PAP Premium"),
    INSIDE_SALES_PME("Inside Sales PME");

    @NotNull
    private final String descricao;
}
