package br.com.xbrain.autenticacao.modules.site.predicate;

import br.com.xbrain.autenticacao.infra.PredicateBase;
import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.comum.enums.ETimeZone;
import org.apache.commons.lang.StringUtils;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import static br.com.xbrain.autenticacao.modules.comum.enums.ESituacao.A;
import static br.com.xbrain.autenticacao.modules.site.model.QSite.site;
import static java.util.Objects.nonNull;

public class SitePredicate extends PredicateBase {

    public SitePredicate comId(Integer id) {
        if (nonNull(id)) {
            builder.and(site.id.eq(id));
        }

        return this;
    }

    public SitePredicate ignorarSite(Integer id) {
        if (nonNull(id)) {
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
        filtrarLista(cidadesIds)
            .map(site.cidades.any().id::in)
            .map(builder::and);

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
        if (!isEmpty(coordenadoresOuSupervidoresIds)) {
            builder.and(site.coordenadores.any().id.in(coordenadoresOuSupervidoresIds))
                .or(site.supervisores.any().id.in(coordenadoresOuSupervidoresIds));
        }
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

    private Optional<List<Integer>> filtrarLista(List<Integer> lista) {
        return Optional.ofNullable(lista)
            .filter(Predicate.not(super::isEmpty));
    }
}
