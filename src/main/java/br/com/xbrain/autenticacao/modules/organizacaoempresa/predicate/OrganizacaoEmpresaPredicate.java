package br.com.xbrain.autenticacao.modules.organizacaoempresa.predicate;

import br.com.xbrain.autenticacao.infra.PredicateBase;
import br.com.xbrain.autenticacao.modules.organizacaoempresa.enums.ESituacaoOrganizacaoEmpresa;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoNivel;
import br.com.xbrain.xbrainutils.CnpjUtils;
import org.apache.commons.lang.StringUtils;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import static br.com.xbrain.autenticacao.modules.organizacaoempresa.model.QOrganizacaoEmpresa.organizacaoEmpresa;
import static java.util.Objects.nonNull;

public class OrganizacaoEmpresaPredicate extends PredicateBase {

    public OrganizacaoEmpresaPredicate comId(Integer id) {
        if (nonNull(id)) {
            builder.and(organizacaoEmpresa.id.eq(id));
        }
        return this;
    }

    public OrganizacaoEmpresaPredicate comRazaoSocial(String razaoSocial) {
        Optional.ofNullable(razaoSocial)
            .filter(StringUtils::isNotBlank)
            .map(organizacaoEmpresa.razaoSocial::containsIgnoreCase)
            .map(builder::and);

        return this;
    }

    public OrganizacaoEmpresaPredicate comCnpj(String cnpj) {
        Optional.ofNullable(cnpj)
            .filter(StringUtils::isNotBlank)
            .map(CnpjUtils::getNumerosCnpj)
            .map(organizacaoEmpresa.cnpj::containsIgnoreCase)
            .map(builder::and);

        return this;
    }

    public OrganizacaoEmpresaPredicate comNivel(Integer nivelId) {
        if (nivelId != null) {
            builder.and(organizacaoEmpresa.nivel.id.eq(nivelId));
        }
        return this;
    }

    public OrganizacaoEmpresaPredicate comModalidades(List<Integer> modalidadesEmpresaIds) {
        filtrarLista(modalidadesEmpresaIds)
            .map(organizacaoEmpresa.modalidadesEmpresa.any().id::in)
            .map(builder::and);

        return this;
    }

    public OrganizacaoEmpresaPredicate comSituacao(ESituacaoOrganizacaoEmpresa situacao) {
        Optional.ofNullable(situacao)
            .map(organizacaoEmpresa.situacao::eq)
            .map(builder::and);

        return this;
    }

    public OrganizacaoEmpresaPredicate comCodigo(String codigo) {
        Optional.ofNullable(codigo)
            .filter(StringUtils::isNotBlank)
            .map(organizacaoEmpresa.codigo::containsIgnoreCase)
            .map(builder::and);

        return this;
    }

    public OrganizacaoEmpresaPredicate comCodigoNivel(CodigoNivel codigoNivel) {
        if (nonNull(codigoNivel)) {
            builder.and(organizacaoEmpresa.nivel.codigo.eq(codigoNivel));
        }
        return this;

    }

    private Optional<List<Integer>> filtrarLista(List<Integer> lista) {
        return Optional.ofNullable(lista)
            .filter(Predicate.not(super::isEmpty));
    }
}
