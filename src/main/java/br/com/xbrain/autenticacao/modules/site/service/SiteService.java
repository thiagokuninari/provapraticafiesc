package br.com.xbrain.autenticacao.modules.site.service;

import br.com.xbrain.autenticacao.modules.comum.dto.PageRequest;
import br.com.xbrain.autenticacao.modules.comum.dto.SelectResponse;
import br.com.xbrain.autenticacao.modules.comum.exception.NotFoundException;
import br.com.xbrain.autenticacao.modules.comum.exception.ValidacaoException;
import br.com.xbrain.autenticacao.modules.comum.repository.UfRepository;
import br.com.xbrain.autenticacao.modules.comum.util.StringUtil;
import br.com.xbrain.autenticacao.modules.site.dto.SiteFiltros;
import br.com.xbrain.autenticacao.modules.site.dto.SiteRequest;
import br.com.xbrain.autenticacao.modules.site.model.Site;
import br.com.xbrain.autenticacao.modules.site.repository.SiteRepository;
import br.com.xbrain.autenticacao.modules.usuario.model.Cidade;
import br.com.xbrain.autenticacao.modules.usuario.repository.CidadeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static br.com.xbrain.autenticacao.modules.comum.enums.ESituacao.A;
import static br.com.xbrain.autenticacao.modules.comum.enums.ESituacao.I;
import static java.math.BigInteger.ZERO;
import static java.util.stream.Collectors.toList;

@Service
@Transactional
public class SiteService {

    private static final NotFoundException EX_NAO_ENCONTRADO = new NotFoundException("Site não encontrado.");
    private static final ValidacaoException EX_SITE_EXISTENTE = new ValidacaoException("Site já cadastro no sistema.");
    private static final ValidacaoException EX_CIDADE_VINCULADA_A_OUTRO_SITE =
        new ValidacaoException("Existem cidades vinculadas à outro site.");

    @Autowired
    private SiteRepository siteRepository;
    @Autowired
    private UfRepository ufRepository;
    @Autowired
    private CidadeRepository cidadeRepository;

    @Transactional(readOnly = true)
    public Site findById(Integer id) {
        return siteRepository.findById(id).orElseThrow(() -> EX_NAO_ENCONTRADO);
    }

    @Transactional(readOnly = true)
    public Page<Site> getAll(SiteFiltros filtros, PageRequest pageRequest) {
        return siteRepository.findAll(filtros.toPredicate(), pageRequest);
    }

    public Site save(SiteRequest request) {
        validarDadosCadastro(request);
        return siteRepository.save(Site.of(request));
    }

    private void validarDadosCadastro(SiteRequest request) {
        verificarExistencia(request);
        verificarInclusaoCidades(request);
    }

    private void verificarExistencia(SiteRequest request) {
        if (siteRepository.findAll()
            .stream()
            .anyMatch(site -> StringUtil.existeSemelhancaEntreNomes(site.getNome(), request.getNome())
                && !site.getId().equals(request.getId()))) {
            throw EX_SITE_EXISTENTE;
        }
    }

    public Site update(SiteRequest request) {
        validarDadosCadastro(request);
        var site = findById(request.getId());
        site.update(request);
        return site;
    }

    public void inativar(Integer id) {
        var site = findById(id);
        if (Objects.equals(site.getSituacao(), A)) {
            site.inativar();
        }
    }

    public void ativar(Integer id) {
        var site = findById(id);
        if (Objects.equals(site.getSituacao(), I)) {
            site.ativar();
        }
    }

    public List<SelectResponse> buscarEstadosNaoAtribuidosEmSites(Integer siteExclusoId) {
        return Optional.ofNullable(siteExclusoId)
            .map(ufRepository::buscarEstadosNaoAtribuidosEmSitesExcetoPor)
            .orElseGet(ufRepository::buscarEstadosNaoAtribuidosEmSites)
            .stream()
            .map(estado -> SelectResponse.convertFrom(estado.getId(), estado.getNome()))
            .collect(toList());
    }

    public List<SelectResponse> buscarCidadesNaoAtribuidasEmSitesPorEstadosids(List<Integer> estadosIds, Integer siteIgnoradoId) {
        return buscarCidadesDisponiveis(estadosIds, siteIgnoradoId)
            .stream()
            .map(cidade -> SelectResponse.convertFrom(cidade.getId(), cidade.getNomeComUf()))
            .collect(toList());
    }

    private void verificarInclusaoCidades(SiteRequest siteRequest) {
        if (siteRequest.isIncluirCidadesDisponiveis()) {
            incluirCidadesDisponiveis(siteRequest);
        } else {
            validarCidadesDisponiveis(siteRequest);
        }
    }

    private void incluirCidadesDisponiveis(SiteRequest siteRequest) {
        siteRequest.setCidadesIds(
            buscarCidadesDisponiveis(siteRequest.getEstadosIds(), siteRequest.getId())
                .stream()
                .map(Cidade::getId)
                .collect(toList())
        );
    }

    private void validarCidadesDisponiveis(SiteRequest siteRequest) {
        siteRepository.findFirstByCidadesIdInAndIdNot(siteRequest.getCidadesIds(),
            Optional.ofNullable(siteRequest.getId()).orElse(ZERO.intValue()))
            .ifPresent(site -> {
                throw EX_CIDADE_VINCULADA_A_OUTRO_SITE;
            });
    }

    private List<Cidade> buscarCidadesDisponiveis(List<Integer> estadosIds, Integer siteIgnoradoId) {
        return Optional.ofNullable(siteIgnoradoId)
            .map(id -> cidadeRepository
                .buscarCidadesNaoAtribuidasEmSitesPorEstadosIdsExcetoPor(estadosIds, id))
            .orElseGet(() -> cidadeRepository
                .buscarCidadesNaoAtribuidasEmSitesPorEstadosIds(estadosIds));
    }
}
