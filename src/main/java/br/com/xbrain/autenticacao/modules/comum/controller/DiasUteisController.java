package br.com.xbrain.autenticacao.modules.comum.controller;

import br.com.xbrain.autenticacao.modules.comum.dto.DiasUteisRequest;
import br.com.xbrain.autenticacao.modules.comum.service.DiasUteisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping(value = "api/dias-uteis")
public class DiasUteisController {

    @Autowired
    private DiasUteisService diasUteisService;

    @GetMapping
    public LocalDateTime getDataComDiasUteisAdicionado(@Validated DiasUteisRequest request) {
        return diasUteisService.getDataComDiasUteisAdicionado(request);
    }
}
