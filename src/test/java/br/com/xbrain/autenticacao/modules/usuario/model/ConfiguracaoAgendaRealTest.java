package br.com.xbrain.autenticacao.modules.usuario.model;

import org.junit.Test;

import static br.com.xbrain.autenticacao.modules.organizacaoempresa.dto.OrganizacaoEmpresaHistoricoResponseTest.umUsuarioAutenticado;
import static br.com.xbrain.autenticacao.modules.usuario.helpers.UsuarioAgendamentoHelpers.*;
import static org.assertj.core.api.Assertions.assertThat;

public class ConfiguracaoAgendaRealTest {

    @Test
    public void of_deveCriarModelCompleta_quandoPassarRequestComQualquerTipoConfig() {
        assertThat(ConfiguracaoAgendaReal.of(umaConfiguracaoAgendaRequestEstruturaCompleta(), umUsuarioAutenticado()))
            .isEqualToIgnoringGivenFields(umaConfiguracaoAgendaEstruturaCompleta(), "dataCadastro");
    }
}
