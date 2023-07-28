package br.com.xbrain.autenticacao.modules.permissao.controller;

import br.com.xbrain.autenticacao.modules.comum.dto.PageRequest;
import br.com.xbrain.autenticacao.modules.permissao.dto.CargoDepartamentoFuncionalidadeRequest;
import br.com.xbrain.autenticacao.modules.permissao.dto.CargoDepartamentoFuncionalidadeResponse;
import br.com.xbrain.autenticacao.modules.permissao.filtros.CargoDepartamentoFuncionalidadeFiltros;
import br.com.xbrain.autenticacao.modules.permissao.model.CargoDepartamentoFuncionalidade;
import br.com.xbrain.autenticacao.modules.permissao.service.CargoDepartamentoFuncionalidadeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "api/cargo-departamento-funcionalidade")
@RequiredArgsConstructor
public class CargoDepartamentoFuncionalidadeController {

    private final CargoDepartamentoFuncionalidadeService service;

    @PostMapping
    public void save(@Validated @RequestBody CargoDepartamentoFuncionalidadeRequest request) {
        service.save(request);
    }

    @GetMapping
    public List<CargoDepartamentoFuncionalidadeResponse> getAll(CargoDepartamentoFuncionalidadeFiltros filtro) {
        List<CargoDepartamentoFuncionalidade> lista = service.getCargoDepartamentoFuncionalidadeByFiltro(filtro);
        return CargoDepartamentoFuncionalidadeResponse.convertFrom(lista);
    }

    @GetMapping("pages")
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

    @PutMapping("remover/{id}")
    public void remover(@PathVariable("id") int id) {
        service.remover(id);
    }

    @PutMapping("deslogar/{cargoId}/{departamentoId}")
    public void deslogarUsuarios(@PathVariable("cargoId") int cargoId, @PathVariable("departamentoId") int departamentoId) {
        service.deslogar(cargoId, departamentoId);
    }
}
