package br.com.xbrain.autenticacao.modules.usuario.controller;

import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioEquipeDto;
import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioNomeResponse;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo;
import br.com.xbrain.autenticacao.modules.usuario.service.UsuarioSiteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("coordenadores/disponiveis")
    public List<UsuarioNomeResponse> buscarUsuariosDisponiveisPorCargo(@RequestParam(required = false) List<Integer> cidadesIds) {
        return usuarioSiteService.getCoordenadoresDisponiveisPorCidade(cidadesIds);
    }

    @GetMapping("editar/{siteId}/coordenador")
    public List<UsuarioNomeResponse> editarCoordenadorSite(@PathVariable Integer siteId,
                                                           @RequestParam(required = false) List<Integer> cidadesIds) {
        return usuarioSiteService.buscarCoordenadoresDisponiveisEVinculadosAoSite(siteId, cidadesIds);
    }

    @GetMapping("editar/{siteId}/supervisor")
    public List<UsuarioNomeResponse> editarSupervisorSite(@PathVariable Integer siteId,
                                                          @RequestParam List<Integer> coordenadoresIds) {
        return usuarioSiteService.buscarSupervisoresDisponiveisEVinculadosAoSite(coordenadoresIds, siteId);
    }

    @GetMapping("supervisores-hierarquia/disponiveis")
    public List<UsuarioNomeResponse> getSupervidoresSemSitePorCoodenadoresId(@RequestParam List<Integer> coordenadoresIds) {
        return usuarioSiteService.getSupervisoresSemSitePorCoordenadorsId(coordenadoresIds);
    }

    @GetMapping("{siteId}/vendedores-hierarquia")
    public List<UsuarioEquipeDto> getVendoresDoSiteIdPorHierarquiaComEquipe(@PathVariable Integer siteId,
                                                                            @RequestParam Integer usuarioId,
                                                                            @RequestParam(required = false, defaultValue = "true")
                                                                                    boolean buscarInativo ) {
        return usuarioSiteService.getVendoresDoSiteIdPorHierarquiaComEquipe(siteId, usuarioId, buscarInativo);
    }

    @GetMapping("{siteId}/coordenadores")
    public List<UsuarioNomeResponse> coordenadoresDoSiteId(@PathVariable Integer siteId) {
        return usuarioSiteService.coordenadoresDoSiteId(siteId);
    }
}
