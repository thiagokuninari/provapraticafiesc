package br.com.xbrain.autenticacao.modules.horarioacesso.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PutMapping;

import br.com.xbrain.autenticacao.modules.horarioacesso.dto.HorarioAcessoDiaDto;
import br.com.xbrain.autenticacao.modules.horarioacesso.service.HorarioAcessoService;

public class HorarioAcessoController {

    @Autowired
    private HorarioAcessoService service;

    @PutMapping("/edita-dias-horarios")
    public void editarHorarios(Integer id, List<HorarioAcessoDiaDto> horarios) {
        service.editaAcesso(id, horarios);
    }
}
