package br.com.xbrain.autenticacao.modules.usuario.enums;

import br.com.xbrain.autenticacao.modules.comum.dto.SelectResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    public static List<SelectResponse> getTiposCanal() {
        return Stream.of(ETipoCanal.values())
            .map(tipoCanal -> SelectResponse.of(tipoCanal.name(), tipoCanal.getDescricao().toUpperCase()))
            .collect(Collectors.toList());
    }
}
