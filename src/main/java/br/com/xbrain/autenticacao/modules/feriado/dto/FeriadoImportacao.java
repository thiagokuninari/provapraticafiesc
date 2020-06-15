package br.com.xbrain.autenticacao.modules.feriado.dto;

import br.com.xbrain.autenticacao.modules.comum.model.Uf;
import br.com.xbrain.autenticacao.modules.feriado.enums.ESituacaoFeriado;
import br.com.xbrain.autenticacao.modules.feriado.enums.ETipoFeriado;
import br.com.xbrain.autenticacao.modules.usuario.model.Cidade;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static br.com.xbrain.autenticacao.modules.feriado.enums.ETipoFeriado.NACIONAL;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FeriadoImportacao {

    private String nome;
    private LocalDate dataFeriado;
    private ETipoFeriado tipoFeriado;
    private LocalDateTime dataCadasatro;
    private Uf uf;
    private Cidade cidade;
    private ESituacaoFeriado situacao;
    private List<String> motivoNaoImportacao;

    public boolean isTipoFeriadoComUfObrigatorio() {
        return tipoFeriado.equals(ETipoFeriado.ESTADUAL)
            || tipoFeriado.equals(ETipoFeriado.MUNICIPAL);
    }

    public boolean isTipoFeriadoComCidadeObrigatorio() {
        return tipoFeriado.equals(ETipoFeriado.MUNICIPAL);
    }

    public boolean isFeriadoNacional() {
        return tipoFeriado.equals(NACIONAL);
    }
}
