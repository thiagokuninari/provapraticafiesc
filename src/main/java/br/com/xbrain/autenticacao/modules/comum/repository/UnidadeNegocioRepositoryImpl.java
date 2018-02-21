package br.com.xbrain.autenticacao.modules.comum.repository;

import br.com.xbrain.autenticacao.infra.CustomRepository;
import br.com.xbrain.autenticacao.modules.comum.model.UnidadeNegocio;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQueryFactory;

import java.util.ArrayList;
import java.util.List;

import static br.com.xbrain.autenticacao.modules.comum.model.QUnidadeNegocio.unidadeNegocio;

public class UnidadeNegocioRepositoryImpl extends CustomRepository<UnidadeNegocio> implements UnidadeNegocioRepositoryCustom {
    @Override
    public List<UnidadeNegocio> findAll(Predicate predicate) {
        return new ArrayList<>(new JPAQueryFactory(entityManager)
                .select(unidadeNegocio)
                .from(unidadeNegocio)
                .where(predicate)
                .orderBy(unidadeNegocio.nome.asc())
                .fetch());
    }

}
