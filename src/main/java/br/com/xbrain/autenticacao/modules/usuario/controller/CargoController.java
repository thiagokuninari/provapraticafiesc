package br.com.xbrain.autenticacao.modules.usuario.controller;

import br.com.xbrain.autenticacao.modules.usuario.dto.CargoRequest;
import br.com.xbrain.autenticacao.modules.usuario.dto.CargoResponse;
import br.com.xbrain.autenticacao.modules.usuario.model.Cargo;
import br.com.xbrain.autenticacao.modules.usuario.service.CargoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "api/cargos")
public class CargoController {

    @Autowired
    private CargoService service;

    @RequestMapping(method = RequestMethod.GET)
    public Iterable<Cargo> getAll(Integer nivelId) {
        return service.getAll(nivelId);
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
