package br.com.xbrain.autenticacao.modules.usuario.repository;

import br.com.xbrain.autenticacao.infra.CustomRepository;
import br.com.xbrain.autenticacao.modules.comum.dto.SelectResponse;
import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.comum.model.SubCluster;
import br.com.xbrain.autenticacao.modules.comum.model.Uf;
import br.com.xbrain.autenticacao.modules.permissao.model.PermissaoEspecial;
import br.com.xbrain.autenticacao.modules.usuario.dto.*;
import br.com.xbrain.autenticacao.modules.usuario.enums.AreaAtuacao;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoNivel;
import br.com.xbrain.autenticacao.modules.usuario.enums.ECanal;
import br.com.xbrain.autenticacao.modules.usuario.model.*;
import br.com.xbrain.autenticacao.modules.usuario.predicate.UsuarioPredicate;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.querydsl.core.types.*;
import com.querydsl.jpa.impl.JPADeleteClause;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.util.ObjectUtils;

import javax.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static br.com.xbrain.autenticacao.modules.comum.enums.ESituacao.A;
import static br.com.xbrain.autenticacao.modules.comum.enums.ESituacao.I;
import static br.com.xbrain.autenticacao.modules.comum.model.QEmpresa.empresa;
import static br.com.xbrain.autenticacao.modules.comum.model.QRegional.regional;
import static br.com.xbrain.autenticacao.modules.comum.model.QSubCluster.subCluster;
import static br.com.xbrain.autenticacao.modules.comum.model.QUf.uf1;
import static br.com.xbrain.autenticacao.modules.comum.model.QUnidadeNegocio.unidadeNegocio;
import static br.com.xbrain.autenticacao.modules.comum.util.Constantes.QTD_MAX_IN_NO_ORACLE;
import static br.com.xbrain.autenticacao.modules.organizacaoempresa.model.QOrganizacaoEmpresa.organizacaoEmpresa;
import static br.com.xbrain.autenticacao.modules.permissao.model.QCargoDepartamentoFuncionalidade.cargoDepartamentoFuncionalidade;
import static br.com.xbrain.autenticacao.modules.permissao.model.QFuncionalidade.funcionalidade;
import static br.com.xbrain.autenticacao.modules.permissao.model.QPermissaoEspecial.permissaoEspecial;
import static br.com.xbrain.autenticacao.modules.site.model.QSite.site;
import static br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo.*;
import static br.com.xbrain.autenticacao.modules.usuario.enums.CodigoDepartamento.COMERCIAL;
import static br.com.xbrain.autenticacao.modules.usuario.enums.CodigoNivel.OPERACAO;
import static br.com.xbrain.autenticacao.modules.usuario.enums.CodigoNivel.RECEPTIVO;
import static br.com.xbrain.autenticacao.modules.usuario.enums.ECanal.*;
import static br.com.xbrain.autenticacao.modules.usuario.model.QCargo.cargo;
import static br.com.xbrain.autenticacao.modules.usuario.model.QCidade.cidade;
import static br.com.xbrain.autenticacao.modules.usuario.model.QConfiguracao.configuracao;
import static br.com.xbrain.autenticacao.modules.usuario.model.QDepartamento.departamento;
import static br.com.xbrain.autenticacao.modules.usuario.model.QNivel.nivel;
import static br.com.xbrain.autenticacao.modules.usuario.model.QUsuario.usuario;
import static br.com.xbrain.autenticacao.modules.usuario.model.QUsuarioCidade.usuarioCidade;
import static br.com.xbrain.autenticacao.modules.usuario.model.QUsuarioHierarquia.usuarioHierarquia;
import static br.com.xbrain.autenticacao.modules.usuario.model.QUsuarioSenhaIncorretaHistorico.usuarioSenhaIncorretaHistorico;
import static com.querydsl.core.types.dsl.Expressions.stringTemplate;
import static com.querydsl.jpa.JPAExpressions.select;

@SuppressWarnings("PMD.TooManyStaticImports")
@Slf4j
public class UsuarioRepositoryImpl extends CustomRepository<Usuario> implements UsuarioRepositoryCustom {

