package br.com.xbrain.autenticacao.modules.feriado.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FeriadoImportacaoRequest {

    @NotNull
    private Integer anoReferencia;
}
