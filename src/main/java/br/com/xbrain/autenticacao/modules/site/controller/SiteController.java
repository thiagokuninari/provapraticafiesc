package br.com.xbrain.autenticacao.modules.site.controller;

import br.com.xbrain.autenticacao.modules.comum.dto.PageRequest;
import br.com.xbrain.autenticacao.modules.comum.dto.SelectResponse;
import br.com.xbrain.autenticacao.modules.site.dto.*;
import br.com.xbrain.autenticacao.modules.site.service.SiteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "api/sites")
public class SiteController {

    @Autowired
    private SiteService service;

    @GetMapping
    public Page<SiteResponse> getSites(SiteFiltros filtros, PageRequest pageRequest) {
        return service.getAll(filtros, pageRequest)
            .map(SiteResponse::of);
    }

    @GetMapping("/estado/{estadoId}")
    public List<SelectResponse> getSitesByEstadoId(@PathVariable Integer estadoId) {
        return service.getSitesByEstadoId(estadoId);
    }

    @GetMapping("/ativos")
    public List<SelectResponse> getAllAtivos(SiteFiltros filtros) {
        return service.getAllAtivos(filtros);
    }

    @GetMapping("{id}/supervisores")
    public List<SiteSupervisorResponse> getAllSupervisoresBySiteId(@PathVariable Integer id) {
        return service.getAllSupervisoresBySiteId(id);
    }

    @GetMapping("{id}")
    public SiteResponse getById(@PathVariable Integer id) {
        return SiteResponse.of(service.findById(id), true);
    }

    @GetMapping("{id}/detalhe")
    public SiteDetalheResponse getDetalheSiteById(@PathVariable Integer id) {
        return SiteDetalheResponse.of(service.findById(id));
    }

    @PostMapping
    public SiteResponse save(@RequestBody @Validated SiteRequest request) {
        return SiteResponse.of(service.save(request));
    }

    @PutMapping
    public SiteResponse update(@RequestBody @Validated SiteRequest request) {
        return SiteResponse.of(service.update(request));
    }

    @PutMapping("{id}/inativar")
    public void inativar(@PathVariable Integer id) {
        service.inativar(id);
    }

    @PutMapping("{id}/ativar")
    public void ativar(@PathVariable Integer id) {
        service.ativar(id);
    }

    @GetMapping("estados-disponiveis")
    public List<SelectResponse> buscarEstadosDisponiveis(Integer siteIgnoradoId) {
        return service.buscarEstadosNaoAtribuidosEmSites(siteIgnoradoId);
    }

    @GetMapping("cidades-disponiveis")
    public List<SelectResponse> buscarCidadesDisponiveisPorEstadosIds(@RequestParam List<Integer> estadosIds,
                                                                      Integer siteIgnoradoId) {

        return service.buscarCidadesNaoAtribuidasEmSitesPorEstadosIds(estadosIds, siteIgnoradoId);
    }

    @PutMapping("adicionar-discadora")
    public void adicionarDiscadora(@RequestBody SiteDiscadoraRequest request) {
        service.adicionarDiscadora(request.getDiscadoraId(), request.getSites());
    }

    @PutMapping("remover-discadora")
    public void removerDiscadora(@RequestBody SiteDiscadoraRequest request) {
        service.removerDiscadora(request.getSiteId());
    }
}
