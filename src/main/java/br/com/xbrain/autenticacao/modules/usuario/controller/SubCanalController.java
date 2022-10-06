package br.com.xbrain.autenticacao.modules.usuario.controller;

import br.com.xbrain.autenticacao.modules.usuario.dto.SubCanalDto;
import br.com.xbrain.autenticacao.modules.usuario.service.SubCanalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping(path = "api/sub-canais")
public class SubCanalController {

    @Autowired
    private SubCanalService service;

    @GetMapping
    public List<SubCanalDto> getAllSubCanais() {
        return service.getAll();
    }

    @GetMapping("exceto-inside-sales")
    public List<SubCanalDto> getAllSubCanaisExcetoInsideSalesPme() {
        return service.getAllExcetoInsideSalesPme();
    }

    @GetMapping("{id}")
    public SubCanalDto getSubCanalById(@PathVariable int id) {
        return service.getSubCanalById(id);
    }

    @GetMapping("usuario-subcanal/{usuarioId}")
    public Set<SubCanalDto> getByUsuarioId(@PathVariable Integer usuarioId) {
        return service.getSubCanalByUsuarioId(usuarioId);
    }
}
