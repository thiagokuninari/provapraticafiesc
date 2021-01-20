package br.com.xbrain.autenticacao.modules.email.model;

import br.com.xbrain.autenticacao.modules.comum.enums.EmailPrioridade;

import java.util.List;

public class EmailBuilder {

    private Email email;

    public EmailBuilder() {
        this.email = new Email();
    }

    public EmailBuilder comDestinatarios(List<String> destinatarios) {
        email.setTo(destinatarios);
        return this;
    }

    public EmailBuilder comCorpo(String corpo) {
        email.setBody(corpo);
        return this;
    }

    public EmailBuilder comAssunto(String assunto) {
        email.setSubject(assunto);
        return this;
    }

    public EmailBuilder comCopia(List<String> destinatarios) {
        email.setCc(destinatarios);
        return this;
    }

    public EmailBuilder comCopiaOculta(List<String> destinatarios) {
        email.setBcc(destinatarios);
        return this;
    }

    public EmailBuilder comResponderPara(String destino) {
        email.setReplyTo(destino);
        return this;
    }
    public EmailBuilder comPriority (EmailPrioridade prioridade) {
        email.setPriority(prioridade);
        return this;
    }

    public Email build() {
        return email;
    }

}
