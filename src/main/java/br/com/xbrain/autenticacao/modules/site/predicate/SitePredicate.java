package br.com.xbrain.autenticacao.modules.site.predicate;

import br.com.xbrain.autenticacao.infra.PredicateBase;
import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.comum.enums.ETimeZone;
import org.apache.commons.lang.StringUtils;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import static br.com.xbrain.autenticacao.modules.site.model.QSite.site;

public class SitePredicate extends PredicateBase {

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

    private Optional<List<Integer>> filtrarLista(List<Integer> lista) {
        return Optional.ofNullable(lista)
            .filter(Predicate.not(super::isEmpty));
    }
}
