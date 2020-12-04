package br.com.xbrain.autenticacao.modules.cep.controller;

import br.com.xbrain.autenticacao.modules.cep.service.ConsultaCepService;
import br.com.xbrain.autenticacao.modules.usuario.dto.CidadeUfResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "api/cep")
public class CepController {

    @Autowired
    private ConsultaCepService consultaCepService;

    @GetMapping("{cep}")
    public ResponseEntity<CidadeUfResponse> buscarCidadeEstado(@PathVariable String cep) {
        return ResponseEntity.ok(consultaCepService.consultarCep(cep));
    }
}
