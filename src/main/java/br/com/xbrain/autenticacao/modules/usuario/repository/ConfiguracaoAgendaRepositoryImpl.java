package br.com.xbrain.autenticacao.modules.usuario.repository;

import br.com.xbrain.autenticacao.modules.comum.dto.PageRequest;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoNivel;
import br.com.xbrain.autenticacao.modules.usuario.enums.ECanal;
import br.com.xbrain.autenticacao.modules.usuario.enums.ETipoCanal;
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
    public Optional<Integer> findQtdHorasAdicionaisByCanal(ECanal canal) {
        return Optional.ofNullable(
            new JPAQueryFactory(entityManager)
                .select(configuracaoAgenda.qtdHorasAdicionais)
                .from(configuracaoAgenda)
                .where(configuracaoAgenda.canal.eq(canal))
                .orderBy(configuracaoAgenda.qtdHorasAdicionais.desc())
                .fetchFirst()
        );
    }

    @Override
    public Optional<Integer> findQtdHorasAdicionaisByNivel(CodigoNivel nivel) {
        return Optional.ofNullable(
            new JPAQueryFactory(entityManager)
                .select(configuracaoAgenda.qtdHorasAdicionais)
                .from(configuracaoAgenda)
                .where(configuracaoAgenda.nivel.eq(nivel))
                .orderBy(configuracaoAgenda.qtdHorasAdicionais.desc())
                .fetchFirst()
        );
    }

    @Override
    public Optional<Integer> findQtdHorasAdicionaisByEstruturaAa(String estruturaAa) {
        return Optional.ofNullable(
            new JPAQueryFactory(entityManager)
                .select(configuracaoAgenda.qtdHorasAdicionais)
                .from(configuracaoAgenda)
                .where(configuracaoAgenda.estruturaAa.equalsIgnoreCase(estruturaAa))
                .orderBy(configuracaoAgenda.qtdHorasAdicionais.desc())
                .fetchFirst()
        );
    }

    @Override
    public Optional<Integer> findQtdHorasAdicionaisBySubcanal(ETipoCanal subcanal) {
        return Optional.ofNullable(
            new JPAQueryFactory(entityManager)
                .select(configuracaoAgenda.qtdHorasAdicionais)
                .from(configuracaoAgenda)
                .where(configuracaoAgenda.subcanal.eq(subcanal))
                .orderBy(configuracaoAgenda.qtdHorasAdicionais.desc())
                .fetchFirst()
        );
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
