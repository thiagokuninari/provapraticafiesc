package helpers;

import br.com.xbrain.autenticacao.modules.comum.enums.CodigoEmpresa;
import br.com.xbrain.autenticacao.modules.comum.model.Empresa;
import br.com.xbrain.autenticacao.modules.comum.model.UnidadeNegocio;

public class Empresas {

    public static final Empresa CLARO_MOVEL = empresa(1, "Claro Móvel", CodigoEmpresa.CLARO_MOVEL);
    public static final Empresa CLARO_TV = empresa(2, "Claro TV", CodigoEmpresa.CLARO_TV);
    public static final Empresa NET = empresa(3, "NET", CodigoEmpresa.NET);
    public static final Empresa XBRAIN = empresa(4, "XBRAIN", CodigoEmpresa.XBRAIN);
    public static final Empresa CLARO_RESIDENCIAL = empresa(5, "Claro Residencial", CodigoEmpresa.CLARO_RESIDENCIAL);

    private static Empresa empresa(int id, String nome, CodigoEmpresa codigo) {
        var empresa = new Empresa();
        empresa.setId(id);
        empresa.setNome(nome);
        empresa.setUnidadeNegocio(new UnidadeNegocio(1));
        empresa.setCodigo(codigo);

        return empresa;
    }

}
