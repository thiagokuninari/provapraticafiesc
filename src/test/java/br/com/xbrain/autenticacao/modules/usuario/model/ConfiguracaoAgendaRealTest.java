package br.com.xbrain.autenticacao.modules.usuario.model;

import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoNivel;
import br.com.xbrain.autenticacao.modules.usuario.enums.ECanal;
import br.com.xbrain.autenticacao.modules.usuario.enums.ETipoCanal;
import org.junit.Test;

import static br.com.xbrain.autenticacao.modules.organizacaoempresa.dto.OrganizacaoEmpresaHistoricoResponseTest.umUsuarioAutenticado;
import static br.com.xbrain.autenticacao.modules.usuario.helpers.UsuarioAgendamentoHelpers.umaConfiguracaoAgenda;
import static br.com.xbrain.autenticacao.modules.usuario.helpers.UsuarioAgendamentoHelpers.umaConfiguracaoAgendaRequest;
import static org.assertj.core.api.Assertions.assertThat;

public class ConfiguracaoAgendaRealTest {

    @Test
    public void of_deveAplicarParametroDeEstrutura_quandoTipoConfiguracaoEstrutura() {
        assertThat(ConfiguracaoAgendaReal.of(
            umaConfiguracaoAgendaRequest("AGENTE_AUTORIZADO"), umUsuarioAutenticado()))
            .isEqualToIgnoringGivenFields(umaConfiguracaoAgenda("AGENTE_AUTORIZADO"), "dataCadastro");
    }

    @Test
    public void of_deveAplicarParametroDeSubcanal_quandoTipoConfiguracaoSubcanal() {
        assertThat(ConfiguracaoAgendaReal.of(
            umaConfiguracaoAgendaRequest(ETipoCanal.PAP), umUsuarioAutenticado()))
            .isEqualToIgnoringGivenFields(umaConfiguracaoAgenda(ETipoCanal.PAP), "dataCadastro");
    }

    @Test
    public void of_deveAplicarParametroDeCanal_quandoTipoConfiguracaoCanal() {
        assertThat(ConfiguracaoAgendaReal.of(
            umaConfiguracaoAgendaRequest(ECanal.AGENTE_AUTORIZADO), umUsuarioAutenticado()))
            .isEqualToIgnoringGivenFields(umaConfiguracaoAgenda(ECanal.AGENTE_AUTORIZADO), "dataCadastro");
    }

    @Test
    public void of_deveAplicarParametroDeNivel_quandoTipoConfiguracaoNivel() {
        assertThat(ConfiguracaoAgendaReal.of(
            umaConfiguracaoAgendaRequest(CodigoNivel.AGENTE_AUTORIZADO), umUsuarioAutenticado()))
            .isEqualToIgnoringGivenFields(umaConfiguracaoAgenda(CodigoNivel.AGENTE_AUTORIZADO), "dataCadastro");
    }
}
