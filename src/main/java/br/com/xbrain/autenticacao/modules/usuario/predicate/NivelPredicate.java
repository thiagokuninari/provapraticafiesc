package br.com.xbrain.autenticacao.modules.usuario.predicate;

import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.comum.enums.Eboolean;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoNivel;
import com.querydsl.core.BooleanBuilder;

import static br.com.xbrain.autenticacao.modules.usuario.model.QNivel.nivel;

public class NivelPredicate {

    private BooleanBuilder builder;

    public NivelPredicate() {
        this.builder = new BooleanBuilder();
    }

    public NivelPredicate isAtivo() {
        builder.and(nivel.situacao.eq(ESituacao.A));
        return this;
    }

    public NivelPredicate exibeProprioNivelSeNaoVisualizarGeral(boolean visualizaGeral, CodigoNivel codigoNivel) {
        if (!visualizaGeral) {
            builder.and(nivel.codigo.eq(codigoNivel));
        }
        return this;
    }

    public NivelPredicate exibeXbrainSomenteParaXbrain(boolean isXbrain) {
        if (!isXbrain) {
            builder.and(nivel.codigo.ne(CodigoNivel.XBRAIN));
        }
        return this;
    }

    public NivelPredicate exibeSomenteParaCadastro(boolean isCadastro) {
        if (isCadastro) {
            builder.and(nivel.exibirCadastroUsuario.eq(Eboolean.V));
        }
        return this;
    }

    public BooleanBuilder build() {
        return this.builder;
    }
}
