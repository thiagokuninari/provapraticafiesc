package br.com.xbrain.autenticacao.modules.usuario.controller;

import br.com.xbrain.autenticacao.modules.comum.dto.PageRequest;
import br.com.xbrain.autenticacao.modules.usuario.dto.CargoFiltros;
import br.com.xbrain.autenticacao.modules.usuario.dto.CargoRequest;
import br.com.xbrain.autenticacao.modules.usuario.dto.CargoResponse;
import br.com.xbrain.autenticacao.modules.usuario.model.Cargo;
import br.com.xbrain.autenticacao.modules.usuario.service.CargoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
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
    public Iterable<CargoResponse> getAll(Integer nivelId) {
        return CargoResponse.convertFrom((List<Cargo>)service.getAll(nivelId));
    }

    @GetMapping("/gerencia")
    public PageImpl<CargoResponse> getAll(PageRequest pageRequest, CargoFiltros filtros) {
        Page<Cargo> cargo = service.getAll(pageRequest, filtros);

        PageImpl obj = new PageImpl<>(
                cargo
                        .getContent()
                        .stream()
                        .map(CargoResponse::new)
                        .collect(Collectors.toList()),
                pageRequest,
                cargo.getTotalElements());
        return obj;
    }

    @GetMapping("/{id}")
    public CargoResponse getCargo(@PathVariable("id") Integer id) throws Exception {
        return CargoResponse.convertFrom(service.findById(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CargoResponse save(@Validated @RequestBody CargoRequest request) throws Exception {
        return CargoResponse.convertFrom(service.save(CargoRequest.convertFrom(request)));
    }

    @PutMapping("/{id}")
    public CargoResponse update(@Validated @RequestBody CargoRequest request, @PathVariable Integer id) throws Exception {
        request.setId(id);
        return CargoResponse.convertFrom(service.update(CargoRequest.convertFrom(request)));
    }
}
