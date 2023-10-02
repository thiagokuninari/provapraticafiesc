package br.com.xbrain.autenticacao.modules.comum.controller;

import br.com.xbrain.autenticacao.modules.comum.dto.SelectResponse;
import br.com.xbrain.autenticacao.modules.comum.model.Uf;
import br.com.xbrain.autenticacao.modules.comum.service.UfService;
import br.com.xbrain.autenticacao.modules.usuario.dto.UfResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/ufs")
@RequiredArgsConstructor
public class UfController {

    private final UfService ufService;

    @GetMapping
    public Iterable<Uf> getAll() {
        return ufService.findAll(new Sort("nome"));
    }

    @GetMapping("/todas")
    public List<SelectResponse> getAllUfs() {
        return ufService.findAll();
    }

    @GetMapping("/por-regional")
    public List<SelectResponse> getAllByRegional(@RequestParam Integer regionalId) {
        return ufService.findAllByRegionalId(regionalId);
    }

    @GetMapping("/por-regional-com-uf")
    public List<UfResponse> getAllByRegionalComUf(@RequestParam Integer regionalId) {
        return ufService.findAllByRegionalIdComUf(regionalId);
    }
}
