package br.com.xbrain.autenticacao.modules.feriado.repository;

import br.com.xbrain.autenticacao.modules.feriado.model.Feriado;
import org.springframework.cache.annotation.Cacheable;

import java.time.LocalDate;
import java.util.List;

import static br.com.xbrain.autenticacao.config.CacheConfig.FERIADOS_DATA_CACHE_NAME;

public interface FeriadoRepositoryCustom {

    List<Feriado> findAllByAnoAtual(LocalDate now);

    @Cacheable(
            cacheManager = "concurrentCacheManager",
            cacheNames = FERIADOS_DATA_CACHE_NAME,
            unless = "#cidade == null || #uf == null")
    boolean hasFeriadoNacionalOuRegional(LocalDate data, String cidade, String uf);
}
