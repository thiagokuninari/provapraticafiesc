package br.com.xbrain.autenticacao.modules.usuario.controller;

import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioResponse;
import br.com.xbrain.autenticacao.modules.usuario.enums.AreaAtuacao;
import br.com.xbrain.autenticacao.modules.usuario.service.SupervisorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping(value = "api/supervisor")
public class SupervisorController {

    @Autowired
    private SupervisorService service;

    @GetMapping("/assistentes-vendedores/{id}")
    public List<UsuarioResponse> getAssistentesEVendedores(@PathVariable Integer id) {
        return service.getAssistentesEVendedoresD2dDaCidadeDoSupervisor(id);
    }

    @GetMapping("/por-area-atuacao/{areaAtuacao}/{id}")
    public List<UsuarioResponse> getPorAreaAtuacao(@PathVariable AreaAtuacao areaAtuacao,
                                                   @PathVariable Integer id) {
        return service.getSupervisoresPorAreaAtuacao(areaAtuacao, Collections.singletonList(id));
    }
}
