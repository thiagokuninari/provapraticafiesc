package br.com.xbrain.autenticacao.modules.comum.predicate;

import br.com.xbrain.autenticacao.infra.PredicateBase;
import br.com.xbrain.autenticacao.modules.autenticacao.dto.UsuarioAutenticado;
import br.com.xbrain.autenticacao.modules.comum.model.QSubCluster;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoFuncionalidade;
import br.com.xbrain.autenticacao.modules.usuario.model.QCidade;
import com.querydsl.jpa.JPAExpressions;

import static br.com.xbrain.autenticacao.modules.comum.model.QCluster.cluster;
import static br.com.xbrain.autenticacao.modules.usuario.model.QUsuario.usuario;
import static br.com.xbrain.autenticacao.modules.usuario.model.QUsuarioCidade.usuarioCidade;

public class ClusterPredicate extends PredicateBase {

    private ClusterPredicate daClusterUsuarioPorUsuario(Integer usuarioId) {
        builder.and(cluster.id.in(
                JPAExpressions.select(cluster.id)
                        .from(usuario)
                        .leftJoin(usuario.cidades, usuarioCidade)
                        .leftJoin(usuarioCidade.cidade, QCidade.cidade)
                        .leftJoin(QCidade.cidade.subCluster, QSubCluster.subCluster)
                        .leftJoin(QSubCluster.subCluster.cluster, cluster)
                        .where(usuarioCidade.dataBaixa.isNull().and(usuario.id.eq(usuarioId)))));
        return this;
    }

    public ClusterPredicate filtrarPermitidos(UsuarioAutenticado usuarioAutenticado) {
        if (!usuarioAutenticado.hasPermissao(CodigoFuncionalidade.AUT_VISUALIZAR_GERAL)) {
            daClusterUsuarioPorUsuario(usuarioAutenticado.getId());
        }
        return this;
    }
}
