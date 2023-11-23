package br.com.xbrain.autenticacao.modules.comum.controller;

import br.com.xbrain.autenticacao.modules.comum.dto.GrupoDto;
import br.com.xbrain.autenticacao.modules.comum.service.GrupoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "api/grupos")
@RequiredArgsConstructor
public class GrupoController {

    private final GrupoService service;

    @GetMapping
    public List<GrupoDto> getAtivosPorRegional(@RequestParam(required = false) Integer regionalId) {
        if (regionalId != null) {
            return service.getAllByRegionalId(regionalId);
        } else {
            return service.getAllAtiva();
        }
    }

    @GetMapping("/regional/{regionalId}/usuario/{usuarioId}")
    public List<GrupoDto> getAllByRegionalIdAndUsuarioId(@PathVariable Integer regionalId, @PathVariable Integer usuarioId) {
        return service.getAllByRegionalIdAndUsuarioId(regionalId, usuarioId);
    }

    @GetMapping("comunicados")
    public List<GrupoDto> getAtivosParaComunicados(@RequestParam Integer regionalId) {
        return service.getAtivosParaComunicados(regionalId);
    }

    @GetMapping("/{grupoId}")
    public GrupoDto findById(@PathVariable Integer grupoId) {
        return service.findById(grupoId);
    }
}
