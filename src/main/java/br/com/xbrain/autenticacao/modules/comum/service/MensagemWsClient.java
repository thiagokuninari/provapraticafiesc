package br.com.xbrain.autenticacao.modules.comum.service;

import br.com.xbrain.autenticacao.infra.mensagemWs.wsdl.EnviarEmail;
import br.com.xbrain.autenticacao.infra.mensagemWs.wsdl.EnviarEmailResponse;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;

import javax.xml.bind.JAXBElement;

public class MensagemWsClient extends WebServiceGatewaySupport {

    static final String USUARIO_WS = "PARCEIROS_ONLINE";
    static final String SENHA_WS = "P4RC31R0S0NL1N3";
    static final Short PRIORIDADE_MEDIA = (short) 2;

    public boolean enviarEmail(String assunto, String conteudo, String emails) {
        EnviarEmail enviarEmail = new EnviarEmail();
        enviarEmail.setUsuario(USUARIO_WS);
        enviarEmail.setSenha(SENHA_WS);
        enviarEmail.setPrioridade(PRIORIDADE_MEDIA);
        enviarEmail.setAssunto(assunto);
        enviarEmail.setConteudo(conteudo);
        enviarEmail.setEmailLista(emails);

        EnviarEmailResponse response = (EnviarEmailResponse) ((JAXBElement) getWebServiceTemplate()
                .marshalSendAndReceive(enviarEmail)).getValue();

        return response.isReturn();
    }
}