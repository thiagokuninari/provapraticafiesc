package br.com.xbrain.autenticacao.modules.usuario.repository;

import br.com.xbrain.autenticacao.infra.CustomRepository;
import br.com.xbrain.autenticacao.modules.comum.dto.SelectResponse;
import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.comum.model.SubCluster;
import br.com.xbrain.autenticacao.modules.permissao.model.PermissaoEspecial;
import br.com.xbrain.autenticacao.modules.usuario.dto.*;
import br.com.xbrain.autenticacao.modules.usuario.enums.AreaAtuacao;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoNivel;
import br.com.xbrain.autenticacao.modules.usuario.enums.ECanal;
import br.com.xbrain.autenticacao.modules.usuario.model.*;
import br.com.xbrain.autenticacao.modules.usuario.predicate.UsuarioPredicate;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPADeleteClause;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static br.com.xbrain.autenticacao.modules.comum.enums.ESituacao.A;
import static br.com.xbrain.autenticacao.modules.comum.model.QCluster.cluster;
import static br.com.xbrain.autenticacao.modules.comum.model.QEmpresa.empresa;
import static br.com.xbrain.autenticacao.modules.comum.model.QGrupo.grupo;
import static br.com.xbrain.autenticacao.modules.comum.model.QRegional.regional;
import static br.com.xbrain.autenticacao.modules.comum.model.QSubCluster.subCluster;
import static br.com.xbrain.autenticacao.modules.comum.model.QUnidadeNegocio.unidadeNegocio;
import static br.com.xbrain.autenticacao.modules.permissao.model.QCargoDepartamentoFuncionalidade.cargoDepartamentoFuncionalidade;
import static br.com.xbrain.autenticacao.modules.permissao.model.QFuncionalidade.funcionalidade;
import static br.com.xbrain.autenticacao.modules.permissao.model.QPermissaoEspecial.permissaoEspecial;
import static br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo.*;
import static br.com.xbrain.autenticacao.modules.usuario.enums.CodigoDepartamento.COMERCIAL;
import static br.com.xbrain.autenticacao.modules.usuario.enums.CodigoNivel.OPERACAO;
import static br.com.xbrain.autenticacao.modules.usuario.model.QCargo.cargo;
import static br.com.xbrain.autenticacao.modules.usuario.model.QCidade.cidade;
import static br.com.xbrain.autenticacao.modules.usuario.model.QDepartamento.departamento;
import static br.com.xbrain.autenticacao.modules.usuario.model.QNivel.nivel;
import static br.com.xbrain.autenticacao.modules.usuario.model.QUsuario.usuario;
import static br.com.xbrain.autenticacao.modules.usuario.model.QUsuarioCidade.usuarioCidade;
import static br.com.xbrain.autenticacao.modules.usuario.model.QUsuarioHierarquia.usuarioHierarquia;
import static com.querydsl.core.types.dsl.Expressions.stringTemplate;
import static com.querydsl.jpa.JPAExpressions.select;

@SuppressWarnings("PMD.TooManyStaticImports")
public class UsuarioRepositoryImpl extends CustomRepository<Usuario> implements UsuarioRepositoryCustom {

    private static final int TRINTA_DOIS_DIAS = 32;
    private static final Integer CARGO_SUPERVISOR_ID = 10;
    private static final int ID_NIVEL_OPERACAO = 1;

