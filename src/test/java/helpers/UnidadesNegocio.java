package helpers;

import br.com.xbrain.autenticacao.modules.comum.model.UnidadeNegocio;

public class UnidadesNegocio {

    public static UnidadeNegocio UNIDADE_PESSOAL = unidadeNegocio(2, "Pessoal");
    public static UnidadeNegocio UNIDADE_RESIDENCIAL_E_COMBOS = unidadeNegocio(3, "Residencial e Combos");

    private static UnidadeNegocio unidadeNegocio(int id, String nome) {
        return new UnidadeNegocio(id, nome);
    }
}
