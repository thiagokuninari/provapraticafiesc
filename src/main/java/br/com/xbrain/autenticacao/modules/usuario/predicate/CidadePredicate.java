package br.com.xbrain.autenticacao.modules.usuario.predicate;

import br.com.xbrain.autenticacao.modules.autenticacao.dto.UsuarioAutenticado;
import br.com.xbrain.autenticacao.modules.usuario.model.QCidade;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.JPAExpressions;

import java.util.Optional;

import static br.com.xbrain.autenticacao.modules.usuario.enums.CodigoFuncionalidade.AUT_VISUALIZAR_GERAL;
import static br.com.xbrain.autenticacao.modules.usuario.model.QUsuario.usuario;
import static br.com.xbrain.autenticacao.modules.usuario.model.QUsuarioCidade.usuarioCidade;

public class CidadePredicate {

    private QCidade cidade = QCidade.cidade;
    private BooleanBuilder builder;

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

    public CidadePredicate comCodigoCidadeDbm(Integer codigoCidadeDbm) {
        Optional.ofNullable(codigoCidadeDbm)
            .map(cidade.cidadesDbm.any().codigoCidadeDbm::eq)
            .ifPresent(builder::and);

        return this;
    }
}
