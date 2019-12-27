package br.com.xbrain.autenticacao.modules.usuario.controller;

import br.com.xbrain.autenticacao.modules.comum.dto.PageRequest;
import br.com.xbrain.autenticacao.modules.usuario.dto.CargoFiltros;
import br.com.xbrain.autenticacao.modules.usuario.dto.CargoRequest;
import br.com.xbrain.autenticacao.modules.usuario.dto.CargoResponse;
import br.com.xbrain.autenticacao.modules.usuario.service.CargoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
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
        var cargos = service.getAll(pageRequest, filtros);
        return new PageImpl<>(cargos.getContent()
                .stream().map(CargoResponse::of)
                .collect(Collectors.toList()),
                pageRequest,
                cargos.getTotalElements());
    }

    @PostMapping
    public CargoResponse save(@Validated @RequestBody CargoRequest request) throws Exception {
        return CargoResponse.of(service.save(CargoRequest.convertFrom(request)));
    }

    @PutMapping("/{id}")
    public CargoResponse update(@Validated @RequestBody CargoRequest request, @PathVariable Integer id) throws Exception {
        request.setId(id);
        return CargoResponse.of(service.update(CargoRequest.convertFrom(request)));
    }
}
