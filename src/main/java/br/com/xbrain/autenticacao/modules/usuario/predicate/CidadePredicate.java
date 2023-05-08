package br.com.xbrain.autenticacao.modules.usuario.predicate;

import br.com.xbrain.autenticacao.modules.autenticacao.dto.UsuarioAutenticado;
import br.com.xbrain.autenticacao.modules.usuario.model.QCidade;
import com.google.common.collect.Lists;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.jpa.JPAExpressions;

import java.util.List;
import java.util.stream.Collectors;

import static br.com.xbrain.autenticacao.modules.comum.util.Constantes.QTD_MAX_IN_NO_ORACLE;
import static br.com.xbrain.autenticacao.modules.usuario.enums.CodigoFuncionalidade.AUT_VISUALIZAR_GERAL;
import static br.com.xbrain.autenticacao.modules.usuario.model.QUsuario.usuario;
import static br.com.xbrain.autenticacao.modules.usuario.model.QUsuarioCidade.usuarioCidade;
import static org.springframework.util.ObjectUtils.isEmpty;

@SuppressWarnings("PMD.TooManyStaticImports")
public class CidadePredicate {

    private final QCidade cidade = QCidade.cidade;
    private final BooleanBuilder builder;

    public CidadePredicate() {
        this.builder = new BooleanBuilder();
    }

    public CidadePredicate comNome(String nome) {
        if (nome != null) {
            builder.and(cidade.nome.likeIgnoreCase(nome));
        }
        return this;
    }

    public CidadePredicate comUf(String nome) {
        if (nome != null) {
            builder.and(cidade.uf.uf.likeIgnoreCase(nome));
        }
        return this;
    }

    public BooleanBuilder build() {
        return this.builder;
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
        if (!usuarioAutenticado.hasPermissao(AUT_VISUALIZAR_GERAL)) {
            dasCidadesQueOUsuarioEstaVinculado(usuarioAutenticado.getId());
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
