package br.com.xbrain.autenticacao.modules.comum.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SelectResponse implements Serializable {

    private Object value;
    private String label;

    public static SelectResponse of(Object value, String label) {
        return new SelectResponse(value, label);
    }

    public static Integer getValueInt(SelectResponse usuarioSelectResponse) {
        return (Integer) usuarioSelectResponse.value;
    }
}
