package br.com.xbrain.autenticacao.modules.usuario.predicate;

import br.com.xbrain.autenticacao.infra.PredicateBase;

import java.time.LocalDateTime;

import static br.com.xbrain.autenticacao.modules.usuario.model.QUsuarioHistorico.usuarioHistorico;

public class UsuarioHistoricoPredicate extends PredicateBase {

    private static final int TRINTA_E_DOIS = 32;

    public UsuarioHistoricoPredicate comDataCadastro() {
        final LocalDateTime trintaEDoisDiasAtras = LocalDateTime.now().minusDays(TRINTA_E_DOIS);

        builder.and(usuarioHistorico.dataCadastro.before(trintaEDoisDiasAtras)
                .or(usuarioHistorico.dataCadastro.eq(trintaEDoisDiasAtras)));

        return this;
    }

}
