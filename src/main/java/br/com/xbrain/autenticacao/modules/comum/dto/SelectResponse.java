package br.com.xbrain.autenticacao.modules.comum.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class SelectResponse implements Serializable {

    private Object value;
    private String label;

    public static SelectResponse convertFrom(Object value, String label) {
        SelectResponse selectResponse = new SelectResponse();
        selectResponse.setValue(value);
        selectResponse.setLabel(label);
        return selectResponse;
    }
}
