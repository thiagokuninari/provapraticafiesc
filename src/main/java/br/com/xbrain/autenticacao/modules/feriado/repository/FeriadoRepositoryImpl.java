package br.com.xbrain.autenticacao.modules.feriado.repository;

import br.com.xbrain.autenticacao.infra.CustomRepository;
import br.com.xbrain.autenticacao.modules.comum.enums.Eboolean;
import br.com.xbrain.autenticacao.modules.comum.model.QUf;
import br.com.xbrain.autenticacao.modules.feriado.dto.FeriadoMesAnoResponse;
import br.com.xbrain.autenticacao.modules.feriado.enums.ESituacaoFeriado;
import br.com.xbrain.autenticacao.modules.feriado.model.Feriado;
import br.com.xbrain.autenticacao.modules.usuario.model.QCidade;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQueryFactory;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Optional;

import static br.com.xbrain.autenticacao.modules.feriado.model.QFeriado.feriado;

public class FeriadoRepositoryImpl extends CustomRepository<Feriado> implements FeriadoRepositoryCustom {

    @Override
    public List<Feriado> findAllByAnoAtual(LocalDate now) {
        return new JPAQueryFactory(entityManager)
            .select(feriado)
            .from(feriado)
            .where(
                feriado.situacao.eq(ESituacaoFeriado.ATIVO)
                    .and(feriado.dataFeriado.between(
                        now.with(TemporalAdjusters.firstDayOfYear()),
                        now.with(TemporalAdjusters.lastDayOfYear())))
            )
            .fetch();
    }

    @Override
    public boolean hasFeriadoNacionalOuRegional(LocalDate data, String cidade, String uf) {
        return new JPAQueryFactory(entityManager)
            .select(feriado.id.countDistinct())
            .from(feriado)
            .leftJoin(feriado.cidade, QCidade.cidade)
            .leftJoin(QCidade.cidade.uf, QUf.uf1)
            .where(
                feriado.dataFeriado.eq(data)
                    .and(feriado.situacao.eq(ESituacaoFeriado.ATIVO))
                    .and(feriado.feriadoNacional.eq(Eboolean.V)
                        .or(QCidade.cidade.nome.eq(cidade.toUpperCase())
                            .and(QUf.uf1.uf.eq(uf.toUpperCase())
                                .or(QUf.uf1.nome.eq(uf.toUpperCase())))))
            )
            .fetchCount() > 0;
    }

    @Override
    public List<LocalDate> findAllDataFeriadoByCidadeId(Integer cidadeId) {
        return new JPAQueryFactory(entityManager)
            .select(feriado.dataFeriado)
            .from(feriado)
            .leftJoin(feriado.cidade, QCidade.cidade)
            .where(feriado.feriadoNacional.eq(Eboolean.V)
                .or(QCidade.cidade.id.eq(cidadeId)))
            .distinct()
            .fetch();
    }

    @Override
    public Optional<Feriado> findByPredicate(Predicate predicate) {
        return Optional.ofNullable(new JPAQueryFactory(entityManager)
            .select(feriado)
            .from(feriado)
            .where(predicate)
            .fetchOne());
    }

    @Override
    public void exluirByFeriadoIds(List<Integer> feriadoIds) {
        new JPAQueryFactory(entityManager)
            .update(feriado)
            .set(feriado.situacao, ESituacaoFeriado.EXCLUIDO)
            .where(feriado.id.in(feriadoIds))
            .execute();
    }

    @Override
    public void updateFeriadoNomeEDataByIds(List<Integer> feriadoIds, String nome, LocalDate dataFeriado) {
        new JPAQueryFactory(entityManager)
            .update(feriado)
            .set(feriado.nome, nome)
            .set(feriado.dataFeriado, dataFeriado)
            .where(feriado.id.in(feriadoIds))
            .execute();
    }

    @Override
    public List<FeriadoMesAnoResponse> buscarTotalDeFeriadosPorMesAno() {
        return new JPAQueryFactory(entityManager)
            .select(Projections.constructor(FeriadoMesAnoResponse.class,
                feriado.dataFeriado.year(),
                feriado.dataFeriado.month(),
                feriado.id.count())
            )
            .from(feriado)
            .where(feriado.feriadoNacional.eq(Eboolean.V))
            .groupBy(feriado.dataFeriado.year(), feriado.dataFeriado.month())
            .orderBy(feriado.dataFeriado.year().asc(), feriado.dataFeriado.month().asc())
            .fetch();
    }
}
