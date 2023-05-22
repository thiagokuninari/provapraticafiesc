package br.com.xbrain.autenticacao.modules.usuario.helpers;

import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import br.com.xbrain.autenticacao.modules.usuario.model.UsuarioCidade;
import br.com.xbrain.autenticacao.modules.usuario.model.UsuarioCidadePk;

import java.util.Set;

public class UsuarioCidadeHelper {

    public static Set<UsuarioCidade> listaUsuarioCidadesDoParana() {
        return Set.of(
            usuarioCidadeCambe(),
            usuarioCidadeMaringa(),
            usuarioCidadeCascavel(),
            usuarioCidadeCuritiba(),
            usuarioCidadeLondrina(),
            usuarioCidadeBandeirantes(),
            usuarioCidadeCampinaDaLagoa()
        );
    }

    public static Set<UsuarioCidade> listaUsuarioCidadeDeDistritosDeLondrina() {
        return Set.of(
            usuarioCidadeIrere(),
            usuarioCidadeWarta(),
            usuarioCidadeSaoLuiz(),
            usuarioCidadePaiquere(),
            usuarioCidadeGuaravera(),
            usuarioCidadeMaravilha(),
            usuarioCidadeLerroville()
        );
    }

    public static UsuarioCidade usuarioCidadeBandeirantes() {
        var usuarioCidade = new UsuarioCidade();
        usuarioCidade.setUsuarioCidadePk(new UsuarioCidadePk(121314, 3248));
        usuarioCidade.setUsuario(Usuario.builder().id(121314).build());
        usuarioCidade.setCidade(CidadeHelper.cidadeBandeirantes());

        return usuarioCidade;
    }

    public static UsuarioCidade usuarioCidadeCambe() {
        var usuarioCidade = new UsuarioCidade();
        usuarioCidade.setUsuarioCidadePk(new UsuarioCidadePk(121314, 3270));
        usuarioCidade.setUsuario(Usuario.builder().id(121314).build());
        usuarioCidade.setCidade(CidadeHelper.cidadeCambe());

        return usuarioCidade;
    }

    public static UsuarioCidade usuarioCidadeCampinaDaLagoa() {
        var usuarioCidade = new UsuarioCidade();
        usuarioCidade.setUsuarioCidadePk(new UsuarioCidadePk(121314, 3272));
        usuarioCidade.setUsuario(Usuario.builder().id(121314).build());
        usuarioCidade.setCidade(CidadeHelper.cidadeCampinaDaLagoa());

        return usuarioCidade;
    }

    public static UsuarioCidade usuarioCidadeCascavel() {
        var usuarioCidade = new UsuarioCidade();
        usuarioCidade.setUsuarioCidadePk(new UsuarioCidadePk(121314, 3287));
        usuarioCidade.setUsuario(Usuario.builder().id(121314).build());
        usuarioCidade.setCidade(CidadeHelper.cidadeCascavel());

        return usuarioCidade;
    }

    public static UsuarioCidade usuarioCidadeCuritiba() {
        var usuarioCidade = new UsuarioCidade();
        usuarioCidade.setUsuarioCidadePk(new UsuarioCidadePk(121314, 3312));
        usuarioCidade.setUsuario(Usuario.builder().id(121314).build());
        usuarioCidade.setCidade(CidadeHelper.cidadeCuritiba());

        return usuarioCidade;
    }

    public static UsuarioCidade usuarioCidadeLondrina() {
        var usuarioCidade = new UsuarioCidade();
        usuarioCidade.setUsuarioCidadePk(new UsuarioCidadePk(121314, 5578));
        usuarioCidade.setUsuario(Usuario.builder().id(121314).build());
        usuarioCidade.setCidade(CidadeHelper.cidadeLondrina());

        return usuarioCidade;
    }

    public static UsuarioCidade usuarioCidadeMaringa() {
        var usuarioCidade = new UsuarioCidade();
        usuarioCidade.setUsuarioCidadePk(new UsuarioCidadePk(121314, 3426));
        usuarioCidade.setUsuario(Usuario.builder().id(121314).build());
        usuarioCidade.setCidade(CidadeHelper.cidadeMaringa());

        return usuarioCidade;
    }

    public static UsuarioCidade usuarioCidadeGuaravera() {
        var usuarioCidade = new UsuarioCidade();
        usuarioCidade.setUsuarioCidadePk(new UsuarioCidadePk(151617, 30858));
        usuarioCidade.setUsuario(Usuario.builder().id(151617).build());
        usuarioCidade.setCidade(CidadeHelper.distritoGuaravera());

        return usuarioCidade;
    }

    public static UsuarioCidade usuarioCidadeIrere() {
        var usuarioCidade = new UsuarioCidade();
        usuarioCidade.setUsuarioCidadePk(new UsuarioCidadePk(151617, 30813));
        usuarioCidade.setUsuario(Usuario.builder().id(151617).build());
        usuarioCidade.setCidade(CidadeHelper.distritoIrere());

        return usuarioCidade;
    }

    public static UsuarioCidade usuarioCidadeLerroville() {
        var usuarioCidade = new UsuarioCidade();
        usuarioCidade.setUsuarioCidadePk(new UsuarioCidadePk(151617, 30732));
        usuarioCidade.setUsuario(Usuario.builder().id(151617).build());
        usuarioCidade.setCidade(CidadeHelper.distritoLerroville());

        return usuarioCidade;
    }

    public static UsuarioCidade usuarioCidadeMaravilha() {
        var usuarioCidade = new UsuarioCidade();
        usuarioCidade.setUsuarioCidadePk(new UsuarioCidadePk(151617, 30757));
        usuarioCidade.setUsuario(Usuario.builder().id(151617).build());
        usuarioCidade.setCidade(CidadeHelper.distritoMaravilha());

        return usuarioCidade;
    }

    public static UsuarioCidade usuarioCidadePaiquere() {
        var usuarioCidade = new UsuarioCidade();
        usuarioCidade.setUsuarioCidadePk(new UsuarioCidadePk(151617, 30676));
        usuarioCidade.setUsuario(Usuario.builder().id(151617).build());
        usuarioCidade.setCidade(CidadeHelper.distritoPaiquere());

        return usuarioCidade;
    }

    public static UsuarioCidade usuarioCidadeSaoLuiz() {
        var usuarioCidade = new UsuarioCidade();
        usuarioCidade.setUsuarioCidadePk(new UsuarioCidadePk(151617, 30848));
        usuarioCidade.setUsuario(Usuario.builder().id(151617).build());
        usuarioCidade.setCidade(CidadeHelper.distritoSaoLuiz());

        return usuarioCidade;
    }

    public static UsuarioCidade usuarioCidadeWarta() {
        var usuarioCidade = new UsuarioCidade();
        usuarioCidade.setUsuarioCidadePk(new UsuarioCidadePk(151617, 30910));
        usuarioCidade.setUsuario(Usuario.builder().id(151617).build());
        usuarioCidade.setCidade(CidadeHelper.distritoWarta());

        return usuarioCidade;
    }
}
