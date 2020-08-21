package br.com.xbrain.autenticacao.modules.comum.controller;

import br.com.xbrain.autenticacao.modules.comum.dto.OrganizacaoResponse;
import br.com.xbrain.autenticacao.modules.comum.dto.SelectResponse;
import br.com.xbrain.autenticacao.modules.comum.filtros.OrganizacaoFiltros;
import br.com.xbrain.autenticacao.modules.comum.service.OrganizacaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "api/organizacoes")
public class OrganizacaoController {

    @Autowired
    private OrganizacaoService service;

    @GetMapping("select")
    public List<SelectResponse> getAllSelect(OrganizacaoFiltros filtros) {
        return service.getAllSelect(filtros).stream()
            .map(organizacao -> SelectResponse.of(organizacao.getId(), organizacao.getNome()))
            .collect(Collectors.toList());
    }

    @GetMapping("{id}")
    public OrganizacaoResponse getById(@PathVariable Integer id) {
        return service.getById(id);
    }
}
