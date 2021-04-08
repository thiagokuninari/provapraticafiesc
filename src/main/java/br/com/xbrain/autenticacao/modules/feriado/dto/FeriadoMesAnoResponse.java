package br.com.xbrain.autenticacao.modules.feriado.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeriadoMesAnoResponse {

    private Integer ano;
    private Integer mes;
    private Long qtdFeriadosNacionais;
}
