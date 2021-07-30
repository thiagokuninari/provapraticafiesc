package br.com.xbrain.autenticacao.modules.comum.controller;

import br.com.xbrain.autenticacao.modules.comum.dto.RegionalDto;
import br.com.xbrain.autenticacao.modules.comum.dto.SelectResponse;
import br.com.xbrain.autenticacao.modules.comum.model.Regional;
import br.com.xbrain.autenticacao.modules.comum.service.RegionalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "api/regionais")
public class RegionalController {

    @Autowired
    private RegionalService service;

    @RequestMapping(method = RequestMethod.GET)
    public Iterable<Regional> getAtivos() {
        return service.getAll();
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
}