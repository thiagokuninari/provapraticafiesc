package br.com.xbrain.autenticacao.modules.comum.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring4.SpringTemplateEngine;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

@Service
public class EmailService {

    private final Logger logger = LoggerFactory.getLogger(EmailService.class);

    @Value("${app-config.nome}")
    private String nomeSistema;
    @Value("${app-config.url}")
    private String urlSistema;
    //@Value("${app-config.url-login-direto}")
    //private String urlLoginDireto;
    @Value("${app-config.url-estatico}")
    private String urlEstatico;
    @Value("${app-config.email.enviar}")
    private boolean enviarEmail;
    @Value("${app-config.email.emails}")
    private String emails;

    @Autowired
    private MensagemWsClient mensagemWsClient;

    @Autowired
    private SpringTemplateEngine templateEngine;

    public void enviarEmail(List<String> emailsDestino, String assunto, String conteudo) {
        if (emailsDestino != null && !emailsDestino.isEmpty()) {
            try {
                if (enviarEmail) {
                    boolean enviado = mensagemWsClient.enviarEmail(assunto, conteudo, getEmails(emailsDestino));
                    if (!enviado) {
                        logger.warn("Erro ao enviar email");
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                logger.warn("Exception ao enviar email", ex);
            }
        }
    }

    public void enviarEmailTemplate(List<String> emailsDestino, String assunto, String template, Context context) {
        context.setVariable("urlEstatico", urlEstatico);
        context.setVariable("nomeSistema", nomeSistema);
        context.setVariable("urlSistema", urlSistema);
        context.setVariable("assunto", assunto);
        context.setVariable("urlSistemaLoginDireto", "teste");
        context.setVariable("dataEmail", LocalDate.now()
                .format(DateTimeFormatter.ofPattern("EEEE, dd 'de' MMMM 'de' yyyy", new Locale("pt","BR"))));
        context.setVariable("includeConteudo", template);

        String htmlContent = templateEngine.process("email-template", context);
        enviarEmail(emailsDestino, assunto, htmlContent);
    }

    public void enviarEmailTemplateBasico(List<String> emailsDestino, String assunto, Context context) {
        String htmlContent = templateEngine.process("comunicado-template-email", context);
        enviarEmail(emailsDestino, assunto, htmlContent);
    }

    private String getEmails(List<String> emailsDestino) {
        if (emails != null && emails.trim().length() > 0) {
            return emails;
        }
        return String.join(",", emailsDestino);
    }
}