package br.com.xbrain.autenticacao.modules.comum.predicate;

import br.com.xbrain.autenticacao.infra.PredicateBase;
import br.com.xbrain.autenticacao.modules.autenticacao.dto.UsuarioAutenticado;
import br.com.xbrain.autenticacao.modules.comum.model.QUnidadeNegocio;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;

import static br.com.xbrain.autenticacao.modules.usuario.enums.CodigoFuncionalidade.AUT_GER_USUARIO;
import static br.com.xbrain.autenticacao.modules.usuario.enums.CodigoFuncionalidade.POL_GERENCIAR_USUARIOS_EXECUTIVO;

public class UnidadeNegocioPredicate extends PredicateBase {

    private static String NOME_EMPRESA = "xbrain";
    private QUnidadeNegocio unidadeNegocio = QUnidadeNegocio.unidadeNegocio;

    private UnidadeNegocioPredicate daUnidadeUsuarioPorUsuario(Usuario usuario) {
        builder.and(unidadeNegocio.id.in(usuario.getUnidadesNegociosId()));
        return this;
    }

    public UnidadeNegocioPredicate withoutXbrain() {
        builder.and(unidadeNegocio.nome.notEqualsIgnoreCase(NOME_EMPRESA));
        return this;
    }

    public UnidadeNegocioPredicate filtrarPermitidos(UsuarioAutenticado usuarioAutenticado) {
        if (!usuarioAutenticado.hasPermissao(AUT_GER_USUARIO)
                && usuarioAutenticado.hasPermissao(POL_GERENCIAR_USUARIOS_EXECUTIVO)) {
            daUnidadeUsuarioPorUsuario(usuarioAutenticado.getUsuario());
        }
        return this;
    }

}
