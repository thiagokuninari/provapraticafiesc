package helpers;

import br.com.xbrain.autenticacao.modules.comum.enums.CodigoUnidadeNegocio;
import br.com.xbrain.autenticacao.modules.comum.model.UnidadeNegocio;

import static br.com.xbrain.autenticacao.modules.comum.enums.CodigoUnidadeNegocio.*;

public class UnidadesNegocio {

    public static final UnidadeNegocio UNIDADE_PESSOAL = unidadeNegocio(1, "Pessoal", PESSOAL);
    public static final UnidadeNegocio UNIDADE_RESIDENCIAL_E_COMBOS =
        unidadeNegocio(2, "Residencial e Combos", RESIDENCIAL_COMBOS);
    public static final UnidadeNegocio UNIDADE_XBRAIN = unidadeNegocio(3, "Xbrain", XBRAIN);
    public static final UnidadeNegocio UNIDADE_CLARO_RESIDENCIAL =
        unidadeNegocio(4, "Claro Residencial", CLARO_RESIDENCIAL);

    private static UnidadeNegocio unidadeNegocio(int id, String nome, CodigoUnidadeNegocio codigo) {
        return new UnidadeNegocio(id, nome, codigo);
    }
}
