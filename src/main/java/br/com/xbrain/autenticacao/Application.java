package br.com.xbrain.autenticacao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.Arrays;

@EntityScan(
        basePackageClasses = { Application.class, Jsr310JpaConverters.class })
@EnableZuulProxy
@SpringBootApplication
public class Application {

    private final Logger log = LoggerFactory.getLogger(Application.class);

    @Autowired
    private Environment env;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @PostConstruct
    public void initApplication() throws IOException {
        if (env.getActiveProfiles().length == 0) {
            log.warn("Nenhum profile configurado, rodando com configuracao default.");
        } else {
            log.info("Rodando com Spring profile : {}",
                    Arrays.toString(env.getActiveProfiles()));
        }
    }
}
