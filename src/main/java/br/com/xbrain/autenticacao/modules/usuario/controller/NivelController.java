package br.com.xbrain.autenticacao.modules.usuario.controller;

import br.com.xbrain.autenticacao.modules.usuario.enums.NivelTipoVisualizacao;
import br.com.xbrain.autenticacao.modules.usuario.model.Nivel;
import br.com.xbrain.autenticacao.modules.usuario.service.NivelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "api/niveis")
public class NivelController {

    @Autowired
    private NivelService service;

    @GetMapping
    public Iterable<Nivel> getAll() {
        return service.getAll();
    }

    @GetMapping(value = "/permitidos/{tipoVisualizacao}")
    public Iterable<Nivel> getPermitidos(@PathVariable NivelTipoVisualizacao tipoVisualizacao) {
        return service.getPermitidos(tipoVisualizacao);
    }

    @GetMapping("comunicados")
    public List<Nivel> getNiveisParaComunicados() {
        return service.getPermitidosParaComunicados();
    }
}
