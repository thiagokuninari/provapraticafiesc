package br.com.xbrain.autenticacao.modules.usuario.helpers;

import br.com.xbrain.autenticacao.modules.permissao.model.PermissaoEspecial;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;

import java.util.List;
import java.util.stream.Collectors;

import static br.com.xbrain.autenticacao.modules.feeder.service.FeederUtil.*;

public class PermissoesHelper {

    public static List<PermissaoEspecial> umaListaPermissoesFuncionalidadesFeederParaAa(Integer usuarioId) {
        return FUNCIONALIDADES_FEEDER_PARA_AA.stream()
            .map(id -> PermissaoEspecial.builder().id(id).usuario(Usuario.builder().id(usuarioId).build()).build())
            .collect(Collectors.toList());
    }

    public static PermissaoEspecial umaPermissaoTratarLead(Integer usuarioId) {
        return PermissaoEspecial.builder().id(3046).usuario(Usuario.builder().id(usuarioId).build()).build();
    }

    public static List<PermissaoEspecial> umaListaPermissoesFuncionalidadesFeederParaMsoResidencial(Integer usuarioId) {
        return FUNCIONALIDADES_FEEDER_PARA_MSO_RESIDENCIAL.stream()
            .map(id -> PermissaoEspecial.builder().id(id).usuario(Usuario.builder().id(usuarioId).build()).build())
            .collect(Collectors.toList());
    }

    public static List<PermissaoEspecial> umaListaPermissoesFuncionalidadesFeederParaMsoEmpresarial(Integer usuarioId) {
        return FUNCIONALIDADES_FEEDER_PARA_MSO_EMPRESARIAL.stream()
            .map(id -> PermissaoEspecial.builder().id(id).usuario(Usuario.builder().id(usuarioId).build()).build())
            .collect(Collectors.toList());
    }

    public static PermissaoEspecial umaPermissaoIndicacaoPremium() {
        return PermissaoEspecial.builder().id(3062).build();
    }

    public static PermissaoEspecial umaPermissaoIndicacaoInsideSalesPme() {
        return PermissaoEspecial.builder().id(3071).build();
    }

    public static PermissaoEspecial umaPermissaoSocialHub() {
        return PermissaoEspecial.builder().id(30000).build();
    }
}
