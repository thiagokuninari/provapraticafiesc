package br.com.xbrain.autenticacao.modules.solicitacaoramal.dto;

import br.com.xbrain.autenticacao.modules.solicitacaoramal.enums.ESituacao;
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
    private ESituacao situacao;
    private String observacao;

}
