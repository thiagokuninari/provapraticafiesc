package br.com.xbrain.autenticacao.modules.usuario.helpers;

import br.com.xbrain.autenticacao.modules.permissao.model.PermissaoEspecial;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;

import java.util.List;
import java.util.stream.Collectors;

import static br.com.xbrain.autenticacao.modules.feeder.service.FeederUtil.*;

public class PermissoesHelper {

    public static List<PermissaoEspecial> umaListaPermissoesFuncionalidadesFeederParaAa(Integer idUsuario) {
        return FUNCIONALIDADES_FEEDER_PARA_AA.stream()
            .map(id -> PermissaoEspecial.builder().id(id).usuario(Usuario.builder().id(idUsuario).build()).build())
            .collect(Collectors.toList());
    }

    public static PermissaoEspecial umaPermissaoTratarLead(Integer idUsuario) {
        return PermissaoEspecial.builder().id(3046).usuario(Usuario.builder().id(idUsuario).build()).build();
    }

    public static List<PermissaoEspecial> umaListaPermissoesFuncionalidadesFeederParaMsoResidencial(Integer idUsuario) {
        return FUNCIONALIDADES_FEEDER_PARA_MSO_RESIDENCIAL.stream()
            .map(id -> PermissaoEspecial.builder().id(id).usuario(Usuario.builder().id(idUsuario).build()).build())
            .collect(Collectors.toList());
    }

    public static List<PermissaoEspecial> umaListaPermissoesFuncionalidadesFeederParaMsoEmpresarial(Integer idUsuario) {
        return FUNCIONALIDADES_FEEDER_PARA_MSO_EMPRESARIAL.stream()
            .map(id -> PermissaoEspecial.builder().id(id).usuario(Usuario.builder().id(idUsuario).build()).build())
            .collect(Collectors.toList());
    }
}
