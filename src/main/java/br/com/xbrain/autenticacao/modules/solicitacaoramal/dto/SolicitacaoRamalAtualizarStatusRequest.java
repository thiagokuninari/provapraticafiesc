package br.com.xbrain.autenticacao.modules.solicitacaoramal.dto;

import br.com.xbrain.autenticacao.modules.solicitacaoramal.enums.ESituacaoSolicitacao;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SolicitacaoRamalAtualizarStatusRequest {

    @NotNull
    private Integer idSolicitacao;
    @NotNull
    private ESituacaoSolicitacao situacao;
    private String observacao;

}
