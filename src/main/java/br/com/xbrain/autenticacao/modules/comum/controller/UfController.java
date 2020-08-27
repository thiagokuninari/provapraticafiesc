package br.com.xbrain.autenticacao.modules.comum.controller;

import br.com.xbrain.autenticacao.modules.comum.dto.SelectResponse;
import br.com.xbrain.autenticacao.modules.comum.model.Uf;
import br.com.xbrain.autenticacao.modules.comum.service.UfService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/ufs")
public class UfController {

    @Autowired
    private UfService ufService;

    @GetMapping
    public Iterable<Uf> getAll() {
        return ufService.findAll(new Sort("nome"));
    }

    @GetMapping("/todas")
    public List<SelectResponse> getAllUfs() {
        return ufService.findAll();
    }
}
