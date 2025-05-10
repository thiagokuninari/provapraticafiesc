package com.provapratica.api.controller;

import com.provapratica.api.dto.EspecialidadeRequest;
import com.provapratica.api.dto.EspecialidadeResponse;
import com.provapratica.api.service.EspecialidadeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("api/especialidade")
public class EspecialidadeController {

    @Autowired
    private EspecialidadeService especialidadeService;

    @PostMapping
    @ResponseStatus(CREATED)
    public EspecialidadeResponse save(@RequestBody @Validated EspecialidadeRequest request) {
        return especialidadeService.save(request);
    }
}
