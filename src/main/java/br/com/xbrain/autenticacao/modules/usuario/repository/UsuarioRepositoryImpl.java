package br.com.xbrain.autenticacao.modules.usuario.repository;

import br.com.xbrain.autenticacao.modules.permissao.model.PermissaoEspecial;
import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioFiltrosHierarquia;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoFuncionalidade;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoNivel;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import br.com.xbrain.autenticacao.modules.usuario.model.UsuarioHierarquia;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static br.com.xbrain.autenticacao.modules.permissao.model.QPermissaoEspecial.permissaoEspecial;
import static br.com.xbrain.autenticacao.modules.usuario.model.QCargo.cargo;
import static br.com.xbrain.autenticacao.modules.usuario.model.QUsuario.usuario;
import static br.com.xbrain.autenticacao.modules.usuario.model.QUsuarioHierarquia.usuarioHierarquia;

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
                        .leftJoin(usuario.cidades).fetchJoin()
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

    @Override
    @SuppressWarnings("unchecked")
    public List<Object[]> getUsuariosSuperiores(UsuarioFiltrosHierarquia filtros) {
        return entityManager.createNativeQuery(
                "SELECT U.ID "
                        + "     , U.NOME "
                        + "     , U.CPF "
                        + "     , U.EMAIL_01 "
                        + "     , N.CODIGO AS NIVEL "
                        + "     , D.CODIGO AS DEPARTAMENTO "
                        + "     , C.CODIGO AS CARAGO "
                        + "     , LISTAGG(E.CODIGO, ',') WITHIN GROUP (ORDER BY E.CODIGO) AS EMPRESAS "
                        + "     , LISTAGG(UN.CODIGO, ',') WITHIN GROUP (ORDER BY UN.CODIGO) AS UNIDADES_NEGOCIOS "
                        + "  FROM USUARIO_HIERARQUIA UH "
                        + "  JOIN USUARIO U ON U.ID = UH.FK_USUARIO_SUPERIOR "
                        + "  JOIN CARGO C ON C.ID = U.FK_CARGO "
                        + "  JOIN DEPARTAMENTO D ON D.ID = U.FK_DEPARTAMENTO "
                        + "  JOIN NIVEL N ON N.ID = D.FK_NIVEL "
                        + "  JOIN USUARIO_EMPRESA UE ON UE.FK_USUARIO = U.ID "
                        + "  JOIN EMPRESA E ON E.ID = UE.FK_EMPRESA "
                        + "  JOIN USUARIO_UNIDADE_NEGOCIO UNE ON UNE.FK_USUARIO = U.ID "
                        + "  JOIN UNIDADE_NEGOCIO UN ON UN.ID = UNE.FK_UNIDADE_NEGOCIO "
                        + " WHERE C.CODIGO = :_codigoCargo "
                        + "   AND D.CODIGO = :_codigoDepartamento "
                        + "   AND N.CODIGO = :_codigoNivel "
                        + " GROUP BY U.ID, U.NOME, U.CPF, U.EMAIL_01, N.CODIGO, D.CODIGO, C.CODIGO "
                        + "  START WITH UH.FK_USUARIO IN :_idUsuario "
                        + " CONNECT BY PRIOR UH.FK_USUARIO_SUPERIOR = UH.FK_USUARIO ")
                .setParameter("_codigoCargo", filtros.getCodigoCargo().toString())
                .setParameter("_codigoDepartamento", filtros.getCodigoDepartamento().toString())
                .setParameter("_codigoNivel", filtros.getCodigoNivel().toString())
                .setParameter("_idUsuario", filtros.getUsuarioId())
                .getResultList();
    }

    @Override
    public Optional<UsuarioHierarquia> getUsuarioSuperior(Integer usuarioId) {
        return Optional.ofNullable(
                new JPAQueryFactory(entityManager)
                        .select(usuarioHierarquia)
                        .from(usuarioHierarquia)
                        .join(usuarioHierarquia.usuario).fetchJoin()
                        .join(usuarioHierarquia.usuarioSuperior).fetchJoin()
                        .where(usuarioHierarquia.usuario.id.eq(usuarioId))
                        .distinct()
                        .fetchOne());
    }

    @Override
    public List<UsuarioHierarquia> getUsuarioSuperiores(Integer usuarioId) {
        return new JPAQueryFactory(entityManager)
                        .select(usuarioHierarquia)
                        .from(usuarioHierarquia)
                        .join(usuarioHierarquia.usuario).fetchJoin()
                        .join(usuarioHierarquia.usuarioSuperior).fetchJoin()
                        .where(usuarioHierarquia.usuario.id.eq(usuarioId))
                        .distinct()
                        .fetch();
    }

    @Override
    public List<PermissaoEspecial> getUsuariosByPermissao(CodigoFuncionalidade codigoFuncionalidade) {
        return new JPAQueryFactory(entityManager)
                .select(permissaoEspecial)
                .from(permissaoEspecial)
                .innerJoin(permissaoEspecial.usuario).fetchJoin()
                .innerJoin(permissaoEspecial.funcionalidade).fetchJoin()
                .where(permissaoEspecial.funcionalidade.role.eq(codigoFuncionalidade.toString()))
                .fetch();
    }

    @Override
    public List<Usuario> getUsuariosByNivel(CodigoNivel codigoNivel) {
        return new JPAQueryFactory(entityManager)
                .select(usuario)
                .from(usuario)
                .innerJoin(usuario.cargo, cargo)
                .where(cargo.nivel.codigo.eq(codigoNivel))
                .orderBy(usuario.nome.asc())
                .fetch();
    }
}
