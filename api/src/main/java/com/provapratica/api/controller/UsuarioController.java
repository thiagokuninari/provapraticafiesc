package com.provapratica.api.controller;

import com.provapratica.api.dto.UsuarioRequest;
import com.provapratica.api.dto.UsuarioResponse;
import com.provapratica.api.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("api/usuario")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @PostMapping
    @ResponseStatus(CREATED)
    public UsuarioResponse save(@RequestBody @Validated UsuarioRequest usuarioRequest) {
        return usuarioService.save(usuarioRequest);
    }
}
