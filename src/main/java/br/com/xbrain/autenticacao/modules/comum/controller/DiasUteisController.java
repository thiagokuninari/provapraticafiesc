package br.com.xbrain.autenticacao.modules.comum.controller;

import br.com.xbrain.autenticacao.modules.comum.dto.DiasUteisRequest;
import br.com.xbrain.autenticacao.modules.comum.dto.DiasUteisRequestCidadeUf;
import br.com.xbrain.autenticacao.modules.comum.service.DiasUteisService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping(value = "api/dias-uteis")
@RequiredArgsConstructor
public class DiasUteisController {

    private final DiasUteisService diasUteisService;

    @GetMapping
    public LocalDateTime getDataComDiasUteisAdicionado(@Validated DiasUteisRequest request) {
        return diasUteisService.getDataComDiasUteisAdicionado(request);
    }

    @GetMapping("cidade-uf")
    public LocalDateTime getDataComDiasUteisAdicionadoECidadeUf(@Validated DiasUteisRequestCidadeUf request) {
        return diasUteisService.getDataComDiasUteisAdicionadoECidadeUf(request);
    }
}
