package br.com.xbrain.autenticacao.modules.comum.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SelectResponse {

    private Object value;
    private String label;

    public static SelectResponse convertFrom(Object value, String label) {
        SelectResponse selectResponse = new SelectResponse();
        selectResponse.setValue(value);
        selectResponse.setLabel(label);
        return selectResponse;
    }

    public static Integer getValueInt(SelectResponse usuarioSelectResponse) {
        return (Integer) usuarioSelectResponse.value;
    }
}
