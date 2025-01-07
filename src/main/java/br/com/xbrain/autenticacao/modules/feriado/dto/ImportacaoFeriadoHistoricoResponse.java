package br.com.xbrain.autenticacao.modules.feriado.dto;

import br.com.xbrain.autenticacao.modules.feriado.enums.ESituacaoFeriadoAutomacao;
import br.com.xbrain.autenticacao.modules.feriado.importacaoautomatica.model.ImportacaoFeriado;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class ImportacaoFeriadoHistoricoResponse {

    private Integer id;
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm")
    private LocalDateTime dataCadastro;
    private ESituacaoFeriadoAutomacao situacaoFeriadoAutomacao;
    private Integer usuarioCadastroId;
    private String usuarioCadastroNome;
    private String descricao;

    public static ImportacaoFeriadoHistoricoResponse of(ImportacaoFeriado importacaoFeriado) {
        var importacaoHistorico = new ImportacaoFeriadoHistoricoResponse();
        BeanUtils.copyProperties(importacaoFeriado, importacaoHistorico);
        return importacaoHistorico;
    }
}
