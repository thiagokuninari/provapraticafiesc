package com.provapratica.api.controller;

import com.provapratica.api.dto.*;
import com.provapratica.api.service.AgendamentoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("api/agendamento")
public class AgendamentoController {

    @Autowired
    private AgendamentoService agendamentoService;

    @PostMapping
    @ResponseStatus(CREATED)
    public AgendamentoResponse save(@RequestBody @Validated AgendamentoRequest agendamentoRequest) {
        return agendamentoService.save(agendamentoRequest);
    }

    @PutMapping("{id}/editar")
    public AgendamentoResponse update(@PathVariable Integer id,
                                    @Validated @RequestBody AgendamentoRequest agendamentoRequest) {
        return agendamentoService.update(id, agendamentoRequest);
    }
}
