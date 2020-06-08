package br.com.xbrain.autenticacao.modules.feriado.repository;

import br.com.xbrain.autenticacao.infra.CustomRepository;
import br.com.xbrain.autenticacao.modules.comum.enums.Eboolean;
import br.com.xbrain.autenticacao.modules.comum.model.QUf;
import br.com.xbrain.autenticacao.modules.feriado.enums.ESituacaoFeriado;
import br.com.xbrain.autenticacao.modules.feriado.model.Feriado;
import br.com.xbrain.autenticacao.modules.usuario.model.QCidade;
import com.querydsl.jpa.impl.JPAQueryFactory;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

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
}
