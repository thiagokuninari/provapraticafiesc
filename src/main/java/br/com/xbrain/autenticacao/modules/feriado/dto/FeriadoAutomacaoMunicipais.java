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
public class FeriadoAutomacaoMunicipais {

    private Integer cidadeId;
    private String cidadeNome;
    private List<FeriadoAutomacao> feriadosMunicipais;

}
