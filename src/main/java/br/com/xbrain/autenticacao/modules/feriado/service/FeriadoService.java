package br.com.xbrain.autenticacao.modules.feriado.service;

import br.com.xbrain.autenticacao.modules.autenticacao.service.AutenticacaoService;
import br.com.xbrain.autenticacao.modules.call.service.CallService;
import br.com.xbrain.autenticacao.modules.comum.dto.PageRequest;
import br.com.xbrain.autenticacao.modules.comum.enums.Eboolean;
import br.com.xbrain.autenticacao.modules.comum.exception.NotFoundException;
import br.com.xbrain.autenticacao.modules.comum.exception.ValidacaoException;
import br.com.xbrain.autenticacao.modules.comum.model.Uf;
import br.com.xbrain.autenticacao.modules.comum.util.DataHoraAtual;
import br.com.xbrain.autenticacao.modules.feriado.dto.*;
import br.com.xbrain.autenticacao.modules.feriado.enums.ESituacaoFeriado;
import br.com.xbrain.autenticacao.modules.feriado.model.Feriado;
import br.com.xbrain.autenticacao.modules.feriado.model.FeriadoSingleton;
import br.com.xbrain.autenticacao.modules.feriado.predicate.FeriadoPredicate;
import br.com.xbrain.autenticacao.modules.feriado.repository.FeriadoRepository;
import br.com.xbrain.autenticacao.modules.mailing.service.MailingService;
import br.com.xbrain.autenticacao.modules.usuario.service.CidadeService;
import br.com.xbrain.xbrainutils.DateUtils;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static br.com.xbrain.autenticacao.config.CacheConfig.FERIADOS_DATA_CACHE_NAME;

@Slf4j
@Service
public class FeriadoService {

    private static final int MAX_RESULTADO = 1000;
    private static final NotFoundException EX_NAO_ENCONTRADO = new NotFoundException("Feriado não encontrado.");
    private static final ValidacaoException EX_TIPO_FERIADO_NAO_EDITAVEL =
        new ValidacaoException("Não é permitido editar o Tipo do Feriado.");
    private static final String EDITADO = "EDITADO";
    private static final String EXCLUIDO = "EXCLUIDO";
    private static final String IMPORTADO = "IMPORTADO";
    private static final String CADASTRADO = "CADASTRADO MANUAL";
    private static final ValidacaoException EX_FERIADO_JA_CADASTRADO =
        new ValidacaoException("Já existe feriado com os mesmos dados.");

    @Value("${app-config.upload-async}")
    private boolean uploadAsync;
    @Autowired
    private FeriadoRepository repository;
    @Autowired
    private DataHoraAtual dataHoraAtual;
    @Autowired
    private CidadeService cidadeService;
    @Autowired
    private AutenticacaoService autenticacaoService;
    @Autowired
    private FeriadoHistoricoService historicoService;
    @Autowired
    private CallService callService;
    @Autowired
    private MailingService mailingService;

    public boolean consulta() {
        return repository.findByDataFeriadoAndFeriadoNacionalAndSituacao(dataHoraAtual.getData(),
            Eboolean.V,
            ESituacaoFeriado.ATIVO).isPresent();
    }

    public boolean consulta(String data) {
        return repository.findByDataFeriadoAndFeriadoNacionalAndSituacao(DateUtils.parseStringToLocalDate(data),
            Eboolean.V,
            ESituacaoFeriado.ATIVO).isPresent();
    }

    public boolean consulta(String data, Integer cidadeId) {
        return repository.findByDataFeriadoAndCidadeIdAndSituacao(DateUtils.parseStringToLocalDate(data),
            cidadeId,
            ESituacaoFeriado.ATIVO).isPresent();
    }

    public Feriado save(FeriadoRequest request) {
        Feriado feriado = FeriadoRequest.convertFrom(request);
        feriado.setDataCadastro(LocalDateTime.now());
        return repository.save(feriado);
    }

    public Iterable<Feriado> findAllByAnoAtual() {
        return repository.findAllByAnoAtual(dataHoraAtual.getData());
    }

    public void loadFeriados() {
        FeriadoSingleton.getInstance()
            .setFeriados(repository.findAllByAnoAtual(LocalDate.now())
                .stream()
                .map(Feriado::getDataFeriado)
                .collect(Collectors.toSet()));

        FeriadoSingleton.getInstance()
            .setFeriadosNacionais(new HashSet<>(repository.findAllNacional(LocalDate.now())));
    }

