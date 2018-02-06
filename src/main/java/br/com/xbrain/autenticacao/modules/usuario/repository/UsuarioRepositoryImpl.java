package br.com.xbrain.autenticacao.modules.usuario.repository;

import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static br.com.xbrain.autenticacao.modules.usuario.model.QCargo.cargo;
import static br.com.xbrain.autenticacao.modules.usuario.model.QUsuario.usuario;

public class UsuarioRepositoryImpl implements UsuarioRepositoryCustom {

    @Autowired
    private EntityManager entityManager;

    public Optional<Usuario> findByEmail(String email) {
        return Optional.ofNullable(
                new JPAQueryFactory(entityManager)
                        .select(usuario)
                        .from(usuario)
                        .innerJoin(usuario.cargo, cargo).fetchJoin()
                        .innerJoin(cargo.nivel).fetchJoin()
                        .innerJoin(usuario.departamento).fetchJoin()
                        .innerJoin(usuario.empresas).fetchJoin()
                        .where(
                                usuario.email.equalsIgnoreCase(email)
                        )
                        .fetchOne());
    }

    public Optional<Usuario> findComplete(Integer id) {
        return Optional.ofNullable(
                new JPAQueryFactory(entityManager)
                        .select(usuario)
                        .from(usuario)
                        .join(usuario.cargo, cargo).fetchJoin()
                        .join(cargo.nivel).fetchJoin()
                        .join(usuario.departamento).fetchJoin()
                        .join(usuario.empresas).fetchJoin()
                        .where(usuario.id.eq(id))
                        .distinct()
                        .fetchOne()
        );
    }

    public Optional<Usuario> findComHierarquia(Integer id) {
        return Optional.ofNullable(
                new JPAQueryFactory(entityManager)
                        .select(usuario)
                        .from(usuario)
                        .join(usuario.cargo, cargo).fetchJoin()
                        .join(cargo.nivel).fetchJoin()
                        .join(usuario.departamento).fetchJoin()
                        .leftJoin(usuario.usuariosHierarquia).fetchJoin()
                        .where(usuario.id.eq(id))
                        .distinct()
                        .fetchOne()
        );
    }

    @Override
    public Optional<Usuario> findComCidade(Integer id) {
        return Optional.ofNullable(
                new JPAQueryFactory(entityManager)
                        .select(usuario)
                        .from(usuario)
                        .join(usuario.cidades).fetchJoin()
                        .where(usuario.id.eq(id))
                        .distinct()
                        .orderBy(usuario.id.asc())
                        .fetchOne()
        );
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Integer> getUsuariosSubordinados(Integer usuarioId) {
        List<BigDecimal> result = entityManager
                .createNativeQuery(
                        " SELECT FK_USUARIO"
                                + " FROM usuario_hierarquia"
                                + " START WITH FK_USUARIO_SUPERIOR = :_usuarioId "
                                + " CONNECT BY PRIOR FK_USUARIO = FK_USUARIO_SUPERIOR")
                .setParameter("_usuarioId", usuarioId)
                .getResultList();
        return result
                .stream()
                .map(BigDecimal::intValue)
                .collect(Collectors.toList());
    }

    public List<Usuario> getUsuariosFilter(Predicate predicate) {
        return new JPAQueryFactory(entityManager)
                .select(usuario)
                .from(usuario)
                .where(predicate)
                .fetch();
    }
}
