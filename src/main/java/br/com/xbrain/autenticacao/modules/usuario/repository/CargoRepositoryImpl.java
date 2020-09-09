package br.com.xbrain.autenticacao.modules.usuario.repository;

import br.com.xbrain.autenticacao.infra.CustomRepository;
import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.usuario.model.Cargo;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static br.com.xbrain.autenticacao.infra.JoinDescriptor.innerJoin;
import static br.com.xbrain.autenticacao.modules.usuario.model.QCargo.cargo;

public class CargoRepositoryImpl extends CustomRepository<Cargo> implements CargoRepositoryCustom {

    public Page<Cargo> findAll(Predicate predicate, Pageable pageable) {
        return super.findAll(
            List.of(innerJoin(cargo.nivel)),
            predicate,
            pageable);
    }

    @Override
    public List<Cargo> findAll(Predicate predicate) {
        return new JPAQueryFactory(entityManager)
            .select(cargo)
            .from(cargo)
            .where(cargo.situacao.eq(ESituacao.A)
                .and(predicate))
            .orderBy(cargo.nome.asc())
            .fetch();
    }

    @Override
    public List<Cargo> buscarTodosComNiveis(Predicate predicate) {
        return new JPAQueryFactory(entityManager)
            .selectFrom(cargo)
            .join(cargo.nivel).fetchJoin()
            .where(predicate, cargo.situacao.eq(ESituacao.A))
            .orderBy(cargo.nome.asc(), cargo.nivel.nome.asc())
            .fetch();
    }
}


