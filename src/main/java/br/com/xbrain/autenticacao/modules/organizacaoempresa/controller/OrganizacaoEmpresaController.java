package br.com.xbrain.autenticacao.modules.organizacaoempresa.controller;

import br.com.xbrain.autenticacao.modules.comum.dto.PageRequest;
import br.com.xbrain.autenticacao.modules.organizacaoempresa.dto.OrganizacaoEmpresaFiltros;
import br.com.xbrain.autenticacao.modules.organizacaoempresa.dto.OrganizacaoEmpresaRequest;
import br.com.xbrain.autenticacao.modules.organizacaoempresa.dto.OrganizacaoEmpresaResponse;
import br.com.xbrain.autenticacao.modules.organizacaoempresa.service.OrganizacaoEmpresaService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "api/organizacao-empresa")
public class OrganizacaoEmpresaController {

    private final OrganizacaoEmpresaService service;

    @GetMapping
    public Page<OrganizacaoEmpresaResponse> getOrganizacaoEmpresa(OrganizacaoEmpresaFiltros filtros,
                                                                  PageRequest pageRequest) {
        return service.getAll(filtros, pageRequest)
            .map(OrganizacaoEmpresaResponse::of);
    }

    @GetMapping("{id}")
    public OrganizacaoEmpresaResponse findById(@PathVariable Integer id) {
        return OrganizacaoEmpresaResponse.of(service.findById(id));
    }

    @PostMapping
    @ResponseStatus(CREATED)
    public OrganizacaoEmpresaResponse save(@RequestBody @Validated OrganizacaoEmpresaRequest request) {
        return OrganizacaoEmpresaResponse.of(service.save(request));
    }

    @PutMapping("{id}/inativar")
    public void inativar(@PathVariable Integer id) {
        service.inativar(id);
    }

    @PutMapping("{id}/ativar")
    public void ativar(@PathVariable Integer id) {
        service.ativar(id);
    }

    @PutMapping("{id}/editar")
    public OrganizacaoEmpresaResponse update(@PathVariable Integer id,
                                             @Validated @RequestBody OrganizacaoEmpresaRequest request) {
        return OrganizacaoEmpresaResponse.of(service.update(id, request));
    }

    @GetMapping("nivel")
    public List<OrganizacaoEmpresaResponse> findAllAtivosByNivelId(@NotNull @RequestParam Integer nivelId) {
        return service.findAllAtivosByNivelId(nivelId);
    }

    @GetMapping("por-nivel")
    public List<OrganizacaoEmpresaResponse> findByNivel(@NotNull @RequestParam Integer nivelId) {
        return service.findAllByNivelId(nivelId);
    }
}
