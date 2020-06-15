package br.com.xbrain.autenticacao.modules.feriado.dto;

import lombok.*;
import org.hibernate.validator.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FeriadoImportacaoRequest {

    @NotBlank
    private String descricao;
    @NonNull
    private Integer anoReferencia;
}
