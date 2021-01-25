package br.com.xbrain.autenticacao.modules.email.model;

import br.com.xbrain.autenticacao.modules.comum.enums.EmailPrioridade;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Email {

    private List<String> bcc;
    private String body;
    private List<String> cc;
    private String replyTo;
    private String subject;
    private List<String> to;
    private EmailPrioridade priority;

}
