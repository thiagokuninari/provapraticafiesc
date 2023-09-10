package br.com.xbrain.autenticacao.modules.site.controller;

import br.com.xbrain.autenticacao.modules.comum.dto.PageRequest;
import br.com.xbrain.autenticacao.modules.comum.dto.SelectResponse;
import br.com.xbrain.autenticacao.modules.site.dto.*;
import br.com.xbrain.autenticacao.modules.site.service.SiteService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.Collection;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "api/sites")
public class SiteController {

    private final SiteService service;

    @GetMapping
    public Page<SiteResponse> getSites(SiteFiltros filtros, PageRequest pageRequest) {
        return service.getAll(filtros, pageRequest)
            .map(SiteResponse::of);
    }

    @GetMapping("exportar-csv")
    public void exportarCsv(SiteFiltros filtros, HttpServletResponse response) {
        service.gerarRelatorioDiscadorasCsv(filtros, response);
    }

    @GetMapping("/estado/{estadoId}")
    public List<SelectResponse> getSitesByEstadoId(@PathVariable Integer estadoId) {
        return service.getSitesByEstadoId(estadoId);
    }

    @GetMapping("/ativos")
    public List<SelectResponse> getAllAtivos(SiteFiltros filtros) {
        return service.getAllAtivos(filtros);
    }

    @GetMapping("{id}/supervisores")
    public List<SiteSupervisorResponse> getAllSupervisoresBySiteId(@PathVariable Integer id) {
        return service.getAllSupervisoresBySiteId(id);
    }

    @GetMapping("{id}/supervisores/hierarquia/{usuarioSuperiorId}")
    public List<SiteSupervisorResponse> getAllSupervisoresByHierarquia(@PathVariable Integer id,
                                                                       @PathVariable Integer usuarioSuperiorId) {
        return service.getAllSupervisoresByHierarquia(id, usuarioSuperiorId);
    }

    @GetMapping("{id}")
    public SiteResponse getById(@PathVariable Integer id) {
        return SiteResponse.of(service.findById(id), true);
    }

    @GetMapping("{id}/detalhe")
    public SiteDetalheResponse getDetalheSiteById(@PathVariable Integer id) {
        return SiteDetalheResponse.of(service.findById(id));
    }

    @GetMapping("{id}/usuarios/ids")
    public Collection<Integer> getUsuariosIdsBySiteId(@PathVariable Integer id) {
        return service.getUsuariosIdsBySiteId(id);
    }

    @PostMapping
    public SiteResponse save(@RequestBody @Validated SiteRequest request) {
        return SiteResponse.of(service.save(request));
    }

    @PutMapping
    public SiteResponse update(@RequestBody @Validated SiteRequest request) {
        return SiteResponse.of(service.update(request));
    }

    @PutMapping("{id}/inativar")
    public void inativar(@PathVariable Integer id) {
        service.inativar(id);
    }

    @GetMapping("estados-disponiveis")
    public List<SelectResponse> buscarEstadosDisponiveis(Integer siteIgnoradoId) {
        return service.buscarEstadosNaoAtribuidosEmSites(siteIgnoradoId);
    }

    @GetMapping("usuario-logado")
    public List<SelectResponse> buscarSitesVinculadosAoUsuarioLogado() {
        return service.getAllByUsuarioLogado();
    }

    @GetMapping("cidades-disponiveis")
    public List<SelectResponse> buscarCidadesDisponiveisPorEstadosIds(@RequestParam List<Integer> estadosIds,
                                                                      Integer siteIgnoradoId) {

        return service.buscarCidadesNaoAtribuidasEmSitesPorEstadosIds(estadosIds, siteIgnoradoId);
    }

    @PutMapping("adicionar-discadora")
    public void adicionarDiscadora(@RequestBody SiteDiscadoraRequest request) {
        service.adicionarDiscadora(request.getDiscadoraId(), request.getSites());
    }

    @PutMapping("remover-discadora")
    public void removerDiscadora(@RequestBody SiteDiscadoraRequest request) {
        service.removerDiscadora(request.getSiteId());
    }

    @GetMapping("/supervisor/{supervisorId}")
    public SiteResponse getSiteBySupervisorId(@PathVariable Integer supervisorId) {
        return service.getSiteBySupervisorId(supervisorId);
    }

    @GetMapping("permitidos")
    public List<SelectResponse> findSitesPermitidosAoUsuarioAutenticado() {
        return service.findSitesPermitidosAoUsuarioAutenticado();
    }

    @GetMapping("assistentes-da-hierarquia/{usuariosSuperioresIds}")
    public List<UsuarioSiteResponse> buscarAssistentesAtivosDaHierarquiaDosUsuariosSuperioresIds(@PathVariable List<Integer>
                                                                                               usuariosSuperioresIds) {
        return service.buscarAssistentesAtivosDaHierarquiaDosUsuariosSuperioresIds(usuariosSuperioresIds);
    }

    @GetMapping("vendedores-da-hierarquia/{usuarioSuperiorId}/sem-equipe-venda")
    public List<UsuarioSiteResponse> buscarVendedoresAtivosDaHierarquiaDoUsuarioSuperiorIdSemEquipeVenda(
        @PathVariable Integer usuarioSuperiorId) {

        return service.buscarVendedoresAtivosDaHierarquiaDoUsuarioSuperiorIdSemEquipeVenda(usuarioSuperiorId);
    }

    @GetMapping("coordenadores/{usuarioId}")
    public List<Integer> buscarCoordenadoresIdsAtivosDoUsuarioId(@PathVariable Integer usuarioId) {
        return service.buscarCoordenadoresIdsAtivosDoUsuarioId(usuarioId);
    }

    @GetMapping("cidade-uf/{cidade}/{uf}")
    public SiteCidadeResponse buscarSiteCidadePorCidadeUf(@PathVariable String cidade, @PathVariable String uf) {
        return service.buscarSiteCidadePorCidadeUf(cidade, uf);
    }

    @GetMapping("codigo-cidade-dbm/{codigoCidadeDbm}")
    public SiteCidadeResponse buscarSiteCidadePorCodigoCidadeDbm(@PathVariable Integer codigoCidadeDbm) {
        return service.buscarSiteCidadePorCodigoCidadeDbm(codigoCidadeDbm);
    }

    @GetMapping("ddd/{ddd}")
    public SiteCidadeResponse buscarSiteCidadePorDdd(@PathVariable Integer ddd) {
        return service.buscarSiteCidadePorDdd(ddd);
    }

    @GetMapping("buscar/todos")
    public List<SiteResponse> buscarTodos(SiteFiltros filtros) {
        return service.buscarTodos(filtros);
    }
}
