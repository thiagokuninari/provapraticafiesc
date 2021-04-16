package br.com.xbrain.autenticacao.modules.usuario.helpers;

import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoNivel;
import br.com.xbrain.autenticacao.modules.usuario.predicate.UsuarioPredicate;

import java.util.List;

import static br.com.xbrain.autenticacao.modules.usuario.enums.CodigoCargo.*;

public class UsuarioPredicateHelper {

    public static UsuarioPredicate umVendedoresFeederPredicateComSocioPrincipalESituacaoAtiva(List<Integer> ids) {
        var predicate = new UsuarioPredicate();

        predicate.comIds(ids);
        predicate.comCodigosNiveis(List.of(CodigoNivel.AGENTE_AUTORIZADO));
        predicate.comSituacoes(List.of(ESituacao.A));
        predicate.comCodigosCargos(List.of(AGENTE_AUTORIZADO_VENDEDOR_D2D, AGENTE_AUTORIZADO_VENDEDOR_BACKOFFICE_D2D,
            AGENTE_AUTORIZADO_SOCIO));

        return predicate;
    }

    public static UsuarioPredicate umVendedoresFeederPredicateComSocioPrincipalETodasSituacaoes(List<Integer> ids) {
        var predicate = new UsuarioPredicate();

        predicate.comIds(ids);
        predicate.comCodigosNiveis(List.of(CodigoNivel.AGENTE_AUTORIZADO));
        predicate.comSituacoes(List.of(ESituacao.A, ESituacao.I, ESituacao.R));
        predicate.comCodigosCargos(List.of(AGENTE_AUTORIZADO_VENDEDOR_D2D, AGENTE_AUTORIZADO_VENDEDOR_BACKOFFICE_D2D,
            AGENTE_AUTORIZADO_SOCIO));

        return predicate;
    }

    public static UsuarioPredicate umVendedoresFeederPredicateSemSocioPrincipalESituacaoAtiva(List<Integer> ids) {
        var predicate = new UsuarioPredicate();

        predicate.comIds(ids);
        predicate.comCodigosNiveis(List.of(CodigoNivel.AGENTE_AUTORIZADO));
        predicate.comSituacoes(List.of(ESituacao.A));
        predicate.comCodigosCargos(List.of(AGENTE_AUTORIZADO_VENDEDOR_D2D, AGENTE_AUTORIZADO_VENDEDOR_BACKOFFICE_D2D));

        return predicate;
    }
}
