package br.com.xbrain.autenticacao.modules.solicitacaoramal.dto;

import br.com.xbrain.autenticacao.modules.solicitacaoramal.enums.ESituacao;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.Arrays;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SolicitacaoRamalAtualizarStatusRequest {

    @NotNull
    private Integer idSolicitacao;
    @NotNull
    private String situacao;
    private String observacao;

    public ESituacao convertStringSituacaoForEnum() {
        return Arrays.stream(ESituacao.values()).filter(s -> s.getDescricao().equals(this.situacao))
                .findFirst()
                .get();
    }
}