    @Autowired
    private EntityManager entityManager;
    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

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
    public Optional<List<Cidade>> findComCidade(Integer id) {
        return Optional.ofNullable(
                new JPAQueryFactory(entityManager)
                        .select(cidade)
                        .from(cidade)
                        .leftJoin(cidade.cidadeUsuarios, usuarioCidade).fetchJoin()
                        .leftJoin(cidade.uf).fetchJoin()
                        .where(usuarioCidade.usuario.id.eq(id))
                        .fetch()
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
    public List<UsuarioSubordinadoDto> getUsuariosCompletoSubordinados(Integer usuarioId) {
        return jdbcTemplate.query(" SELECT FK_USUARIO AS ID"
                        + "     , U.NOME "
                        + "     , U.CPF "
                        + "     , U.EMAIL_01 AS EMAIL"
                        + "     , N.CODIGO AS CODIGO_NIVEL "
                        + "     , D.CODIGO AS CODIGO_DEPARTAMENTO "
                        + "     , C.CODIGO AS CODIGO_CARGO "
                        + "     , C.NOME AS NOME_CARGO "
                        + " FROM usuario_hierarquia UH"
                        + "  JOIN USUARIO U ON U.ID = UH.FK_USUARIO "
                        + "  JOIN CARGO C ON C.ID = U.FK_CARGO "
                        + "  JOIN DEPARTAMENTO D ON D.ID = U.FK_DEPARTAMENTO "
                        + "  JOIN NIVEL N ON N.ID = D.FK_NIVEL "
                        + " GROUP BY FK_USUARIO, U.NOME, U.CPF, U.EMAIL_01, N.CODIGO, D.CODIGO, C.CODIGO, C.NOME"
                        + " START WITH FK_USUARIO_SUPERIOR = :usuarioId "
                        + " CONNECT BY NOCYCLE PRIOR FK_USUARIO = FK_USUARIO_SUPERIOR",
                new MapSqlParameterSource().addValue("usuarioId", usuarioId),
                new BeanPropertyRowMapper<>(UsuarioSubordinadoDto.class));
    }

    @Override
    public List<UsuarioAutoComplete> getSubordinadosDoGerenteComCargoExecutivoOrExecutivoHunter(Integer usuarioId) {
        return jdbcTemplate.query(" SELECT FK_USUARIO AS VALUE"
                        + "     , U.NOME AS TEXT"
                        + " FROM usuario_hierarquia UH"
                        + "  JOIN USUARIO U ON U.ID = UH.FK_USUARIO "
                        + "  JOIN CARGO C ON C.ID = U.FK_CARGO "
                        + "  JOIN DEPARTAMENTO D ON D.ID = U.FK_DEPARTAMENTO "
                        + "  JOIN NIVEL N ON N.ID = D.FK_NIVEL "
                        + "  WHERE U.SITUACAO = 'A' AND C.CODIGO IN (:codigoCargos)"
                        + " GROUP BY FK_USUARIO, U.NOME, U.CPF, U.EMAIL_01, N.CODIGO, D.CODIGO, C.CODIGO, C.NOME"
                        + " START WITH FK_USUARIO_SUPERIOR = :usuarioId "
                        + " CONNECT BY NOCYCLE PRIOR FK_USUARIO = FK_USUARIO_SUPERIOR",
                new MapSqlParameterSource()
                        .addValue("usuarioId", usuarioId)
                        .addValue("codigoCargos", List.of(EXECUTIVO.name(), EXECUTIVO_HUNTER.name())),
                new BeanPropertyRowMapper<>(UsuarioAutoComplete.class));
    }

    @Override
    public List<UsuarioAutoComplete> findAllExecutivosOperacaoDepartamentoComercial() {
        return new JPAQueryFactory(entityManager)
            .select(
                Projections.constructor(UsuarioAutoComplete.class, usuario.id, usuario.nome))
            .from(usuario)
            .innerJoin(usuario.cargo, cargo)
            .innerJoin(usuario.departamento, departamento)
            .innerJoin(departamento.nivel, nivel)
            .where(cargo.codigo.in(EXECUTIVO, EXECUTIVO_HUNTER)
                .and(departamento.codigo.eq(COMERCIAL)
                    .and(nivel.codigo.eq(OPERACAO)))
                .and(usuario.situacao.eq(A)))
            .orderBy(usuario.nome.asc())
            .fetch();
    }

    @Override
    public List<UsuarioAutoComplete> findAllExecutivosDosIds(List<Integer> idsPermitidos) {
        return new JPAQueryFactory(entityManager)
            .select(
                Projections.constructor(UsuarioAutoComplete.class, usuario.id, usuario.nome))
            .from(usuario)
            .innerJoin(usuario.cargo, cargo)
            .innerJoin(usuario.departamento, departamento)
            .innerJoin(departamento.nivel, nivel)
            .where(cargo.codigo.in(EXECUTIVO, EXECUTIVO_HUNTER)
                .and(departamento.codigo.eq(COMERCIAL)
                    .and(nivel.codigo.eq(OPERACAO)))
                .and(usuario.situacao.eq(A))
                .and(usuario.id.in(idsPermitidos)))
            .orderBy(usuario.nome.asc())
            .fetch();
    }

    @Override
    public List<UsuarioAutoComplete> findAllExecutivosDosIdsCoordenadorGerente(List<Integer> idsPermitidos, Integer usuarioId) {
        return new JPAQueryFactory(entityManager)
            .select(
                Projections.constructor(UsuarioAutoComplete.class, usuario.id, usuario.nome))
            .from(usuario)
            .innerJoin(usuario.cargo, cargo)
            .innerJoin(usuario.departamento, departamento)
            .innerJoin(departamento.nivel, nivel)
            .where(cargo.codigo.in(EXECUTIVO, EXECUTIVO_HUNTER)
                .and(departamento.codigo.eq(COMERCIAL)
                    .and(nivel.codigo.eq(OPERACAO)))
                .and(usuario.situacao.eq(A))
                .and(usuario.id.in(idsPermitidos))
                .and(usuario.id.in(
                    new JPAQueryFactory(entityManager)
                        .select(usuarioHierarquia.usuario.id)
                        .from(usuarioHierarquia)
                        .where(usuarioHierarquia.usuario.id.in(idsPermitidos)
                            .and(usuarioHierarquia.usuarioSuperior.id.eq(usuarioId))
                        )
                )))
            .orderBy(usuario.nome.asc())
            .fetch();
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
    public List<UsuarioResponse> getUsuariosSuperiores(UsuarioFiltrosHierarquia filtros) {
        return jdbcTemplate.query("SELECT U.ID "
                                + "     , U.NOME "
                                + "     , U.CPF "
                                + "     , U.EMAIL_01 AS EMAIL"
                                + "     , N.CODIGO AS CODIGO_NIVEL "
                                + "     , D.CODIGO AS CODIGO_DEPARTAMENTO "
                                + "     , C.CODIGO AS CODIGO_CARGO "
                                + "     , LISTAGG(E.CODIGO, ',') WITHIN GROUP (ORDER BY E.CODIGO) AS CODIGO_EMPRESAS "
                                + "     , LISTAGG(UN.CODIGO, ',') WITHIN GROUP (ORDER BY UN.CODIGO) AS CODIGO_UNIDADES_NEGOCIO "
                                + "     , U.SITUACAO "
                                + "  FROM USUARIO_HIERARQUIA UH "
                                + "  JOIN USUARIO U ON U.ID = UH.FK_USUARIO_SUPERIOR "
                                + "  JOIN CARGO C ON C.ID = U.FK_CARGO "
                                + "  JOIN DEPARTAMENTO D ON D.ID = U.FK_DEPARTAMENTO "
                                + "  JOIN NIVEL N ON N.ID = D.FK_NIVEL "
                                + "  JOIN USUARIO_EMPRESA UE ON UE.FK_USUARIO = U.ID "
                                + "  JOIN EMPRESA E ON E.ID = UE.FK_EMPRESA "
                                + "  JOIN USUARIO_UNIDADE_NEGOCIO UNE ON UNE.FK_USUARIO = U.ID "
                                + "  JOIN UNIDADE_NEGOCIO UN ON UN.ID = UNE.FK_UNIDADE_NEGOCIO "
                                + " WHERE C.CODIGO = :codigoCargo "
                                + "   AND D.CODIGO = :codigoDepartamento "
                                + "   AND N.CODIGO = :codigoNivel "
                                + "   AND U.SITUACAO = 'A' "
                                + " GROUP BY U.ID, U.NOME, U.CPF, U.EMAIL_01, N.CODIGO, D.CODIGO, C.CODIGO, U.SITUACAO "
                                + "  START WITH UH.FK_USUARIO IN (:usuarioId) "
                                + " CONNECT BY NOCYCLE PRIOR UH.FK_USUARIO_SUPERIOR = UH.FK_USUARIO ",
                new MapSqlParameterSource().addValues(getParameters(filtros))
                        .addValue("usuarioId", filtros.getUsuarioId()),
                new BeanPropertyRowMapper(UsuarioResponse.class));
    }

    @Override
    public List<Usuario> getUsuariosSuperioresDoExecutivoDoAa(Integer usuarioId) {
        return new JPAQueryFactory(entityManager)
            .select(usuario)
            .from(usuario)
            .leftJoin(usuario.cargo, cargo).fetchJoin()
            .leftJoin(usuario.departamento, departamento).fetchJoin()
            .where(usuario.id.in(
                select(usuarioHierarquia.usuarioSuperior.id)
                    .from(usuarioHierarquia)
                    .where(usuarioHierarquia.usuario.id.eq(usuarioId))
            )
                .and(usuario.cargo.codigo.in(COORDENADOR_OPERACAO, GERENTE_OPERACAO))
                .and(usuario.departamento.codigo.eq(COMERCIAL))
                .and(usuario.situacao.eq(A)))
            .fetch();
    }

    private Map<String, String> getParameters(UsuarioFiltrosHierarquia filtros) {
        return Map.of("codigoCargo", filtros.getCodigoCargo().toString(),
                "codigoDepartamento", filtros.getCodigoDepartamento().toString(),
                "codigoNivel", filtros.getCodigoNivel().toString());
    }

    @Override
    public List<Usuario> findAllLideresComerciaisDoExecutivo(Integer executivoId) {
        var usuarioSuperior = new QUsuario("usuarioSuperior");

        return new JPAQueryFactory(entityManager)
            .selectFrom(usuarioHierarquia)
            .innerJoin(usuarioHierarquia.usuarioSuperior, usuarioSuperior).fetchJoin()
            .innerJoin(usuarioSuperior.cargo, cargo)
            .innerJoin(usuarioSuperior.departamento, departamento)
            .innerJoin(cargo.nivel, nivel)
            .where(usuarioHierarquia.usuario.id.eq(executivoId)
                .and(usuarioSuperior.situacao.eq(A))
                .and(cargo.codigo.in(GERENTE_OPERACAO, COORDENADOR_OPERACAO))
                .and(departamento.codigo.eq(COMERCIAL)
                    .and(nivel.codigo.eq(OPERACAO))))
            .fetch()
            .stream()
            .map(UsuarioHierarquia::getUsuarioSuperior)
            .collect(Collectors.toList());
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
                .orderBy(usuarioHierarquia.dataCadastro.desc())
                .distinct()
                .fetchFirst());
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
                        .and(usuario.situacao.eq(A)))
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
            .where(new UsuarioPredicate().ouComUsuariosIds(usuariosIds).build())
                .fetch();
    }

    @Override
    public List<Usuario> findAllUsuariosSemDataUltimoAcesso() {
        return new JPAQueryFactory(entityManager)
                .select(Projections.constructor(Usuario.class, usuario.id, usuario.email))
                .from(usuario)
                .where(usuario.situacao.eq(A)
                        .and(usuario.dataUltimoAcesso.isNull()
                                .and(usuario.dataCadastro.before(LocalDateTime.now().minusDays(TRINTA_DOIS_DIAS)))))
                .fetch();
    }

    @Override
    public FunilProspeccaoUsuarioDto findUsuarioGerenteByUf(Integer ufId) {
        return new JPAQueryFactory(entityManager)
            .select(Projections.constructor(
                FunilProspeccaoUsuarioDto.class,
                usuarioCidade.usuario.id))
            .from(usuarioCidade)
            .innerJoin(usuarioCidade.usuario, usuario)
            .innerJoin(usuarioCidade.cidade, cidade)
            .where(usuario.cargo.codigo.eq(GERENTE_OPERACAO)
                .and(cidade.uf.id.eq(ufId)))
            .fetchFirst();
    }

    @Override
    public long deleteUsuarioHierarquia(Integer usuarioId) {
        return new JPADeleteClause(entityManager, usuarioHierarquia)
                .where(usuarioHierarquia.usuario.id.eq(usuarioId))
                .execute();
    }

    @Override
    public List<UsuarioExecutivoResponse> findAllExecutivosBySituacao(ESituacao situacao) {
        return new JPAQueryFactory(entityManager)
            .select(Projections.constructor(UsuarioExecutivoResponse.class,
                usuario.id, usuario.email, usuario.nome))
            .from(usuario)
            .join(usuario.cargo, cargo)
            .where(usuario.situacao.eq(situacao)
            .and(usuario.cargo.codigo.in(EXECUTIVO, EXECUTIVO_HUNTER)))
            .fetch();
    }

    @Override
    public List<Usuario> findUsuariosByCodigoCargo(CodigoCargo codigoCargo) {
        return new JPAQueryFactory(entityManager)
            .selectDistinct(usuario)
            .from(usuario)
            .innerJoin(usuario.cargo, cargo)
            .where(cargo.codigo.eq(codigoCargo)
                .and(usuario.situacao.eq(A)))
            .fetch();
    }

    @Override
    public List<UsuarioNomeResponse> getSupervisoresSubclusterDoUsuario(Integer usuarioId) {
        var subclusterIdList = getSubclustersUsuario(usuarioId)
            .stream()
            .map(SubCluster::getId)
            .collect(Collectors.toList());

        return new JPAQueryFactory(entityManager)
            .select(Projections.bean(UsuarioNomeResponse.class,
                usuario.id,
                usuario.nome))
            .from(usuarioHierarquia)
            .innerJoin(usuarioHierarquia.usuario, usuario)
            .innerJoin(usuario.cargo, cargo)
            .innerJoin(usuario.cidades, usuarioCidade)
            .innerJoin(usuarioCidade.cidade, cidade)
            .innerJoin(cidade.subCluster, subCluster)
            .where(subCluster.id.in(subclusterIdList)
                .and(cargo.id.eq(CARGO_SUPERVISOR_ID)))
            .distinct()
            .fetch();
    }

    @Override
    public List<UsuarioNomeResponse> buscarUsuariosPorCanalECargo(ECanal canal, CodigoCargo cargo) {
        return new JPAQueryFactory(entityManager)
            .select(Projections.fields(UsuarioNomeResponse.class, usuario.id, usuario.nome))
            .from(usuario)
            .where(usuario.canais.any().eq(canal)
                .and(usuario.cargo.codigo.eq(cargo)))
            .fetch();
    }

    @Override
    public List<Integer> buscarIdsUsuariosPorCargosIds(List<Integer> cargosIds) {
        return new JPAQueryFactory(entityManager)
            .select(usuario.id)
            .from(usuario)
            .join(usuario.cargo, cargo)
            .where(usuario.cargo.id.in(cargosIds))
            .fetch();
    }

    @Override
    public List<UsuarioSituacaoResponse> findUsuariosByIds(List<Integer> usuariosIds) {
        return new JPAQueryFactory(entityManager)
            .select(Projections.constructor(UsuarioSituacaoResponse.class,
                usuario.id,
                usuario.nome,
                usuario.situacao))
            .from(usuario)
            .where(usuario.id.in(usuariosIds))
            .fetch();
    }

    @Override
    public List<UsuarioResponse> findUsuariosAtivosOperacaoComercialByCargoId(Integer cargoId) {
        return new JPAQueryFactory(entityManager)
            .select(Projections.constructor(
                UsuarioResponse.class,
                usuario.id,
                usuario.nome,
                usuario.email,
                cargo.nome,
                cargo.codigo))
            .from(usuario)
            .leftJoin(usuario.departamento, departamento)
            .leftJoin(usuario.cargo, cargo)
            .leftJoin(cargo.nivel, nivel)
            .where(usuario.situacao.eq(A)
                .and(departamento.codigo.eq(COMERCIAL))
                .and(nivel.codigo.eq(OPERACAO))
                .and(cargo.id.eq(cargoId)))
            .orderBy(usuario.id.asc())
            .fetch();
    }

    @Override
    public List<SelectResponse> findAllAtivosByNivelOperacaoCanalAa() {
        return new JPAQueryFactory(entityManager)
            .select(Projections.constructor(SelectResponse.class,
                usuario.id,
                usuario.nome))
            .from(usuario)
            .leftJoin(usuario.cargo, cargo)
            .leftJoin(cargo.nivel, nivel)
            .where(usuario.situacao.eq(A).and(nivel.id.eq(ID_NIVEL_OPERACAO))
                .and(usuario.canais.contains(ECanal.AGENTE_AUTORIZADO)))
            .orderBy(usuario.nome.asc())
            .fetch();
    }
}