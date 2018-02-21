package br.com.xbrain.autenticacao.modules.comum;

import br.com.xbrain.autenticacao.modules.autenticacao.dto.UsuarioAutenticado;
import br.com.xbrain.autenticacao.modules.comum.model.QEmpresa;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import com.querydsl.core.BooleanBuilder;

import static br.com.xbrain.autenticacao.modules.usuario.enums.CodigoFuncionalidade.POL_GERENCIAR_USUARIOS_EXECUTIVO;

public class EmpresaPredicate {
    private QEmpresa empresa = QEmpresa.empresa;
    private BooleanBuilder builder;

    public EmpresaPredicate() {
        this.builder = new BooleanBuilder();
    }

    public EmpresaPredicate ignorarXbrain(Boolean ignorar) {
        if (ignorar != null && ignorar) {
            builder.and(empresa.nome.notLike("Xbrain"));
        }
        return this;
    }

    public EmpresaPredicate daUnidadeDeNegocio(Integer unidadeId) {
        if (unidadeId != null) {
            builder.and(empresa.unidadeNegocio.id.eq(unidadeId));
        }
        return this;
    }

    private EmpresaPredicate daEmpresaUsuarioPorUsuario(Usuario usuario) {
        builder.and(empresa.id.in(usuario.getEmpresasId()));
        return this;
    }

    public EmpresaPredicate filtrarPermitidos(UsuarioAutenticado usuarioAutenticado) {
        if (usuarioAutenticado.hasPermissao(POL_GERENCIAR_USUARIOS_EXECUTIVO)) {
            daEmpresaUsuarioPorUsuario(usuarioAutenticado.getUsuario());
        }
        return this;
    }

    public BooleanBuilder build() {
        return this.builder;
    }
}
