package br.com.xbrain.autenticacao.modules.feriado.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FeriadoAutomacaoEstadual {

    private String sigla;
    private List<FeriadoAutomacao> feriados;

}
