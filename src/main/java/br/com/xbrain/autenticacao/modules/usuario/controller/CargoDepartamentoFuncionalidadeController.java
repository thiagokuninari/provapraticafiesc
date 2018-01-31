package br.com.xbrain.autenticacao.modules.usuario.controller;

import br.com.xbrain.autenticacao.modules.permissao.dto.FuncionalidadeFiltro;
import br.com.xbrain.autenticacao.modules.permissao.dto.FuncionalidadeResponse;
import br.com.xbrain.autenticacao.modules.usuario.dto.FuncionalidadeSaveRequest;
import br.com.xbrain.autenticacao.modules.usuario.service.CargoDepartamentoFuncionalidadeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "api/cargo-departamento-funcionalidade")
public class CargoDepartamentoFuncionalidadeController {

    @Autowired
    private CargoDepartamentoFuncionalidadeService service;

    @RequestMapping(method = RequestMethod.GET)
    public List<FuncionalidadeResponse> getAll(FuncionalidadeFiltro filtro) {
        return FuncionalidadeResponse.convertFromCargoDepartamentoFuncionalidade(
                service.getCargoDepartamentoFuncionalidadeByFiltro(filtro));
    }

    @RequestMapping(method = RequestMethod.POST)
    public void save(@Validated @RequestBody FuncionalidadeSaveRequest request) {
        service.save(request);
    }
}
