package br.com.xbrain.autenticacao.modules.call.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SuporteVendasBkoRequest {

    @NotNull
    Integer fornecedorId;
    @NotBlank
    String nome;

    public static SuporteVendasBkoRequest of(Integer fornecedorId, String nome) {
        return SuporteVendasBkoRequest.builder()
            .fornecedorId(fornecedorId)
            .nome(nome)
            .build();
    }
}
