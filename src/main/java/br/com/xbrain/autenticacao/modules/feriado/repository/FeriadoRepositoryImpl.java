package br.com.xbrain.autenticacao.modules.feriado.repository;

import br.com.xbrain.autenticacao.infra.CustomRepository;
import br.com.xbrain.autenticacao.modules.feriado.model.Feriado;
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
                .where(feriado.dataFeriado.between(
                        now.with(TemporalAdjusters.firstDayOfYear()),
                        now.with(TemporalAdjusters.lastDayOfYear())
                ))
                .fetch();
    }
}
