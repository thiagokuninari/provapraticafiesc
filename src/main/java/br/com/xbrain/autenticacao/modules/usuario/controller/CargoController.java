package br.com.xbrain.autenticacao.modules.usuario.controller;

import br.com.xbrain.autenticacao.modules.usuario.dto.CargoComNivelResponse;
import br.com.xbrain.autenticacao.modules.usuario.dto.CargoResponse;
import br.com.xbrain.autenticacao.modules.usuario.service.CargoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "api/cargos")
public class CargoController {

    @Autowired
    private CargoService service;

    @GetMapping
    public List<CargoResponse> getAll(Integer nivelId) {
        return service.getPermitidosPorNivel(nivelId)
                .stream()
                .map(CargoResponse::of)
                .collect(Collectors.toList());
    }

    @GetMapping("com-nivel")
    public List<CargoComNivelResponse> getAllComNiveisConcatenado(@RequestParam List<Integer> niveisId) {
        return service.getPermitidosPorNiveis(niveisId)
                .stream()
                .map(CargoComNivelResponse::of)
                .collect(Collectors.toList());
    }
}
