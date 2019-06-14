package br.com.xbrain.autenticacao.modules.comum.predicate;

import br.com.xbrain.autenticacao.infra.PredicateBase;
import br.com.xbrain.autenticacao.modules.autenticacao.dto.UsuarioAutenticado;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoFuncionalidade;
import br.com.xbrain.autenticacao.modules.usuario.model.QCidade;
import com.querydsl.jpa.JPAExpressions;

import static br.com.xbrain.autenticacao.modules.comum.model.QSubCluster.subCluster;
import static br.com.xbrain.autenticacao.modules.usuario.model.QUsuario.usuario;
import static br.com.xbrain.autenticacao.modules.usuario.model.QUsuarioCidade.usuarioCidade;

public class SubClusterPredicate extends PredicateBase {

    private SubClusterPredicate daSubClusterUsuarioPorUsuario(Integer usuarioId) {
        builder.and(subCluster.id.in(
                JPAExpressions.select(subCluster.id)
                        .from(usuario)
                        .leftJoin(usuario.cidades, usuarioCidade)
                        .leftJoin(usuarioCidade.cidade, QCidade.cidade)
                        .leftJoin(QCidade.cidade.subCluster, subCluster)
                        .where(usuarioCidade.dataBaixa.isNull().and(usuario.id.eq(usuarioId)))));
        return this;
    }

    public SubClusterPredicate filtrarPermitidos(UsuarioAutenticado usuarioAutenticado) {
        if (!usuarioAutenticado.hasPermissao(CodigoFuncionalidade.AUT_VISUALIZAR_GERAL)) {
            daSubClusterUsuarioPorUsuario(usuarioAutenticado.getId());
        }
        return this;
    }

    public SubClusterPredicate filtrarPermitidos(Integer usuarioId) {
        daSubClusterUsuarioPorUsuario(usuarioId);
        return this;
    }
}
