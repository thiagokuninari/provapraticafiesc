package br.com.xbrain.autenticacao.modules.usuario.controller;

import br.com.xbrain.autenticacao.modules.comum.service.CanalService;
import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioHierarquiaFiltros;
import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioNomeResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "api/usuarios-hierarquia")
public class HierarquiaController {

    @Autowired
    private CanalService canalService;

    @GetMapping("coordenadores-subordinados")
    public List<UsuarioNomeResponse> getCoordenadoresSubordinados(UsuarioHierarquiaFiltros usuarioHierarquiaFiltros) {
        canalService.usuarioHierarquia().validarCanal(usuarioHierarquiaFiltros);
        return canalService.usuarioHierarquia().coordenadoresSubordinadosHierarquia(usuarioHierarquiaFiltros);

    }

    @GetMapping("supervisores-subordinados")
    public List<UsuarioNomeResponse> getSupervisoresSubordinados(UsuarioHierarquiaFiltros usuarioHierarquiaFiltros) {
        canalService.usuarioHierarquia().validarCanal(usuarioHierarquiaFiltros);
        return canalService.usuarioHierarquia().supervisoresDaHierarquia(usuarioHierarquiaFiltros);
    }

    @GetMapping("vendedores-subordinados")
    public List<UsuarioNomeResponse> getVendedoresSubordinados(UsuarioHierarquiaFiltros usuarioHierarquiaFiltros) {
        canalService.usuarioHierarquia().validarCanal(usuarioHierarquiaFiltros);
        return canalService.usuarioHierarquia().vendedoresDaHierarquia(usuarioHierarquiaFiltros);
    }
}
