package br.com.xbrain.autenticacao.modules.organizacaoempresa.service;

import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.call.service.CallService;
import br.com.xbrain.autenticacao.modules.comum.dto.PageRequest;
import br.com.xbrain.autenticacao.modules.comum.dto.SelectResponse;
import br.com.xbrain.autenticacao.modules.comum.enums.ENivel;
import br.com.xbrain.autenticacao.modules.comum.exception.NotFoundException;
import br.com.xbrain.autenticacao.modules.comum.exception.ValidacaoException;
import br.com.xbrain.autenticacao.modules.organizacaoempresa.dto.*;
import br.com.xbrain.autenticacao.modules.organizacaoempresa.enums.EHistoricoAcao;
import br.com.xbrain.autenticacao.modules.organizacaoempresa.enums.ESituacaoOrganizacaoEmpresa;
import br.com.xbrain.autenticacao.modules.organizacaoempresa.model.OrganizacaoEmpresa;
import br.com.xbrain.autenticacao.modules.organizacaoempresa.rabbitmq.OrganizacaoEmpresaMqSender;
import br.com.xbrain.autenticacao.modules.organizacaoempresa.repository.OrganizacaoEmpresaRepository;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoNivel;
import br.com.xbrain.autenticacao.modules.usuario.enums.ECanal;
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

import static br.com.xbrain.autenticacao.modules.usuario.enums.CodigoNivel.BACKOFFICE_SUPORTE_VENDAS;

@Service
@RequiredArgsConstructor
public class  OrganizacaoEmpresaService {

    private static final NotFoundException EX_NAO_ENCONTRADO = new NotFoundException("Organização não encontrada.");
    private static final NotFoundException EX_NIVEL_NAO_ENCONTRADO =
        new NotFoundException("Nível empresa não encontrada.");
    private static final ValidacaoException EX_ORGANIZACAO_EXISTENTE =
        new ValidacaoException("Organização já cadastrada com o mesmo nome ou código nesse nível.");
    private static final ValidacaoException EX_ORGANIZACAO_ATIVA =
        new ValidacaoException("Organização já está ativa.");
    private static final ValidacaoException EX_ORGANIZACAO_INATIVA =
        new ValidacaoException("Organização já está inativa.");
    private static final ValidacaoException EX_CANAL_VAZIO =
        new ValidacaoException("Esse nível requer um canal válido.");

    private final OrganizacaoEmpresaRepository organizacaoEmpresaRepository;
    private final OrganizacaoEmpresaHistoricoService historicoService;
    private final AutenticacaoService autenticacaoService;
    private final NivelRepository nivelRepository;
    private final OrganizacaoEmpresaMqSender organizacaoEmpresaMqSender;
    private final CallService callService;

    public OrganizacaoEmpresa findById(Integer id) {
        return organizacaoEmpresaRepository.findById(id).orElseThrow(() -> EX_NAO_ENCONTRADO);
    }

    public Page<OrganizacaoEmpresa> getAll(OrganizacaoEmpresaFiltros filtros, PageRequest pageRequest) {
        return organizacaoEmpresaRepository.findAll(filtros.toPredicate().build(), pageRequest);
    }

    public OrganizacaoEmpresa save(OrganizacaoEmpresaRequest request) {
        var nivel = findNivelById(request.getNivelId());

        validarNivelOperacao(nivel.getCodigo(), request.getCanal());
        validarNomeECodigoPorNivelId(request.getNome(), request.getCodigo(), request.getNivelId());
        var organizacaoEmpresa = organizacaoEmpresaRepository.save(OrganizacaoEmpresa.of(request,
            autenticacaoService.getUsuarioId(), nivel));
        salvarConfiguracaoSuporteVendas(nivel.getCodigo(), organizacaoEmpresa.getId(), organizacaoEmpresa.getNome());
        return organizacaoEmpresa;
    }

    @Transactional
    public void inativar(Integer id) {
        var organizacaoEmpresa = findById(id);
        if (!organizacaoEmpresa.isAtivo()) {
            throw EX_ORGANIZACAO_INATIVA;
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
            throw EX_ORGANIZACAO_ATIVA;
        }

        organizacaoEmpresa.setSituacao(ESituacaoOrganizacaoEmpresa.A);
        historicoService.salvarHistorico(organizacaoEmpresa, EHistoricoAcao.ATIVACAO,
            autenticacaoService.getUsuarioAutenticado());
        organizacaoEmpresaRepository.save(organizacaoEmpresa);
    }

    @Transactional
    public OrganizacaoEmpresa update(Integer id, OrganizacaoEmpresaRequest request) throws ValidacaoException {
        var organizacaoEmpresaToUpdate = findById(id);

        validarNomeECodigoParaUpdate(request.getNome(), request.getCodigo(), request.getNivelId(), id);

        historicoService.salvarHistorico(organizacaoEmpresaToUpdate,
            EHistoricoAcao.EDICAO, autenticacaoService.getUsuarioAutenticado());

        var organizacaoNome = organizacaoEmpresaToUpdate.getNome();
        organizacaoEmpresaToUpdate.setNome(request.getNome());
        organizacaoEmpresaToUpdate.setCodigo(request.getCodigo());

        var organizacaoNomeAtualizado = request.getNome();
        var nivelId = organizacaoEmpresaToUpdate.getNivel().getId();
        var organizacaoEmpresaUpdate = new OrganizacaoEmpresaUpdateDto(organizacaoNome, organizacaoNomeAtualizado, nivelId);

        organizacaoEmpresaMqSender.sendUpdateNomeSucess(organizacaoEmpresaUpdate);
        return organizacaoEmpresaRepository.save(organizacaoEmpresaToUpdate);
    }

    public void validarNomeECodigoPorNivelId(String nome, String codigo, Integer nivel) {
        if (organizacaoEmpresaRepository.existsByCodigoAndNivelId(codigo, nivel)
            || organizacaoEmpresaRepository.existsByNomeAndNivelId(nome, nivel)) {
            throw EX_ORGANIZACAO_EXISTENTE;
        }
    }

    public void validarNomeECodigoParaUpdate(String nome, String codigo, Integer nivelId, Integer id) {
        if (organizacaoEmpresaRepository.existsByNomeAndNivelIdAndIdNot(nome, nivelId, id)
            || organizacaoEmpresaRepository.existsByCodigoAndNivelIdAndIdNot(codigo, nivelId, id)) {
            throw EX_ORGANIZACAO_EXISTENTE;
        }
    }

    private void validarNivelOperacao(CodigoNivel nivel, ECanal canal) {
        if (CodigoNivel.OPERACAO == nivel && canal == null) {
            throw EX_CANAL_VAZIO;
        }
    }

    public Nivel findNivelById(Integer id) {
        return nivelRepository.findById(id)
            .orElseThrow(() -> EX_NIVEL_NAO_ENCONTRADO);
    }

    public List<OrganizacaoEmpresaResponse> findAllAtivos(OrganizacaoEmpresaFiltros filtros) {
        validarFiltrosConsultaAtivos(filtros);

        var predicate = filtros.toPredicate().comSituacao(ESituacaoOrganizacaoEmpresa.A).build();

        return organizacaoEmpresaRepository
            .findAll(predicate)
            .stream().map(OrganizacaoEmpresaResponse::of)
            .collect(Collectors.toList());
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

    private void salvarConfiguracaoSuporteVendas(CodigoNivel nivel, Integer fornecedorId, String nome) {
        if (BACKOFFICE_SUPORTE_VENDAS == nivel){
            callService.salvarConfiguracaoSuporteVendas(fornecedorId, nome);
        }
    }
}
