package br.com.xbrain.autenticacao.modules.site.controller;

import br.com.xbrain.autenticacao.modules.comum.dto.PageRequest;
import br.com.xbrain.autenticacao.modules.site.dto.SiteFiltros;
import br.com.xbrain.autenticacao.modules.site.dto.SiteRequest;
import br.com.xbrain.autenticacao.modules.site.dto.SiteResponse;
import br.com.xbrain.autenticacao.modules.site.service.SiteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "api/sites")
public class SiteController {

    @Autowired
    private SiteService service;

    @GetMapping
    public Page<SiteResponse> getSites(PageRequest pageRequest, SiteFiltros filtros) {
        return service.getAll(pageRequest, filtros)
                .map(SiteResponse::of);
    }

    @GetMapping("{id}")
    public SiteResponse getById(@PathVariable("id") Integer id) {
        return SiteResponse.of(service.findById(id));
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
    public void inativar(@PathVariable("id") Integer id) {
        service.inativar(id);
    }

    @PutMapping("{id}/ativar")
    public void ativar(@PathVariable("id") Integer id) {
        service.ativar(id);
    }

}
