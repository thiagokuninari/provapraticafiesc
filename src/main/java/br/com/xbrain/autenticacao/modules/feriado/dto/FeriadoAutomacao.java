package br.com.xbrain.autenticacao.modules.feriado.dto;

import br.com.xbrain.autenticacao.modules.feriado.enums.ETipoFeriado;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FeriadoAutomacao {

    private String dataFeriado;
    private String diaSemana;
    private String nome;
    private ETipoFeriado tipoFeriado;
    private Integer cidadeId;
    private String cidadeNome;
    private Integer ufId;
    private String uf;
    private Integer ano;
}
