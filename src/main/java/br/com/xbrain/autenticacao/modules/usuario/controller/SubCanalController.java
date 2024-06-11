package br.com.xbrain.autenticacao.modules.usuario.controller;

import br.com.xbrain.autenticacao.modules.comum.dto.PageRequest;
import br.com.xbrain.autenticacao.modules.comum.enums.Eboolean;
import br.com.xbrain.autenticacao.modules.usuario.dto.SubCanalCompletDto;
import br.com.xbrain.autenticacao.modules.usuario.dto.SubCanalDto;
import br.com.xbrain.autenticacao.modules.usuario.dto.SubCanalFiltros;
import br.com.xbrain.autenticacao.modules.usuario.dto.SubCanalHistoricoResponse;
import br.com.xbrain.autenticacao.modules.usuario.service.SubCanalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping(path = "api/sub-canais")
public class SubCanalController {

    @Autowired
    private SubCanalService service;

    @GetMapping
    public List<SubCanalDto> getAllSubCanais() {
        return service.getAll();
    }

    @GetMapping("listar")
    public Page<SubCanalCompletDto> getAllSubCanaisConfiguracoes(PageRequest pageRequest, SubCanalFiltros filtros) {
        return service.getAllConfiguracoes(pageRequest, filtros);
    }

    @GetMapping("{id}")
    public SubCanalDto getSubCanalById(@PathVariable int id) {
        return service.getSubCanalById(id);
    }

    @GetMapping("{id}/detalhar")
    public SubCanalCompletDto getSubCanalCompletById(@PathVariable int id) {
        return service.getSubCanalCompletById(id);
    }

    @GetMapping("usuario-subcanal/{usuarioId}")
    public Set<SubCanalDto> getByUsuarioId(@PathVariable Integer usuarioId) {
        return service.getSubCanalByUsuarioId(usuarioId);
    }

    @PostMapping("editar")
    public void editar(@RequestBody @Validated SubCanalCompletDto request) {
        service.editar(request);
    }

    @GetMapping("{id}/verificar-nova-checagem-credito-d2d")
    public Eboolean isNovaChecagemCreditoD2d(@PathVariable Integer id) {
        return service.isNovaChecagemCreditoD2d(id);
    }

    @GetMapping("{id}/verificar-nova-checagem-viabilidade-d2d")
    public Eboolean isNovaChecagemViabilidadeD2d(@PathVariable Integer id) {
        return service.isNovaChecagemViabilidadeD2d(id);
    }

    @GetMapping("{id}/historico")
    public Page<SubCanalHistoricoResponse> getHistorico(PageRequest pageable, @PathVariable Integer id) {
        return service.getHistorico(id, pageable);
    }
}
