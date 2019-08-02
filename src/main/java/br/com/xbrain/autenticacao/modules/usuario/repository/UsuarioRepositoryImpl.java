package br.com.xbrain.autenticacao.modules.usuario.repository;

import br.com.xbrain.autenticacao.infra.CustomRepository;
import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.comum.model.SubCluster;
import br.com.xbrain.autenticacao.modules.permissao.model.PermissaoEspecial;
import br.com.xbrain.autenticacao.modules.usuario.dto.*;
import br.com.xbrain.autenticacao.modules.usuario.enums.AreaAtuacao;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoNivel;
import br.com.xbrain.autenticacao.modules.usuario.enums.ECanal;
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
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static br.com.xbrain.autenticacao.modules.comum.model.QCluster.cluster;
import static br.com.xbrain.autenticacao.modules.comum.model.QEmpresa.empresa;
import static br.com.xbrain.autenticacao.modules.comum.model.QGrupo.grupo;
import static br.com.xbrain.autenticacao.modules.comum.model.QRegional.regional;
import static br.com.xbrain.autenticacao.modules.comum.model.QSubCluster.subCluster;
import static br.com.xbrain.autenticacao.modules.comum.model.QUnidadeNegocio.unidadeNegocio;
import static br.com.xbrain.autenticacao.modules.permissao.model.QCargoDepartamentoFuncionalidade.cargoDepartamentoFuncionalidade;
import static br.com.xbrain.autenticacao.modules.permissao.model.QFuncionalidade.funcionalidade;
import static br.com.xbrain.autenticacao.modules.permissao.model.QPermissaoEspecial.permissaoEspecial;
import static br.com.xbrain.autenticacao.modules.usuario.model.QCargo.cargo;
import static br.com.xbrain.autenticacao.modules.usuario.model.QCidade.cidade;
import static br.com.xbrain.autenticacao.modules.usuario.model.QDepartamento.departamento;
import static br.com.xbrain.autenticacao.modules.usuario.model.QUsuario.usuario;
import static br.com.xbrain.autenticacao.modules.usuario.model.QUsuarioCidade.usuarioCidade;
import static br.com.xbrain.autenticacao.modules.usuario.model.QUsuarioHierarquia.usuarioHierarquia;
import static com.querydsl.core.types.dsl.Expressions.stringTemplate;
import static com.querydsl.jpa.JPAExpressions.select;

@SuppressWarnings("PMD.TooManyStaticImports")
public class UsuarioRepositoryImpl extends CustomRepository<Usuario> implements UsuarioRepositoryCustom {

    private static final int TRINTA_DOIS_DIAS = 32;

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
                                        .and(usuario.situacao.ne(ESituacao.R))
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
                        .leftJoin(usuario.empresas).fetchJoin()
                        .leftJoin(usuario.organizacao).fetchJoin()
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

    @Override
    @SuppressWarnings("unchecked")
    public List<Object[]> getSubordinadosPorCargo(Integer usuarioId, String codigoCargo) {
        return entityManager
                .createNativeQuery(
                        " SELECT UH.FK_USUARIO "
                                + " , U.NOME "
                                + " , U.EMAIL_01 "
                                + " , C.NOME AS NOME_CARGO "
                                + " FROM USUARIO_HIERARQUIA UH"
                                + " JOIN USUARIO U ON U.ID = UH.FK_USUARIO "
                                + " JOIN CARGO C ON C.ID = U.FK_CARGO "
                                + " WHERE C.CODIGO = :_codigoCargo"
                                + " GROUP BY FK_USUARIO, U.NOME, U.EMAIL_01, C.NOME"
                                + " START WITH UH.FK_USUARIO_SUPERIOR = :_usuarioId "
                                + " CONNECT BY NOCYCLE PRIOR UH.FK_USUARIO = UH.FK_USUARIO_SUPERIOR")
                .setParameter("_usuarioId", usuarioId)
                .setParameter("_codigoCargo", codigoCargo)
                .getResultList();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Object[]> getUsuariosCompletoSubordinados(Integer usuarioId, CodigoCargo codigoCargo) {
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
                                + "  JOIN NIVEL N ON N.ID = D.FK_NIVEL " + where(codigoCargo)
                                + " GROUP BY FK_USUARIO, U.NOME, U.CPF, U.EMAIL_01, N.CODIGO, D.CODIGO, C.CODIGO, C.NOME"
                                + " START WITH FK_USUARIO_SUPERIOR = :_usuarioId "
                                + " CONNECT BY NOCYCLE PRIOR FK_USUARIO = FK_USUARIO_SUPERIOR")
                .setParameter("_usuarioId", usuarioId)
                .getResultList();
    }

