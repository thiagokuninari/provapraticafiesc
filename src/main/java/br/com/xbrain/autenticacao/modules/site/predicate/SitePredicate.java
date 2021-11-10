package br.com.xbrain.autenticacao.modules.site.predicate;

import br.com.xbrain.autenticacao.infra.PredicateBase;
import br.com.xbrain.autenticacao.modules.autenticacao.dto.UsuarioAutenticado;
import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.comum.enums.ETimeZone;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoFuncionalidade;
import com.google.common.collect.Lists;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.jpa.JPAExpressions;
import org.apache.commons.lang.StringUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static br.com.xbrain.autenticacao.modules.comum.enums.ESituacao.A;
import static br.com.xbrain.autenticacao.modules.site.model.QSite.site;
import static br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo.COORDENADOR_OPERACAO;
import static br.com.xbrain.autenticacao.modules.usuario.model.QUsuario.usuario;
import static br.com.xbrain.autenticacao.modules.usuario.model.QUsuarioCidade.usuarioCidade;
import static br.com.xbrain.autenticacao.modules.usuario.model.QUsuarioHierarquia.usuarioHierarquia;
import static java.util.Objects.nonNull;

@SuppressWarnings("PMD.TooManyStaticImports")
public class SitePredicate extends PredicateBase {

    private static final int QTD_MAX_NO_ORACLE = 1000;

    public SitePredicate comId(Integer id) {
        if (nonNull(id)) {
            builder.and(site.id.eq(id));
        }
        return this;
    }

    public SitePredicate excetoId(Integer id) {
        if (nonNull(id)) {
            builder.and(site.id.ne(id));
        }
        return this;
    }

    public SitePredicate ignorarSite(Integer id) {
        if (!isEmpty(id)) {
            builder.and(site.id.ne(id));
        }
        return this;
    }

    public SitePredicate comNome(String nome) {
        Optional.ofNullable(nome)
            .filter(StringUtils::isNotBlank)
            .map(site.nome::containsIgnoreCase)
            .map(builder::and);

        return this;
    }

    public SitePredicate comCidades(List<Integer> cidadesIds) {
        if (!CollectionUtils.isEmpty(cidadesIds)) {
            builder.and(
                ExpressionUtils.anyOf(
                    Lists.partition(cidadesIds, QTD_MAX_NO_ORACLE)
                        .stream()
                        .map(site.cidades.any().id::in)
                        .collect(Collectors.toList()))
            );
        }
        return this;
    }

    public SitePredicate comEstados(List<Integer> estadosIds) {
        filtrarLista(estadosIds)
            .map(site.estados.any().id::in)
            .map(builder::and);

        return this;
    }

    public SitePredicate comCoordenadores(List<Integer> coordenadoresIds) {
        filtrarLista(coordenadoresIds)
            .map(site.coordenadores.any().id::in)
            .map(builder::and);

        return this;
    }

    public SitePredicate comCoordenadoresOuSupervisor(Integer usuarioId) {
        if (!isEmpty(usuarioId)) {
            builder.and(site.coordenadores.any().id.eq(usuarioId))
                .or(site.supervisores.any().id.eq(usuarioId));
        }
        return this;
    }

    public SitePredicate comCoordenadoresOuSupervisores(List<Integer> coordenadoresOuSupervidoresIds) {
        builder.and(site.coordenadores.any().id.in(coordenadoresOuSupervidoresIds)
            .or(site.supervisores.any().id.in(coordenadoresOuSupervidoresIds)));
        return this;
    }

    public SitePredicate comSupervisores(List<Integer> supervisoresIds) {
        filtrarLista(supervisoresIds)
            .map(site.supervisores.any().id::in)
            .map(builder::and);

        return this;
    }

    public SitePredicate comSituacao(ESituacao situacao) {
        Optional.ofNullable(situacao)
            .map(site.situacao::eq)
            .map(builder::and);

        return this;
    }

    public SitePredicate comTimeZone(ETimeZone timeZone) {
        Optional.ofNullable(timeZone)
            .map(site.timeZone::eq)
            .map(builder::and);

        return this;
    }

    public SitePredicate ignorarTodos() {
        builder.and(site.id.isNull());

        return this;
    }

    public SitePredicate todosSitesAtivos() {
        builder.and(site.situacao.eq(A));
        return this;
    }

    public SitePredicate naoPossuiDiscadora(Boolean naoPossuiDiscadora) {
        if (nonNull(naoPossuiDiscadora) && naoPossuiDiscadora) {
            builder.and(site.discadoraId.isNull());
        }

        return this;
    }

