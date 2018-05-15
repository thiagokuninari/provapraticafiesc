package br.com.xbrain.autenticacao.modules.comum.controller;

import br.com.xbrain.autenticacao.modules.comum.dto.GrupoDto;
import br.com.xbrain.autenticacao.modules.comum.service.GrupoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "api/grupos")
public class GrupoController {

    @Autowired
    private GrupoService service;

    @RequestMapping(method = RequestMethod.GET)
    public List<GrupoDto> getAtivosPorRegional(@RequestParam(required = false) Integer regionalId) {
        if (regionalId != null) {
            return service.getAllByRegionalId(regionalId);
        } else {
            return service.getAllAtiva();
        }
    }
}