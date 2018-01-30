package br.com.xbrain.autenticacao.modules.permissao.controller;

import br.com.xbrain.autenticacao.modules.permissao.dto.FuncionalidadeFiltro;
import br.com.xbrain.autenticacao.modules.permissao.dto.FuncionalidadeResponse;
import br.com.xbrain.autenticacao.modules.permissao.repository.FuncionalidadeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "api/funcionalidades")
public class FuncionalidadeController {

    @Autowired
    private FuncionalidadeRepository repository;

    @RequestMapping(method = RequestMethod.GET)
    public Iterable<FuncionalidadeResponse> getAll(FuncionalidadeFiltro filtro) {
        return FuncionalidadeResponse.convertFrom(repository.findAll());
    }

    //TODO endpoint que retorna todas as funcionalidades de  acordo com os filtros selecionados na tela
    //TODO endpoint que inclui funcionalidades para nivel - departamento - cargo
    //TODO endpoint que remove funcionalidades de nivel - departamento - cargo
    //TODO manter histórico de inclusão e remoção
}
