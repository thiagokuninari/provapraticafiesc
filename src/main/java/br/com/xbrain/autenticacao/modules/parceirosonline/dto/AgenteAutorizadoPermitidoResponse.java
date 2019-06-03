package br.com.xbrain.autenticacao.modules.parceirosonline.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.codehaus.jackson.annotate.JsonSetter;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AgenteAutorizadoPermitidoResponse {
    private Integer id;
    private String cnpjRazaoSocial;
    private String cnpj;

    @JsonSetter("value")
    public void setValue(int id) {
        this.id = id;
    }

    @JsonSetter("text")
    public void setText(String text) {
        this.cnpjRazaoSocial = text;
    }
}
