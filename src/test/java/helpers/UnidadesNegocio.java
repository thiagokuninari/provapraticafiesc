package helpers;

import br.com.xbrain.autenticacao.modules.comum.model.UnidadeNegocio;

public class UnidadesNegocio {

    public static UnidadeNegocio UNIDADE_PESSOAL = unidadeNegocio(1, "Pessoal");
    public static UnidadeNegocio UNIDADE_RESIDENCIAL_E_COMBOS = unidadeNegocio(2, "Residencial e Combos");
    public static UnidadeNegocio UNIDADE_XBRAIN = unidadeNegocio(3, "Xbrain");

    private static UnidadeNegocio unidadeNegocio(int id, String nome) {
        return new UnidadeNegocio(id, nome);
    }
}
