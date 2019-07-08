package br.com.xbrain.autenticacao.modules.comum.repository;

import br.com.xbrain.autenticacao.infra.CustomRepository;
import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.comum.model.Grupo;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQueryFactory;

import java.util.List;

import static br.com.xbrain.autenticacao.modules.comum.model.QGrupo.grupo;

public class GrupoRepositoryImpl extends CustomRepository<Grupo> implements GrupoRepositoryCustom {

    @Override
    public List<Grupo> findAllByRegionalId(Integer regionalId, Predicate predicate) {
        return new JPAQueryFactory(entityManager)
                .select(grupo)
                .from(grupo)
                .where(grupo.situacao.eq(ESituacao.A)
                        .and(grupo.regional.id.eq(regionalId))
                        .and(predicate))
                .orderBy(grupo.nome.asc())
                .fetch();
    }
}