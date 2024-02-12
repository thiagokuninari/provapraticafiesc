package br.com.xbrain.autenticacao.modules.usuario.repository;

import br.com.xbrain.autenticacao.modules.comum.dto.PageRequest;
import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoNivel;
import br.com.xbrain.autenticacao.modules.usuario.enums.ECanal;
import br.com.xbrain.autenticacao.modules.usuario.model.ConfiguracaoAgendaReal;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import javax.persistence.EntityManager;
import java.util.Optional;

import static br.com.xbrain.autenticacao.modules.usuario.model.QConfiguracaoAgendaReal.configuracaoAgendaReal;

@RequiredArgsConstructor
public class ConfiguracaoAgendaRealRepositoryImpl implements ConfiguracaoAgendaRealRepositoryCustom {

    private final EntityManager entityManager;

    @Override
    public Optional<Integer> findQtdHorasAdicionaisByCanal(ECanal canal) {
        return Optional.ofNullable(
            new JPAQueryFactory(entityManager)
                .select(configuracaoAgendaReal.qtdHorasAdicionais)
                .from(configuracaoAgendaReal)
                .where(configuracaoAgendaReal.canal.eq(canal)
                    .and(configuracaoAgendaReal.situacao.ne(ESituacao.I)))
                .orderBy(configuracaoAgendaReal.qtdHorasAdicionais.desc())
                .fetchFirst()
        );
    }

    @Override
    public Optional<Integer> findQtdHorasAdicionaisByNivel(CodigoNivel nivel) {
        return Optional.ofNullable(
            new JPAQueryFactory(entityManager)
                .select(configuracaoAgendaReal.qtdHorasAdicionais)
                .from(configuracaoAgendaReal)
                .where(configuracaoAgendaReal.nivel.eq(nivel)
                    .and(configuracaoAgendaReal.situacao.ne(ESituacao.I)))
                .orderBy(configuracaoAgendaReal.qtdHorasAdicionais.desc())
                .fetchFirst()
        );
    }

    @Override
    public Optional<Integer> findQtdHorasAdicionaisByEstruturaAa(String estruturaAa) {
        return Optional.ofNullable(
            new JPAQueryFactory(entityManager)
                .select(configuracaoAgendaReal.qtdHorasAdicionais)
                .from(configuracaoAgendaReal)
                .where(configuracaoAgendaReal.estruturaAa.equalsIgnoreCase(estruturaAa)
                    .and(configuracaoAgendaReal.situacao.ne(ESituacao.I)))
                .orderBy(configuracaoAgendaReal.qtdHorasAdicionais.desc())
                .fetchFirst()
        );
    }

    @Override
    public Optional<Integer> findQtdHorasAdicionaisBySubcanal(Integer subcanalId) {
        return Optional.ofNullable(
            new JPAQueryFactory(entityManager)
                .select(configuracaoAgendaReal.qtdHorasAdicionais)
                .from(configuracaoAgendaReal)
                .where(configuracaoAgendaReal.subcanalId.eq(subcanalId)
                    .and(configuracaoAgendaReal.situacao.ne(ESituacao.I)))
                .orderBy(configuracaoAgendaReal.qtdHorasAdicionais.desc())
                .fetchFirst()
        );
    }

    @Override
    public Page<ConfiguracaoAgendaReal> findAllByPredicate(Predicate predicate, PageRequest pageable) {
        var query = new JPAQueryFactory(entityManager)
            .selectFrom(configuracaoAgendaReal)
            .where(predicate)
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        return new PageImpl<>(query, pageable, query.size());
    }
}
