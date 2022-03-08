package br.com.xbrain.autenticacao.modules.notificacao.service;

import br.com.xbrain.autenticacao.modules.comum.enums.EmailPrioridade;
import br.com.xbrain.autenticacao.modules.email.service.EmailService;
import br.com.xbrain.autenticacao.modules.notificacao.dto.BoaVindaAgenteAutorizadoRequest;
import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioDadosAcessoRequest;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;

import java.util.Arrays;

@Service
public class NotificacaoService {

    @Autowired
    private EmailService emailService;

    @Async
    public void enviarEmailDadosDeAcesso(Usuario usuario, String senhaDescriptografada) {
        Context context = new Context();
        context.setVariable("nome", usuario.getNome());
        context.setVariable("email", usuario.getEmail().toLowerCase());
        context.setVariable("senha", senhaDescriptografada);

        // TODO: Quando permitido, alterar novamente para Conexão Claro Brasil
        /*emailService.enviarEmailTemplate(
                Arrays.asList(usuario.getEmail()),
                "Conexão Claro Brasil - Seja bem-vindo(a)",
                "confirmacao-cadastro",
                context);*/
        emailService.enviarEmailTemplate(
                Arrays.asList(usuario.getEmail()),
                "Parceiros Online - Seja bem-vindo(a)",
                "confirmacao-cadastro",
                context);
    }

    @Async
    public void enviarEmailAtualizacaoSenha(Usuario usuario, String senhaDescriptografada) {
        Context context = new Context();
        context.setVariable("nome", usuario.getNome());
        context.setVariable("email", usuario.getEmail().toLowerCase());
        context.setVariable("senha", senhaDescriptografada);

        emailService.enviarEmailTemplate(
                Arrays.asList(usuario.getEmail()),
                "Conexão Claro Brasil - Alteração de dados de acesso",
                "reenvio-senha",
                context);
    }

    @Async
    public void enviarEmailResetSenha(Usuario usuario, String link) {
        Context context = new Context();
        context.setVariable("nome", usuario.getNome());
        context.setVariable("email", usuario.getEmail().toLowerCase());
        context.setVariable("link", link);

        emailService.enviarEmailTemplate(
                Arrays.asList(usuario.getEmail()),
                "Conexão Claro Brasil - Confirmação de Alterar a Senha",
                "confirmar-reset-senha",
                context,
                EmailPrioridade.ALTA);
    }

    @Async
    public void enviarEmailAtualizacaoEmail(Usuario usuario,
                                            UsuarioDadosAcessoRequest usuarioDadosAcessoRequest) {
        Context context = new Context();
        context.setVariable("nome", usuario.getNome());
        context.setVariable("emailNovo", usuarioDadosAcessoRequest.getEmailNovo().toLowerCase());
        context.setVariable("emailAntigo", usuarioDadosAcessoRequest.getEmailAtual().toLowerCase());

        emailService.enviarEmailTemplate(
                Arrays.asList(usuarioDadosAcessoRequest.getEmailAtual()),
                "Conexão Claro Brasil - Alteração de dados de acesso",
                "alteracao-email",
                context);
    }

    @Async
    public void enviarEmailBoaVindaAgenteAutorizado(BoaVindaAgenteAutorizadoRequest request) {
        Context context = new Context();
        context.setVariable("nome", request.getAgenteAutorizadoRazaoSocial());
        context.setVariable("link", request.getLink());
        context.setVariable("senha", request.getSenha());

        emailService.enviarEmailConexaoClaroBrasil(
                request.getEmails(),
                "Conexão Claro Brasil - Seja bem-vindo(a)",
                "boas-vindas-aa",
                context);
    }
}
