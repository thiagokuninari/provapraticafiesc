package helpers;

import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.comum.model.Grupo;
import br.com.xbrain.autenticacao.modules.comum.model.Regional;

public class GrupoHelper {

    public static Grupo umGrupoNorteDoParana() {
        return new Grupo(20, "NORTE DO PARANÁ", new Regional(3), ESituacao.A);
    }

    public static Grupo umGrupoMarilia() {
        return new Grupo(15, "MARILIA", new Regional(2), ESituacao.A);
    }

    public static Grupo umGrupoNordeste() {
        return new Grupo(4, "NORDESTE", new Regional(1), ESituacao.A);
    }

    public static Grupo umGrupoPortoVelho() {
        return new Grupo(1, "PORTO VELHO", new Regional(20), ESituacao.A);
    }
}
