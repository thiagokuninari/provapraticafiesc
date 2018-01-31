package br.com.xbrain.autenticacao.config;

import br.com.xbrain.autenticacao.modules.comum.service.MensagemWsClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

@Configuration
public class MensagemWsConfig {

    @Value("${app-config.url-mensagemws}")
    private String urlWs;

    public Jaxb2Marshaller marshaller() {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setContextPath("br.com.xbrain.autenticacao.infra.mensagemWs.wsdl");
        return marshaller;
    }

    @Bean
    public MensagemWsClient mensagemWsClient() {
        Jaxb2Marshaller marshaller = marshaller();
        MensagemWsClient mensagemWsClient = new MensagemWsClient();
        mensagemWsClient.setDefaultUri(urlWs);
        mensagemWsClient.setMarshaller(marshaller);
        mensagemWsClient.setUnmarshaller(marshaller);
        return mensagemWsClient;
    }
}
