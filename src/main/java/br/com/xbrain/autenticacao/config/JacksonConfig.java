package br.com.xbrain.autenticacao.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.hibernate4.Hibernate4Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

@Configuration
public class JacksonConfig {

    @Bean
    public ObjectMapper viewsObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(MapperFeature.USE_GETTERS_AS_SETTERS, false);

        //Hibernate config
        objectMapper.disable(MapperFeature.DEFAULT_VIEW_INCLUSION);
        Hibernate4Module hibernateModule = new Hibernate4Module();
        hibernateModule.configure(Hibernate4Module.Feature.FORCE_LAZY_LOADING, false);
        hibernateModule.configure(Hibernate4Module.Feature.USE_TRANSIENT_ANNOTATION, false);
        objectMapper.registerModule(hibernateModule);

        //Javatime config
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        objectMapper.configure(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS, false);

        return objectMapper;
    }

    @Bean
    public MappingJackson2HttpMessageConverter jackson2Converter() {
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setObjectMapper(viewsObjectMapper());
        return converter;
    }
}
