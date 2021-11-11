package br.com.xbrain.autenticacao.modules.horarioacesso.controller;

import br.com.xbrain.autenticacao.modules.horarioacesso.dto.HorarioAcessoRequest;
import br.com.xbrain.autenticacao.modules.horarioacesso.service.HorarioAcessoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PutMapping;

public class HorarioAcessoController {

    @Autowired
    private HorarioAcessoService service;

    @PutMapping("/edita-dias-horarios")
    public void editarHorarios(HorarioAcessoRequest horarioAcesso) {
        service.editHorario(horarioAcesso);
    }
}
