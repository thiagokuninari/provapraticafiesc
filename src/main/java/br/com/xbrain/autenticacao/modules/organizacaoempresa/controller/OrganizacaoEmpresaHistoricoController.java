package br.com.xbrain.autenticacao.modules.organizacaoempresa.controller;

import br.com.xbrain.autenticacao.modules.organizacaoempresa.dto.OrganizacaoEmpresaHistoricoResponse;
import br.com.xbrain.autenticacao.modules.organizacaoempresa.service.OrganizacaoEmpresaHistoricoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "api/organizacao-empresa-historico")
public class OrganizacaoEmpresaHistoricoController {

    @Autowired
    private OrganizacaoEmpresaHistoricoService organizacaoEmpresaHistoricoService;

    @GetMapping("/{id}")
    public List<OrganizacaoEmpresaHistoricoResponse> getHistoricoDaOrganizacaoEmpresa(
        @PathVariable("id") Integer organizacaoEmpresaId) {
        return organizacaoEmpresaHistoricoService.obterHistoricoDaOrganizacaoEmpresa(organizacaoEmpresaId);
    }
}

