package br.com.xbrain.autenticacao.modules.usuario.predicate;

import br.com.xbrain.autenticacao.infra.PredicateBase;
import br.com.xbrain.autenticacao.modules.autenticacao.dto.UsuarioAutenticado;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoFuncionalidade;
import com.querydsl.core.BooleanBuilder;

import java.util.List;

import static br.com.xbrain.autenticacao.modules.usuario.model.QCargo.cargo;

public class CargoPredicate extends PredicateBase {

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

    private CargoPredicate comId(List<Integer> cargosId) {
        builder.and(cargo.id.in(cargosId));
        return this;
    }

    public CargoPredicate filtrarPermitidos(UsuarioAutenticado usuarioAutenticado, List<Integer> cargosId) {
        if (!usuarioAutenticado.hasPermissao(CodigoFuncionalidade.AUT_VISUALIZAR_GERAL)) {
            comId(cargosId);
        }
        return this;
    }

    public BooleanBuilder build() {
        return this.builder;
    }

}
