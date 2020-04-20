package br.com.xbrain.autenticacao.modules.comum.controller;

import br.com.xbrain.autenticacao.modules.comum.dto.SelectResponse;
import br.com.xbrain.autenticacao.modules.comum.service.OrganizacaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "api/organizacoes")
public class OrganizacaoController {

    @Autowired
    private OrganizacaoService service;

    @GetMapping("select")
    public List<SelectResponse> getAllSelect(@RequestParam(value = "nivelId", required = false) Integer nivelId) {
        return service.getAllSelect(nivelId).stream()
            .map(organizacao -> SelectResponse.of(organizacao.getId(), organizacao.getNome()))
                .collect(Collectors.toList());
    }
}
