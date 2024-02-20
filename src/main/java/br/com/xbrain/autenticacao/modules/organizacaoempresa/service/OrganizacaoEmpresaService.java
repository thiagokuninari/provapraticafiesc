package br.com.xbrain.autenticacao.modules.organizacaoempresa.service;

import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.call.service.CallService;
import br.com.xbrain.autenticacao.modules.comum.dto.PageRequest;
import br.com.xbrain.autenticacao.modules.comum.dto.SelectResponse;
import br.com.xbrain.autenticacao.modules.comum.exception.NotFoundException;
import br.com.xbrain.autenticacao.modules.comum.exception.ValidacaoException;
import br.com.xbrain.autenticacao.modules.organizacaoempresa.dto.OrganizacaoEmpresaFiltros;
import br.com.xbrain.autenticacao.modules.organizacaoempresa.dto.OrganizacaoEmpresaRequest;
import br.com.xbrain.autenticacao.modules.organizacaoempresa.dto.OrganizacaoEmpresaResponse;
import br.com.xbrain.autenticacao.modules.organizacaoempresa.dto.OrganizacaoEmpresaUpdateDto;
import br.com.xbrain.autenticacao.modules.organizacaoempresa.enums.EHistoricoAcao;
import br.com.xbrain.autenticacao.modules.organizacaoempresa.enums.ESituacaoOrganizacaoEmpresa;
import br.com.xbrain.autenticacao.modules.organizacaoempresa.model.OrganizacaoEmpresa;
import br.com.xbrain.autenticacao.modules.organizacaoempresa.rabbitmq.OrganizacaoEmpresaMqSender;
import br.com.xbrain.autenticacao.modules.organizacaoempresa.repository.OrganizacaoEmpresaRepository;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoNivel;
import br.com.xbrain.autenticacao.modules.usuario.enums.ECanal;
import br.com.xbrain.autenticacao.modules.usuario.model.Nivel;
import br.com.xbrain.autenticacao.modules.usuario.repository.NivelRepository;
import br.com.xbrain.autenticacao.modules.usuario.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Service
@RequiredArgsConstructor
public class OrganizacaoEmpresaService {

    private static final NotFoundException EX_NAO_ENCONTRADO = new NotFoundException("Organização não encontrada.");
    private static final NotFoundException EX_NIVEL_NAO_ENCONTRADO =
        new NotFoundException("Nível empresa não encontrada.");
    public static final NotFoundException EX_ORGANIZACAO_NAO_ENCONTRADA =
        new NotFoundException("Organização não encontrada.");
    private static final ValidacaoException EX_ORGANIZACAO_EXISTENTE =
        new ValidacaoException("Organização já cadastrada com o mesmo nome ou descrição nesse nível.");
    private static final ValidacaoException EX_ORGANIZACAO_ATIVA =
        new ValidacaoException("Organização já está ativa.");
    private static final ValidacaoException EX_ORGANIZACAO_INATIVA =
        new ValidacaoException("Organização já está inativa.");
    private static final ValidacaoException EX_CANAL_VAZIO =
        new ValidacaoException("Esse nível requer um canal válido.");
    public static final ValidacaoException EX_ORGANIZACAO_CODIGO_CADASTRADA =
        new ValidacaoException("Organização já cadastrada com o mesmo código nesse nível.");

    private final OrganizacaoEmpresaRepository organizacaoEmpresaRepository;
    private final OrganizacaoEmpresaHistoricoService historicoService;
    private final AutenticacaoService autenticacaoService;
    private final NivelRepository nivelRepository;
    private final OrganizacaoEmpresaMqSender organizacaoEmpresaMqSender;
    private final UsuarioService usuarioService;
    private final CallService callService;

    public OrganizacaoEmpresa findById(Integer id) {
        return organizacaoEmpresaRepository.findById(id).orElseThrow(() -> EX_NAO_ENCONTRADO);
    }

    public Page<OrganizacaoEmpresa> getAll(OrganizacaoEmpresaFiltros filtros, PageRequest pageRequest) {
        return organizacaoEmpresaRepository.findAll(filtros.toPredicate().build(), pageRequest);
    }

    public List<OrganizacaoEmpresaResponse> findAll() {
        return organizacaoEmpresaRepository.findAll()
            .stream()
            .map(OrganizacaoEmpresaResponse::of)
            .collect(Collectors.toList());
    }

    public OrganizacaoEmpresaResponse save(OrganizacaoEmpresaRequest request) {
        var nivel = findNivelById(request.getNivelId());

        validarNivelOperacao(nivel.getCodigo(), request.getCanal());
        validarNivelBackoffice(nivel.getCodigo(), request.getCodigo());
        validarNomeEDescricaoPorNivelId(request.getNome(), request.getDescricao(), request.getNivelId());
        validarCodigoPorNivelId(request.getCodigo(), request.getNivelId());
        var organizacao = OrganizacaoEmpresa.of(request, autenticacaoService.getUsuarioId(), nivel);
        organizacaoEmpresaRepository.save(organizacao);

        return OrganizacaoEmpresaResponse.of(organizacao);
    }

