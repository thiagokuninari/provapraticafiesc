package br.com.xbrain.autenticacao.modules.permissao.controller;

import br.com.xbrain.autenticacao.modules.permissao.dto.FuncionalidadeFiltro;
import br.com.xbrain.autenticacao.modules.permissao.dto.FuncionalidadeResponse;
import br.com.xbrain.autenticacao.modules.permissao.service.FuncionalidadeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "api/funcionalidades")
public class FuncionalidadeController {

    @Autowired
    private FuncionalidadeService service;

    @RequestMapping(method = RequestMethod.GET)
    public List<FuncionalidadeResponse> getAll(FuncionalidadeFiltro filtro) {
        return service.getAll();
    }

    //TODO endpoint que retorna todas as funcionalidades de  acordo com os filtros selecionados na tela
    //TODO endpoint que inclui funcionalidades para nivel - departamento - cargo
    //TODO endpoint que remove funcionalidades de nivel - departamento - cargo
    //TODO manter histórico de inclusão e remoção
}
