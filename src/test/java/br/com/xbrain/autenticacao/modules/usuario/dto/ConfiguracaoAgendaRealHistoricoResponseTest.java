package br.com.xbrain.autenticacao.modules.usuario.dto;

import org.junit.Test;

import static br.com.xbrain.autenticacao.modules.usuario.helpers.UsuarioAgendamentoHelpers.umaConfiguracaoAgendaHistorico;
import static br.com.xbrain.autenticacao.modules.usuario.helpers.UsuarioAgendamentoHelpers.umaConfiguracaoAgendaHistoricoResponse;
import static org.assertj.core.api.Assertions.assertThat;

public class ConfiguracaoAgendaRealHistoricoResponseTest {

    @Test
    public void of_deveGerarResponse_quandoSolicitado() {
        assertThat(ConfiguracaoAgendaRealHistoricoResponse.of(umaConfiguracaoAgendaHistorico()))
            .isEqualTo(umaConfiguracaoAgendaHistoricoResponse());
    }
}
