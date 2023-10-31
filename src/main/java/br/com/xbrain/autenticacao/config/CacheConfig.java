package br.com.xbrain.autenticacao.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class CacheConfig {

    public static final String FERIADOS_DATA_CACHE_NAME = "feriadoDataCidadeUf";
    public static final String CIDADES_DISTRITOS_CACHE_NAME = "cidadesDistritos";

    @Bean
    public CacheManager concurrentCacheManager() {
        return new ConcurrentMapCacheManager();
    }
}
