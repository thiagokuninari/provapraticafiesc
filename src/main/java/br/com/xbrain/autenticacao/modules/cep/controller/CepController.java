package br.com.xbrain.autenticacao.modules.cep.controller;

import br.com.xbrain.autenticacao.modules.cep.dto.ConsultaCepResponse;
import br.com.xbrain.autenticacao.modules.cep.service.ConsultaCepService;
import br.com.xbrain.autenticacao.modules.usuario.dto.CidadeUfResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "api/cep")
public class CepController {

    private final ConsultaCepService consultaCepService;

    @GetMapping("{cep}")
    public ResponseEntity<CidadeUfResponse> buscarCidadeEstado(@PathVariable String cep) {
        return ResponseEntity.ok(consultaCepService.consultarCep(cep));
    }

    @PostMapping
    public List<ConsultaCepResponse> buscarCidadesPorCeps(@RequestBody List<String> ceps) {
        return consultaCepService.consultarCeps(ceps);
    }
}
