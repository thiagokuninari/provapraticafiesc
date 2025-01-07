package br.com.xbrain.autenticacao.modules.comum.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class MessageException {

    private String message;
    private String field;

    public MessageException(@JsonProperty("message") String message) {
        this.message = message;
    }

    public MessageException(String field, String message) {
        this.field = field;
        this.message = message;
    }
}
