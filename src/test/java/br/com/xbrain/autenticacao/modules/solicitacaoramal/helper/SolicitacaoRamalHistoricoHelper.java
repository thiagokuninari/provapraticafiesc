package br.com.xbrain.autenticacao.modules.solicitacaoramal.helper;

import br.com.xbrain.autenticacao.modules.solicitacaoramal.model.SolicitacaoRamalHistorico;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;

import java.time.LocalDateTime;
import java.util.List;

import static br.com.xbrain.autenticacao.modules.solicitacaoramal.enums.ESituacaoSolicitacao.CONCLUIDO;
import static br.com.xbrain.autenticacao.modules.solicitacaoramal.helper.SolicitacaoRamalHelper.umaSolicitacaoRamal;

public class SolicitacaoRamalHistoricoHelper {

    public static SolicitacaoRamalHistorico umaSolicitacaoRamalHistorico(Integer id) {
        return SolicitacaoRamalHistorico.builder()
            .id(1)
            .comentario("um comentário")
            .dataCadastro(LocalDateTime.of(2024, 8, 05, 0, 0, 0))
            .situacao(CONCLUIDO)
            .usuario(Usuario.builder().nome("nome do usuario").build())
            .solicitacaoRamal(umaSolicitacaoRamal(1))
            .build();
    }

    public static List<SolicitacaoRamalHistorico> umaListaSolicitacaoRamalHistorico() {
        return List.of(umaSolicitacaoRamalHistorico(1),
            umaSolicitacaoRamalHistorico(2));
    }
}
