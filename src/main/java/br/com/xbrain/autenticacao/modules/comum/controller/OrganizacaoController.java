package br.com.xbrain.autenticacao.modules.comum.controller;

import br.com.xbrain.autenticacao.modules.comum.dto.OrganizacaoResponse;
import br.com.xbrain.autenticacao.modules.comum.dto.SelectResponse;
import br.com.xbrain.autenticacao.modules.comum.filtros.OrganizacaoFiltros;
import br.com.xbrain.autenticacao.modules.comum.service.OrganizacaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "api/organizacoes")
@RequiredArgsConstructor
public class OrganizacaoController {

    private final OrganizacaoService service;

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
