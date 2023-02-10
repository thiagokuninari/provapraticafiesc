package br.com.xbrain.autenticacao.modules.usuario.helpers;

import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.comum.model.Cluster;
import br.com.xbrain.autenticacao.modules.comum.model.SubCluster;

import java.util.List;

public class SubClusterHelper {

    public static SubCluster umSubClusterMarilia() {
        return new SubCluster(166, "MARÍLIA", new Cluster(49), ESituacao.A, List.of(), null);
    }

    public static SubCluster umSubClusterParanavai() {
        return new SubCluster(185, "BRI - PARANAVAÍ - PR", new Cluster(45), ESituacao.A, List.of(), null);
    }

    public static SubCluster umSubClusterLondrina() {
        return new SubCluster(189, "LONDRINA", new Cluster(45), ESituacao.A, List.of(), null);
    }

    public static SubCluster umSubClusterMaringa() {
        return new SubCluster(191, "MARINGÁ", new Cluster(45), ESituacao.A, List.of(), null);
    }

    public static SubCluster umSubClusterArapiraca() {
        return new SubCluster(68, "BRI - ARAPIRACA - AL", new Cluster(16), ESituacao.A, List.of(), null);
    }

    public static SubCluster umSubClusterSaoPaulo() {
        return new SubCluster(181, "SAO PAULO", new Cluster(43), ESituacao.A, List.of(), null);
    }

    public static SubCluster umSubClusterRioDeJaneiro() {
        return new SubCluster(127, "RIO DE JANEIRO", new Cluster(30), ESituacao.A, List.of(), null);
    }

    public static SubCluster umSubClusterRemotoSulMinas() {
        return new SubCluster(103, "REMOTO - SUL MG", new Cluster(23), ESituacao.A, List.of(), null);
    }

    public static SubCluster umSubClusterCoronelFabriciano() {
        return new SubCluster(99, "BRI - CORONEL FABRICIANO - MG", new Cluster(22), ESituacao.A, List.of(), null);
    }
}
