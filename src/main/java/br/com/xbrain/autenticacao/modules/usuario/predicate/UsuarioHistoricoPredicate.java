package br.com.xbrain.autenticacao.modules.usuario.predicate;

import br.com.xbrain.autenticacao.infra.PredicateBase;

import java.time.LocalDateTime;

import static br.com.xbrain.autenticacao.modules.usuario.model.QUsuarioHistorico.usuarioHistorico;

public class UsuarioHistoricoPredicate extends PredicateBase {

    @SuppressWarnings({"PMD.MagicNumber", "checkstyle:MagicNumber"})
    public UsuarioHistoricoPredicate comDataCadastro() {
        final LocalDateTime trintaEDoisDiasAtras = LocalDateTime.now().minusDays(32);

        builder.and(usuarioHistorico.dataCadastro.before(trintaEDoisDiasAtras)
                .or(usuarioHistorico.dataCadastro.eq(trintaEDoisDiasAtras)));

        return this;
    }

}
