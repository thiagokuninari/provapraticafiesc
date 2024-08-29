package br.com.xbrain.autenticacao.modules.feriado.repository;

import br.com.xbrain.autenticacao.modules.feriado.dto.FeriadoCidadeEstadoResponse;
import br.com.xbrain.autenticacao.modules.feriado.dto.FeriadoMesAnoResponse;
import br.com.xbrain.autenticacao.modules.feriado.enums.ESituacaoFeriado;
import br.com.xbrain.autenticacao.modules.feriado.enums.ETipoFeriado;
import br.com.xbrain.autenticacao.modules.feriado.model.Feriado;
import br.com.xbrain.autenticacao.modules.usuario.model.Cidade;
import com.querydsl.core.types.Predicate;
import org.springframework.cache.annotation.Cacheable;

import java.time.LocalDate;
import java.util.List;

import static br.com.xbrain.autenticacao.config.CacheConfig.FERIADOS_DATA_CACHE_NAME;

public interface FeriadoRepositoryCustom {

    List<Feriado> findAllByAnoAtual(LocalDate now);

    List<LocalDate> findAllDataFeriadoByCidadeId(Integer cidadeId);

    List<LocalDate> findAllDataFeriadoByCidadeEUf(String cidade, String uf);

    boolean existsByPredicate(Predicate predicate);

    boolean existsByDataFeriadoAndCidadeIdOrUfId(LocalDate data, Integer cidadeId, Integer ufId, ESituacaoFeriado situacao);

    void exluirByFeriadoIds(List<Integer> feriadoIds);

    void updateFeriadoNomeEDataByIds(List<Integer> feriadoIds, String nome, LocalDate dataFeriado);

    @Cacheable(
        cacheManager = "concurrentCacheManager",
        cacheNames = FERIADOS_DATA_CACHE_NAME,
        unless = "#cidade == null || #uf == null")
    boolean hasFeriadoMunicipal(LocalDate data, String cidade, String uf);

    @Cacheable(
        cacheManager = "concurrentCacheManager",
        cacheNames = FERIADOS_DATA_CACHE_NAME,
        unless = "#cidade == null || #uf == null")
    boolean hasFeriadoEstadual(LocalDate data, String cidade, String uf);

    @Cacheable(
        cacheManager = "concurrentCacheManager",
        cacheNames = FERIADOS_DATA_CACHE_NAME,
        unless = "#cidade == null || #uf == null")
    boolean hasFeriadoNacional(LocalDate data);

    List<String> buscarEstadosFeriadosEstaduaisPorData(LocalDate data);

    List<FeriadoCidadeEstadoResponse> buscarFeriadosMunicipaisPorData(LocalDate data);

    List<FeriadoMesAnoResponse> buscarTotalDeFeriadosPorMesAno();

    List<LocalDate> findAllNacional(LocalDate now);

    boolean hasFeriadoByCidadeIdAndDataAtual(Integer cidadeId, LocalDate dataAtual);

    Cidade findUtimaCidadeFeriadoCadastradoByAno(Integer ano);

    long findTotalFeriadosImportadosByTipoFeriado(ETipoFeriado tipoFeriado, Integer importacaoFeriadoId);
}
