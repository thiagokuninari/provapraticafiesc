package br.com.xbrain.autenticacao.modules.feriado.dto;

import lombok.*;
import org.hibernate.validator.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FeriadoImportacaoRequest {

    @NotBlank
    private String descricao;
    @NotNull
    private Integer anoReferencia;
}
