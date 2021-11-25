package br.com.xbrain.autenticacao.modules.horarioacesso.controller;

import br.com.xbrain.autenticacao.modules.comum.dto.PageRequest;
import br.com.xbrain.autenticacao.modules.horarioacesso.dto.HorarioAcessoFiltros;
import br.com.xbrain.autenticacao.modules.horarioacesso.dto.HorarioAcessoRequest;
import br.com.xbrain.autenticacao.modules.horarioacesso.dto.HorarioAcessoResponse;
import br.com.xbrain.autenticacao.modules.horarioacesso.model.HorarioAtuacao;
import br.com.xbrain.autenticacao.modules.horarioacesso.repository.HorarioAtuacaoRepository;
import br.com.xbrain.autenticacao.modules.horarioacesso.service.HorarioAcessoService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/horarios-acesso")
public class HorarioAcessoController {

    @Autowired
    private HorarioAcessoService service;

    @Autowired
    private HorarioAtuacaoRepository atuacaoRepository;

    @GetMapping("/teste/{id}")
    public List<HorarioAtuacao> getAll(@PathVariable("id") int id) {
        return atuacaoRepository.findByHorarioAcessoId(id);
    }

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
}
