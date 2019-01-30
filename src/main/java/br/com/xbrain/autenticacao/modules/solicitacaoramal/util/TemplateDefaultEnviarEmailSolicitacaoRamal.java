package br.com.xbrain.autenticacao.modules.solicitacaoramal.util;

import br.com.xbrain.autenticacao.modules.solicitacaoramal.model.SolicitacaoRamal;
import org.thymeleaf.context.Context;

import java.util.Arrays;
import java.util.List;

public abstract class TemplateDefaultEnviarEmailSolicitacaoRamal {

    public static final List<String> DESTINATARIOS = Arrays.asList("suporte@conexaoclarobrasil.com.br",
                                                                   "alison@xbrain.com.br",
                                                                   "wilian@xbrain.com.br");
    public static final String ASSUNTO_EMAIL_CADASTRAR = "Nova Solicitação de Ramal";
    public static final String ASSUNTO_EMAIL_EXPIRAR = "Solicitação de Ramal irá expirar em 48h";
    public static final String TEMPLATE_EMAIL = "solicitacao-ramal";

    public abstract Context obterContexto(SolicitacaoRamal solicitacaoRamal);

}
