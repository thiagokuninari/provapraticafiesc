package br.com.xbrain.autenticacao.modules.usuario.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.validation.constraints.NotNull;

@Getter
@AllArgsConstructor
public enum ETipoCanal {
    PAP(1, "PAP"),
    PAP_PME(2, "PAP PME"),
    PAP_PREMIUM(3, "PAP Premium"),
    INSIDE_SALES_PME(4, "Inside Sales PME"),
    PAP_CONDOMINIO(5, "PAP CONDOMINIO");

    @NotNull
    private final Integer id;
    @NotNull
    private final String descricao;
}
