package br.com.xbrain.autenticacao.modules.usuario.repository;

import br.com.xbrain.autenticacao.infra.CustomRepository;
import br.com.xbrain.autenticacao.modules.permissao.model.PermissaoEspecial;
import br.com.xbrain.autenticacao.modules.permissao.model.QPermissaoEspecial;
import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioFiltrosHierarquia;
import br.com.xbrain.autenticacao.modules.usuario.dto.UsuarioHierarquiaResponse;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoFuncionalidade;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoNivel;
import br.com.xbrain.autenticacao.modules.usuario.model.*;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import javax.persistence.EntityManager;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static br.com.xbrain.autenticacao.modules.usuario.model.QCargo.cargo;
import static br.com.xbrain.autenticacao.modules.usuario.model.QUsuario.usuario;
import static br.com.xbrain.autenticacao.modules.usuario.model.QUsuarioCidade.usuarioCidade;
import static br.com.xbrain.autenticacao.modules.usuario.model.QUsuarioHierarquia.usuarioHierarquia;

public class UsuarioRepositoryImpl extends CustomRepository<Usuario> implements UsuarioRepositoryCustom {

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

    public Optional<Usuario> findUsuarioByEmail(String email) {
        return Optional.ofNullable(
                new JPAQueryFactory(entityManager)
                        .select(usuario)
                        .from(usuario)
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
                                + " CONNECT BY NOCYCLE PRIOR FK_USUARIO = FK_USUARIO_SUPERIOR")
                .setParameter("_usuarioId", usuarioId)
                .getResultList();
        return result
                .stream()
                .map(BigDecimal::intValue)
                .collect(Collectors.toList());
    }

    @SuppressWarnings("unchecked")
    public List<Integer> getUsuariosSubordinadosByCidade(Integer usuarioId) {
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

    @Override
    @SuppressWarnings("unchecked")
    public List<Object[]> getUsuariosCompletoSubordinados(Integer usuarioId) {
        return entityManager
                .createNativeQuery(
                        " SELECT FK_USUARIO"
                                + "     , U.NOME "
                                + "     , U.CPF "
                                + "     , U.EMAIL_01 "
                                + "     , N.CODIGO AS NIVEL "
                                + "     , D.CODIGO AS DEPARTAMENTO "
                                + "     , C.CODIGO AS CARGO "
                                + "     , C.NOME AS NOME_CARGO "
                                + " FROM usuario_hierarquia UH"
                                + "  JOIN USUARIO U ON U.ID = UH.FK_USUARIO "
                                + "  JOIN CARGO C ON C.ID = U.FK_CARGO "
                                + "  JOIN DEPARTAMENTO D ON D.ID = U.FK_DEPARTAMENTO "
                                + "  JOIN NIVEL N ON N.ID = D.FK_NIVEL "
                                + " GROUP BY FK_USUARIO, U.NOME, U.CPF, U.EMAIL_01, N.CODIGO, D.CODIGO, C.CODIGO, C.NOME"
                                + " START WITH FK_USUARIO_SUPERIOR = :_usuarioId "
                                + " CONNECT BY NOCYCLE PRIOR FK_USUARIO = FK_USUARIO_SUPERIOR")
                .setParameter("_usuarioId", usuarioId)
                .getResultList();
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
                        + "     , C.CODIGO AS CARGO "
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
                        + " CONNECT BY NOCYCLE PRIOR UH.FK_USUARIO_SUPERIOR = UH.FK_USUARIO ")
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
                .select(QPermissaoEspecial.permissaoEspecial)
                .from(QPermissaoEspecial.permissaoEspecial)
                .innerJoin(QPermissaoEspecial.permissaoEspecial.usuario).fetchJoin()
                .innerJoin(QPermissaoEspecial.permissaoEspecial.funcionalidade).fetchJoin()
                .where(QPermissaoEspecial.permissaoEspecial.funcionalidade.role.eq(codigoFuncionalidade.toString())
                        .and(QPermissaoEspecial.permissaoEspecial.dataBaixa.isNull()))
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

    @Override
    public List<Integer> getUsuariosPorCidade(Integer idUsuario) {
        QUsuarioCidade usuarioCidadeGeral = new QUsuarioCidade("usuarioCidadeGeral");
        return new JPAQueryFactory(entityManager)
                .select(usuarioCidadeGeral.usuario.id)
                .from(usuarioCidade)
                .innerJoin(usuarioCidade.cidade.cidadeUsuarios, usuarioCidadeGeral)
                .where(usuarioCidade.usuario.id.eq(idUsuario)
                        .and(usuarioCidade.dataBaixa.isNull())
                        .and(usuarioCidadeGeral.dataBaixa.isNull()))
                .distinct()
                .fetch();
    }

    @Override
    public Page<Usuario> findAll(Predicate predicate, Pageable pageable) {

        Expression<Cargo> expressionCargo = Projections.fields(Cargo.class,
                cargo.id,
                cargo.nome,
                cargo.codigo,
                cargo.situacao,
                cargo.nivel
        ).as("cargo");

        Expression<Departamento> expressionDepartamento = Projections.fields(Departamento.class,
                QDepartamento.departamento.id,
                QDepartamento.departamento.nome,
                QDepartamento.departamento.codigo,
                QDepartamento.departamento.situacao
        ).as("departamento");

        Expression<Usuario> expressionUsuario = Projections.fields(Usuario.class,
                QUsuario.usuario.id,
                QUsuario.usuario.nome,
                QUsuario.usuario.email,
                QUsuario.usuario.situacao,
                expressionCargo,
                expressionDepartamento);

        return super.findAllUsuarios(
                expressionUsuario,
                predicate,
                pageable);
    }

    @Override
    public Optional<Usuario> findComConfiguracao(Integer usuarioId) {
        return Optional.ofNullable(
                new JPAQueryFactory(entityManager)
                        .select(usuario)
                        .from(usuario)
                        .join(usuario.configuracao).fetchJoin()
                        .where(usuario.id.eq(usuarioId))
                        .fetchOne());
    }

    @Override
    public List<UsuarioHierarquiaResponse> findAllUsuariosHierarquia(Predicate predicate) {
        return new JPAQueryFactory(entityManager)
                .select(Projections.constructor(UsuarioHierarquiaResponse.class, usuario.id, usuario.nome))
                .from(usuario)
                .where(predicate)
                .distinct()
                .orderBy(usuario.nome.asc())
                .fetch();
    }
}
