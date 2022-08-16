package br.com.xbrain.autenticacao.modules.permissao.controller;

import br.com.xbrain.autenticacao.modules.permissao.dto.PermissaoEspecialRequest;
import br.com.xbrain.autenticacao.modules.permissao.model.PermissaoEspecial;
import br.com.xbrain.autenticacao.modules.permissao.service.PermissaoEspecialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "api/permissoes-especiais")
public class PermissaoEspecialController {

    @Autowired
    private PermissaoEspecialService service;

    @PostMapping
    public void save(@Validated @RequestBody PermissaoEspecialRequest request) {
        service.save(request);
    }

    @PutMapping(value = "remover/{usuarioId}/{funcionalidadeId}")
    public PermissaoEspecial remover(@PathVariable("usuarioId") Integer usuarioId,
                                     @PathVariable("funcionalidadeId") Integer funcionalidadeId) {
        return service.remover(usuarioId, funcionalidadeId);
    }

    @PostMapping("processa-permissoes-gerentes-coordenadores")
    public void processaPermissoesEspeciaisGerentesCoordenadores(){
        service.processaPermissoesEspeciaisGerentesCoordenadores();
    }
}
