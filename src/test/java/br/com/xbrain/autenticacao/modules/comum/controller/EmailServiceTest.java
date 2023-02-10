package br.com.xbrain.autenticacao.modules.comum.controller;

import br.com.xbrain.autenticacao.modules.comum.enums.EmailPrioridade;
import br.com.xbrain.autenticacao.modules.email.service.EmailService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasItems;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class EmailServiceTest {

    private static final String ASSUNTO_EMAIL = "Teste de envio de Email";
    private static final String CONTEUDO_EMAIL = "<html><head></head><body><h1>Olá mundo</h1></body></html>";
    @Mock
    private RestTemplate restTemplate;
    @InjectMocks
    private EmailService emailService;

    @Test
    public void deveEnviarEmail() {
        ReflectionTestUtils.setField(emailService, "enviarEmail", true);
        ReflectionTestUtils.setField(emailService, "urlServico", "http://api-hom.conexaoclarobrasil.com.br/email");

        emailService.enviarEmail(Collections.singletonList("luisdias@xbrain.com.br"),
                ASSUNTO_EMAIL,
                CONTEUDO_EMAIL,
                "XBRAIN",
                EmailPrioridade.NORMAL);

        verify(restTemplate, times(1)).postForEntity(anyString(), any(), any());
    }

    @Test
    public void deveEnviarEmailSomenteParaOsEmailsConfigurados() {
        List<String> emails = Arrays.asList("teste@xbrain.com.br", "emailteste@xbrain.com.br");
        ReflectionTestUtils.setField(emailService, "enviarEmail", true);
        ReflectionTestUtils.setField(emailService, "emails", String.join(",", emails));

        List<String> emailsParaEnviar = emailService.getEmails(Collections.singletonList("luisdias@xbrain.com.br"));
        assertThat(emailsParaEnviar, hasItems("teste@xbrain.com.br", "emailteste@xbrain.com.br"));
    }

    @Test
    public void naoDeveEnviarEmailSeEstiverDesativado() {
        ReflectionTestUtils.setField(emailService, "enviarEmail", false);

        emailService.enviarEmail(
                Collections.singletonList("luisdias@xbrain.com.br"),
                ASSUNTO_EMAIL,
                CONTEUDO_EMAIL,
                "XBRAIN",
                EmailPrioridade.NORMAL);

        verify(restTemplate, never()).postForEntity(anyString(), any(), any());
    }

    @Test
    public void naoDeveEnviarEmailSemEmpresaAlias() {
        emailService.enviarEmail(
                Collections.singletonList("luisdias@xbrain.com.br"),
                ASSUNTO_EMAIL,
                CONTEUDO_EMAIL,
                null,
                EmailPrioridade.NORMAL);

        verify(restTemplate, never()).postForEntity(anyString(), any(), any());
    }

    @Test
    public void naoDeveEnviarEmailSemDestinatarios() {
        emailService.enviarEmail(
                new ArrayList<>(),
                ASSUNTO_EMAIL,
                CONTEUDO_EMAIL,
                "XBRAIN",
                EmailPrioridade.NORMAL);

        verify(restTemplate, never()).postForEntity(anyString(), any(), any());
    }
}
