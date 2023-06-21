package br.com.xbrain.autenticacao.infra;

import br.com.xbrain.autenticacao.modules.usuario.model.QCargo;
import br.com.xbrain.autenticacao.modules.usuario.model.QDepartamento;
import br.com.xbrain.autenticacao.modules.usuario.model.QUsuario;
import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.support.Querydsl;
import org.springframework.data.querydsl.SimpleEntityPathResolver;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.lang.reflect.ParameterizedType;
import java.util.List;

import static java.util.Collections.emptyList;

public class CustomRepository<T> {

    @PersistenceContext
    protected EntityManager entityManager;
    protected EntityPath<T> path;
    protected Querydsl querydsl;

    public CustomRepository() {
        //noinspection unchecked
        Class<T> domainClass = (Class<T>) ((ParameterizedType)
                getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        this.path = SimpleEntityPathResolver.INSTANCE.createPath(domainClass);
    }

    @PostConstruct
    public void init() {
        querydsl = new Querydsl(entityManager, new PathBuilder<T>(path.getType(), path.getMetadata()));
    }

    public JPAQuery<Object> createQuery(Predicate predicate) {
        return querydsl.createQuery(path).where(predicate);
    }

    /**
     * Reimplementado método findAll utilizando Predicate + Pageable + EntityGraph
     * A versão original do spring esta bugada
     * https://jira.spring.io/browse/DATAJPA-684
     */
    public Page<T> findAll(Predicate predicate, Pageable pageable, String entityGraph, JoinDescriptor... joinDescriptors) {

        final JPAQuery<Object> countQuery = createQuery(predicate);
        final JPAQuery query = (JPAQuery) querydsl.applyPagination(pageable, createQuery(predicate));

        if (entityGraph != null) {
            query.setHint(EntityGraph.EntityGraphType.LOAD.getKey(),
                    entityManager.getEntityGraph(entityGraph));
        }

        for (JoinDescriptor joinDescriptor : joinDescriptors) {
            join(joinDescriptor, query);
            join(joinDescriptor, countQuery);
        }

        final long total = countQuery.fetchCount();
        final List<T> content = pageable == null || total > pageable.getOffset() ? query.fetch() : emptyList();

        return new PageImpl<T>(content, pageable, total);
    }

    public Page<T> findAll(List<JoinDescriptor> joinDescriptors, Predicate predicate, Pageable pageable) {

        final JPAQuery<Object> countQuery = createQuery(predicate);
        final JPAQuery query = (JPAQuery) querydsl.applyPagination(pageable, createQuery(predicate));

        joinDescriptors.forEach(join -> {
            join(join, query);
            join(join, countQuery);
        });

        final long total = countQuery.fetchCount();
        final List<T> content = pageable == null || total > pageable.getOffset() ? query.fetch() : emptyList();

        return new PageImpl<T>(content, pageable, total);
    }

    public Page<T> findAll(List<JoinDescriptor> joinDescriptors, Predicate predicate, Pageable pageable, Sort... sorts) {

        final JPAQuery<Object> countQuery = createQuery(predicate);
        Sort sort = pageable.getSort();
        for (Sort orders : sorts) {
            sort = sort.and(orders);
        }
        final JPAQuery querySort = (JPAQuery) querydsl.applySorting(sort, createQuery(predicate));
        final JPAQuery query = (JPAQuery) querydsl.applyPagination(pageable, querySort);

        joinDescriptors.forEach(join -> {
            join(join, query);
            join(join, countQuery);
        });

        final long total = countQuery.fetchCount();
        final List<T> content = pageable == null || total > pageable.getOffset() ? query.fetch() : emptyList();

        return new PageImpl<T>(content, pageable, total);
    }

    public Page<T> findAllUsuarios(Expression<T> columns, Predicate predicate, Pageable pageable) {

        final JPAQuery<?> countQuery = createQuery(predicate)
                .select(columns);
        final JPAQuery<?> query = (JPAQuery<?>) querydsl.applyPagination(pageable, createQuery(predicate))
                .select(columns)
                .innerJoin(QUsuario.usuario.cargo, QCargo.cargo)
                .innerJoin(QUsuario.usuario.departamento, QDepartamento.departamento);

        final long total = countQuery.fetchCount();
        final List<T> content = pageable == null || total > pageable.getOffset() ? (List<T>) query.fetch() : emptyList();

        return new PageImpl<T>(content, pageable, total);
    }

    private void join(JoinDescriptor joinDescriptor, JPQLQuery query) {
        switch (joinDescriptor.type) {
            case INNERJOIN:
                if (joinDescriptor.alias != null) {
                    if (joinDescriptor.path != null) {
                        query.innerJoin(joinDescriptor.path, joinDescriptor.alias).fetchJoin();
                    } else {
                        query.innerJoin(joinDescriptor.collectionExpression, joinDescriptor.alias).fetchJoin();
                    }
                } else {
                    if (joinDescriptor.path != null) {
                        query.innerJoin(joinDescriptor.path).fetchJoin();
                    } else {
                        query.innerJoin(joinDescriptor.collectionExpression).fetchJoin();
                    }
                }
                break;
            case LEFTJOIN:
                if (joinDescriptor.alias != null) {
                    if (joinDescriptor.path != null) {
                        query.leftJoin(joinDescriptor.path, joinDescriptor.alias).fetchJoin();
                    } else {
                        query.leftJoin(joinDescriptor.collectionExpression, joinDescriptor.alias).fetchJoin();
                    }
                } else {
                    if (joinDescriptor.path != null) {
                        query.leftJoin(joinDescriptor.path).fetchJoin();
                    } else {
                        query.leftJoin(joinDescriptor.collectionExpression).fetchJoin();
                    }
                }
                break;
            default:
                break;
        }
    }
}