    public List<Usuario> getSuperioresDoUsuario(Integer usuarioId) {
        return new JPAQueryFactory(entityManager)
                .select(usuarioHierarquia.usuarioSuperior)
                .from(usuarioHierarquia)
                .leftJoin(usuarioHierarquia.usuario, usuario)
                .where(usuarioHierarquia.usuario.id.eq(usuarioId))
                .fetch();
    }

    public List<Usuario> getSuperioresDoUsuarioPorCargo(Integer usuarioId, CodigoCargo codigoCargo) {
        return new JPAQueryFactory(entityManager)
                .select(usuarioHierarquia.usuarioSuperior)
                .from(usuarioHierarquia)
                .leftJoin(usuarioHierarquia.usuario, usuario)
                .where(usuario.id.eq(usuarioId)
                        .and(usuarioHierarquia.usuarioSuperior.cargo.codigo.eq(codigoCargo)))
                .fetch();
    }

    private String where(CodigoCargo codigoCargo) {
        return Objects.nonNull(codigoCargo)
                ? " WHERE C.CODIGO LIKE '" + codigoCargo.name().toUpperCase() + "'"
                : "";
    }

    public List<Usuario> getUsuariosFilter(Predicate predicate) {
        return new JPAQueryFactory(entityManager)
                .select(usuario)
                .from(usuario)
                .where(predicate)
                .orderBy(usuario.cargo.nome.asc(),
                        usuario.nome.asc())
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
                        + "     , U.SITUACAO  "
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
                        + " GROUP BY U.ID, U.NOME, U.CPF, U.EMAIL_01, N.CODIGO, D.CODIGO, C.CODIGO, U.SITUACAO "
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
    public List<PermissaoEspecial> getUsuariosByPermissao(String codigoFuncionalidade) {
        return new JPAQueryFactory(entityManager)
                .select(permissaoEspecial)
                .from(permissaoEspecial)
                .innerJoin(permissaoEspecial.usuario).fetchJoin()
                .innerJoin(permissaoEspecial.funcionalidade).fetchJoin()
                .where(permissaoEspecial.funcionalidade.role.eq(codigoFuncionalidade)
                        .and(permissaoEspecial.dataBaixa.isNull()))
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

    @Override
    public List<UsuarioCsvResponse> getUsuariosCsv(Predicate predicate) {
        return new JPAQueryFactory(entityManager)
                .select(
                        Projections.constructor(UsuarioCsvResponse.class,
                                usuario.id,
                                usuario.nome,
                                usuario.email,
                                usuario.telefone,
                                usuario.cpf,
                                cargo.nome,
                                departamento.nome,
                                stringTemplate("wm_concat({0})", unidadeNegocio.nome),
                                stringTemplate("wm_concat({0})", empresa.nome),
                                usuario.situacao
                        )
                )
                .from(usuario)
                .leftJoin(usuario.cargo, cargo)
                .leftJoin(usuario.departamento, departamento)
                .leftJoin(usuario.unidadesNegocios, unidadeNegocio)
                .leftJoin(usuario.empresas, empresa)
                .where(predicate)
                .groupBy(usuario.id, usuario.nome, usuario.email, usuario.telefone, usuario.cpf, usuario.rg,
                        cargo.nome, departamento.nome, usuario.situacao)
                .orderBy(usuario.nome.asc())
                .fetch();
    }

    @Override
    public Optional<Usuario> findByEmailIgnoreCaseAndSituacaoNot(String email, ESituacao situacao) {
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
                                        .and(usuario.situacao.ne(ESituacao.R))
                        )
                        .fetchOne());
    }

