package br.com.xbrain.autenticacao.modules.usuario.enums;

import br.com.xbrain.autenticacao.modules.comum.exception.ValidacaoException;
import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.Objects;

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

    public static ETipoCanal valueOf(Integer subcanalId) {
        return Arrays.stream(values())
            .filter(tipo -> Objects.equals(tipo.id, subcanalId))
            .findFirst()
            .orElseThrow(() -> new ValidacaoException("Sub-canal não encontrado."));
    }
}
