package br.com.xbrain.autenticacao.modules.organizacaoempresa.controller;

import br.com.xbrain.autenticacao.modules.comum.dto.SelectResponse;
import br.com.xbrain.autenticacao.modules.organizacaoempresa.service.ModalidadeEmpresaService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/modalidade-empresa")
public class ModalidadeEmpresaController {

    private final ModalidadeEmpresaService service;

    @GetMapping
    public List<SelectResponse> buscarModalidadeEmpresa() {
        return service.getAllModalidadeEmpresa();
    }
}
