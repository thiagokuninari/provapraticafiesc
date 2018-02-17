package br.com.xbrain.autenticacao.modules.comum.predicate;

import br.com.xbrain.autenticacao.infra.PredicateBase;
import br.com.xbrain.autenticacao.modules.autenticacao.dto.UsuarioAutenticado;
import br.com.xbrain.autenticacao.modules.comum.model.QCluster;
import br.com.xbrain.autenticacao.modules.comum.model.QGrupo;
import br.com.xbrain.autenticacao.modules.comum.model.QSubCluster;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoFuncionalidade;
import br.com.xbrain.autenticacao.modules.usuario.model.QCidade;
import br.com.xbrain.autenticacao.modules.usuario.model.QUsuario;
import br.com.xbrain.autenticacao.modules.usuario.model.QUsuarioCidade;
import com.querydsl.jpa.JPAExpressions;

public class GrupoPredicate extends PredicateBase {

    private QGrupo grupo = QGrupo.grupo;
    private QUsuario usuario = QUsuario.usuario;

    private GrupoPredicate daEmpresaUsuarioPorUsuario(Integer usuarioId) {
        builder.and(grupo.id.in(
                JPAExpressions.select(grupo.id)
                        .from(usuario)
                        .leftJoin(usuario.cidades, QUsuarioCidade.usuarioCidade)
                        .leftJoin(QUsuarioCidade.usuarioCidade.cidade, QCidade.cidade)
                        .leftJoin(QCidade.cidade.subCluster, QSubCluster.subCluster)
                        .leftJoin(QSubCluster.subCluster.cluster, QCluster.cluster)
                        .leftJoin(QCluster.cluster.grupo, grupo)
                        .where(QUsuarioCidade.usuarioCidade.dataBaixa.isNull().and(usuario.id.eq(usuarioId)))));
        return this;
    }

    public GrupoPredicate filtrarPermitidos(UsuarioAutenticado usuarioAutenticado) {
        if (usuarioAutenticado.hasPermissao(CodigoFuncionalidade.POL_GERENCIAR_USUARIOS_EXECUTIVO)) {
            daEmpresaUsuarioPorUsuario(usuarioAutenticado.getId());
        }
        return this;
    }
}
