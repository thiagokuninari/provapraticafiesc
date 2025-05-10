package com.provapratica.api.controller;

import com.provapratica.api.domain.Nivel;
import com.provapratica.api.dto.EstudanteResponse;
import com.provapratica.api.dto.NivelResponse;
import com.provapratica.api.service.NivelService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/niveis")
@RequiredArgsConstructor
public class NivelController {

    private final NivelService nivelService;

    @GetMapping
    public List<NivelResponse> findAll() {
        return nivelService.findAll();

    }
}
