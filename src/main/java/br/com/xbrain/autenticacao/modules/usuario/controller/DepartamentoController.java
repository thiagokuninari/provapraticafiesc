package br.com.xbrain.autenticacao.modules.usuario.controller;

import br.com.xbrain.autenticacao.modules.usuario.model.Departamento;
import br.com.xbrain.autenticacao.modules.usuario.service.DepartamentoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "api/departamentos")
public class DepartamentoController {

    @Autowired
    private DepartamentoService service;

    @GetMapping
    public Iterable<Departamento> get(Integer nivelId) {
        return service.getPermitidosPorNivel(nivelId);
    }

    @GetMapping("cargo-id")
    public List<Departamento> getByCargoId(Integer cargoId) {
        return service.getPermitidosPorCargo(cargoId);
    }
}
