package br.com.xbrain.autenticacao.modules.notificacao.service;

import br.com.xbrain.autenticacao.modules.comum.enums.EmailPrioridade;
import br.com.xbrain.autenticacao.modules.email.service.EmailService;
import br.com.xbrain.autenticacao.modules.notificacao.dto.BoaVindaAgenteAutorizadoRequest;
import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioDadosAcessoRequest;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.thymeleaf.context.Context;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class NotificacaoServiceTest {

    @InjectMocks
    private NotificacaoService service;

    @Mock
    private EmailService emailService;

    @Captor
    private ArgumentCaptor<Context> contextCaptor;

    @Test
    public void enviarEmailDadosDeAcesso_deveConfigurarEEnviarEmailCorretamente_quandoSolicitado() {
        var usuario = umUsuario();

        service.enviarEmailDadosDeAcesso(
            usuario, "senha123");

        verify(emailService).enviarEmailTemplate(
            eq(List.of(usuario.getEmail())),
            eq("Parceiros Online - Seja bem-vindo(a)"),
            eq("confirmacao-cadastro"),
            contextCaptor.capture());

        var capturedContext = contextCaptor.getValue();

        assertEquals("Nome Usuario", capturedContext.getVariables().get("nome"));
        assertEquals("email@xbrain.com.br", capturedContext.getVariables().get("email"));
        assertEquals("senha123", capturedContext.getVariables().get("senha"));
    }

    @Test
    public void enviarEmailAtualizacaoSenha_deveConfigurarEEnviarEmailCorretamente_quandoSolicitado() {
        var usuario = umUsuario();

        service.enviarEmailAtualizacaoSenha(
            usuario, "senha12345");

        verify(emailService).enviarEmailTemplate(
            eq(List.of(usuario.getEmail())),
            eq("Conexão Claro Brasil - Alteração de dados de acesso"),
            eq("reenvio-senha"),
            contextCaptor.capture());

        var capturedContext = contextCaptor.getValue();

        assertEquals("Nome Usuario", capturedContext.getVariables().get("nome"));
        assertEquals("email@xbrain.com.br", capturedContext.getVariables().get("email"));
        assertEquals("senha12345", capturedContext.getVariables().get("senha"));
    }

    @Test
    public void enviarEmailResetSenha_deveConfigurarEEnviarEmailCorretamente_quandoSolicitado() {
        var usuario = umUsuario();

        service.enviarEmailResetSenha(
            usuario, "conexaoclarobrasil.com.br/login");

        verify(emailService).enviarEmailTemplate(
            eq(List.of(usuario.getEmail())),
            eq("Conexão Claro Brasil - Confirmação de Alterar a Senha"),
            eq("confirmar-reset-senha"),
            contextCaptor.capture(),
            eq(EmailPrioridade.ALTA));

        var capturedContext = contextCaptor.getValue();

        assertEquals("Nome Usuario", capturedContext.getVariables().get("nome"));
        assertEquals("email@xbrain.com.br", capturedContext.getVariables().get("email"));
        assertEquals("conexaoclarobrasil.com.br/login", capturedContext.getVariables().get("link"));
    }

    @Test
    public void enviarEmailAtualizacaoEmail_deveConfigurarEEnviarEmailCorretamente_quandoSolicitado() {
        var usuario = umUsuario();
        var usuarioDadosAcessoRequest = umUsuarioDadosAcessoRequest();
        service.enviarEmailAtualizacaoEmail(
            usuario, usuarioDadosAcessoRequest);

        verify(emailService).enviarEmailTemplate(
            eq(List.of(usuarioDadosAcessoRequest.getEmailAtual())),
            eq("Conexão Claro Brasil - Alteração de dados de acesso"),
            eq("alteracao-email"),
            contextCaptor.capture());

        var capturedContext = contextCaptor.getValue();

        assertEquals("Nome Usuario", capturedContext.getVariables().get("nome"));
        assertEquals("emailnovo@xbrain.com.br", capturedContext.getVariables().get("emailNovo"));
        assertEquals("emailatual@xbrain.com.br", capturedContext.getVariables().get("emailAntigo"));
    }

    @Test
    public void enviarEmailBoaVindaAgenteAutorizado_deveConfigurarEEnviarEmailCorretamente_quandoSolicitado() {
        var usuario = umBoaVindaAgenteAutorizadoRequest();
        service.enviarEmailBoaVindaAgenteAutorizado(usuario);

        verify(emailService).enviarEmailConexaoClaroBrasil(
            eq(usuario.getEmails()),
            eq("Conexão Claro Brasil - Seja bem-vindo(a)"),
            eq("boas-vindas-aa"),
            contextCaptor.capture());

        var capturedContext = contextCaptor.getValue();

        assertEquals("CLARO S.A", capturedContext.getVariables().get("nome"));
        assertEquals("conexaoclarobrasil.com.br/login", capturedContext.getVariables().get("link"));
        assertEquals("xbrain@123", capturedContext.getVariables().get("senha"));
    }

    private Usuario umUsuario() {
        return Usuario.builder()
            .nome("Nome Usuario")
            .email("email@xbrain.com.br")
            .senha("senha123")
            .build();
    }

    private UsuarioDadosAcessoRequest umUsuarioDadosAcessoRequest() {
        return UsuarioDadosAcessoRequest.builder()
            .emailAtual("emailAtual@xbrain.com.br")
            .emailNovo("emailNovo@xbrain.com.br")
            .build();
    }

    private BoaVindaAgenteAutorizadoRequest umBoaVindaAgenteAutorizadoRequest() {
        var request = new BoaVindaAgenteAutorizadoRequest();
        request.setAgenteAutorizadoRazaoSocial("CLARO S.A");
        request.setSenha("xbrain@123");
        request.setLink("conexaoclarobrasil.com.br/login");
        request.setEmails(List.of("email@xbrain.com.br"));
        return request;
    }
}
