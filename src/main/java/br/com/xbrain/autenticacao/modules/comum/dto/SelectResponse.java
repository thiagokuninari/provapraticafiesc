package br.com.xbrain.autenticacao.modules.comum.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SelectResponse implements Serializable {

    private Object value;
    private String label;

    public static SelectResponse of(Object value, String label) {
        return new SelectResponse(value, label);
    }
}
