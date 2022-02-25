package br.com.xbrain.autenticacao.modules.usuario.service;

import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioHierarquiaFiltros;
import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioNomeResponse;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo;

import java.util.List;

public interface IUsuarioHierarquia {

    List<Integer> getUsuariosDaHierarquia(Integer usuarioId, CodigoCargo cargo);

    List<UsuarioNomeResponse> coordenadoresSubordinadosHierarquia(UsuarioHierarquiaFiltros usuarioHierarquiaFiltros);

    List<UsuarioNomeResponse> supervisoresDaHierarquia(UsuarioHierarquiaFiltros usuarioHierarquiaFiltros);

    List<UsuarioNomeResponse> vendedoresDaHierarquia(UsuarioHierarquiaFiltros usuarioHierarquiaFiltros);

    List<UsuarioNomeResponse> filtrarHierarquia(List<UsuarioNomeResponse> usuariosDisponiveis,
                                                        CodigoCargo usuarioAutenticado);

    void validarCanal(UsuarioHierarquiaFiltros usuarioHierarquiaFiltros);

    List<UsuarioNomeResponse> vendedoresDaHierarquiaPorSite(UsuarioHierarquiaFiltros usuarioHierarquiaFiltros);

}
