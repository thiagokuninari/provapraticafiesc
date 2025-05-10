package com.provapratica.api.controller;

import com.provapratica.api.dto.LoginRequest;
import com.provapratica.api.dto.LoginResponse;
import com.provapratica.api.infra.security.TokenService;
import com.provapratica.api.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UsuarioRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody LoginRequest loginRequest){
        var usuario = this.repository.findByEmail(loginRequest.email()).orElseThrow(() ->
                new RuntimeException("Usuário não encontrado"));
        if (passwordEncoder.matches(usuario.getSenha(), loginRequest.senha())){
            var token = this.tokenService.generateToken(usuario);
            return ResponseEntity.ok(new LoginResponse(usuario.getNome(), token));
        }
        return ResponseEntity.badRequest().build();
    }

    @PostMapping("/register")
    public ResponseEntity register(@RequestBody LoginRequest loginRequest){
        var usuario = this.repository.findByEmail(loginRequest.email()).orElseThrow(() ->
                new RuntimeException("Usuário não encontrado"));
        if (passwordEncoder.matches(usuario.getSenha(), loginRequest.senha())){
            var token = this.tokenService.generateToken(usuario);
            return ResponseEntity.ok(new LoginResponse(usuario.getNome(), token));
        }
        return ResponseEntity.badRequest().build();
    }
}
