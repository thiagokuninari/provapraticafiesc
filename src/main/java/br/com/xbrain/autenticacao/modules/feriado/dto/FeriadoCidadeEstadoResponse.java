package br.com.xbrain.autenticacao.modules.feriado.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeriadoCidadeEstadoResponse {

    private String cidade;
    private String estado;
}
