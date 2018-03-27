package br.com.xbrain.autenticacao.modules.usuario.predicate;

import br.com.xbrain.autenticacao.modules.autenticacao.dto.UsuarioAutenticado;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoFuncionalidade;
import br.com.xbrain.autenticacao.modules.usuario.model.QDepartamento;
import com.querydsl.core.BooleanBuilder;

public class DepartamentoPredicate {

    private static String NOME_DEPARTAMENTO = "COMERCIAL";
    private QDepartamento departamento = QDepartamento.departamento;
    private BooleanBuilder builder;

    public DepartamentoPredicate() {
        this.builder = new BooleanBuilder();
    }

    private DepartamentoPredicate daComercial() {
        builder.and(departamento.nome.equalsIgnoreCase(NOME_DEPARTAMENTO));
        return this;
    }

    public DepartamentoPredicate deNivelId(Integer nivelId) {
        builder.and(departamento.nivel.id.eq(nivelId));
        return this;
    }

    public DepartamentoPredicate filtrarPermitidos(UsuarioAutenticado usuarioAutenticado) {
        if (!usuarioAutenticado.hasPermissao(CodigoFuncionalidade.AUT_VISUALIZAR_USUARIO)
                && usuarioAutenticado.hasPermissao(CodigoFuncionalidade.POL_GERENCIAR_USUARIOS_EXECUTIVO)) {
            daComercial();
        }
        return this;
    }

    public BooleanBuilder build() {
        return this.builder;
    }

}
