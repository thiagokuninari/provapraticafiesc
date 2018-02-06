package helpers;

import br.com.xbrain.autenticacao.modules.comum.model.Empresa;

public class Empresas {

    public static final Empresa CLARO_MOVEL = empresa(1, "Claro Móvel");
    public static final Empresa CLARO_TV = empresa(2, "Claro TV");
    public static final Empresa NET = empresa(3, "NET");

    private static Empresa empresa(int id, String nome) {
        Empresa empresa = new Empresa();
        empresa.setId(id);
        empresa.setNome(nome);
        return empresa;
    }

}