    @Override
    public List<UsuarioResponse> getUsuariosDaMesmaCidadeDoUsuarioId(Integer usuarioId,
                                                                     List<CodigoCargo> cargos,
                                                                     ECanal canal) {
        return new JPAQueryFactory(entityManager)
                .select(Projections.constructor(UsuarioResponse.class,
                        usuario.id,
                        usuario.nome,
                        usuario.cargo.codigo))
                .from(usuarioCidade)
                .join(usuarioCidade.usuario, usuario)
                .where(usuario.cargo.codigo.in(cargos)
                        .and(usuario.canais.any().eq(canal))
                        .and(usuario.cidades.any().cidade.id.in(
                                select(usuarioCidade.cidade.id)
                                        .from(usuarioCidade)
                                        .where(usuarioCidade.usuario.id.eq(usuarioId))))
                        .and(usuario.situacao.eq(ESituacao.A)))
                .distinct()
                .fetch();
    }

    @Override
    public List<UsuarioResponse> getUsuariosPorAreaAtuacao(AreaAtuacao areaAtuacao,
                                                           List<Integer> areasAtuacaoIds,
                                                           CodigoCargo cargo,
                                                           ECanal canal) {
        return new JPAQueryFactory(entityManager)
                .select(Projections.constructor(UsuarioResponse.class,
                        usuarioCidade.usuario.id,
                        usuarioCidade.usuario.nome,
                        usuarioCidade.usuario.cargo.codigo))
                .from(usuarioCidade)
                .join(usuarioCidade.cidade, cidade)
                .join(cidade.subCluster, subCluster)
                .join(subCluster.cluster, cluster)
                .join(cluster.grupo, grupo)
                .join(grupo.regional, regional)
                .where(usuarioCidade.usuario.cargo.codigo.eq(cargo)
                        .and(usuarioCidade.usuario.canais.any().eq(canal))
                        .and(areaAtuacao.getPredicate().apply(areasAtuacaoIds)))
                .distinct()
                .fetch();
    }

    public List<SubCluster> getSubclustersUsuario(Integer usuarioId) {
        return new JPAQueryFactory(entityManager)
                .select(subCluster)
                .from(usuarioCidade)
                .innerJoin(usuarioCidade.cidade, cidade)
                .innerJoin(cidade.subCluster, subCluster)
                .where(usuarioCidade.usuario.id.eq(usuarioId)
                        .and(usuarioCidade.dataBaixa.isNull()))
                .orderBy(subCluster.nome.asc())
                .distinct()
                .fetch();
    }

    @Override
    public List<UsuarioPermissoesResponse> getUsuariosIdAndPermissoes(List<Integer> usuariosIds, List<String> funcionalidades) {
        var permissoes = select(stringTemplate("wm_concat({0})", cargoDepartamentoFuncionalidade.funcionalidade.role))
                .from(cargoDepartamentoFuncionalidade)
                .innerJoin(cargoDepartamentoFuncionalidade.cargo, cargo)
                .innerJoin(cargoDepartamentoFuncionalidade.departamento, departamento)
                .innerJoin(cargoDepartamentoFuncionalidade.funcionalidade, funcionalidade)
                .where(cargo.eq(usuario.cargo)
                        .and(departamento.eq(usuario.departamento))
                        .and(funcionalidade.role.in(funcionalidades)));
        var permissoesEspeciais = select(stringTemplate("wm_concat({0})", funcionalidade.role))
                .from(permissaoEspecial)
                .innerJoin(permissaoEspecial.funcionalidade, funcionalidade)
                .where(permissaoEspecial.usuario.id.eq(usuario.id)
                        .and(permissaoEspecial.funcionalidade.role.in(funcionalidades))
                        .and(permissaoEspecial.dataBaixa.isNull()));

        return new JPAQueryFactory(entityManager)
                .select(Projections.constructor(
                        UsuarioPermissoesResponse.class,
                        usuario.id,
                        permissoes,
                        permissoesEspeciais)
                )
                .from(usuario)
                .leftJoin(usuario.cargo)
                .leftJoin(usuario.departamento)
                .where(usuario.id.in(usuariosIds))
                .fetch();
    }

    @Override
    public List<Usuario> findAllUsuariosSemDataUltimoAcesso() {
        return new JPAQueryFactory(entityManager)
                .select(Projections.constructor(Usuario.class, usuario.id, usuario.email))
                .from(usuario)
                .where(usuario.situacao.eq(ESituacao.A)
                        .and(usuario.dataUltimoAcesso.isNull()
                                .and(usuario.dataCadastro.before(LocalDateTime.now().minusDays(TRINTA_DOIS_DIAS)))))
                .fetch();
    }
}
