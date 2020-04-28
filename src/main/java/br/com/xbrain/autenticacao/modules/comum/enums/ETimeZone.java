package br.com.xbrain.autenticacao.modules.comum.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum ETimeZone {

    ACT("Horário do Acre", "America/Rio_Branco"),
    AMT("Horário do Amazonas", "America/Manaus"),
    BRT("Horário de Brasília", "America/Sao_Paulo"),
    FNT("Horário de Fernando de Noronha", "America/Noronha");

    private String descricao;
    private String zoneId;

    @JsonProperty("codigo")
    public String getcodigo() {
        return name();
    }
}
