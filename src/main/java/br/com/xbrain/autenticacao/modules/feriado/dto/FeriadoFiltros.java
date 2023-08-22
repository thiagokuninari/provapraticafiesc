package br.com.xbrain.autenticacao.modules.feriado.dto;

import br.com.xbrain.autenticacao.modules.feriado.enums.ESituacaoFeriadoAutomacao;
import br.com.xbrain.autenticacao.modules.feriado.enums.ETipoFeriado;
import br.com.xbrain.autenticacao.modules.feriado.predicate.FeriadoPredicate;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FeriadoFiltros {

    private String nome;
    private ETipoFeriado tipoFeriado;
    private Integer estadoId;
    private Integer cidadeId;
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private LocalDate dataInicio;
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private LocalDate dataFim;
    private ESituacaoFeriadoAutomacao situacaoFeriadoAutomacao;

    @JsonIgnore
    public FeriadoPredicate toPredicate() {
        return new FeriadoPredicate()
            .comNome(nome)
            .comTipoFeriado(tipoFeriado)
            .comPeriodoDeDataFeriado(dataInicio, dataFim)
            .comCidadeId(cidadeId, estadoId)
            .comEstado(estadoId)
            .excetoFeriadosFilhos()
            .excetoExcluidos();
    }
}
