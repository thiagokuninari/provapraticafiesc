package br.com.xbrain.autenticacao.modules.notificacao.service;

import br.com.xbrain.autenticacao.modules.comum.service.EmailService;
import br.com.xbrain.autenticacao.modules.notificacao.dto.BoaVindaAgenteAutorizadoRequest;
import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioDadosAcessoRequest;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;

import java.util.Arrays;

@Service
public class NotificacaoService {

    @Autowired
    private EmailService emailService;

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

    public void enviarEmailAtualizacaoSenha(Usuario usuario, String senhaDescriptografada) {
        Context context = new Context();
        context.setVariable("nome", usuario.getNome());
        context.setVariable("email", usuario.getEmail().toLowerCase());
        context.setVariable("senha", senhaDescriptografada);

        // TODO: Quando permitido, alterar novamente para Conexão Claro Brasil
        /*emailService.enviarEmailTemplate(
                Arrays.asList(usuario.getEmail()),
                "Conexão Claro Brasil - Alteração de dados de acesso",
                "reenvio-senha",
                context);*/
        emailService.enviarEmailTemplate(
                Arrays.asList(usuario.getEmail()),
                "Parceiros Online - Alteração de dados de acesso",
                "reenvio-senha",
                context);
    }

    public void enviarEmailAtualizacaoEmail(Usuario usuario,
                                            UsuarioDadosAcessoRequest usuarioDadosAcessoRequest) {
        Context context = new Context();
        context.setVariable("nome", usuario.getNome());
        context.setVariable("emailNovo", usuarioDadosAcessoRequest.getEmailNovo().toLowerCase());
        context.setVariable("emailAntigo", usuarioDadosAcessoRequest.getEmailAtual().toLowerCase());

        // TODO: Quando permitido, alterar novamente para Conexão Claro Brasil
        /*emailService.enviarEmailTemplate(
                Arrays.asList(usuarioDadosAcessoRequest.getEmailAtual()),
                "Conexão Claro Brasil - Alteração de dados de acesso",
                "alteracao-email",
                context);*/
        emailService.enviarEmailTemplate(
                Arrays.asList(usuarioDadosAcessoRequest.getEmailAtual()),
                "Parceiros Online - Alteração de dados de acesso",
                "alteracao-email",
                context);
    }

    public void enviarEmailBoaVindaAgenteAutorizado(BoaVindaAgenteAutorizadoRequest request) {
        Context context = new Context();
        context.setVariable("nome", request.getAgenteAutorizadoRazaoSocial());
        context.setVariable("link", request.getLink().toLowerCase());
        context.setVariable("senha", request.getSenha());

        emailService.enviarEmailConexaoClaroBrasil(
                request.getEmails(),
                "Conexão Claro Brasil - Seja bem-vindo(a)",
                "boas-vindas-aa",
                context);
    }
}
