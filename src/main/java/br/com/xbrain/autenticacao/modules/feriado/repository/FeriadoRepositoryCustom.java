package br.com.xbrain.autenticacao.modules.feriado.repository;

import br.com.xbrain.autenticacao.modules.feriado.dto.FeriadoCidadeEstadoResponse;
import br.com.xbrain.autenticacao.modules.feriado.dto.FeriadoMesAnoResponse;
import br.com.xbrain.autenticacao.modules.feriado.model.Feriado;
import com.querydsl.core.types.Predicate;
import org.springframework.cache.annotation.Cacheable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static br.com.xbrain.autenticacao.config.CacheConfig.FERIADOS_DATA_CACHE_NAME;

public interface FeriadoRepositoryCustom {

    List<Feriado> findAllByAnoAtual(LocalDate now);

    List<LocalDate> findAllDataFeriadoByCidadeId(Integer cidadeId);

    List<LocalDate> findAllDataFeriadoByCidadeEUf(String cidade, String uf);

    Optional<Feriado> findByPredicate(Predicate predicate);

    void exluirByFeriadoIds(List<Integer> feriadoIds);

    void updateFeriadoNomeEDataByIds(List<Integer> feriadoIds, String nome, LocalDate dataFeriado);

    @Cacheable(
            cacheManager = "concurrentCacheManager",
            cacheNames = FERIADOS_DATA_CACHE_NAME,
            unless = "#cidade == null || #uf == null")
    boolean hasFeriadoNacionalOuRegional(LocalDate data, String cidade, String uf);

    List<String> buscarEstadosFeriadosEstaduaisPorData(LocalDate data);

    List<FeriadoCidadeEstadoResponse> buscarFeriadosMunicipaisPorData(LocalDate data);

    List<FeriadoMesAnoResponse> buscarTotalDeFeriadosPorMesAno();

    List<LocalDate> findAllNacional(LocalDate now);

    boolean hasFeriadoByCidadeIdAndDataAtual(Integer cidadeId, LocalDate dataAtual);
}
