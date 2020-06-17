package br.com.xbrain.autenticacao.modules.feriado.dto;

import lombok.*;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FeriadoImportacaoRequest {

    @NotBlank
    private String descricao;
    @NotEmpty
    private Integer anoReferencia;
}
