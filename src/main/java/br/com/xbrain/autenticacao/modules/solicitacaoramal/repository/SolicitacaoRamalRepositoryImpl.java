package br.com.xbrain.autenticacao.modules.solicitacaoramal.repository;

import br.com.xbrain.autenticacao.infra.CustomRepository;
import br.com.xbrain.autenticacao.infra.JoinDescriptor;
import br.com.xbrain.autenticacao.modules.solicitacaoramal.model.QSolicitacaoRamal;
import br.com.xbrain.autenticacao.modules.solicitacaoramal.model.SolicitacaoRamal;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static br.com.xbrain.autenticacao.modules.solicitacaoramal.enums.ESituacaoSolicitacao.*;
import static br.com.xbrain.autenticacao.modules.solicitacaoramal.model.QSolicitacaoRamal.solicitacaoRamal;
import static br.com.xbrain.autenticacao.modules.usuario.model.QCargo.cargo;
import static br.com.xbrain.autenticacao.modules.usuario.model.QUsuario.usuario;

@SuppressWarnings("PMD.TooManyStaticImports")
public class SolicitacaoRamalRepositoryImpl
        extends CustomRepository<SolicitacaoRamal>
        implements SolicitacaoRamalRepositoryCustom {

    @Override
    public Page<SolicitacaoRamal> findAll(Pageable pageable, Predicate predicate) {
        return super.findAll(
                Collections.singletonList(JoinDescriptor.innerJoin(solicitacaoRamal.usuario)),
                predicate,
                pageable
        );
    }

    @Override
    public PageImpl<SolicitacaoRamal> findAllGerencia(Pageable pageable, Predicate predicate) {
        final QSolicitacaoRamal solicitacaoAuxiliar = new QSolicitacaoRamal("solicitacao");
        List<SolicitacaoRamal> solicitacoes = new JPAQueryFactory(entityManager)
                .select(
                        Projections.constructor(SolicitacaoRamal.class,
                                solicitacaoRamal.id,
                                solicitacaoRamal.agenteAutorizadoId,
                                solicitacaoRamal.agenteAutorizadoNome,
                                solicitacaoRamal.agenteAutorizadoCnpj,
                                solicitacaoRamal.situacao,
                                solicitacaoRamal.quantidadeRamais,
                                solicitacaoRamal.dataCadastro,
                                solicitacaoRamal.dataFinalizacao,
                                new JPAQueryFactory(entityManager)
                                        .select(usuario)
                                        .from(usuario)
                                        .where(usuario.id.eq(solicitacaoRamal.usuario.id)))
                ).from(solicitacaoRamal)
                .where(solicitacaoRamal.id.eq(new JPAQueryFactory(entityManager)
                        .select(solicitacaoAuxiliar.id.max())
                        .from(solicitacaoAuxiliar)
                        .where(solicitacaoAuxiliar.agenteAutorizadoId.eq(solicitacaoRamal.agenteAutorizadoId)))
                        .and(predicate))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(solicitacaoRamal.id.desc())
                .fetch();

        return new PageImpl<>(solicitacoes, pageable, countSolicitacaoRamal(predicate));
    }

    private long countSolicitacaoRamal(Predicate predicate) {
        return new JPAQueryFactory(entityManager)
                .selectDistinct(solicitacaoRamal.agenteAutorizadoId)
                .from(solicitacaoRamal)
                .where(predicate)
                .fetchCount();
    }

    @Override
    public List<SolicitacaoRamal> findAllBySituacaoAndDataEnviadoEmailExpiracaoIsNull() {
        return new JPAQueryFactory(entityManager)
                .select(solicitacaoRamal)
                .from(solicitacaoRamal)
                .where(solicitacaoRamal.situacao.eq(PENDENTE)
                        .or(solicitacaoRamal.situacao.eq(EM_ANDAMENTO))
                        .and(solicitacaoRamal.dataEnviadoEmailExpiracao.isNull()))
                .orderBy(solicitacaoRamal.id.asc())
                .fetch();
    }

    @Override
    public List<SolicitacaoRamal> findAllByAgenteAutorizadoIdAndSituacaoDiferentePendenteOuEmAndamento(Integer aaId) {
        return new JPAQueryFactory(entityManager)
                .select(solicitacaoRamal)
                .from(solicitacaoRamal)
                .where(solicitacaoRamal.agenteAutorizadoId.eq(aaId)
                        .and(solicitacaoRamal.situacao.eq(PENDENTE)
                                .or(solicitacaoRamal.situacao.eq(EM_ANDAMENTO))))
                .fetch();
    }

    @Override
    public List<SolicitacaoRamal> findAllByAgenteAutorizadoIdAndSituacaoEnviadoOuConcluido(Integer aaId) {
        return new JPAQueryFactory(entityManager)
            .select(solicitacaoRamal)
            .from(solicitacaoRamal)
            .where(solicitacaoRamal.agenteAutorizadoId.eq(aaId)
                .and(solicitacaoRamal.situacao.in(ENVIADO, CONCLUIDO)))
            .fetch();
    }

    @Override
    public Optional<SolicitacaoRamal> findBySolicitacaoId(Integer solicitacaoId) {
        return Optional.ofNullable(
                new JPAQueryFactory(entityManager)
                        .select(solicitacaoRamal)
                        .from(solicitacaoRamal)
                        .innerJoin(solicitacaoRamal.usuariosSolicitados, usuario).fetchJoin()
                        .innerJoin(usuario.cargo, cargo).fetchJoin()
                        .where(solicitacaoRamal.id.eq(solicitacaoId))
                        .fetchOne()
        );
    }

    @Override
    public List<SolicitacaoRamal> findAllByPredicate(Predicate predicate) {
        return new JPAQueryFactory(entityManager)
                    .selectFrom(solicitacaoRamal)
                    .where(predicate)
                    .fetch();
    }
}
