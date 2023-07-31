package helpers;

import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.comum.model.Cluster;
import br.com.xbrain.autenticacao.modules.comum.model.Grupo;

public class ClusterHelper {

    public static Cluster umClusterNorteDoParana() {
        return new Cluster(45, "NORTE DO PARANÁ", new Grupo(20), ESituacao.A);
    }

    public static Cluster umClusterMarilia() {
        return new Cluster(39, "MARÍLIA", new Grupo(15), ESituacao.A);
    }

    public static Cluster umClusterAlagoas() {
        return new Cluster(16, "ALAGOAS", new Grupo(4), ESituacao.A);
    }

    public static Cluster umClusterPortoVelho() {
        return new Cluster(1, "PORTO VELHO", new Grupo(1), ESituacao.A);
    }
}
