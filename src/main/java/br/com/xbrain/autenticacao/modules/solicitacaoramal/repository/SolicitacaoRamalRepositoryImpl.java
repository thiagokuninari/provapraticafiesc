package br.com.xbrain.autenticacao.modules.solicitacaoramal.repository;

import br.com.xbrain.autenticacao.infra.CustomRepository;
import br.com.xbrain.autenticacao.infra.JoinDescriptor;
import br.com.xbrain.autenticacao.modules.comum.enums.Eboolean;
import br.com.xbrain.autenticacao.modules.solicitacaoramal.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.solicitacaoramal.model.SolicitacaoRamal;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.List;

import static br.com.xbrain.autenticacao.modules.solicitacaoramal.model.QSolicitacaoRamal.solicitacaoRamal;

public class SolicitacaoRamalRepositoryImpl
        extends CustomRepository<SolicitacaoRamal>
            implements SolicitacaoRamalRepositoryCustom {

    @Override
    public List<SolicitacaoRamal> findAllByUsuarioId(Pageable pageable, Integer usuarioId, Predicate predicate) {
        return new JPAQueryFactory(entityManager)
                .select(solicitacaoRamal)
                .from(solicitacaoRamal)
                .innerJoin(solicitacaoRamal.usuario)
                .where(solicitacaoRamal.usuario.id.eq(usuarioId).and(predicate))
                .orderBy(solicitacaoRamal.id.desc())
                .fetch();
    }

    @Override
    public Page<SolicitacaoRamal> findAll(Pageable pageable, Predicate predicate) {
        return super.findAll(
                Collections.singletonList(JoinDescriptor.innerJoin(solicitacaoRamal.usuario)),
                predicate,
                pageable
        );
    }

    @Override
    public List<SolicitacaoRamal> findAllBySituacaoPendenteOrEmAndamentoAndEnviouEmailExpiracaoFalse() {
        return new JPAQueryFactory(entityManager)
                .select(solicitacaoRamal)
                .from(solicitacaoRamal)
                .where(solicitacaoRamal.situacao.eq(ESituacao.PD).or(solicitacaoRamal.situacao.eq(ESituacao.EA))
                        .and(solicitacaoRamal.enviouEmailExpiracao.eq(Eboolean.F)))
                .orderBy(solicitacaoRamal.id.asc())
                .fetch();
    }
}
