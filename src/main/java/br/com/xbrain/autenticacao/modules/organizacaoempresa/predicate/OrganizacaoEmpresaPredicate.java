package br.com.xbrain.autenticacao.modules.organizacaoempresa.predicate;

import br.com.xbrain.autenticacao.infra.PredicateBase;
import br.com.xbrain.autenticacao.modules.organizacaoempresa.enums.ESituacaoOrganizacaoEmpresa;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoNivel;
import br.com.xbrain.autenticacao.modules.usuario.enums.ECanal;
import org.apache.commons.lang.StringUtils;

import java.util.Optional;

import static br.com.xbrain.autenticacao.modules.organizacaoempresa.model.QOrganizacaoEmpresa.organizacaoEmpresa;
import static java.util.Objects.nonNull;

public class OrganizacaoEmpresaPredicate extends PredicateBase {

    public OrganizacaoEmpresaPredicate comId(Integer id) {
        if (nonNull(id)) {
            builder.and(organizacaoEmpresa.id.eq(id));
        }
        return this;
    }

    public OrganizacaoEmpresaPredicate comNome(String nome) {
        Optional.ofNullable(nome)
            .filter(StringUtils::isNotBlank)
            .map(organizacaoEmpresa.nome::containsIgnoreCase)
            .map(builder::and);

        return this;
    }

    public OrganizacaoEmpresaPredicate comDescricao(String descricao) {
        if (StringUtils.isNotBlank(descricao)) {
            builder.and(organizacaoEmpresa.descricao.containsIgnoreCase(descricao));
        }

        return this;
    }

    public OrganizacaoEmpresaPredicate comNivel(Integer nivelId) {
        if (nivelId != null) {
            builder.and(organizacaoEmpresa.nivel.id.eq(nivelId));
        }
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

    public OrganizacaoEmpresaPredicate comECanal(ECanal canal) {
        if (canal != null) {
            builder.and(organizacaoEmpresa.canal.eq(canal));
        }
        return this;
    }
}
