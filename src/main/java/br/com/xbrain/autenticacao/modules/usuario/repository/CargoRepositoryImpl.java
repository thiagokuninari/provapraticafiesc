package br.com.xbrain.autenticacao.modules.usuario.repository;

import br.com.xbrain.autenticacao.infra.CustomRepository;
import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.usuario.model.Cargo;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static br.com.xbrain.autenticacao.infra.JoinDescriptor.innerJoin;
import static br.com.xbrain.autenticacao.modules.usuario.model.QCargo.cargo;
import static br.com.xbrain.autenticacao.modules.usuario.model.QUsuario.usuario;
import static java.util.Arrays.asList;

public class CargoRepositoryImpl extends CustomRepository<Cargo> implements CargoRepositoryCustom {

    public Page<Cargo> findAll(Predicate predicate, Pageable pageable) {
        return super.findAll(
                asList(
                        innerJoin(cargo.nivel)
                ),
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
    public Iterable<Cargo> findBySituacaoAndNivelId(ESituacao situacao, Integer nivelId) {
        return new JPAQueryFactory(entityManager)
                .select(cargo)
                .from(cargo)
                .innerJoin(cargo.nivel).fetchJoin()
                .where(
                        cargo.nivel.id.eq(nivelId)
                                .and(cargo.situacao.eq(situacao))
                )
                .orderBy(cargo.nome.asc())
                .fetch();
    }

    @Override
    public Optional<Cargo> findByUsuarioId(Integer usuarioId) {
        return Optional.ofNullable(
                new JPAQueryFactory(entityManager)
                        .select(cargo)
                        .from(usuario)
                        .innerJoin(usuario.cargo).fetchJoin()
                        .where(usuario.id.eq(usuarioId))
                        .fetchOne());
    }
}
