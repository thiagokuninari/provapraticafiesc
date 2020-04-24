package br.com.xbrain.autenticacao.modules.comum.controller;

import br.com.xbrain.autenticacao.modules.comum.dto.SelectResponse;
import br.com.xbrain.autenticacao.modules.comum.filtros.OrganizacaoFiltros;
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
    public List<SelectResponse> getAllSelect(@RequestParam(value = "nivelId", required = false) Integer nivelId,
                                             OrganizacaoFiltros filtros) {
        return service.getAllSelect(nivelId, filtros).stream()
            .map(organizacao -> SelectResponse.convertFrom(organizacao.getId(), organizacao.getNome()))
            .collect(Collectors.toList());
    }
}
