package br.com.xbrain.autenticacao.modules.usuario.repository;

import br.com.xbrain.autenticacao.modules.comum.dto.PageRequest;
import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.usuario.model.ConfiguracaoAgenda;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import javax.persistence.EntityManager;
import java.util.Optional;

import static br.com.xbrain.autenticacao.modules.usuario.model.QConfiguracaoAgenda.configuracaoAgenda;

@RequiredArgsConstructor
public class ConfiguracaoAgendaRepositoryImpl implements ConfiguracaoAgendaRepositoryCustom {

    private final EntityManager entityManager;

    @Override
    public Optional<ConfiguracaoAgenda> findByPredicateOrderByQtdHorasDesc(Predicate predicate) {
        return Optional.ofNullable(
            new JPAQueryFactory(entityManager)
                .selectFrom(configuracaoAgenda)
                .where(configuracaoAgenda.situacao.eq(ESituacao.A)
                    .and(predicate))
                .orderBy(configuracaoAgenda.qtdHorasAdicionais.desc())
                .fetchFirst());
    }

    @Override
    public Page<ConfiguracaoAgenda> findAllByPredicate(Predicate predicate, PageRequest pageable) {
        var query = new JPAQueryFactory(entityManager)
            .selectFrom(configuracaoAgenda)
            .where(predicate)
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        return new PageImpl<>(query, pageable, query.size());
    }
}
