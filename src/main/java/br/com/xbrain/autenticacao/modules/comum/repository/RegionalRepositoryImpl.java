package br.com.xbrain.autenticacao.modules.comum.repository;

import br.com.xbrain.autenticacao.infra.CustomRepository;
import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.comum.model.Regional;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQueryFactory;

import java.util.List;

import static br.com.xbrain.autenticacao.modules.comum.model.QRegional.regional;

public class RegionalRepositoryImpl extends CustomRepository<Regional> implements RegionalRepositoryCustom {

    @Override
    public List<Regional> getAll(Predicate predicate) {
        return new JPAQueryFactory(entityManager)
                .select(regional)
                .from(regional)
                .where(regional.situacao.eq(ESituacao.A).and(predicate))
                .orderBy(regional.nome.asc())
                .fetch();
    }

}
