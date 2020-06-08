package br.com.xbrain.autenticacao.modules.feriado.service;

import br.com.xbrain.autenticacao.modules.comum.dto.PageRequest;
import br.com.xbrain.autenticacao.modules.comum.enums.Eboolean;
import br.com.xbrain.autenticacao.modules.comum.exception.NotFoundException;
import br.com.xbrain.autenticacao.modules.comum.exception.ValidacaoException;
import br.com.xbrain.autenticacao.modules.comum.util.DataHoraAtual;
import br.com.xbrain.autenticacao.modules.feriado.dto.FeriadoFiltros;
import br.com.xbrain.autenticacao.modules.feriado.dto.FeriadoRequest;
import br.com.xbrain.autenticacao.modules.feriado.dto.FeriadoResponse;
import br.com.xbrain.autenticacao.modules.feriado.model.Feriado;
import br.com.xbrain.autenticacao.modules.feriado.model.FeriadoSingleton;
import br.com.xbrain.autenticacao.modules.feriado.predicate.FeriadoPredicate;
import br.com.xbrain.autenticacao.modules.feriado.repository.FeriadoRepository;
import br.com.xbrain.autenticacao.modules.usuario.service.CidadeService;
import br.com.xbrain.xbrainutils.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

import static br.com.xbrain.autenticacao.config.CacheConfig.FERIADOS_DATA_CACHE_NAME;

@Slf4j
@Service
public class FeriadoService {

    private static final NotFoundException EX_NAO_ENCONTRADO = new NotFoundException("Feriado não encontrado.");
    private static final ValidacaoException EX_TIPO_FERIADO_NAO_EDITAVEL =
        new ValidacaoException("Não é permitido editar o Tipo do Feriado.");

    @Autowired
    private FeriadoRepository repository;
    @Autowired
    private DataHoraAtual dataHoraAtual;
    @Autowired
    private CidadeService cidadeService;

    public boolean consulta(String data) {
        return repository.findByDataFeriadoAndFeriadoNacional(DateUtils.parseStringToLocalDate(data), Eboolean.V).isPresent();
    }

    public boolean consulta(String data, Integer cidadeId) {
        return repository.findByDataFeriadoAndCidadeId(DateUtils.parseStringToLocalDate(data), cidadeId).isPresent();
    }

    public Feriado save(FeriadoRequest request) {
        Feriado feriado = FeriadoRequest.convertFrom(request);
        feriado.setDataCadastro(LocalDateTime.now());
        return repository.save(feriado);
    }

    public Iterable<Feriado> findAllByAnoAtual() {
        return repository.findAllByAnoAtual(dataHoraAtual.getData());
    }

    public void loadAllFeriados() {
        FeriadoSingleton.getInstance()
                .setFeriados(repository.findAllByAnoAtual(LocalDate.now())
                        .stream()
                        .map(Feriado::getDataFeriado)
                        .collect(Collectors.toSet()));
    }

    public boolean isFeriadoHojeNaCidadeUf(String cidade, String uf) {
        return repository.hasFeriadoNacionalOuRegional(dataHoraAtual.getData(), cidade, uf);
    }

    public Page<FeriadoResponse> obterFeriadosByFiltros(PageRequest pageRequest, FeriadoFiltros filtros) {
        return repository.findAll(filtros.toPredicate().build(), pageRequest)
            .map(FeriadoResponse::of);
    }

    public FeriadoResponse getFeriadoById(Integer id) {
        return FeriadoResponse.of(findById(id));
    }

    @Transactional
    public FeriadoResponse salvarFeriado(FeriadoRequest request) {
        request.validarDadosObrigatorios();
        var feriado = repository.save(Feriado.of(request));
        salvarFeriadoEstadualParaCidadesDoEstado(feriado);
        return FeriadoResponse.of(feriado);
    }

    @Transactional
    public FeriadoResponse editarFeriado(FeriadoRequest request) {
        request.validarDadosObrigatorios();
        var feriado = findById(request.getId());
        validarTipoFeriado(feriado, request);
        var feriadoEditado = repository.save(Feriado.ofFeriadoEditado(feriado, request));
        editarFeriadosFilhos(feriadoEditado);
        return FeriadoResponse.of(feriadoEditado);
    }

    @Transactional
    public void excluirFeriado(Integer id) {
        var feriado = findById(id);
        deletarFeriadosFilhos(feriado);
        repository.delete(feriado);
    }

    private Feriado findById(Integer id) {
        return repository.findById(id).orElseThrow(() -> EX_NAO_ENCONTRADO);
    }

    private void salvarFeriadoEstadualParaCidadesDoEstado(Feriado feriadoPai) {
        if (feriadoPai.isFeriadoEstadual()) {
            cidadeService.getAllCidadeByUf(feriadoPai.getUf().getId())
                .forEach(cidade -> repository.save(Feriado.criarFeriadoFilho(cidade, feriadoPai)));
        }
    }

    private void validarTipoFeriado(Feriado feriado, FeriadoRequest feriadoRequest) {
        if (!feriado.getTipoFeriado().equals(feriadoRequest.getTipoFeriado())) {
            throw EX_TIPO_FERIADO_NAO_EDITAVEL;
        }
    }

    private void editarFeriadosFilhos(Feriado feriadoPai) {
        if (feriadoPai.isFeriadoEstadual()) {
            deletarFeriadosFilhos(feriadoPai);
            salvarFeriadoEstadualParaCidadesDoEstado(feriadoPai);
        }
    }

    private void deletarFeriadosFilhos(Feriado feriadoPai) {
        if (feriadoPai.isFeriadoEstadual()) {
            repository.delete(repository.findAll(new FeriadoPredicate().comFeriadoPaiId(feriadoPai.getId()).build()));
        }
    }

    @CacheEvict(
            cacheManager = "concurrentCacheManager",
            cacheNames = FERIADOS_DATA_CACHE_NAME,
            allEntries = true)
    public void flushCacheFeriados() {
        log.info("Flush Cache Feriados");
    }
}
