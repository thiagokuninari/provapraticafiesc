package br.com.xbrain.autenticacao.modules.usuario.repository;

import br.com.xbrain.autenticacao.infra.CustomRepository;
import br.com.xbrain.autenticacao.modules.usuario.model.Nivel;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQueryFactory;

import java.util.List;

import static br.com.xbrain.autenticacao.modules.usuario.model.QNivel.nivel;

public class NivelRepositoryImpl extends CustomRepository<Nivel> implements NivelRepositoryCustom {

    @Override
    public List<Nivel> getAll(Predicate predicate) {
        return new JPAQueryFactory(entityManager)
                .select(nivel)
                .from(nivel)
                .where(predicate)
                .orderBy(nivel.nome.asc())
                .fetch();
    }
}
