package br.com.xbrain.autenticacao.modules.organizacaoempresa.service;

import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.comum.dto.PageRequest;
import br.com.xbrain.autenticacao.modules.comum.exception.NotFoundException;
import br.com.xbrain.autenticacao.modules.comum.exception.ValidacaoException;
import br.com.xbrain.autenticacao.modules.organizacaoempresa.dto.OrganizacaoEmpresaFiltros;
import br.com.xbrain.autenticacao.modules.organizacaoempresa.dto.OrganizacaoEmpresaRequest;
import br.com.xbrain.autenticacao.modules.organizacaoempresa.dto.OrganizacaoEmpresaResponse;
import br.com.xbrain.autenticacao.modules.organizacaoempresa.enums.EHistoricoAcao;
import br.com.xbrain.autenticacao.modules.organizacaoempresa.enums.ESituacaoOrganizacaoEmpresa;
import br.com.xbrain.autenticacao.modules.organizacaoempresa.model.ModalidadeEmpresa;
import br.com.xbrain.autenticacao.modules.organizacaoempresa.model.OrganizacaoEmpresa;
import br.com.xbrain.autenticacao.modules.organizacaoempresa.repository.ModalidadeEmpresaRepository;
import br.com.xbrain.autenticacao.modules.organizacaoempresa.repository.OrganizacaoEmpresaRepository;
import br.com.xbrain.autenticacao.modules.usuario.model.Nivel;
import br.com.xbrain.autenticacao.modules.usuario.repository.NivelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class  OrganizacaoEmpresaService {

    private static final NotFoundException EX_NAO_ENCONTRADO = new NotFoundException("Organização não encontrada.");
    private static final NotFoundException EX_NIVEL_NAO_ENCONTRADO =
        new NotFoundException("Nível empresa não encontrada.");
    private static final NotFoundException EX_MODALIDADE_EMPRESA_NAO_ENCONTRADA =
        new NotFoundException("Modalidade empresa não encontrada.");
    private static final ValidacaoException ORGANIZACAO_EXISTENTE =
        new ValidacaoException("Organização já cadastrada com o mesmo nome.");
    private static final ValidacaoException CNPJ_OU_RAZAO_SOCIAL_EXISTENTE =
        new ValidacaoException("Organização já cadastrada.");
    private static final ValidacaoException CNPJ_EXISTENTE =
        new ValidacaoException("Organização já cadastrada com o mesmo CNPJ.");
    private static final ValidacaoException ORGANIZACAO_ATIVA =
        new ValidacaoException("Organização já está ativa.");
    private static final ValidacaoException ORGANIZACAO_INATIVA =
        new ValidacaoException("Organização já está inativa.");

    @Autowired
    private OrganizacaoEmpresaRepository organizacaoEmpresaRepository;

    @Autowired
    private OrganizacaoEmpresaHistoricoService historicoService;

    @Autowired
    private AutenticacaoService autenticacaoService;

    @Autowired
    private NivelRepository nivelRepository;

    @Autowired
    private ModalidadeEmpresaRepository modalidadeEmpresaRepository;

    public OrganizacaoEmpresa findById(Integer id) {
        return organizacaoEmpresaRepository.findById(id).orElseThrow(() -> EX_NAO_ENCONTRADO);
    }

    public Page<OrganizacaoEmpresa> getAll(OrganizacaoEmpresaFiltros filtros, PageRequest pageRequest) {
        return organizacaoEmpresaRepository.findAll(filtros.toPredicate().build(), pageRequest);
    }

    public OrganizacaoEmpresa save(OrganizacaoEmpresaRequest request) {
        validarRazaoSocial(request);
        validarCnpj(request);
        var nivel = validarNivel(request.getNivelId());
        var modalidades = validarModalidadeEmpresa(request.getModalidadesEmpresaIds());

        return organizacaoEmpresaRepository.save(OrganizacaoEmpresa.of(request,
            autenticacaoService.getUsuarioId(), nivel, modalidades));
    }

    @Transactional
    public void inativar(Integer id) {
        var organizacaoEmpresa = findById(id);
        if (!organizacaoEmpresa.isAtivo()) {
            throw ORGANIZACAO_INATIVA;
        }
        organizacaoEmpresa.setSituacao(ESituacaoOrganizacaoEmpresa.I);
        historicoService.salvarHistorico(organizacaoEmpresa, EHistoricoAcao.INATIVACAO,
            autenticacaoService.getUsuarioAutenticado());

        organizacaoEmpresaRepository.save(organizacaoEmpresa);
    }

    @Transactional
    public void ativar(Integer id) {
        var organizacaoEmpresa = findById(id);
        if (organizacaoEmpresa.isAtivo()) {
            throw ORGANIZACAO_ATIVA;

        }
        organizacaoEmpresa.setSituacao(ESituacaoOrganizacaoEmpresa.A);
        historicoService.salvarHistorico(organizacaoEmpresa, EHistoricoAcao.ATIVACAO,
            autenticacaoService.getUsuarioAutenticado());

        organizacaoEmpresaRepository.save(organizacaoEmpresa);
    }

    @Transactional
    public OrganizacaoEmpresa update(Integer id, OrganizacaoEmpresaRequest request) throws ValidacaoException {
        if (validarOrganizacaoJaExistente(id, request)) {
            throw CNPJ_OU_RAZAO_SOCIAL_EXISTENTE;
        }
        var organizacaoEmpresaToUpdate = findById(id);

        var nivel = validarNivel(request.getNivelId());
        var modalidades = validarModalidadeEmpresa(request.getModalidadesEmpresaIds());

        organizacaoEmpresaToUpdate.of(request, modalidades, nivel);

        historicoService.salvarHistorico(organizacaoEmpresaToUpdate,
            EHistoricoAcao.EDICAO, autenticacaoService.getUsuarioAutenticado());

        return organizacaoEmpresaRepository.save(organizacaoEmpresaToUpdate);
    }

    private boolean validarOrganizacaoJaExistente(Integer id, OrganizacaoEmpresaRequest request) {
        return organizacaoEmpresaRepository.existsByRazaoSocialAndCnpjAndIdNot(request.getNome(),
            request.getCnpjSemMascara(), id);
    }

    private void validarCnpj(OrganizacaoEmpresaRequest request) {
        if (organizacaoEmpresaRepository.existsByCnpj(request.getCnpjSemMascara())) {
            throw CNPJ_EXISTENTE;
        }
    }

    private void validarRazaoSocial(OrganizacaoEmpresaRequest request) {
        if (organizacaoEmpresaRepository.existsByRazaoSocialIgnoreCase(request.getNome())) {
            throw ORGANIZACAO_EXISTENTE;
        }
    }

    public Nivel validarNivel(Integer id) {
        return nivelRepository.findById(id)
            .orElseThrow(() -> EX_NIVEL_NAO_ENCONTRADO);
    }

    public List<ModalidadeEmpresa> validarModalidadeEmpresa(List<Integer> ids) {
        var modalidades = modalidadeEmpresaRepository.findAll(ids);
        if (CollectionUtils.isEmpty(modalidades)) {
            throw EX_MODALIDADE_EMPRESA_NAO_ENCONTRADA;
        }
        return modalidades;
    }

    public List<OrganizacaoEmpresaResponse> findAllAtivosByNivelId(Integer nivelId) {
        var organizacoes = organizacaoEmpresaRepository.findAllByNivelIdAndSituacao(nivelId, ESituacaoOrganizacaoEmpresa.A)
            .stream().map(OrganizacaoEmpresaResponse::of).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(organizacoes)) {
            throw EX_NAO_ENCONTRADO;
        }
        return organizacoes;
    }

    public List<OrganizacaoEmpresaResponse> findAllByNivelId(Integer nivelId) {
        var organizacoes = organizacaoEmpresaRepository.findAllByNivelId(nivelId)
            .stream().map(OrganizacaoEmpresaResponse::of).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(organizacoes)) {
            throw EX_NAO_ENCONTRADO;
        }
        return organizacoes;
    }

    public List<OrganizacaoEmpresa> getAllSelect(OrganizacaoEmpresaFiltros filtros) {
        return organizacaoEmpresaRepository.findByPredicate(getFiltros(filtros).toPredicate().build());
    }

    private OrganizacaoEmpresaFiltros getFiltros(OrganizacaoEmpresaFiltros filtros) {
        filtros = Objects.isNull(filtros) ? new OrganizacaoEmpresaFiltros() : filtros;
        var usuario = autenticacaoService.getUsuarioAutenticado();
        if (usuario.isBackoffice()) {
            filtros.setOrganizacaoId(usuario.getOrganizacaoId());
        }
        return filtros;
    }

    public OrganizacaoEmpresaResponse getById(Integer id) {
        return OrganizacaoEmpresaResponse.of(organizacaoEmpresaRepository.findById(id)
            .orElseThrow(() -> EX_NAO_ENCONTRADO));
    }
}
