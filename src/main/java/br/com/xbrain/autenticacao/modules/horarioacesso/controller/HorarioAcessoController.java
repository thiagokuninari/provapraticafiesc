package br.com.xbrain.autenticacao.modules.horarioacesso.controller;

import br.com.xbrain.autenticacao.modules.horarioacesso.dto.HorarioAcessoConsultaDto;
import br.com.xbrain.autenticacao.modules.horarioacesso.dto.HorarioAcessoRequest;
import br.com.xbrain.autenticacao.modules.horarioacesso.predicate.HorarioAcessoFiltros;
import br.com.xbrain.autenticacao.modules.horarioacesso.service.HorarioAcessoService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;

public class HorarioAcessoController {

    @Autowired
    private HorarioAcessoService service;

    @GetMapping
    public List<HorarioAcessoConsultaDto> retornaHorarios(HorarioAcessoFiltros filtros) {
        return service.getAll(filtros);
    }

    @PutMapping("/edita-dias-horarios")
    public void editarHorarios(HorarioAcessoRequest horarioAcesso) {
        service.editHorario(horarioAcesso);
    }
}