    private void validarCodigoPorNivelId(String codigo, Integer nivelId) {
        if (isNotBlank(codigo) && organizacaoEmpresaRepository.existsByCodigoAndNivelId(codigo, nivelId)) {
            throw EX_ORGANIZACAO_CODIGO_CADASTRADA;
        }
    }

    private void validarCodigoPorNivelId(String codigo, Integer nivelId, Integer id) {
        if (isNotBlank(codigo) && organizacaoEmpresaRepository.existsByCodigoAndNivelIdAndIdNot(codigo, nivelId, id)) {
            throw EX_ORGANIZACAO_CODIGO_CADASTRADA;
        }
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
        desvincularDiscadoraERamaisSuporteVendas(organizacaoEmpresa);
        usuarioService.inativarPorOrganizacaoEmpresa(id);
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
        ativarConfiguracaoSuporteVendas(organizacaoEmpresa);
    }

    @Transactional
    public OrganizacaoEmpresaResponse update(Integer id, OrganizacaoEmpresaRequest request) throws ValidacaoException {
        var organizacaoEmpresa = findById(id);

        validarNomeEDescricaoParaUpdate(request.getNome(), request.getDescricao(), request.getNivelId(), id);
        validarCodigoPorNivelId(request.getCodigo(), request.getNivelId(), id);

        historicoService.salvarHistorico(organizacaoEmpresa,
            EHistoricoAcao.EDICAO, autenticacaoService.getUsuarioAutenticado());

        var organizacaoDescricaoAntiga = organizacaoEmpresa.getDescricao();
        BeanUtils.copyProperties(request, organizacaoEmpresa);

        var organizacaoEmpresaUpdate = new OrganizacaoEmpresaUpdateDto(
            organizacaoDescricaoAntiga,
            organizacaoEmpresa.getDescricao(),
            organizacaoEmpresa.getNivel().getId()
        );
        organizacaoEmpresaMqSender.sendUpdateNomeSucess(organizacaoEmpresaUpdate);
        organizacaoEmpresaRepository.save(organizacaoEmpresa);

        return OrganizacaoEmpresaResponse.of(organizacaoEmpresa);
    }

    public void validarNomeEDescricaoPorNivelId(String nome, String descricao, Integer nivelId) {
        if (organizacaoEmpresaRepository.existsByDescricaoAndNivelId(descricao, nivelId)
            || organizacaoEmpresaRepository.existsByNomeAndNivelId(nome, nivelId)) {
            throw EX_ORGANIZACAO_EXISTENTE;
        }
    }

    public void validarNomeEDescricaoParaUpdate(String nome, String descricao, Integer nivelId, Integer id) {
        if (organizacaoEmpresaRepository.existsByNomeAndNivelIdAndIdNot(nome, nivelId, id)
            || organizacaoEmpresaRepository.existsByDescricaoAndNivelIdAndIdNot(descricao, nivelId, id)) {
            throw EX_ORGANIZACAO_EXISTENTE;
        }
    }

    private void validarNivelOperacao(CodigoNivel nivel, ECanal canal) {
        if (CodigoNivel.OPERACAO == nivel && canal == null) {
            throw EX_CANAL_VAZIO;
        }
    }

    private void validarNivelBackoffice(CodigoNivel nivel, String codigo) {
        if (nivel == CodigoNivel.BACKOFFICE && StringUtils.isBlank(codigo)) {
            throw new ValidacaoException("O campo código é obrigatório para o nível Backoffice.");
        }
    }

    public Nivel findNivelById(Integer id) {
        return nivelRepository.findById(id)
            .orElseThrow(() -> EX_NIVEL_NAO_ENCONTRADO);
    }

    public OrganizacaoEmpresaResponse findByNome(String nome) {
        var organiazcao = organizacaoEmpresaRepository.findByNome(nome)
            .orElseThrow(() -> EX_ORGANIZACAO_NAO_ENCONTRADA);

        return OrganizacaoEmpresaResponse.of(organiazcao);
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
            .map(organizacao -> SelectResponse.of(organizacao.getId(), organizacao.getDescricao()))
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
        if (StringUtils.isBlank(organizacao)) {
            throw EX_NAO_ENCONTRADO;
        }
        return organizacaoEmpresaRepository.existsByDescricaoAndSituacao(organizacao, ESituacaoOrganizacaoEmpresa.A);
    }

    private void desvincularDiscadoraERamaisSuporteVendas(OrganizacaoEmpresa organizacaoEmpresa) {
        if (organizacaoEmpresa.isSuporteVendas()) {
            callService.desvincularDiscadoraERamaisSuporteVendas(organizacaoEmpresa.getId());
        }
    }

    private void ativarConfiguracaoSuporteVendas(OrganizacaoEmpresa organizacaoEmpresa) {
        if (organizacaoEmpresa.isSuporteVendas()) {
            callService.ativarConfiguracaoSuporteVendas(organizacaoEmpresa.getId());
        }
    }
}
