package br.com.xbrain.autenticacao.modules.organizacaoempresa.service;

import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.comum.dto.PageRequest;
import br.com.xbrain.autenticacao.modules.comum.dto.SelectResponse;
import br.com.xbrain.autenticacao.modules.comum.exception.NotFoundException;
import br.com.xbrain.autenticacao.modules.comum.exception.ValidacaoException;
import br.com.xbrain.autenticacao.modules.organizacaoempresa.dto.*;
import br.com.xbrain.autenticacao.modules.organizacaoempresa.enums.EHistoricoAcao;
import br.com.xbrain.autenticacao.modules.organizacaoempresa.enums.ESituacaoOrganizacaoEmpresa;
import br.com.xbrain.autenticacao.modules.organizacaoempresa.model.OrganizacaoEmpresa;
import br.com.xbrain.autenticacao.modules.organizacaoempresa.rabbitmq.OrganizacaoEmpresaMqSender;
import br.com.xbrain.autenticacao.modules.organizacaoempresa.repository.OrganizacaoEmpresaRepository;
import br.com.xbrain.autenticacao.modules.usuario.model.Nivel;
import br.com.xbrain.autenticacao.modules.usuario.repository.NivelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class  OrganizacaoEmpresaService {

    private static final NotFoundException EX_NAO_ENCONTRADO = new NotFoundException("Organização não encontrada.");
    private static final NotFoundException EX_NIVEL_NAO_ENCONTRADO =
        new NotFoundException("Nível empresa não encontrada.");
    private static final ValidacaoException ORGANIZACAO_EXISTENTE =
        new ValidacaoException("Organização já cadastrada com o mesmo nome.");
    private static final ValidacaoException ORGANIZACAO_ATIVA =
        new ValidacaoException("Organização já está ativa.");
    private static final ValidacaoException ORGANIZACAO_INATIVA =
        new ValidacaoException("Organização já está inativa.");

    private final OrganizacaoEmpresaRepository organizacaoEmpresaRepository;
    private final OrganizacaoEmpresaHistoricoService historicoService;
    private final AutenticacaoService autenticacaoService;
    private final NivelRepository nivelRepository;
    private final OrganizacaoEmpresaMqSender organizacaoEmpresaMqSender;

    public OrganizacaoEmpresa findById(Integer id) {
        return organizacaoEmpresaRepository.findById(id).orElseThrow(() -> EX_NAO_ENCONTRADO);
    }

    public Page<OrganizacaoEmpresa> getAll(OrganizacaoEmpresaFiltros filtros, PageRequest pageRequest) {
        return organizacaoEmpresaRepository.findAll(filtros.toPredicate().build(), pageRequest);
    }

    public OrganizacaoEmpresa save(OrganizacaoEmpresaRequest request) {
        var nivel = findNivelById(request.getNivelId());

        validarNome(request.getNome());
        var organizacaoEmpresa = organizacaoEmpresaRepository.save(OrganizacaoEmpresa.of(request,
            autenticacaoService.getUsuarioId(), nivel));

        organizacaoEmpresaMqSender.sendSuccess(OrganizacaoEmpresaDto.of(organizacaoEmpresa));
        return organizacaoEmpresa;
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
        organizacaoEmpresaMqSender.sendInativarSituacaoSuccess(OrganizacaoEmpresaDto.of(organizacaoEmpresa));
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
        organizacaoEmpresaMqSender.sendAtivarSituacaoSuccess(OrganizacaoEmpresaDto.of(organizacaoEmpresa));
        organizacaoEmpresaRepository.save(organizacaoEmpresa);
    }

    @Transactional
    public OrganizacaoEmpresa update(Integer id, OrganizacaoEmpresaRequest request) throws ValidacaoException {
        var organizacaoEmpresaToUpdate = findById(id);
        validarNomeParaUpdate(request.getNome(), id);

        historicoService.salvarHistorico(organizacaoEmpresaToUpdate,
            EHistoricoAcao.EDICAO, autenticacaoService.getUsuarioAutenticado());

        var organizacaoNome = organizacaoEmpresaToUpdate.getNome();
        organizacaoEmpresaToUpdate.setNome(request.getNome());
        organizacaoEmpresaToUpdate.setCodigo(request.getCodigo());

        var organizacaoNomeAtualizado = request.getNome();
        var nivelId = organizacaoEmpresaToUpdate.getNivel().getId();
        var organizacaoEmpresaUpdate = new OrganizacaoEmpresaUpdateDto(organizacaoNome, organizacaoNomeAtualizado, nivelId);

        organizacaoEmpresaMqSender.sendUpdateNomeSucess(organizacaoEmpresaUpdate);
        var organizacaoEmpresa = organizacaoEmpresaRepository.save(organizacaoEmpresaToUpdate);

        organizacaoEmpresaMqSender.sendUpdateSuccess(OrganizacaoEmpresaDto.of(organizacaoEmpresa));

        return organizacaoEmpresa;
    }

    private void validarNome(String nome) {
        if (organizacaoEmpresaRepository.existsByNomeIgnoreCase(nome)) {
            throw ORGANIZACAO_EXISTENTE;
        }
    }

    private void validarNomeParaUpdate(String nome, Integer id) {
        if (organizacaoEmpresaRepository.existsByNomeAndIdNot(nome, id)) {
            throw ORGANIZACAO_EXISTENTE;
        }
    }

    public Nivel findNivelById(Integer id) {
        return nivelRepository.findById(id)
            .orElseThrow(() -> EX_NIVEL_NAO_ENCONTRADO);
    }

    public List<OrganizacaoEmpresaResponse> findAllAtivos(OrganizacaoEmpresaFiltros filtros) {
        validarFiltrosConsultaAtivos(filtros);

        var predicate = filtros.toPredicate().comSituacao(ESituacaoOrganizacaoEmpresa.A).build();

        var organizacoes = organizacaoEmpresaRepository
            .findAll(predicate)
            .stream().map(OrganizacaoEmpresaResponse::of)
            .collect(Collectors.toList());

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

    public List<SelectResponse> getAllSelect(OrganizacaoEmpresaFiltros filtros) {
        return organizacaoEmpresaRepository.findByPredicate(getFiltros(filtros).toPredicate().build())
            .stream()
            .map(organizacao -> SelectResponse.of(organizacao.getId(), organizacao.getNome()))
            .collect(Collectors.toList());
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

    private void validarFiltrosConsultaAtivos(OrganizacaoEmpresaFiltros filtros) {
        var usuario = autenticacaoService.getUsuarioAutenticado();

        if (filtros.getNivelId() == null) {
            throw new ValidacaoException("O campo nível Id é obrigatório!");
        } else if (!usuario.isGerenteInternetOperacao()) {
            filtros.setOrganizacaoId(usuario.getOrganizacaoId());
        }
    }

    public List<OrganizacaoEmpresaResponse> findAllOrganizacoesAtivasByNiveisIds(List<Integer> niveisIds) {
        var organizacoes = organizacaoEmpresaRepository
            .findAllAtivosByNivelIdInAndSituacao(niveisIds, ESituacaoOrganizacaoEmpresa.A)
            .stream().map(OrganizacaoEmpresaResponse::of).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(organizacoes)) {
            throw EX_NAO_ENCONTRADO;
        }
        return organizacoes;
    }

    public boolean isOrganizacaoAtiva(String organizacao) {
        if (organizacao == null) {
            throw EX_NAO_ENCONTRADO;
        }
        return organizacaoEmpresaRepository.existsByNomeAndSituacao(organizacao, ESituacaoOrganizacaoEmpresa.A);
    }
}
