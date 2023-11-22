package br.com.xbrain.autenticacao.modules.call.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotBlank;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SuporteVendasBkoRequest {

    Integer fornecedorId;
    @NotBlank
    String nome;

    public static SuporteVendasBkoRequest of(Integer fornecedorId, String nome) {
        return SuporteVendasBkoRequest.builder()
            .fornecedorId(fornecedorId)
            .nome(nome)
            .build();
    }

    public static SuporteVendasBkoRequest of(String nome) {
        return SuporteVendasBkoRequest.builder()
            .nome(nome)
            .build();
    }
}
