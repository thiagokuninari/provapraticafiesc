package br.com.xbrain.autenticacao.modules.usuario.predicate;

import br.com.xbrain.autenticacao.infra.PredicateBase;
import br.com.xbrain.autenticacao.modules.autenticacao.dto.UsuarioAutenticado;
import br.com.xbrain.autenticacao.modules.usuario.dto.CidadesUfsRequest;
import br.com.xbrain.autenticacao.modules.comum.enums.Eboolean;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoFuncionalidade;
import com.google.common.collect.Lists;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.jpa.JPAExpressions;
import org.apache.commons.lang.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

import static br.com.xbrain.autenticacao.modules.comum.util.Constantes.QTD_MAX_IN_NO_ORACLE;
import static br.com.xbrain.autenticacao.modules.usuario.model.QCidade.cidade;
import static br.com.xbrain.autenticacao.modules.usuario.model.QUsuario.usuario;
import static br.com.xbrain.autenticacao.modules.usuario.model.QUsuarioCidade.usuarioCidade;

public class CidadePredicate extends PredicateBase {

    public CidadePredicate comNome(String nome) {
        if (StringUtils.isNotBlank(nome)) {
            builder.and(cidade.nome.likeIgnoreCase(nome));
        }

        return this;
    }

    public CidadePredicate comUf(String nome) {
        if (StringUtils.isNotBlank(nome)) {
            builder.and(cidade.uf.uf.likeIgnoreCase(nome));
        }

        return this;
    }

    public CidadePredicate comUfId(Integer ufId) {
        if (ufId != null) {
            builder.and(cidade.uf.id.eq(ufId));
        }

        return this;
    }

    public CidadePredicate comRegionalId(Integer regionalId) {
        if (regionalId != null) {
            builder.and(cidade.regional.id.eq(regionalId));
        }

        return this;
    }

    public CidadePredicate comDistritos(Eboolean apenasDistritos) {
        if (Eboolean.V == apenasDistritos) {
            builder.and(cidade.fkCidade.isNotNull());
        }

        if (Eboolean.F == apenasDistritos) {
            builder.and(cidade.fkCidade.isNull());
        }

        return this;
    }

    private CidadePredicate dasCidadesQueOUsuarioEstaVinculado(Integer usuarioId) {
        builder.and(cidade.id.in(
            JPAExpressions.select(cidade.id)
                .from(usuario)
                .leftJoin(usuario.cidades, usuarioCidade)
                .leftJoin(usuarioCidade.cidade, cidade)
                .where(usuarioCidade.dataBaixa.isNull().and(usuario.id.eq(usuarioId)))));
        return this;
    }

    public CidadePredicate filtrarPermitidos(UsuarioAutenticado usuarioAutenticado) {
        if (!usuarioAutenticado.hasPermissao(CodigoFuncionalidade.AUT_VISUALIZAR_GERAL)) {
            dasCidadesQueOUsuarioEstaVinculado(usuarioAutenticado.getId());
        }

        return this;
    }

    public CidadePredicate comCidadesUfs(CidadesUfsRequest cidadesUfs) {
        if (!cidadesUfs.getCidades().isEmpty() && !cidadesUfs.getUfs().isEmpty()) {
            builder.and(
                ExpressionUtils.anyOf(
                    Lists.partition(cidadesUfs.getCidades(), QTD_MAX_IN_NO_ORACLE)
                        .stream()
                        .map(cidade.nome::in)
                        .collect(Collectors.toList())))
                .and(cidade.uf.uf.in(cidadesUfs.getUfs()));
        }
        return this;
    }

    public CidadePredicate comCodigosIbge(List<String> codigosIbge) {
        if (!codigosIbge.isEmpty()) {
            builder.and(ExpressionUtils.anyOf(
                Lists.partition(codigosIbge, QTD_MAX_IN_NO_ORACLE)
                    .parallelStream()
                    .map(cidade.codigoIbge::in)
                    .collect(Collectors.toList()))
            );
        }

        return this;
    }

    public CidadePredicate comCidadesId(List<Integer> cidadesId) {
        if (!isEmpty(cidadesId)) {
            builder.and(
                ExpressionUtils.anyOf(
                    Lists.partition(cidadesId, QTD_MAX_IN_NO_ORACLE)
                        .stream()
                        .map(cidade.id::in)
                        .collect(Collectors.toList())));
        }

        return this;
    }
}
