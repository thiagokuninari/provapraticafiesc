package com.provapratica.api.controller;

import com.provapratica.api.dto.EstudanteRequest;
import com.provapratica.api.dto.EstudanteResponse;
import com.provapratica.api.service.EstudanteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("api/estudante")
public class EstudanteController {

    @Autowired
    private EstudanteService estudanteService;

    @GetMapping
    public List<EstudanteResponse> findAll() {
        return estudanteService.findAll();

    }

    @PostMapping
    @ResponseStatus(CREATED)
    public EstudanteResponse save(@RequestBody @Validated EstudanteRequest request) {
        return estudanteService.save(request);
    }

    @PutMapping("{id}/editar")
    public EstudanteResponse update(@PathVariable Integer id,
                                             @Validated @RequestBody EstudanteRequest request) {
        return estudanteService.update(id, request);
    }
}
