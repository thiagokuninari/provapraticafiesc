package br.com.xbrain.autenticacao.modules.usuario.repository;

import br.com.xbrain.autenticacao.infra.CustomRepository;
import br.com.xbrain.autenticacao.modules.usuario.model.SubCanal;
import com.querydsl.jpa.impl.JPAQueryFactory;

import java.util.List;

import static br.com.xbrain.autenticacao.modules.usuario.model.QSubCanal.subCanal;

public class SubCanalRepositoryImpl extends CustomRepository<SubCanal> implements SubCanalRepositoryCustom {

    @Override
    public List<SubCanal> findAll() {
        return new JPAQueryFactory(entityManager)
                .selectFrom(subCanal)
                .orderBy(subCanal.id.asc())
                .fetch();
    }
}
