package br.com.xbrain.autenticacao.modules.email.service;

import br.com.xbrain.autenticacao.modules.comum.enums.EmailPrioridade;
import br.com.xbrain.autenticacao.modules.comum.util.StringUtil;
import br.com.xbrain.autenticacao.modules.email.handler.EmailResponseErrorHandler;
import br.com.xbrain.autenticacao.modules.email.model.Email;
import br.com.xbrain.autenticacao.modules.email.model.EmailBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring4.SpringTemplateEngine;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Service
public class EmailService {

    private final Logger logger = LoggerFactory.getLogger(EmailService.class);

    @Value("${app-config.nome}")
    private String nomeSistema;
    @Value("${app-config.url}")
    private String urlSistema;
    @Value("${app-config.email.enviar}")
    private boolean enviarEmail;
    @Value("${app-config.email.emails}")
    private String emails;
    @Value("${app-config.services.emails.url}")
    private String urlServico;
    @Value(("${app-config.email.empresa-alias}"))
    private String empresaAlias;

    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private SpringTemplateEngine templateEngine;

    public void enviarEmailTemplate(List<String> emailsDestino, String assunto, String template, Context context) {
        obterContexto(assunto, template, context);

        String htmlContent = templateEngine.process("email-template", context);
        enviarEmail(emailsDestino, assunto, htmlContent, empresaAlias, EmailPrioridade.NORMAL);
    }

    public void enviarEmailTemplate(List<String> emailsDestino, String assunto, String template, Context context,
                                    EmailPrioridade prioridade) {
        obterContexto(assunto, template, context);

        String htmlContent = templateEngine.process("email-template", context);
        enviarEmail(emailsDestino, assunto, htmlContent, empresaAlias, prioridade);
    }

    public void enviarEmailConexaoClaroBrasil(List<String> emailsDestino, String assunto, String template, Context context) {
        obterContexto(assunto, template, context);

        String htmlContent = templateEngine.process("email-template-conexao", context);
        enviarEmail(emailsDestino, assunto, htmlContent, empresaAlias, EmailPrioridade.NORMAL);
    }

    private void obterContexto(String assunto, String template, Context context) {
        context.setVariable("nomeSistema", nomeSistema);
        context.setVariable("urlSistema", urlSistema);
        context.setVariable("assunto", assunto);
        context.setVariable("urlSistemaLoginDireto", "teste");
        context.setVariable("dataEmail", StringUtil.getDataAtualEmail());
        context.setVariable("includeConteudo", template);
    }

    private String converteEmailJson(Object objeto) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(objeto);
        } catch (IOException ex) {
            logger.error("Erro ao converter objeto para json", ex);
            return null;
        }
    }

    private boolean validaCampos(List<String> emailsDestino, String empresaAlias) {
        return emailsDestino != null
            && !emailsDestino.isEmpty()
            && empresaAlias != null
            && enviarEmail;
    }

    @Async
    public void enviarEmail(List<String> emailsDestino, String assunto, String conteudo, String empresaAlias,
                            EmailPrioridade prioridade) {
        if (validaCampos(emailsDestino, empresaAlias)) {
            Email email = obterEmail(getEmails(emailsDestino), assunto, formataCorpo(conteudo), prioridade);
            HttpEntity<String> emailEntity = processaRequisicao(converteEmailJson(email), MediaType.APPLICATION_JSON_UTF8);
            String url = obterUrl(empresaAlias, false); //AQUI
            restTemplate.postForEntity(url, emailEntity, String.class);
        }
    }

    private HttpEntity processaRequisicao(Object objeto, MediaType tipo) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(tipo);
        return new HttpEntity(objeto, headers);
    }

    private String obterUrl(String alias, boolean temAnexo) {
        StringBuilder url = new StringBuilder(urlServico);
        url.append("/emails/");
        if (temAnexo) {
            url.append("withFiles/");
        }
        url.append(alias);

        return url.toString();
    }

    private Email obterEmail(List<String> emails, String assunto, String conteudo, EmailPrioridade prioridade) {
        return new EmailBuilder()
            .comAssunto(assunto)
            .comCorpo(conteudo)
            .comDestinatarios(emails)
            .comPriority(prioridade)
            .build();
    }

    private String formataCorpo(String content) {
        return content.replaceAll("\n", "");
    }

    public List<String> getEmails(List<String> emailsDestino) {
        if (emails != null && emails.trim().length() > 0) {
            return Arrays.asList(emails.trim().split(","));
        }
        return emailsDestino;
    }

    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplateObj = new RestTemplate();
        restTemplateObj.setErrorHandler(new EmailResponseErrorHandler());
        return restTemplateObj;
    }
}