    private static final int SETE_DIAS = 7;
    private static final int TRES_DIAS = 3;
    private static final Integer CARGO_SUPERVISOR_ID = 10;
    private static final int ID_NIVEL_OPERACAO = 1;
    private static final String CONCATENA_STRINGS = "wm_concat({0})";
    private static final int TRINTA_E_DOIS_DIAS = 32;

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
                .where(usuario.email.equalsIgnoreCase(email)
                        .and(usuario.situacao.ne(ESituacao.R)))
                .orderBy(usuario.dataCadastro.desc())
                .fetchFirst());
    }

    public Optional<Usuario> findComplete(Integer id) {
        return Optional.ofNullable(
            new JPAQueryFactory(entityManager)
                .selectFrom(usuario)
                .join(usuario.cargo, cargo).fetchJoin()
                .join(cargo.nivel).fetchJoin()
                .leftJoin(usuario.canais).fetchJoin()
                .join(usuario.departamento).fetchJoin()
                .leftJoin(usuario.empresas).fetchJoin()
                .leftJoin(usuario.canais).fetchJoin()
                .leftJoin(usuario.organizacaoEmpresa).fetchJoin()
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
        try {
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
        } catch (Exception ex) {
            log.error("Erro ao consultar hierarquia", ex);
            return List.of();
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Object[]> getSubordinadosPorCargo(Integer usuarioId, Set<String> codigosCargos) {
        return entityManager
            .createNativeQuery(
                " SELECT UH.FK_USUARIO "
                    + " , U.NOME "
                    + " , U.EMAIL_01 "
                    + " , C.NOME AS NOME_CARGO "
                    + " , C.CODIGO AS CARGO_CODIGO"
                    + " FROM USUARIO_HIERARQUIA UH"
                    + " JOIN USUARIO U ON U.ID = UH.FK_USUARIO "
                    + " JOIN CARGO C ON C.ID = U.FK_CARGO "
                    + " WHERE C.CODIGO in (:_codigoCargo)"
                    + " GROUP BY UH.FK_USUARIO, U.NOME, U.EMAIL_01, C.NOME, C.CODIGO"
                    + " START WITH UH.FK_USUARIO_SUPERIOR = :_usuarioId "
                    + " CONNECT BY NOCYCLE PRIOR UH.FK_USUARIO = UH.FK_USUARIO_SUPERIOR")
            .setParameter("_usuarioId", usuarioId)
            .setParameter("_codigoCargo", codigosCargos)
            .getResultList();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Object[]> getSubordinadosPorCargo(Integer usuarioId, Set<String> codigosCargos, Integer subCanalId) {
        return entityManager
            .createNativeQuery(
                " SELECT UH.FK_USUARIO "
                    + " , U.NOME "
                    + " , U.EMAIL_01 "
                    + " , C.NOME AS NOME_CARGO "
                    + " , C.CODIGO AS CARGO_CODIGO"
                    + " FROM USUARIO_HIERARQUIA UH"
                    + " JOIN USUARIO U ON U.ID = UH.FK_USUARIO "
                    + " JOIN CARGO C ON C.ID = U.FK_CARGO "
                    + " JOIN USUARIO_SUBCANAL US ON U.ID = US.FK_USUARIO "
                    + " WHERE C.CODIGO in (:_codigoCargo)"
                    + " AND US.FK_SUBCANAL = :_subCanalId"
                    + " GROUP BY UH.FK_USUARIO, U.NOME, U.EMAIL_01, C.NOME, C.CODIGO"
                    + " START WITH UH.FK_USUARIO_SUPERIOR = :_usuarioId "
                    + " CONNECT BY NOCYCLE PRIOR UH.FK_USUARIO = UH.FK_USUARIO_SUPERIOR")
            .setParameter("_usuarioId", usuarioId)
            .setParameter("_codigoCargo", codigosCargos)
            .setParameter("_subCanalId", subCanalId)
            .getResultList();
    }

    public List<Canal> getCanaisByUsuarioIds(List<Integer> usuarioIds) {
        return jdbcTemplate.query(" SELECT FK_USUARIO AS usuarioId, CANAL AS canal"
                + " FROM USUARIO_CANAL"
                + " WHERE FK_USUARIO IN (:usuarioIds)",
            new MapSqlParameterSource()
                .addValue("usuarioIds", usuarioIds),
            new BeanPropertyRowMapper<>(Canal.class));
    }

    public Set<SubCanal> getSubCanaisByUsuarioIds(List<Integer> usuarioIds) {
        return Sets.newHashSet(jdbcTemplate.query(" SELECT * FROM SUB_CANAL"
                + " WHERE ID IN (SELECT FK_SUBCANAL"
                + " FROM USUARIO_SUBCANAL WHERE FK_USUARIO IN (:usuarioIds))",
            new MapSqlParameterSource()
                .addValue("usuarioIds", usuarioIds),
            new BeanPropertyRowMapper<>(SubCanal.class)));
    }

    @Override
    public List<Usuario> findAllVendedoresReceptivos() {
        return new JPAQueryFactory(entityManager)
            .select(usuario)
            .from(usuario)
            .innerJoin(usuario.cargo, cargo)
            .where(cargo.codigo.eq(VENDEDOR_RECEPTIVO))
            .orderBy(usuario.nome.asc())
            .fetch();
    }

    @Override
    public List<Usuario> findAllVendedoresReceptivosByIds(List<Integer> ids) {
        return new JPAQueryFactory(entityManager)
            .select(usuario)
            .from(usuario)
            .innerJoin(usuario.cargo, cargo)
            .where(cargo.codigo.in(VENDEDOR_RECEPTIVO, ADMINISTRADOR)
                .and(usuario.id.in(ids))
            )
            .orderBy(usuario.nome.asc())
            .fetch();
    }

    @Override
    public List<Integer> findAllUsuariosReceptivosIdsByOrganizacaoId(Integer id) {
        return new JPAQueryFactory(entityManager)
            .select(usuario.id)
            .from(usuario)
            .innerJoin(usuario.departamento, departamento)
            .innerJoin(departamento.nivel, nivel)
            .where(nivel.codigo.eq(RECEPTIVO)
                .and(usuario.organizacaoEmpresa.id.eq(id))
            )
            .orderBy(usuario.nome.asc())
            .fetch();
    }

    @Override
    public List<UsuarioSubordinadoDto> getUsuariosCompletoSubordinados(Integer usuarioId) {
        return jdbcTemplate.query(" SELECT FK_USUARIO AS ID"
                + "     , U.NOME "
                + "     , U.CPF "
                + "     , U.EMAIL_01 AS EMAIL"
                + "     , U.SITUACAO AS SITUACAO"
                + "     , N.CODIGO AS CODIGO_NIVEL "
                + "     , D.CODIGO AS CODIGO_DEPARTAMENTO "
                + "     , C.CODIGO AS CODIGO_CARGO "
                + "     , C.NOME AS NOME_CARGO "
                + " FROM usuario_hierarquia UH"
                + "  JOIN USUARIO U ON U.ID = UH.FK_USUARIO "
                + "  JOIN CARGO C ON C.ID = U.FK_CARGO "
                + "  JOIN DEPARTAMENTO D ON D.ID = U.FK_DEPARTAMENTO "
                + "  JOIN NIVEL N ON N.ID = D.FK_NIVEL "
                + " GROUP BY FK_USUARIO, U.NOME, U.CPF, U.EMAIL_01, U.SITUACAO, N.CODIGO, D.CODIGO, C.CODIGO, C.NOME"
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
    public List<UsuarioSubCanalId> getAllSubordinadosComSubCanalId(Integer usuarioSuperiorId) {
        return jdbcTemplate.query(
            " SELECT DISTINCT U.ID AS ID, "
                + " U.NOME AS NOME,"
                + " US.FK_SUBCANAL AS SUB_CANAL_ID"
                + " FROM USUARIO_SUBCANAL US "
                + "  JOIN USUARIO U ON U.ID = US.FK_USUARIO "
                + "  JOIN USUARIO_HIERARQUIA UH ON UH.FK_USUARIO = U.ID "
                + " WHERE U.SITUACAO = 'A' "
                + " START WITH UH.FK_USUARIO_SUPERIOR = :usuarioSuperiorId "
                + " CONNECT BY NOCYCLE PRIOR UH.FK_USUARIO = UH.FK_USUARIO_SUPERIOR ",
            new MapSqlParameterSource()
            .addValue("usuarioSuperiorId", usuarioSuperiorId),
        new BeanPropertyRowMapper<>(UsuarioSubCanalId.class));
    }

    @Override
    public List<UsuarioAutoComplete> findAllExecutivosOperacaoDepartamentoComercial(Predicate predicate) {
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
                .and(predicate))
            .orderBy(usuario.nome.asc())
            .fetch();
    }

    @Override
    public List<UsuarioAutoComplete> findAllResponsaveisDdd() {
        return new JPAQueryFactory(entityManager)
            .select(
                Projections.constructor(UsuarioAutoComplete.class, usuario.id, usuario.nome))
            .from(usuario)
            .innerJoin(usuario.cargo, cargo)
            .innerJoin(usuario.departamento, departamento)
            .innerJoin(departamento.nivel, nivel)
            .where(cargo.codigo.in(EXECUTIVO, EXECUTIVO_HUNTER, ASSISTENTE_OPERACAO, GERENTE_OPERACAO,
                    COORDENADOR_OPERACAO, OPERACAO_ANALISTA)
                .and(departamento.codigo.eq(COMERCIAL))
                .and(nivel.codigo.eq(OPERACAO))
                .and(usuario.canais.any().eq(AGENTE_AUTORIZADO))
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

    public List<Usuario> getAllUsuariosDoUsuarioPapIndireto(List<Integer> usuariosIds) {
        return new JPAQueryFactory(entityManager)
            .selectFrom(usuario)
            .where(
                usuario.cpf.in(
                        select(usuario.cpf)
                            .from(usuario)
                            .where(usuario.id.in(usuariosIds))
                    ))
            .orderBy(usuario.cpf.desc())
            .orderBy(usuario.dataCadastro.desc())
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
    @SuppressWarnings("unchecked")
    public List<Integer> getUsuariosSuperiores(Integer usuarioId) {
        List<BigDecimal> result = entityManager
            .createNativeQuery("SELECT U.ID "
                + "  FROM USUARIO_HIERARQUIA UH "
                + "  JOIN USUARIO U ON U.ID = UH.FK_USUARIO_SUPERIOR "
                + "   AND U.SITUACAO = 'A' "
                + "  START WITH UH.FK_USUARIO IN (:_usuarioId) "
                + " CONNECT BY NOCYCLE PRIOR UH.FK_USUARIO_SUPERIOR = UH.FK_USUARIO ")
            .setParameter("_usuarioId", usuarioId)
            .getResultList();
        return result
            .stream()
            .map(BigDecimal::intValue)
            .collect(Collectors.toList());
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
    public List<Integer> getUsuariosSuperioresIds(List<Integer> usuariosIds) {
        return new JPAQueryFactory(entityManager)
            .select(usuarioHierarquia.usuarioSuperior.id)
            .from(usuarioHierarquia)
            .innerJoin(usuarioHierarquia.usuario, usuario)
            .innerJoin(usuarioHierarquia.usuarioSuperior, usuario)
            .where(ExpressionUtils.anyOf(
                Lists.partition(usuariosIds, QTD_MAX_IN_NO_ORACLE)
                    .stream()
                    .map(usuarioHierarquia.usuario.id::in)
                    .collect(Collectors.toList()))
            ).fetch();
    }

    @Override
    public List<PermissaoEspecial> getUsuariosByPermissaoEspecial(String codigoFuncionalidade) {
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
    public List<Integer> getUsuarioIdsByPermissaoEspecial(String codigoFuncionalidade) {
        return new JPAQueryFactory(entityManager)
            .select(permissaoEspecial.usuario.id)
            .from(permissaoEspecial)
            .innerJoin(permissaoEspecial.funcionalidade)
            .where(permissaoEspecial.funcionalidade.role.eq(codigoFuncionalidade)
                .and(permissaoEspecial.dataBaixa.isNull()))
            .fetch();
    }

    @Override
    public List<Usuario> getUsuariosByNivel(CodigoNivel codigoNivel) {
        return new JPAQueryFactory(entityManager)
            .select(usuario)
            .from(usuario)
            .innerJoin(usuario.cargo, cargo).fetchJoin()
            .leftJoin(cargo.nivel, nivel).fetchJoin()
            .leftJoin(usuario.departamento, departamento).fetchJoin()
            .leftJoin(usuario.configuracao, configuracao).fetchJoin()
            .leftJoin(usuario.unidadesNegocios, unidadeNegocio).fetchJoin()
            .where(cargo.nivel.codigo.eq(codigoNivel))
            .orderBy(usuario.nome.asc())
            .fetch();
    }

    @Override
    public List<Integer> getUsuariosIdsByNivel(CodigoNivel codigoNivel) {
        return new JPAQueryFactory(entityManager)
            .select(usuario.id)
            .from(usuario)
            .innerJoin(usuario.cargo, cargo)
            .innerJoin(cargo.nivel, nivel)
            .where(nivel.codigo.eq(codigoNivel))
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
            QUsuario.usuario.cpf,
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
                    stringTemplate(CONCATENA_STRINGS, unidadeNegocio.nome),
                    stringTemplate(CONCATENA_STRINGS, empresa.nome),
                    usuario.situacao,
                    usuario.dataUltimoAcesso,
                    usuario.loginNetSales,
                    nivel.nome,
                    organizacaoEmpresa.nome,
                    stringTemplate(CONCATENA_STRINGS, usuarioHierarquia.usuarioSuperior.nome)
                )
            )
            .from(usuario)
            .leftJoin(usuario.cargo, cargo)
            .leftJoin(usuario.departamento, departamento)
            .leftJoin(usuario.unidadesNegocios, unidadeNegocio)
            .leftJoin(usuario.empresas, empresa)
            .leftJoin(cargo.nivel, nivel)
            .leftJoin(usuario.organizacaoEmpresa, organizacaoEmpresa)
            .leftJoin(usuario.usuariosHierarquia, usuarioHierarquia)
            .leftJoin(usuarioHierarquia.usuarioSuperior)
            .where(predicate)
            .groupBy(
                usuario.id,
                usuario.nome,
                usuario.email,
                usuario.telefone,
                usuario.cpf,
                cargo.nome,
                departamento.nome,
                usuario.situacao,
                usuario.dataUltimoAcesso,
                usuario.loginNetSales,
                nivel.nome,
                organizacaoEmpresa.nome)
            .fetch();
    }

    @Override
    public Optional<Usuario> findByEmailIgnoreCase(String email) {
        return Optional.ofNullable(
            new JPAQueryFactory(entityManager)
                .select(usuario)
                .from(usuario)
                .innerJoin(usuario.cargo, cargo).fetchJoin()
                .innerJoin(cargo.nivel).fetchJoin()
                .innerJoin(usuario.departamento).fetchJoin()
                .innerJoin(usuario.empresas).fetchJoin()
                .where(usuario.email.equalsIgnoreCase(email)
                        .and(usuario.situacao.ne(ESituacao.R)))
                .orderBy(usuario.dataCadastro.desc())
                .fetchFirst());
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
                .and(usuario.canais.contains(canal))
                .and(usuario.cidades.any().cidade.id.in(
                    select(usuarioCidade.cidade.id)
                        .from(usuarioCidade)
                        .where(usuarioCidade.usuario.id.eq(usuarioId))))
                .and(usuario.situacao.eq(A)))
            .distinct()
            .fetch();
    }

    @Override
    public List<UsuarioResponse> getUsuariosDaMesmaCidadeDoUsuarioId(Integer usuarioId,
                                                                     List<CodigoCargo> cargos,
                                                                     ECanal canal,
                                                                     Integer subCanalId) {
        return new JPAQueryFactory(entityManager)
            .select(Projections.constructor(UsuarioResponse.class,
                usuario.id,
                usuario.nome,
                usuario.cargo.codigo))
            .from(usuarioCidade)
            .join(usuarioCidade.usuario, usuario)
            .where(usuario.cargo.codigo.in(cargos)
                .and(usuario.canais.contains(canal))
                .and(usuario.cidades.any().cidade.id.in(
                    select(usuarioCidade.cidade.id)
                        .from(usuarioCidade)
                        .where(usuarioCidade.usuario.id.eq(usuarioId))))
                .and(usuario.subCanais.any().id.eq(subCanalId))
                .and(usuario.situacao.eq(A)))
            .distinct()
            .fetch();
    }

    @Override
    public List<UsuarioResponse> getUsuariosPorAreaAtuacao(AreaAtuacao areaAtuacao,
                                                           List<Integer> areasAtuacaoIds,
                                                           List<CodigoCargo> cargos,
                                                           Set<ECanal> canais) {
        return new JPAQueryFactory(entityManager)
            .select(Projections.constructor(UsuarioResponse.class,
                usuarioCidade.usuario.id,
                usuarioCidade.usuario.nome,
                usuarioCidade.usuario.cargo.codigo))
            .from(usuarioCidade)
            .join(usuarioCidade.cidade, cidade)
            .join(usuarioCidade.cidade.uf, uf1)
            .join(usuarioCidade.cidade.regional, regional)
            .where(usuarioCidade.usuario.cargo.codigo.in(cargos)
                .and(usuarioCidade.usuario.canais.any().in(canais))
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

    public List<Uf> getUfsUsuario(Integer usuarioId) {
        return new JPAQueryFactory(entityManager)
            .select(uf1)
            .from(usuarioCidade)
            .innerJoin(usuarioCidade.cidade, cidade)
            .innerJoin(cidade.uf, uf1)
            .where(usuarioCidade.usuario.id.eq(usuarioId)
                .and(usuarioCidade.dataBaixa.isNull()))
            .orderBy(uf1.nome.asc())
            .distinct()
            .fetch();
    }

    @Override
    public List<UsuarioPermissoesResponse> getUsuariosIdAndPermissoes(List<Integer> usuariosIds, List<String> funcionalidades) {
        var permissoes = select(stringTemplate(CONCATENA_STRINGS, cargoDepartamentoFuncionalidade.funcionalidade.role))
            .from(cargoDepartamentoFuncionalidade)
            .innerJoin(cargoDepartamentoFuncionalidade.cargo, cargo)
            .innerJoin(cargoDepartamentoFuncionalidade.departamento, departamento)
            .innerJoin(cargoDepartamentoFuncionalidade.funcionalidade, funcionalidade)
            .where(cargo.eq(usuario.cargo)
                .and(departamento.eq(usuario.departamento))
                .and(funcionalidade.role.in(funcionalidades)));
        var permissoesEspeciais = select(stringTemplate(CONCATENA_STRINGS, funcionalidade.role))
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
    public List<UsuarioDto> findAllUsuariosSemDataUltimoAcessoAndDataReativacaoDepoisTresDiasAndNotViabilidade(
        LocalDateTime dataHoraInativarUsuario) {

        return new JPAQueryFactory(entityManager)
            .select(Projections.constructor(UsuarioDto.class,
                usuario.id,
                usuario.email,
                usuario.cargo.nivel.codigo))
            .from(usuario)
            .innerJoin(usuario.cargo, cargo)
            .innerJoin(cargo.nivel, nivel)
            .where(usuario.situacao.eq(A)
                .and(usuario.cargo.nivel.codigo.ne(CodigoNivel.INTEGRACAO))
                .and(usuario.dataUltimoAcesso.isNull())
                .and(usuario.dataCadastro.before(LocalDateTime.now().minusDays(SETE_DIAS)))
                .and(usuario.dataCadastro.after(dataHoraInativarUsuario))
                .and(usuario.dataReativacao.before(LocalDate.now().minusDays(TRES_DIAS).atStartOfDay())
                    .or(usuario.dataReativacao.isNull())))
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
    public List<Integer> findAllIds(Predicate predicate) {
        return new JPAQueryFactory(entityManager)
            .select(usuario.id)
            .from(usuario)
            .where(predicate)
            .distinct()
            .fetch();
    }

    @Override
    public List<Integer> findAllIds(PublicoAlvoComunicadoFiltros filtros) {
        var query = new JPAQueryFactory(entityManager)
            .select(usuario.id)
            .from(usuario);
        montarQuery(query, filtros);

        return query.where(filtros.toPredicate())
            .distinct()
            .fetch();
    }

    @Override
    public List<UsuarioNomeResponse> findAllNomesIds(PublicoAlvoComunicadoFiltros filtros) {
        var query = new JPAQueryFactory(entityManager)
            .select(Projections.constructor(UsuarioNomeResponse.class, usuario.id, usuario.nome, usuario.situacao))
            .from(usuario);
        montarQuery(query, filtros);

        return query.where(filtros.toPredicate())
            .distinct()
            .fetch();
    }

    private void montarQuery(JPAQuery query, PublicoAlvoComunicadoFiltros filtros) {
        var temCidadesIds = !ObjectUtils.isEmpty(filtros.getCidadesIds());
        var temUfId = Objects.nonNull(filtros.getUfId());
        var temRegionalId = Objects.nonNull(filtros.getRegionalId());

        if (temCidadesIds || temUfId || temRegionalId) {
            query.leftJoin(usuario.cidades, usuarioCidade)
                .leftJoin(usuarioCidade.cidade, cidade);
        }
        if (temUfId || temRegionalId) {
            query.leftJoin(cidade.uf, uf1);
        }
        if (temRegionalId) {
            query.leftJoin(cidade.regional, regional);
        }
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
    public List<Integer> findIdUsuariosAtivosByCodigoCargos(List<CodigoCargo> codigoCargos) {
        return new JPAQueryFactory(entityManager)
            .selectDistinct(usuario.id)
            .from(usuario)
            .innerJoin(usuario.cargo, cargo)
            .where(cargo.codigo.in(codigoCargos)
                .and(usuario.situacao.eq(A)))
            .fetch();
    }

    @Override
    public List<UsuarioNomeResponse> getSupervisoresDoSubclusterDoUsuarioPeloCanal(Integer usuarioId, ECanal canal) {
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
                .and(cargo.id.eq(CARGO_SUPERVISOR_ID))
                .and(usuario.canais.contains(canal)))
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
    public List<UsuarioNomeResponse> findCoordenadoresDisponiveis(Predicate sitePredicate) {
        var usuarioCoordenadores = new QUsuario("usuarioCoordenadores");
        return new JPAQueryFactory(entityManager)
            .selectDistinct(Projections.constructor(UsuarioNomeResponse.class, usuario.id, usuario.nome, usuario.situacao))
            .from(usuario, usuario)
            .where(usuario.canais.any().eq(ATIVO_PROPRIO)
                .and(select(site).from(site)
                    .leftJoin(site.coordenadores, usuarioCoordenadores)
                    .where(
                        site.situacao.eq(A)
                            .and(usuario.id.eq(usuarioCoordenadores.id)))
                    .notExists())
                .and(usuario.cargo.codigo.eq(COORDENADOR_OPERACAO))
                .and(sitePredicate)
            ).fetch();
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
            .where(usuario.situacao.eq(A).and(nivel.id.eq(ID_NIVEL_OPERACAO))
                .and(departamento.codigo.eq(COMERCIAL))
                .and(nivel.codigo.eq(OPERACAO))
                .and(cargo.id.eq(cargoId))
                .and(usuario.canais.any().eq(AGENTE_AUTORIZADO)))
            .orderBy(usuario.id.asc())
            .fetch();
    }

    @Override
    public List<SelectResponse> findAllAtivosByNivelOperacaoCanalAa() {
        return new JPAQueryFactory(entityManager)
            .select(Projections.constructor(SelectResponse.class, usuario.id, usuario.nome))
            .from(usuario)
            .innerJoin(usuario.cargo, cargo)
            .innerJoin(cargo.nivel, nivel)
            .where(usuario.situacao.eq(A).and(nivel.id.eq(ID_NIVEL_OPERACAO))
                .and(usuario.canais.any().eq(AGENTE_AUTORIZADO)))
            .fetch();
    }

    @Override
    public List<Integer> obterIdsPorUsuarioCadastroId(Integer usuarioCadastroId) {
        return new JPAQueryFactory(entityManager)
            .select(usuario.id)
            .from(usuario)
            .where(usuario.usuarioCadastro.id.eq(usuarioCadastroId))
            .fetch();
    }

    @Override
    public List<UsuarioNomeResponse> findAllUsuariosNomeComSituacao(Predicate predicate, OrderSpecifier<?>... orderSpecifiers) {
        var projection = Projections.bean(UsuarioNomeResponse.class,
            usuario.id,
            usuario.nome,
            usuario.situacao);
        return new JPAQueryFactory(entityManager)
            .selectDistinct(projection)
            .from(usuario)
            .where(predicate)
            .orderBy(orderSpecifiers)
            .fetch();
    }

    @Override
    public List<UsuarioSituacaoResponse> buscarUsuarioSituacao(Predicate predicate) {
        return new JPAQueryFactory(entityManager)
            .select(Projections.constructor(UsuarioSituacaoResponse.class,
                usuario.id,
                usuario.nome,
                usuario.situacao
            ))
            .from(usuario)
            .where(predicate)
            .fetch();
    }

    @Override
    public List<UsuarioNomeResponse> findSupervisoresSemSitePorCoordenadorId(Predicate sitePredicate) {
        return new JPAQueryFactory(entityManager)
            .selectDistinct(Projections.constructor(UsuarioNomeResponse.class, usuario.id, usuario.nome))
            .from(usuario, usuario)
            .leftJoin(usuario.cargo, cargo)
            .where(cargo.codigo.eq(SUPERVISOR_OPERACAO)
                .and(usuario.canais.any().eq(ATIVO_PROPRIO))
                .and(sitePredicate))
            .fetch();
    }

    @Override
    public List<UsuarioNomeResponse> findSubordinadosAtivoProprioPorUsuarioLogadoIdECargo(Integer usuarioId, CodigoCargo cargo) {
        return jdbcTemplate.query("SELECT DISTINCT U.ID, U.NOME "
                + "FROM USUARIO_HIERARQUIA UH "
                + "JOIN USUARIO U ON U.ID = UH.FK_USUARIO "
                + "JOIN CARGO C ON C.ID = U.FK_CARGO "
                + "JOIN USUARIO_CANAL UC ON UC.FK_USUARIO = U.ID "
                + "WHERE UC.CANAL = :canal "
                + "AND C.CODIGO = :cargo "
                + "START WITH UH.FK_USUARIO_SUPERIOR = :usuarioId "
                + "CONNECT BY NOCYCLE PRIOR UH.FK_USUARIO = FK_USUARIO_SUPERIOR",
            new MapSqlParameterSource()
                .addValue("usuarioId", usuarioId)
                .addValue("canal", ATIVO_PROPRIO.name())
                .addValue("cargo", cargo.name()),
            new BeanPropertyRowMapper<>(UsuarioNomeResponse.class));
    }

    @Override
    public List<UsuarioNomeResponse> findVendedoresPorSiteId(Integer siteId) {
        return jdbcTemplate.query("SELECT U.ID, U.NOME, U.SITUACAO "
                + "FROM USUARIO_HIERARQUIA UH "
                + "JOIN USUARIO U ON U.ID = UH.FK_USUARIO "
                + "JOIN CARGO C ON C.ID = U.FK_CARGO "
                + "WHERE C.CODIGO = :cargo "
                + "START WITH UH.FK_USUARIO_SUPERIOR IN (SELECT S.FK_USUARIO FROM SITE_SUPERVISOR S "
                + "JOIN SITE SI ON SI.ID = S.FK_SITE "
                + "WHERE S.FK_SITE = :siteId AND SI.SITUACAO = 'A') "
                + "CONNECT BY NOCYCLE PRIOR UH.FK_USUARIO = FK_USUARIO_SUPERIOR",
            new MapSqlParameterSource()
                .addValue("siteId", siteId)
                .addValue("cargo", CodigoCargo.OPERACAO_TELEVENDAS.name()),
            new BeanPropertyRowMapper<>(UsuarioNomeResponse.class));
    }

    @Override
    public List<Integer> findUsuariosIdsPorSiteId(Integer siteId) {
        var sql = "SELECT DISTINCT U.ID "
            + "FROM USUARIO_HIERARQUIA UH "
            + "JOIN USUARIO U ON U.ID = UH.FK_USUARIO "
            + "START WITH UH.FK_USUARIO_SUPERIOR IN (SELECT S.FK_USUARIO FROM SITE_COORDENADOR S WHERE S.FK_SITE = :siteId "
            + "UNION SELECT S.FK_USUARIO FROM SITE_SUPERVISOR S WHERE S.FK_SITE = :siteId) "
            + "CONNECT BY NOCYCLE PRIOR UH.FK_USUARIO_SUPERIOR = FK_USUARIO";
        var params = new MapSqlParameterSource()
            .addValue("siteId", siteId);
        var rowMapper = SingleColumnRowMapper.newInstance(Integer.class);
        return jdbcTemplate.query(sql, params, rowMapper);
    }

    @Override
    public List<UsuarioNomeResponse> findCoordenadoresDisponiveisExcetoPorSiteId(Predicate sitePredicate, Integer siteId) {
        var usuarioCoordenadores = new QUsuario("usuarioCoordenadores");
        return new JPAQueryFactory(entityManager)
            .selectDistinct(Projections.constructor(UsuarioNomeResponse.class, usuario.id, usuario.nome, usuario.situacao))
            .from(usuario, usuario)
            .where(usuario.canais.any().eq(ATIVO_PROPRIO)
                .and(select(site).from(site)
                    .leftJoin(site.coordenadores, usuarioCoordenadores)
                    .where(site.situacao.eq(A)
                        .and(site.id.ne(siteId))
                        .and(usuario.id.eq(usuarioCoordenadores.id)))
                    .notExists())
                .and(usuario.cargo.codigo.eq(COORDENADOR_OPERACAO))
                .and(sitePredicate)
            )
            .fetch();
    }

    @Override
    public List<UsuarioResponse> buscarSubordinadosAtivosPorSuperioresIdsECodigosCargos(List<Integer> supervisoresIds,
                                                                                        Set<String> codigosCargos) {
        return jdbcTemplate.query(" SELECT UH.FK_USUARIO AS ID, "
                + "U.NOME AS NOME, "
                + "U.SITUACAO AS SITUACAO, "
                + "C.CODIGO AS CODIGO_CARGO "
                + "FROM USUARIO_HIERARQUIA UH "
                + "JOIN USUARIO U ON U.ID = UH.FK_USUARIO "
                + "JOIN CARGO C ON C.ID = U.FK_CARGO "
                + "WHERE C.CODIGO in (:_codigosCargos) "
                + "AND U.SITUACAO = 'A' "
                + "GROUP BY FK_USUARIO, U.NOME, U.SITUACAO, C.CODIGO "
                + "START WITH UH.FK_USUARIO_SUPERIOR in (:_supervisoresIds) "
                + "CONNECT BY NOCYCLE PRIOR UH.FK_USUARIO = UH.FK_USUARIO_SUPERIOR",
            new MapSqlParameterSource()
                .addValue("_supervisoresIds", supervisoresIds)
                .addValue("_codigosCargos", codigosCargos),
            new BeanPropertyRowMapper<>(UsuarioResponse.class));
    }

    @Override
    public List<UsuarioSituacaoResponse> findVendedoresDoSiteIdPorHierarquiaUsuarioId(List<Integer> usuarioId, Integer siteId) {
        return jdbcTemplate.query("SELECT U.ID, U.NOME, U.SITUACAO "
                + "FROM USUARIO_HIERARQUIA UH "
                + "JOIN USUARIO U ON U.ID = UH.FK_USUARIO "
                + "JOIN CARGO C ON C.ID = U.FK_CARGO "
                + "WHERE C.CODIGO = :cargo "
                + "START WITH UH.FK_USUARIO_SUPERIOR IN "
                + "(SELECT S.FK_USUARIO FROM SITE_SUPERVISOR S JOIN SITE SI ON SI.ID = S.FK_SITE "
                + "WHERE S.FK_SITE = :siteId AND SI.SITUACAO = 'A' AND "
                + "S.FK_USUARIO IN (SELECT FK_USUARIO FROM USUARIO_HIERARQUIA UUH "
                + "START WITH UUH.FK_USUARIO_SUPERIOR IN (:usuarioId) "
                + "CONNECT BY NOCYCLE PRIOR UUH.FK_USUARIO = FK_USUARIO_SUPERIOR) OR UH.FK_USUARIO_SUPERIOR IN (:usuarioId)) "
                + "CONNECT BY NOCYCLE PRIOR UH.FK_USUARIO = FK_USUARIO_SUPERIOR",
            new MapSqlParameterSource()
                .addValue("usuarioId", usuarioId)
                .addValue("siteId", siteId)
                .addValue("cargo", CodigoCargo.OPERACAO_TELEVENDAS.name()),
            new BeanPropertyRowMapper<>(UsuarioSituacaoResponse.class));
    }

    public List<UsuarioCargoResponse> findSuperioresDoUsuarioId(Integer usuarioId) {
        return jdbcTemplate.query("SELECT U.ID, "
                + "U.NOME, "
                + "C.CODIGO AS CODIGO_CARGO "
                + "FROM USUARIO_HIERARQUIA UH "
                + "JOIN USUARIO U ON U.ID = UH.FK_USUARIO_SUPERIOR "
                + "JOIN CARGO C ON C.ID = U.FK_CARGO "
                + "WHERE  U.SITUACAO = 'A' "
                + "START WITH UH.FK_USUARIO IN (:usuarioId) "
                + "CONNECT BY NOCYCLE PRIOR UH.FK_USUARIO_SUPERIOR = UH.FK_USUARIO ",
            new MapSqlParameterSource()
                .addValue("usuarioId", usuarioId),
            new BeanPropertyRowMapper<>(UsuarioCargoResponse.class));

    }

    public List<UsuarioNomeResponse> findCoordenadoresDoSiteId(Integer siteId) {
        return new JPAQueryFactory(entityManager)
            .select(Projections.constructor(UsuarioNomeResponse.class,
                usuario.id, usuario.nome, usuario.situacao))
            .from(site)
            .join(site.coordenadores, usuario)
            .where(site.situacao.eq(A)
                .and(site.id.eq(siteId))
                .and(usuario.situacao.eq(A)))
            .fetch();
    }

    @Override
    public List<UsuarioNomeResponse> findSupervisoresDoSiteIdVinculadoAoCoordenador(Integer siteId, Predicate predicate) {
        var usuarioSite = new QUsuario("usuario");
        return new JPAQueryFactory(entityManager)
            .select(Projections.constructor(UsuarioNomeResponse.class,
                usuario.id, usuario.nome, usuario.situacao))
            .from(usuarioHierarquia, usuarioHierarquia)
            .join(usuarioHierarquia.usuario, usuario)
            .where(usuario.cargo.codigo.eq(SUPERVISOR_OPERACAO)
                .and(usuario.situacao.eq(A))
                .and(predicate)
                .and(usuario.id.in(new JPAQueryFactory(entityManager)
                    .select(usuarioSite.id)
                    .from(site)
                    .join(site.supervisores, usuarioSite)
                    .where(site.id.eq(siteId)
                        .and(site.situacao.eq(A))))))
            .fetch();
    }

    @Override
    public List<UsuarioDto> findAllUltimoAcessoUsuariosComDataReativacaoDepoisTresDiasAndNotViabilidade(
        LocalDateTime dataHoraInativarUsuario) {

        return new JPAQueryFactory(entityManager)
            .select(Projections.constructor(UsuarioDto.class,
                usuario.id,
                usuario.email,
                usuario.cargo.nivel.codigo))
            .from(usuario)
            .innerJoin(usuario.cargo, cargo)
            .innerJoin(cargo.nivel, nivel)
            .where(usuario.dataUltimoAcesso.after(dataHoraInativarUsuario)
                .and(usuario.situacao.ne(I))
                .and(usuario.cargo.nivel.codigo.ne(CodigoNivel.INTEGRACAO))
                .and(usuario.dataUltimoAcesso.before(LocalDateTime.now().minusDays(TRINTA_E_DOIS_DIAS)))
                .and(usuario.dataReativacao.before(LocalDate.now().atStartOfDay().minusDays(TRES_DIAS))
                    .or(usuario.dataReativacao.isNull()))
            )
            .fetch();
    }

    @Override
    public List<Usuario> getUsuariosOperacaoCanalAa(CodigoNivel codigoNivel) {
        return new JPAQueryFactory(entityManager)
            .select(usuario)
            .from(usuario)
            .innerJoin(usuario.cargo, cargo).fetchJoin()
            .leftJoin(cargo.nivel, nivel).fetchJoin()
            .leftJoin(usuario.departamento, departamento).fetchJoin()
            .leftJoin(usuario.configuracao, configuracao).fetchJoin()
            .leftJoin(usuario.unidadesNegocios, unidadeNegocio).fetchJoin()
            .where(cargo.nivel.codigo.eq(codigoNivel).and(usuario.canais.any().eq(AGENTE_AUTORIZADO)))
            .orderBy(usuario.nome.asc())
            .fetch();
    }

    @Override
    public List<Usuario> findBySituacaoAndIdsIn(ESituacao situacao, Predicate predicate) {
        return new JPAQueryFactory(entityManager)
            .selectFrom(usuario)
            .where(usuario.situacao.eq(situacao)
                .and(predicate))
            .fetch();
    }

    @Override
    public List<Integer> findAllIdsBySituacaoAndIdsIn(ESituacao situacao, Predicate predicate) {
        return new JPAQueryFactory(entityManager)
            .select(usuario.id)
            .from(usuario)
            .where(usuario.situacao.eq(situacao)
                .and(predicate))
            .fetch();
    }

    @Override
    public List<Usuario> findByEmailsAndSituacao(Predicate predicate, ESituacao situacao) {
        return new JPAQueryFactory(entityManager)
            .selectFrom(usuario)
            .where(usuario.situacao.eq(situacao)
                .and(predicate))
            .fetch();
    }

    @Override
    public List<Usuario> findByEmails(Predicate predicate) {
        return new JPAQueryFactory(entityManager)
            .selectFrom(usuario)
            .where(predicate)
            .fetch();
    }

    @Override
    public List<Usuario> findByCpfsAndSituacao(Predicate predicate, ESituacao situacao) {
        return new JPAQueryFactory(entityManager)
            .selectFrom(usuario)
            .where(usuario.situacao.eq(situacao)
                .and(predicate))
            .fetch();
    }

    @Override
    public List<Usuario> findByCpfs(Predicate predicate) {
        return new JPAQueryFactory(entityManager)
            .selectFrom(usuario)
            .where(predicate)
            .fetch();
    }

    @Override
    public Optional<Usuario> findUsuarioHistoricoTentativaLoginSenhaIncorretaHoje(String email) {
        return Optional.ofNullable(new JPAQueryFactory(entityManager)
            .selectFrom(usuario)
            .leftJoin(usuario.historicosSenhaIncorretas, usuarioSenhaIncorretaHistorico).fetchJoin()
            .where(usuario.email.eq(email)
                .and(usuario.situacao.eq(A)))
            .fetchOne());
    }

    @Override
    public List<Integer> getIdsUsuariosHierarquiaPorCargos(Set<CodigoCargo> codigoCargos) {
        return new JPAQueryFactory(entityManager)
            .select(usuario.id)
            .from(usuario)
            .where(usuario.cargo.codigo.in(codigoCargos))
            .fetch();
    }

    @Override
    public List<SelectResponse> findByCodigoCargoAndOrganizacaoId(CodigoCargo codigoCargo, Integer organizacaoId) {
        return new JPAQueryFactory(entityManager)
            .select(Projections.constructor(SelectResponse.class, usuario.id, usuario.nome))
            .from(usuario)
            .where(usuario.situacao.eq(A)
                .and(usuario.cargo.codigo.eq(codigoCargo)
                .and(usuario.organizacaoEmpresa.id.eq(organizacaoId))))
            .fetch();
    }

    @Override
    public Optional<Usuario> findByPredicate(Predicate predicate) {
        return Optional.ofNullable(new JPAQueryFactory(entityManager)
            .selectFrom(usuario)
            .where(predicate)
            .fetchFirst());
    }

    @Override
    public Optional<Usuario> findByCpfOrEmailAndSituacaoNotIn(String cpf, String email, List<ESituacao> situacoes) {
        return Optional.ofNullable(new JPAQueryFactory(entityManager)
            .select(usuario)
            .from(usuario)
            .where(usuario.cpf.eq(cpf).or(usuario.email.equalsIgnoreCase(email))
                .and(usuario.situacao.notIn(situacoes)))
            .fetchFirst());
    }

    @Override
    public boolean existeByCpfOrEmailAndSituacaoAtivo(String cpf, String email) {
        return new JPAQueryFactory(entityManager)
            .selectOne()
            .from(usuario)
            .where(usuario.cpf.eq(cpf).or(usuario.email.equalsIgnoreCase(email))
                .and(usuario.situacao.eq(A)))
            .fetchFirst() != null;
    }

    @Override
    public boolean isUsuarioSocioPrincipal(Integer usuarioId) {
        return new JPAQueryFactory(entityManager)
            .selectOne()
            .from(usuario)
            .innerJoin(usuario.cargo, cargo)
            .where(cargo.codigo.eq(AGENTE_AUTORIZADO_SOCIO)
                .and(usuario.id.eq(usuarioId)))
            .fetchFirst() != null;
    }
}
