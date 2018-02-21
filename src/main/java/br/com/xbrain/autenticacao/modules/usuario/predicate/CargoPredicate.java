package br.com.xbrain.autenticacao.modules.usuario.predicate;

import br.com.xbrain.autenticacao.modules.autenticacao.dto.UsuarioAutenticado;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoFuncionalidade;
import br.com.xbrain.autenticacao.modules.usuario.model.QCargo;
import com.querydsl.core.BooleanBuilder;

public class CargoPredicate {

    private QCargo cargo = QCargo.cargo;
    private BooleanBuilder builder;

    public CargoPredicate() {
        this.builder = new BooleanBuilder();
    }

    public CargoPredicate comNome(String nome) {
        if (nome != null) {
            builder.and(cargo.nome.likeIgnoreCase("%" + nome + "%"));
        }
        return this;
    }

    public CargoPredicate comNivel(Integer operacaoId) {
        if (operacaoId != null) {
            builder.and(cargo.nivel.id.eq(operacaoId));
        }
        return this;
    }

    private CargoPredicate daExecutivo() {
        builder.and(cargo.codigo.eq(CodigoCargo.EXECUTIVO));
        return this;
    }

    public CargoPredicate filtrarPermitidos(UsuarioAutenticado usuarioAutenticado) {
        if (!usuarioAutenticado.hasPermissao(CodigoFuncionalidade.AUT_GER_USUARIO)
                && usuarioAutenticado.hasPermissao(CodigoFuncionalidade.POL_GERENCIAR_USUARIOS_EXECUTIVO)) {
            daExecutivo();

        }
        return this;
    }

    public BooleanBuilder build() {
        return this.builder;
    }

}
