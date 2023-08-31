package br.com.xbrain.autenticacao.modules.horarioacesso.controller;

import br.com.xbrain.autenticacao.modules.comum.dto.PageRequest;
import br.com.xbrain.autenticacao.modules.horarioacesso.dto.HorarioAcessoFiltros;
import br.com.xbrain.autenticacao.modules.horarioacesso.dto.HorarioAcessoRequest;
import br.com.xbrain.autenticacao.modules.horarioacesso.dto.HorarioAcessoResponse;
import br.com.xbrain.autenticacao.modules.horarioacesso.service.HorarioAcessoService;
import br.com.xbrain.autenticacao.modules.usuario.enums.ECanal;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/horarios-acesso")
@RequiredArgsConstructor
public class HorarioAcessoController {

    private final HorarioAcessoService service;

    @GetMapping
    public Page<HorarioAcessoResponse> getHorariosAcesso(PageRequest pageable, HorarioAcessoFiltros filtros) {
        return service.getHorariosAcesso(pageable, filtros);
    }

    @GetMapping("{id}")
    public HorarioAcessoResponse getHorarioAcesso(@PathVariable Integer id) {
        return service.getHorarioAcesso(id);
    }

    @GetMapping("{id}/historico")
    public Page<HorarioAcessoResponse> getHistoricos(PageRequest pageable, @PathVariable("id") Integer horarioAcessoId) {
        return service.getHistoricos(pageable, horarioAcessoId);
    }

    @PostMapping
    public HorarioAcessoResponse save(@Validated @RequestBody HorarioAcessoRequest request) {
        return HorarioAcessoResponse.of(service.save(request));
    }

    @GetMapping("status")
    public boolean getHorarioAcessoStatus(@RequestHeader("X-Usuario-Canal") ECanal canal) {
        return service.getStatus(canal);
    }

    @GetMapping("status/{siteId}")
    public boolean getHorarioAcessoStatusByIdSite(@RequestHeader("X-Usuario-Canal") ECanal canal,
                                                  @PathVariable("siteId") int siteId) {
        return service.getStatus(canal, siteId);
    }
}
