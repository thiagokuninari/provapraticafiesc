package br.com.xbrain.autenticacao.modules.comum.controller;

import br.com.xbrain.autenticacao.modules.comum.dto.RegionalDto;
import br.com.xbrain.autenticacao.modules.comum.dto.SelectResponse;
import br.com.xbrain.autenticacao.modules.comum.model.Regional;
import br.com.xbrain.autenticacao.modules.comum.service.RegionalService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "api/regionais")
@RequiredArgsConstructor
public class RegionalController {

    private final RegionalService service;

    @GetMapping
    public Iterable<Regional> getAtivos() {
        return service.getAll();
    }

    @GetMapping("ativas")
    public List<RegionalDto> findAllAtivos() {
        return service.findAllAtivos();
    }

    @GetMapping("comunicados")
    public List<RegionalDto> getAtivosParaComunicados() {
        return service.getAtivosParaComunicados();
    }

    @GetMapping("/usuario/{id}")
    public List<SelectResponse> getAllByUsuarioId(@PathVariable("id") int usuarioId) {
        return service.getAllByUsuarioId(usuarioId);
    }

    @GetMapping("/{regionalId}")
    public RegionalDto findById(@PathVariable Integer regionalId) {
        return service.findById(regionalId);
    }

    @GetMapping("novas-regionais-ids")
    public List<Integer> getNovasRegionaisIds() {
        return service.getNovasRegionaisIds();
    }
}
