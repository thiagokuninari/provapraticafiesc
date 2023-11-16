package helpers;

import br.com.xbrain.autenticacao.modules.comum.model.UnidadeNegocio;

public class UnidadesNegocio {

    public static final UnidadeNegocio UNIDADE_PESSOAL = unidadeNegocio(1, "Pessoal");
    public static final UnidadeNegocio UNIDADE_RESIDENCIAL_E_COMBOS = unidadeNegocio(2, "Residencial e Combos");
    public static final UnidadeNegocio UNIDADE_XBRAIN = unidadeNegocio(3, "Xbrain");
    public static final UnidadeNegocio UNIDADE_CLARO_RESIDENCIAL = unidadeNegocio(4, "Claro Residencial");

    private static UnidadeNegocio unidadeNegocio(int id, String nome) {
        return new UnidadeNegocio(id, nome);
    }
}