    public SitePredicate comDiscadoraId(Integer discadoraId) {
        if (nonNull(discadoraId)) {
            builder.and(site.discadoraId.eq(discadoraId));
        }

        return this;
    }

    public SitePredicate comSupervisoresDisponiveisDosCoordenadores(List<Integer> coordenadoresIds) {
        filtrarLista(coordenadoresIds)
            .map(usuario.usuariosHierarquia.any().usuarioSuperior.id::in)
            .map(builder::and)
            .map(booleanBuilder -> booleanBuilder.and(usuario.id.notIn(
                JPAExpressions.select(usuario.id)
                    .from(site)
                    .join(site.supervisores, usuario)
                    .where(site.situacao.eq(A)))));
        return this;
    }

    public SitePredicate comSupervisoresDisponiveisDosCoordenadoresEsite(List<Integer> coordenadoresIds, Integer siteId) {
        filtrarLista(coordenadoresIds)
            .map(usuario.usuariosHierarquia.any().usuarioSuperior.id::in)
            .map(builder::and)
            .map(booleanBuilder -> booleanBuilder.and(usuario.id.notIn(
                JPAExpressions.select(usuario.id)
                    .from(site)
                    .join(site.supervisores, usuario)
                    .where(site.situacao.eq(A)
                    .and(site.id.ne(siteId))))));
        return this;
    }

    public SitePredicate comCoordenadoresDisponiveis() {
        builder.and(usuario.id.notIn(JPAExpressions
            .select(usuario.id)
            .from(site)
            .join(site.coordenadores, usuario)
            .where(site.situacao.eq(A))))
            .and(usuario.cargo.codigo.eq(COORDENADOR_OPERACAO));
        return this;
    }

    public SitePredicate comCoordenadoresComCidade(List<Integer> cidadesIds) {
        if (!cidadesIds.isEmpty()) {
            builder.and(
                ExpressionUtils.anyOf(
                    Lists.partition(cidadesIds, QTD_MAX_NO_ORACLE)
                        .stream()
                        .map(usuarioCidade.cidade.id::in)
                        .collect(Collectors.toList())))
                    .and(usuario.cargo.codigo.eq(COORDENADOR_OPERACAO));
        }
        return this;
    }

    public SitePredicate comSupervisoresDisponiveis() {
        builder.and(usuario.id.notIn(JPAExpressions
            .select(usuario.id)
            .from(site)
            .join(site.supervisores, usuario)
            .where(site.situacao.eq(A)))
            .and(usuario.cargo.codigo.eq(CodigoCargo.SUPERVISOR_OPERACAO)));
        return this;
    }

    public SitePredicate comFiltroVisualizar(UsuarioAutenticado usuarioAutenticado) {
        if (!usuarioAutenticado.hasPermissao(CodigoFuncionalidade.AUT_VISUALIZAR_GERAL)) {
            builder.and(usuario.id.eq(usuarioAutenticado.getId()));
        }
        return this;
    }

    public SitePredicate comUsuarioSuperior(Integer usuarioId) {
        Optional.ofNullable(usuarioId)
            .map(usuarioHierarquia.usuarioSuperior.id::eq)
            .ifPresent(builder::and);
        return this;
    }

    private Optional<List<Integer>> filtrarLista(List<Integer> lista) {
        return Optional.ofNullable(lista)
            .filter(Predicate.not(super::isEmpty));
    }

    public SitePredicate comCidade(String cidade) {
        Optional.ofNullable(cidade)
            .filter(StringUtils::isNotEmpty)
            .map(site.cidades.any().nome::eq)
            .ifPresent(builder::and);

        return this;
    }

    public SitePredicate comUf(String uf) {
        Optional.ofNullable(uf)
            .filter(StringUtils::isNotEmpty)
            .map(site.cidades.any().uf.uf::eq)
            .ifPresent(builder::and);

        return this;
    }

    public SitePredicate comCodigoCidadeDbm(Integer codigoCidadeDbm) {
        Optional.ofNullable(codigoCidadeDbm)
            .map(site.cidades.any().cidadesDbm.any().codigoCidadeDbm::eq)
            .ifPresent(builder::and);

        return this;
    }

    public SitePredicate comIds(List<Integer> ids) {
        Optional.ofNullable(ids)
            .filter(idsOptional -> !ObjectUtils.isEmpty(ids))
            .map(idsOptional ->
                ExpressionUtils.anyOf(
                    Lists.partition(idsOptional, QTD_MAX_NO_ORACLE)
                        .stream()
                        .map(site.id::in)
                        .collect(Collectors.toList())
                )
            )
            .ifPresent(builder::and);

        return this;
    }
}
