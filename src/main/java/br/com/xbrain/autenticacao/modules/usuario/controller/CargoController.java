package br.com.xbrain.autenticacao.modules.usuario.controller;

import br.com.xbrain.autenticacao.modules.comum.dto.PageRequest;
import br.com.xbrain.autenticacao.modules.comum.dto.SelectResponse;
import br.com.xbrain.autenticacao.modules.usuario.dto.CargoComNivelResponse;
import br.com.xbrain.autenticacao.modules.usuario.dto.CargoFiltros;
import br.com.xbrain.autenticacao.modules.usuario.dto.CargoRequest;
import br.com.xbrain.autenticacao.modules.usuario.dto.CargoResponse;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo;
import br.com.xbrain.autenticacao.modules.usuario.enums.ECanal;
import br.com.xbrain.autenticacao.modules.usuario.service.CargoService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/cargos")
@RequiredArgsConstructor
public class CargoController {

    private final CargoService service;

    @GetMapping
    public List<CargoResponse> getAll(Integer nivelId, @RequestParam(required = false) Set<ECanal> canais,
                                      @RequestParam(required = false, defaultValue = "true") boolean permiteEditarCompleto) {
        return service.getPermitidosPorNivelECanaisPermitidos(nivelId, canais, permiteEditarCompleto)
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

    @GetMapping("com-nivel")
    public List<CargoComNivelResponse> getAllComNiveisConcatenado(@RequestParam List<Integer> niveisId) {
        return service.getPermitidosPorNiveis(niveisId)
            .stream()
            .map(CargoComNivelResponse::of)
            .collect(Collectors.toList());
    }

    @GetMapping("comunicados")
    public List<SelectResponse> getAllPermitidosAoComunicados(@RequestParam List<Integer> niveisId) {
        return service.getPermitidosAosComunicados(niveisId);
    }

    @GetMapping("/codigo-cargos")
    public List<CodigoCargo> getAllCargos() {
        return service.getAllCargos();
    }
}
