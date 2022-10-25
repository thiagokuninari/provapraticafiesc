package br.com.xbrain.autenticacao.modules.comum.predicate;

import br.com.xbrain.autenticacao.infra.PredicateBase;
import br.com.xbrain.autenticacao.modules.autenticacao.dto.UsuarioAutenticado;
import br.com.xbrain.autenticacao.modules.comum.model.QUf;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoFuncionalidade;
import br.com.xbrain.autenticacao.modules.usuario.model.QCidade;
import com.querydsl.jpa.JPAExpressions;

import static br.com.xbrain.autenticacao.modules.comum.model.QRegional.regional;
import static br.com.xbrain.autenticacao.modules.usuario.model.QUsuario.usuario;
import static br.com.xbrain.autenticacao.modules.usuario.model.QUsuarioCidade.usuarioCidade;

public class RegionalPredicate extends PredicateBase {

    private RegionalPredicate daRegionalGerenteCordenador(Integer usuarioId) {
        builder.and(regional.id.in(
                JPAExpressions.select(regional.id)
                        .from(usuario)
                        .leftJoin(usuario.cidades, usuarioCidade)
                        .leftJoin(usuarioCidade.cidade, QCidade.cidade)
                        .leftJoin(QCidade.cidade.uf, QUf.uf1)
                        .leftJoin(QCidade.cidade.regional, regional)
                        .where(usuarioCidade.dataBaixa.isNull().and(usuario.id.eq(usuarioId)))));
        return this;
    }

    public RegionalPredicate filtrarPermitidos(UsuarioAutenticado usuarioAutenticado) {
        if (!usuarioAutenticado.hasPermissao(CodigoFuncionalidade.AUT_VISUALIZAR_GERAL)) {
            daRegionalGerenteCordenador(usuarioAutenticado.getId());
        }
        return this;
    }

}
