package br.com.xbrain.autenticacao.modules.feriado.service;

import br.com.xbrain.autenticacao.modules.comum.enums.Eboolean;
import br.com.xbrain.autenticacao.modules.comum.util.DataHoraAtual;
import br.com.xbrain.autenticacao.modules.feriado.dto.FeriadoMesAnoResponse;
import br.com.xbrain.autenticacao.modules.feriado.dto.FeriadoRequest;
import br.com.xbrain.autenticacao.modules.feriado.model.Feriado;
import br.com.xbrain.autenticacao.modules.feriado.model.FeriadoSingleton;
import br.com.xbrain.autenticacao.modules.feriado.repository.FeriadoRepository;
import br.com.xbrain.xbrainutils.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static br.com.xbrain.autenticacao.config.CacheConfig.FERIADOS_DATA_CACHE_NAME;

@Slf4j
@Service
public class FeriadoService {

    @Autowired
    private FeriadoRepository repository;
    @Autowired
    private DataHoraAtual dataHoraAtual;

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

    @CacheEvict(
            cacheManager = "concurrentCacheManager",
            cacheNames = FERIADOS_DATA_CACHE_NAME,
            allEntries = true)
    public void flushCacheFeriados() {
        log.info("Flush Cache Feriados");
    }

    public List<FeriadoMesAnoResponse> buscarTotalDeFeriadosPorMesAno() {
        return repository.buscarTotalDeFeriadosPorMesAno();
    }
}
