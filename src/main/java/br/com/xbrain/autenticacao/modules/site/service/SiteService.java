package br.com.xbrain.autenticacao.modules.site.service;

import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.comum.dto.PageRequest;
import br.com.xbrain.autenticacao.modules.comum.exception.NotFoundException;
import br.com.xbrain.autenticacao.modules.comum.exception.PermissaoException;
import br.com.xbrain.autenticacao.modules.site.dto.SiteFiltros;
import br.com.xbrain.autenticacao.modules.site.dto.SiteRequest;
import br.com.xbrain.autenticacao.modules.site.model.Site;
import br.com.xbrain.autenticacao.modules.site.repository.SiteRepository;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoFuncionalidade;
import br.com.xbrain.autenticacao.modules.usuario.enums.ECanal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.Objects;

import static br.com.xbrain.autenticacao.modules.comum.enums.ESituacao.A;
import static br.com.xbrain.autenticacao.modules.comum.enums.ESituacao.I;
import static br.com.xbrain.autenticacao.modules.usuario.enums.CodigoFuncionalidade.AUT_2046;
import static br.com.xbrain.autenticacao.modules.usuario.enums.CodigoFuncionalidade.AUT_2047;

@Service
public class SiteService {

    private static final NotFoundException EX_NAO_ENCONTRADO = new NotFoundException("Site não encontrado.");
    @Autowired
    private SiteRepository repository;
    @Autowired
    private AutenticacaoService autenticacaoService;

    public Site findById(Integer id) {
        return repository.findById(id).orElseThrow(() -> EX_NAO_ENCONTRADO);
    }

    public Page<Site> getAll(PageRequest pageRequest, SiteFiltros filtros) {
        validarPermissaoVisualizacao();
        return repository.findAll(filtros.toPredicate(), pageRequest);
    }

    public Site save(SiteRequest request) {
        validarPermissaoEdicao();
        return repository.save(Site.of(request));
    }

    public Site update(SiteRequest request) {
        validarPermissaoEdicao();
        var site = findById(request.getId());
        site.update(request);
        return repository.save(site);
    }

    public void inativar(Integer id) {
        validarPermissaoEdicao();
        var site = findById(id);
        if (Objects.equals(site.getSituacao(), A)) {
            site.inativar();
            repository.save(site);
        }
    }

    public void ativar(Integer id) {
        validarPermissaoEdicao();
        var site = findById(id);
        if (Objects.equals(site.getSituacao(), I)) {
            site.ativar();
            repository.save(site);
        }
    }

    private void validarPermissaoVisualizacao() {
        validarPermissaoCanal(AUT_2046);
    }

    private void validarPermissaoEdicao() {
        validarPermissaoCanal(AUT_2047);
    }

    private void validarPermissaoCanal(CodigoFuncionalidade permissao) {
        var usuario = autenticacaoService.getUsuarioAutenticado();
        if (!usuario.hasPermissao(permissao) || !usuario.hasCanal(ECanal.ATIVO_PROPRIO)) {
            throw new PermissaoException();
        }
    }
}
