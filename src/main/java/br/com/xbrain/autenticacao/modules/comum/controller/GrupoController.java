package br.com.xbrain.autenticacao.modules.comum.controller;

import br.com.xbrain.autenticacao.modules.comum.dto.GrupoDto;
import br.com.xbrain.autenticacao.modules.comum.service.GrupoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/usuario")
    public List<GrupoDto> getAllByRegionalIdAndUsuarioId(@RequestParam Integer regionalId, @RequestParam Integer usuarioId) {
        return service.getAllByRegionalIdAndUsuarioId(regionalId, usuarioId);
    }
}