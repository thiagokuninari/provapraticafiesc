package br.com.xbrain.autenticacao.modules.solicitacaoramal.dto;

import org.junit.Test;

import java.time.LocalDateTime;

import static br.com.xbrain.autenticacao.modules.solicitacaoramal.enums.ESituacaoSolicitacao.CONCLUIDO;
import static br.com.xbrain.autenticacao.modules.solicitacaoramal.helper.SolicitacaoRamalHistoricoHelper.umaSolicitacaoRamalHistorico;
import static org.assertj.core.api.Assertions.assertThat;

public class SolicitacaoRamalHistoricoResponseTest {

    @Test
    public void convertFrom_deveRetornarSolicitacaoRamalHistoricoResponse_quandoSolicitado() {
        assertThat(SolicitacaoRamalHistoricoResponse.convertFrom(umaSolicitacaoRamalHistorico(1)))
            .extracting("id", "comentario", "dataCadastro", "situacao", "usuarioSolicitante")
            .containsExactly(1, "um comentário",
                LocalDateTime.of(2024, 8, 05, 0, 0, 0),
                CONCLUIDO, "nome do usuario");
    }
}
