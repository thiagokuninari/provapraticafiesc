package br.com.xbrain.autenticacao.modules.usuario.model;

import br.com.xbrain.autenticacao.modules.comum.enums.EAcao;
import org.junit.Test;

import static br.com.xbrain.autenticacao.modules.organizacaoempresa.dto.OrganizacaoEmpresaHistoricoResponseTest.umUsuarioAutenticado;
import static br.com.xbrain.autenticacao.modules.usuario.helpers.UsuarioAgendamentoHelpers.umaConfiguracaoAgenda;
import static br.com.xbrain.autenticacao.modules.usuario.helpers.UsuarioAgendamentoHelpers.umaConfiguracaoAgendaHistorico;
import static org.assertj.core.api.Assertions.assertThat;

public class ConfiguracaoAgendaRealHistoricoTest {

    @Test
    public void of_deveGerarHistorico_quandoSolicitado() {
        assertThat(ConfiguracaoAgendaRealHistorico.of(umaConfiguracaoAgenda(), umUsuarioAutenticado(), EAcao.CADASTRO))
            .isEqualToIgnoringGivenFields(umaConfiguracaoAgendaHistorico(), "dataAcao");
    }
}
