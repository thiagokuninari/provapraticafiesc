package br.com.xbrain.autenticacao.modules.usuario.predicate;

import br.com.xbrain.autenticacao.modules.autenticacao.dto.UsuarioAutenticado;
import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoNivel;
import br.com.xbrain.autenticacao.modules.usuario.model.QNivel;
import com.querydsl.core.BooleanBuilder;

import static br.com.xbrain.autenticacao.modules.usuario.enums.CodigoFuncionalidade.AUT_GER_USUARIO;
import static br.com.xbrain.autenticacao.modules.usuario.enums.CodigoFuncionalidade.POL_GERENCIAR_USUARIOS_EXECUTIVO;

public class NivelPredicate {

    private QNivel nivel = QNivel.nivel;
    private BooleanBuilder builder;

    public NivelPredicate() {
        this.builder = new BooleanBuilder();
    }

    private NivelPredicate doNivel() {
        builder.and(nivel.codigo.eq(CodigoNivel.OPERACAO));
        return this;
    }

    public NivelPredicate withoutXbrain() {
        builder.and(nivel.codigo.ne(CodigoNivel.XBRAIN));
        return this;
    }

    public NivelPredicate ativo() {
        builder.and(nivel.situacao.eq(ESituacao.A));
        return this;
    }

    public NivelPredicate filtrarPermitidos(UsuarioAutenticado usuarioAutenticado) {
        if (!usuarioAutenticado.hasPermissao(AUT_GER_USUARIO)
                && usuarioAutenticado.hasPermissao(POL_GERENCIAR_USUARIOS_EXECUTIVO)) {
            doNivel();
        }
        return this;
    }

    public BooleanBuilder build() {
        return this.builder;
    }
}
