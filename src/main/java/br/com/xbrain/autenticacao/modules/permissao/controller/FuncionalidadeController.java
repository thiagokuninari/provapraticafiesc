package br.com.xbrain.autenticacao.modules.permissao.controller;

import br.com.xbrain.autenticacao.modules.permissao.dto.FuncionalidadeResponse;
import br.com.xbrain.autenticacao.modules.permissao.filtros.CargoDepartamentoFuncionalidadeFiltros;
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
    public List<FuncionalidadeResponse> getAll(CargoDepartamentoFuncionalidadeFiltros filtro) {
        return service.getAll();
    }

}
