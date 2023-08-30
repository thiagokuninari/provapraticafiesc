package br.com.xbrain.autenticacao.modules.permissao.controller;

import br.com.xbrain.autenticacao.modules.permissao.dto.PermissaoEspecialRequest;
import br.com.xbrain.autenticacao.modules.permissao.model.PermissaoEspecial;
import br.com.xbrain.autenticacao.modules.permissao.service.PermissaoEspecialService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "api/permissoes-especiais")
@RequiredArgsConstructor
public class PermissaoEspecialController {

    private final PermissaoEspecialService service;

    @PostMapping
    public void save(@Validated @RequestBody PermissaoEspecialRequest request) {
        service.save(request);
    }

    @PutMapping(value = "remover/{usuarioId}/{funcionalidadeId}")
    public PermissaoEspecial remover(@PathVariable("usuarioId") Integer usuarioId,
                                     @PathVariable("funcionalidadeId") Integer funcionalidadeId) {
        return service.remover(usuarioId, funcionalidadeId);
    }

    @PostMapping("processar-permissoes-gerentes-coordenadores")
    public void processarPermissoesEspeciaisGerentesCoordenadores(@RequestParam(required = false) List<Integer> aaIds) {
        service.processarPermissoesEspeciaisGerentesCoordenadores(aaIds);
    }

    @PostMapping("reprocessar-permissoes-socios-secundarios")
    public void reprocessarPermissoesEspeciaisSociosSecundarios(@RequestParam(required = false) List<Integer> aaIds) {
        service.reprocessarPermissoesEspeciaisSociosSecundarios(aaIds);
    }
}
