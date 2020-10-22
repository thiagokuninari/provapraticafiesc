package br.com.xbrain.autenticacao.modules.usuario.controller;

import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioNomeResponse;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo;
import br.com.xbrain.autenticacao.modules.usuario.service.UsuarioSiteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "api/usuarios/site")
public class UsuarioSiteController {

    @Autowired
    private UsuarioSiteService usuarioSiteService;

    @GetMapping("{siteId}/vendedores")
    public List<UsuarioNomeResponse> getVendedoresOperacaoAtivoProprio(@PathVariable Integer siteId) {
        return usuarioSiteService.getVendedoresOperacaoAtivoProprioPorSiteId(siteId);
    }

    @GetMapping("cargo/{codigoCargo}")
    public List<UsuarioNomeResponse> buscarUsuariosSitePorCargo(@PathVariable CodigoCargo codigoCargo) {
        return usuarioSiteService.buscarUsuariosSitePorCargo(codigoCargo);
    }

    @GetMapping("{codigoCargo}/disponiveis")
    public List<UsuarioNomeResponse> buscarUsuariosDisponiveisPorCargo(@PathVariable CodigoCargo codigoCargo) {
        return usuarioSiteService.getUsuariosDisponiveisPorCargo(codigoCargo);
    }

    @GetMapping("editar/{siteId}/{codigoCargo}")
    public List<UsuarioNomeResponse> getUsuariosEditarSite(@PathVariable Integer siteId,
                                                           @PathVariable CodigoCargo codigoCargo) {
        return usuarioSiteService.getUsuariosParaVincularAoSitePorSiteIdECargo(siteId, codigoCargo);
    }
}