    public boolean isFeriadoHojeNaCidadeUf(String cidade, String uf) {
        return repository.hasFeriadoNacionalOuRegional(dataHoraAtual.getData(), cidade, uf);
    }

    public List<String> buscarUfsFeriadosEstaduaisPorData() {
        return repository.buscarEstadosFeriadosEstaduaisPorData(dataHoraAtual.getData());
    }

    public List<FeriadoCidadeEstadoResponse> buscarFeriadosMunicipaisPorDataAtualUfs() {
        return repository.buscarFeriadosMunicipaisPorData(dataHoraAtual.getData());
    }

    public Page<FeriadoResponse> obterFeriadosByFiltros(PageRequest pageRequest, FeriadoFiltros filtros) {
        var feriadosResponse = repository.findAll(filtros.toPredicate().build(), pageRequest)
            .map(FeriadoResponse::of);

        if (feriadosResponse.hasContent()) {
            var distritos = cidadeService.getCidadesDistritos(Eboolean.V);

            feriadosResponse
                .forEach(feriadoResponse -> FeriadoResponse.definirNomeCidadePaiPorDistritos(feriadoResponse, distritos));
        }

        return feriadosResponse;
    }

    public FeriadoResponse getFeriadoById(Integer id) {
        var feriadoResponse = FeriadoResponse.of(findById(id));

        if (feriadoResponse.getFkCidade() != null) {
            var cidadeResponse = cidadeService.getCidadeById(feriadoResponse.getFkCidade());
            feriadoResponse.setCidadePai(cidadeResponse.getNome());
        }

        return feriadoResponse;
    }

    @Transactional
    public FeriadoResponse salvarFeriado(FeriadoRequest request) {
        request.validarDadosObrigatorios();
        validarSeFeriadoJaCadastado(request);
        var feriado = repository.save(Feriado.of(request, autenticacaoService.getUsuarioId()));
        salvarFeriadoEstadualParaCidadesDoEstado(feriado);
        historicoService.salvarHistorico(feriado, CADASTRADO, autenticacaoService.getUsuarioAutenticado());
        flushCacheFeriados();
        flushCacheFeriadoTelefonia();
        flushCacheFeriadoMailing();
        return FeriadoResponse.of(feriado);
    }

    @Transactional
    public Feriado salvarFeriadoImportado(FeriadoImportacao feriadoParaSalvar) {
        var feriadoImportado = repository.save(Feriado.ofFeriadoImportado(feriadoParaSalvar, autenticacaoService.getUsuarioId()));
        salvarFeriadoEstadualParaCidadesDoEstadoAsync(feriadoImportado);
        historicoService.salvarHistorico(feriadoImportado, IMPORTADO, autenticacaoService.getUsuarioAutenticado());
        return feriadoImportado;
    }

    @Transactional
    public FeriadoResponse editarFeriado(FeriadoRequest request) {
        request.validarDadosObrigatorios();
        validarSeFeriadoJaCadastado(request);
        var feriado = findById(request.getId());
        var estadoOriginal = feriado.getUf();
        validarTipoFeriado(feriado, request);
        var feriadoEditado = repository.save(Feriado.ofFeriadoEditado(feriado, request));

        if (feriadoEditado.isFeriadoEstadual()) {
            editarFeriadosFilhos(feriadoEditado, isEstadoAlterado(estadoOriginal, request));
        }
        historicoService.salvarHistorico(feriadoEditado, EDITADO, autenticacaoService.getUsuarioAutenticado());
        flushCacheFeriados();
        flushCacheFeriadoTelefonia();
        flushCacheFeriadoMailing();
        return FeriadoResponse.of(feriadoEditado);
    }

    @Transactional
    public void excluirFeriado(Integer id) {
        var feriado = findById(id);
        excluirFeriadosFilhos(feriado);
        feriado.excluir();
        var feriadoExcluido = repository.save(feriado);
        historicoService.salvarHistorico(feriadoExcluido, EXCLUIDO, autenticacaoService.getUsuarioAutenticado());
        flushCacheFeriados();
        flushCacheFeriadoTelefonia();
        flushCacheFeriadoMailing();
    }

    public Feriado findById(Integer id) {
        return repository.findById(id).orElseThrow(() -> EX_NAO_ENCONTRADO);
    }

