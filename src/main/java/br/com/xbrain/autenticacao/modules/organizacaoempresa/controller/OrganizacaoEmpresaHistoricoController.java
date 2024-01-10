package br.com.xbrain.autenticacao.modules.organizacaoempresa.controller;

import br.com.xbrain.autenticacao.modules.organizacaoempresa.dto.OrganizacaoEmpresaHistoricoResponse;
import br.com.xbrain.autenticacao.modules.organizacaoempresa.service.OrganizacaoEmpresaHistoricoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "api/organizacao-empresa-historico")
public class OrganizacaoEmpresaHistoricoController {

    private final OrganizacaoEmpresaHistoricoService organizacaoEmpresaHistoricoService;

    @GetMapping("/{id}")
    public List<OrganizacaoEmpresaHistoricoResponse> getHistoricoDaOrganizacaoEmpresa(
        @PathVariable("id") Integer organizacaoEmpresaId) {
        return organizacaoEmpresaHistoricoService.obterHistoricoDaOrganizacaoEmpresa(organizacaoEmpresaId);
    }
}

