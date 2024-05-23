package br.com.xbrain.autenticacao;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableScheduling;

@EntityScan(
        basePackageClasses = {Application.class, Jsr310JpaConverters.class})
@EnableZuulProxy
@EnableFeignClients
@SpringBootApplication
@EnableCircuitBreaker
@EnableScheduling
@EnableRetry
public class Application {

    public static void main(String[] args) {

        SpringApplication.run(Application.class, args);
    }
}
