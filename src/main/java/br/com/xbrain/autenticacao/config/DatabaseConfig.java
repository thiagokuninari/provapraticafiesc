package br.com.xbrain.autenticacao.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.sql.DataSource;

@Configuration
@Profile(value = "importacao")
public class DatabaseConfig {

    @Bean
    @Primary
    @ConfigurationProperties(prefix = "spring.datasource")
    @Qualifier(value = "primary")
    public DataSource primaryDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "parceiros")
    @ConfigurationProperties(prefix = "spring.datasource-parceiros")
    @Qualifier(value = "parceiros")
    public DataSource parceirosDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean
    @Qualifier(value = "primary")
    public NamedParameterJdbcTemplate namedParameterJdbcTemplateAuth() {
        return new NamedParameterJdbcTemplate(primaryDataSource());
    }

    @Bean
    @Qualifier(value = "parceiros")
    public NamedParameterJdbcTemplate namedParameterJdbcTemplateParceiros() {
        return new NamedParameterJdbcTemplate(parceirosDataSource());
    }

}
