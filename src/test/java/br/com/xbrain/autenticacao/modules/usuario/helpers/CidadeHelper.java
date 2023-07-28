package br.com.xbrain.autenticacao.modules.usuario.helpers;

import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.comum.model.Uf;
import br.com.xbrain.autenticacao.modules.usuario.model.Cidade;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import br.com.xbrain.autenticacao.modules.usuario.model.UsuarioCidade;
import br.com.xbrain.autenticacao.modules.usuario.model.UsuarioCidadePk;

import java.util.Collections;
import java.util.List;

import static br.com.caelum.stella.type.Estado.*;
import static br.com.xbrain.autenticacao.modules.usuario.helpers.SubClusterHelper.*;

public class CidadeHelper {

    public static Cidade umaCidadeLondrina() {
        return Cidade.builder()
            .id(1111)
            .nome("LONDRINA")
            .codigoIbge("4113700")
            .uf(new Uf(1, "PARANA", PR.name(), Collections.emptyList()))
            .subCluster(umSubClusterLondrina())
            .build();
    }

    public static Cidade umaCidadeSaoPaulo() {
        return Cidade.builder()
            .id(2222)
            .nome("SAO PAULO")
            .codigoIbge("4113700")
            .uf(new Uf(2, "SAO PAULO", SP.name(), Collections.emptyList()))
            .subCluster(umSubClusterSaoPaulo())
            .build();
    }

    public static Cidade umaCidadeRioDeJaneiro() {
        return Cidade.builder()
            .id(3333)
            .nome("RIO DE JANEIRO")
            .codigoIbge("4113700")
            .uf(new Uf(19, "RIO DE JANEIRO", RJ.name(), Collections.emptyList()))
            .subCluster(umSubClusterRioDeJaneiro())
            .build();
    }

    public static Cidade umaCidadeCapitolio() {
        return Cidade.builder()
            .id(4444)
            .nome("CAPITOLIO")
            .codigoIbge("4113700")
            .uf(new Uf(8, "MINAS GERAIS", MG.name(), Collections.emptyList()))
            .subCluster(umSubClusterRemotoSulMinas())
            .build();
    }

    public static Cidade umaCidadeGama() {
        return Cidade.builder()
            .id(5555)
            .nome("GAMA")
            .codigoIbge("4113700")
            .uf(new Uf(18, "MINAS GERAIS", MG.name(), Collections.emptyList()))
            .subCluster(umSubClusterCoronelFabriciano())
            .build();
    }

    public static UsuarioCidade umUsuarioCidade(Integer usuarioId, Cidade cidade) {
        var usuario = new Usuario(usuarioId);
        usuario.setSituacao(ESituacao.A);
        var usuarioCidade = new UsuarioCidade();
        usuarioCidade.setUsuarioCidadePk(new UsuarioCidadePk(usuario.getId(), umaCidadeLondrina().getId()));
        usuarioCidade.setUsuario(usuario);
        usuarioCidade.setCidade(cidade);

        return usuarioCidade;
    }

    public static List<UsuarioCidade> umaListaUsuarioCidadesDeLondrina() {
        return List.of(
            umUsuarioCidade(1, umaCidadeLondrina()),
            umUsuarioCidade(2, umaCidadeLondrina()),
            umUsuarioCidade(3, umaCidadeLondrina()),
            umUsuarioCidade(4, umaCidadeLondrina()),
            umUsuarioCidade(5, umaCidadeLondrina()),
            umUsuarioCidade(6, umaCidadeLondrina()),
            umUsuarioCidade(7, umaCidadeLondrina()),
            umUsuarioCidade(8, umaCidadeLondrina())
        );
    }

    public static List<UsuarioCidade> umaListaUsuarioCidadesDeSaoPaulo() {
        return List.of(
            umUsuarioCidade(1, umaCidadeSaoPaulo()),
            umUsuarioCidade(2, umaCidadeSaoPaulo()),
            umUsuarioCidade(3, umaCidadeSaoPaulo()),
            umUsuarioCidade(4, umaCidadeSaoPaulo()),
            umUsuarioCidade(5, umaCidadeSaoPaulo()),
            umUsuarioCidade(7, umaCidadeSaoPaulo()),
            umUsuarioCidade(8, umaCidadeSaoPaulo())
        );
    }

    public static List<UsuarioCidade> umaListaUsuarioCidadesDeRioDeJaneiro() {
        return List.of(
            umUsuarioCidade(1, umaCidadeRioDeJaneiro()),
            umUsuarioCidade(3, umaCidadeRioDeJaneiro()),
            umUsuarioCidade(4, umaCidadeRioDeJaneiro()),
            umUsuarioCidade(5, umaCidadeRioDeJaneiro()),
            umUsuarioCidade(7, umaCidadeRioDeJaneiro()),
            umUsuarioCidade(8, umaCidadeRioDeJaneiro())
        );
    }

    public static List<UsuarioCidade> umaListaUsuarioCidadesDeCapitolio() {
        return List.of(
            umUsuarioCidade(1, umaCidadeRioDeJaneiro()),
            umUsuarioCidade(3, umaCidadeRioDeJaneiro()),
            umUsuarioCidade(7, umaCidadeRioDeJaneiro())
        );
    }
}