    private void salvarFeriadoEstadualParaCidadesDoEstado(Feriado feriadoPai) {
        if (feriadoPai.isFeriadoEstadual()) {
            var feriadosFilhos = cidadeService.getAllCidadeByUf(feriadoPai.getUf().getId()).stream()
                .map(cidade -> Feriado.criarFeriadoFilho(cidade, feriadoPai))
                .collect(Collectors.toList());
            repository.save(feriadosFilhos);
        }
    }

    private void salvarFeriadoEstadualParaCidadesDoEstadoAsync(Feriado feriadoPai) {
        if (feriadoPai.isFeriadoEstadual()) {
            if (uploadAsync) {
                CompletableFuture
                    .runAsync(() -> salvarFeriadoEstadualParaCidadesDoEstado(feriadoPai))
                    .exceptionally(ex -> {
                        log.error("Erro ao salvar o feriado estadual para as cidades do estado, feriadoPaiId: "
                            + feriadoPai.getId(),
                            ex);
                        return null;
                    });
            } else {
                salvarFeriadoEstadualParaCidadesDoEstado(feriadoPai);
            }
        }
    }

    private void validarTipoFeriado(Feriado feriado, FeriadoRequest feriadoRequest) {
        if (!feriado.getTipoFeriado().equals(feriadoRequest.getTipoFeriado())) {
            throw EX_TIPO_FERIADO_NAO_EDITAVEL;
        }
    }

    private boolean isEstadoAlterado(Uf ufOriginal, FeriadoRequest request) {
        return !ufOriginal.getId().equals(request.getEstadoId());
    }

    private void editarFeriadosFilhos(Feriado feriadoPai, boolean isEstadoAlterado) {
        if (isEstadoAlterado) {
            excluirFeriadosFilhos(feriadoPai);
            salvarFeriadoEstadualParaCidadesDoEstado(feriadoPai);
        } else {
            editarFeriadosFilhosSemAlteracaoDoEstado(feriadoPai);
        }
    }

    private void editarFeriadosFilhosSemAlteracaoDoEstado(Feriado feriadoPai) {
        Lists
            .partition(
                findFeriadosFilhos(feriadoPai.getId()).stream()
                    .map(Feriado::getId)
                    .collect(Collectors.toList()),
                MAX_RESULTADO)
            .forEach(listPart -> repository.updateFeriadoNomeEDataByIds(listPart, feriadoPai.getNome(),
                feriadoPai.getDataFeriado()));
    }

    private void excluirFeriadosFilhos(Feriado feriadoPai) {
        if (feriadoPai.isFeriadoEstadual()) {
            Lists
                .partition(
                    findFeriadosFilhos(feriadoPai.getId()).stream()
                        .map(Feriado::getId)
                        .collect(Collectors.toList()),
                    MAX_RESULTADO)
                .forEach(repository::exluirByFeriadoIds);
        }
    }

    private List<Feriado> findFeriadosFilhos(Integer feriadoPaiId) {
        return repository.findAll(
            new FeriadoPredicate()
                .comFeriadoPaiId(feriadoPaiId)
                .excetoExcluidos()
                .build());
    }

    private void validarSeFeriadoJaCadastado(FeriadoRequest request) {
        repository.findByPredicate(
            new FeriadoPredicate()
                .comNome(request.getNome())
                .comTipoFeriado(request.getTipoFeriado())
                .comEstado(request.getEstadoId())
                .comCidade(request.getCidadeId(), request.getEstadoId())
                .comDataFeriado(DateUtils.parseStringToLocalDate(request.getDataFeriado()))
                .excetoExcluidos()
                .excetoFeriadosFilhos()
                .build())
            .ifPresent(feriado -> {
                throw EX_FERIADO_JA_CADASTRADO;
            });
    }

    @CacheEvict(
        cacheManager = "concurrentCacheManager",
        cacheNames = FERIADOS_DATA_CACHE_NAME,
        allEntries = true)
    public void flushCacheFeriados() {
        loadFeriados();
        log.info("Flush Cache Feriados");
    }

    public List<FeriadoMesAnoResponse> buscarTotalDeFeriadosPorMesAno() {
        return repository.buscarTotalDeFeriadosPorMesAno();
    }

    public boolean isFeriadoComCidadeId(Integer cidadeId) {
        return repository.hasFeriadoByCidadeIdAndDataAtual(cidadeId, dataHoraAtual.getData());
    }

    public void flushCacheFeriadoTelefonia() {
        callService.cleanCacheFeriadosTelefonia();
    }

    public void flushCacheFeriadoMailing() {
        mailingService.flushCacheFeriadosMailing();
    }
}
