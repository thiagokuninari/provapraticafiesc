package br.com.xbrain.autenticacao.modules.usuario.model;

import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoNivel;
import br.com.xbrain.autenticacao.modules.usuario.enums.ECanal;
import br.com.xbrain.autenticacao.modules.usuario.enums.ETipoCanal;
import org.junit.Test;

import static br.com.xbrain.autenticacao.modules.usuario.helpers.UsuarioAgendamentoHelpers.umaConfiguracaoAgenda;
import static br.com.xbrain.autenticacao.modules.usuario.helpers.UsuarioAgendamentoHelpers.umaConfiguracaoAgendaRequest;
import static org.assertj.core.api.Assertions.assertThat;

public class ConfiguracaoAgendaTest {

    @Test
    public void of_deveAplicarParametroDeEstrutura_quandoTipoConfiguracaoEstrutura() {
        assertThat(ConfiguracaoAgenda.of(umaConfiguracaoAgendaRequest("AGENTE_AUTORIZADO")))
            .isEqualTo(umaConfiguracaoAgenda("AGENTE_AUTORIZADO"));
    }

    @Test
    public void of_deveAplicarParametroDeSubcanal_quandoTipoConfiguracaoSubcanal() {
        assertThat(ConfiguracaoAgenda.of(umaConfiguracaoAgendaRequest(ETipoCanal.PAP)))
            .isEqualTo(umaConfiguracaoAgenda(ETipoCanal.PAP));
    }

    @Test
    public void of_deveAplicarParametroDeCanal_quandoTipoConfiguracaoCanal() {
        assertThat(ConfiguracaoAgenda.of(umaConfiguracaoAgendaRequest(ECanal.AGENTE_AUTORIZADO)))
            .isEqualTo(umaConfiguracaoAgenda(ECanal.AGENTE_AUTORIZADO));
    }

    @Test
    public void of_deveAplicarParametroDeNivel_quandoTipoConfiguracaoNivel() {
        assertThat(ConfiguracaoAgenda.of(umaConfiguracaoAgendaRequest(CodigoNivel.AGENTE_AUTORIZADO)))
            .isEqualTo(umaConfiguracaoAgenda(CodigoNivel.AGENTE_AUTORIZADO));
    }
}
