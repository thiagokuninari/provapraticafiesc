package br.com.xbrain.autenticacao.modules.comum.repository;

import br.com.xbrain.autenticacao.infra.CustomRepository;
import br.com.xbrain.autenticacao.modules.comum.model.Organizacao;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQueryFactory;

import java.util.List;

import static br.com.xbrain.autenticacao.modules.comum.model.QOrganizacao.organizacao;

public class OrganizacaoRepositoryImpl extends CustomRepository<Organizacao> implements OrganizacaoRepositoryCustom {

    @Override
    public List<Organizacao> findByPredicate(Predicate predicate) {
        return new JPAQueryFactory(entityManager)
            .select(organizacao)
            .from(organizacao)
            .where(predicate)
            .orderBy(organizacao.nome.asc())
            .fetch();
    }
}
