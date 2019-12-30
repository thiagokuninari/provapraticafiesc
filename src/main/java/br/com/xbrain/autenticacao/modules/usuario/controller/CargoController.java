package br.com.xbrain.autenticacao.modules.usuario.controller;

import br.com.xbrain.autenticacao.modules.comum.dto.PageRequest;
import br.com.xbrain.autenticacao.modules.usuario.dto.CargoFiltros;
import br.com.xbrain.autenticacao.modules.usuario.dto.CargoRequest;
import br.com.xbrain.autenticacao.modules.usuario.dto.CargoResponse;
import br.com.xbrain.autenticacao.modules.usuario.service.CargoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/gerencia")
    public Page<CargoResponse> getAll(PageRequest pageRequest, CargoFiltros filtros) {
        return service.getAll(pageRequest, filtros)
                .map(CargoResponse::of);
    }

    @GetMapping("/{id}")
    public CargoResponse findCargoById(@PathVariable Integer id) {
        return CargoResponse.of(service.findById(id));
    }

    @PostMapping
    public CargoResponse save(@Validated @RequestBody CargoRequest request) {
        return CargoResponse.of(service.save(CargoRequest.convertFrom(request)));
    }

    @PutMapping
    public CargoResponse update(@Validated @RequestBody CargoRequest request) {
        return CargoResponse.of(service.update(CargoRequest.convertFrom(request)));
    }

    @PutMapping("/altera-situacao")
    public CargoResponse situacao(@Validated @RequestBody CargoRequest request) {
        return CargoResponse.of(service.situacao(request));
    }
}
