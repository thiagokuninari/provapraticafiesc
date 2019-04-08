package br.com.xbrain.autenticacao.modules.solicitacaoramal.repository;

import br.com.xbrain.autenticacao.infra.CustomRepository;
import br.com.xbrain.autenticacao.modules.solicitacaoramal.model.SolicitacaoRamalHistorico;
import com.querydsl.jpa.impl.JPAQueryFactory;

import java.util.List;

import static br.com.xbrain.autenticacao.modules.solicitacaoramal.model.QSolicitacaoRamalHistorico.solicitacaoRamalHistorico;

public class SolicitacaoRamalHistoricoRepositoryImpl
        extends CustomRepository<SolicitacaoRamalHistorico>
        implements SolicitacaoRamalHistoricoRepositoryCustom {

    @Override
    public List<SolicitacaoRamalHistorico> findAllBySolicitacaoRamalId(Integer idSolicitacao) {
        return new JPAQueryFactory(entityManager)
                .select(solicitacaoRamalHistorico)
                .from(solicitacaoRamalHistorico)
                .innerJoin(solicitacaoRamalHistorico.solicitacaoRamal)
                .where(solicitacaoRamalHistorico.solicitacaoRamal.id.eq(idSolicitacao))
                .orderBy(solicitacaoRamalHistorico.id.desc())
                .fetch();
    }

    @Override
    public void deleteAll(Integer solicitacaoId) {
        new JPAQueryFactory(entityManager)
                .delete(solicitacaoRamalHistorico)
                .where(solicitacaoRamalHistorico.solicitacaoRamal.id.eq(solicitacaoId))
                .execute();
    }
}
