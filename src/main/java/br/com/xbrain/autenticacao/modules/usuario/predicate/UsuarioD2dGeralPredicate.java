package br.com.xbrain.autenticacao.modules.usuario.predicate;

import br.com.xbrain.autenticacao.infra.PredicateBase;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoNivel;

import java.util.List;

import static br.com.xbrain.autenticacao.modules.usuario.model.QUsuario.usuario;

public class UsuarioD2dGeralPredicate extends PredicateBase {

    public UsuarioD2dGeralPredicate comNivel(List<CodigoNivel> niveis) {
        if (isNotEmpty(niveis)) {
            builder.and(usuario.cargo.nivel.codigo.in(niveis));
        }
        return this;
    }
}
