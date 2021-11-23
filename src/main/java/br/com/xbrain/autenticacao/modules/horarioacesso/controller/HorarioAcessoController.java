package br.com.xbrain.autenticacao.modules.horarioacesso.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.xbrain.autenticacao.modules.horarioacesso.dto.HorarioAcessoFiltros;
import br.com.xbrain.autenticacao.modules.horarioacesso.dto.HorarioAcessoRequest;
import br.com.xbrain.autenticacao.modules.horarioacesso.dto.HorarioAcessoResponse;
import br.com.xbrain.autenticacao.modules.horarioacesso.service.HorarioAcessoService;

@RestController
@RequestMapping("/api/horarios-acesso")
public class HorarioAcessoController {

    @Autowired
    private HorarioAcessoService service;

    @GetMapping
    public List<HorarioAcessoResponse> getHorariosAcesso(HorarioAcessoFiltros filtros) {
        return service.getHorariosAcesso(filtros);
    }

    @GetMapping("{id}")
    public HorarioAcessoResponse getHorarioAcesso(@PathVariable Integer id) {
        return service.getHorarioAcesso(id);
    }

    @GetMapping("{id}/historico")
    public List<HorarioAcessoResponse> getHistoricos(@PathVariable("id") Integer horarioAcessoId) {
        return service.getHistoricos(horarioAcessoId);
    }

    @PostMapping
    public HorarioAcessoResponse save(@Validated @RequestBody HorarioAcessoRequest request) {
        return HorarioAcessoResponse.of(service.save(request));
    }
}
