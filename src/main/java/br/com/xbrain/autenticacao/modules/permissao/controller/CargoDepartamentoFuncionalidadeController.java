package br.com.xbrain.autenticacao.modules.permissao.controller;

import br.com.xbrain.autenticacao.modules.comum.dto.PageRequest;
import br.com.xbrain.autenticacao.modules.permissao.dto.CargoDepartamentoFuncionalidadeResponse;
import br.com.xbrain.autenticacao.modules.permissao.filtros.CargoDepartamentoFuncionalidadeFiltros;
import br.com.xbrain.autenticacao.modules.permissao.model.CargoDepartamentoFuncionalidade;
import br.com.xbrain.autenticacao.modules.permissao.service.CargoDepartamentoFuncionalidadeService;
import br.com.xbrain.autenticacao.modules.usuario.dto.FuncionalidadeSaveRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "api/cargo-departamento-funcionalidade")
public class CargoDepartamentoFuncionalidadeController {

    @Autowired
    private CargoDepartamentoFuncionalidadeService service;

    @RequestMapping(method = RequestMethod.GET)
    public List<CargoDepartamentoFuncionalidadeResponse> getAll(CargoDepartamentoFuncionalidadeFiltros filtro) {
        List<CargoDepartamentoFuncionalidade> lista = service.getCargoDepartamentoFuncionalidadeByFiltro(filtro);
        return CargoDepartamentoFuncionalidadeResponse.convertFrom(lista);
    }

    @RequestMapping(name = "/pages", method = RequestMethod.GET)
    public PageImpl<CargoDepartamentoFuncionalidadeResponse> getAll(PageRequest pageRequest,
                                                                    CargoDepartamentoFuncionalidadeFiltros filtros) {
        Page<CargoDepartamentoFuncionalidade> page = service.getAll(pageRequest, filtros);
        return new PageImpl<>(
                page
                        .getContent()
                        .stream()
                        .map(CargoDepartamentoFuncionalidadeResponse::new)
                        .collect(Collectors.toList()),
                pageRequest,
                page.getTotalElements());
    }

    @RequestMapping(method = RequestMethod.POST)
    public void save(@Validated @RequestBody FuncionalidadeSaveRequest request) {
        service.save(request);
    }
}
