package br.com.xbrain.autenticacao.modules.horarioacesso.controller;

import br.com.xbrain.autenticacao.modules.horarioacesso.dto.HorarioAcessoConsultaDto;
import br.com.xbrain.autenticacao.modules.horarioacesso.dto.HorarioAcessoRequest;
import br.com.xbrain.autenticacao.modules.horarioacesso.predicate.HorarioAcessoFiltros;
import br.com.xbrain.autenticacao.modules.horarioacesso.service.HorarioAcessoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/api/horarios-acesso")
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

    @GetMapping("/historico/{id}")
    public List<HorarioAcessoConsultaDto> getHistorico(@PathVariable("id") Integer horarioAcessoId) {
        return service.getHistorico(horarioAcessoId);
    }
}
