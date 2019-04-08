package br.com.xbrain.autenticacao.modules.usuario.predicate;

import br.com.xbrain.autenticacao.modules.autenticacao.dto.UsuarioAutenticado;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoDepartamento;
import com.querydsl.core.BooleanBuilder;

import static br.com.xbrain.autenticacao.modules.usuario.enums.CodigoFuncionalidade.AUT_VISUALIZAR_GERAL;
import static br.com.xbrain.autenticacao.modules.usuario.model.QDepartamento.departamento;

public class DepartamentoPredicate {

    private BooleanBuilder builder;

    public DepartamentoPredicate() {
        this.builder = new BooleanBuilder();
    }

    public DepartamentoPredicate doNivel(Integer nivelId) {
        builder.and(departamento.nivel.id.eq(nivelId));
        return this;
    }

    private DepartamentoPredicate comCodigo(CodigoDepartamento codigo) {
        builder.and(departamento.codigo.eq(codigo));
        return this;
    }

    public DepartamentoPredicate filtrarPermitidos(UsuarioAutenticado usuarioAutenticado) {
        if (!usuarioAutenticado.hasPermissao(AUT_VISUALIZAR_GERAL)) {
            comCodigo(usuarioAutenticado.getDepartamentoCodigo());
        }
        return this;
    }

    public BooleanBuilder build() {
        return this.builder;
    }

}
