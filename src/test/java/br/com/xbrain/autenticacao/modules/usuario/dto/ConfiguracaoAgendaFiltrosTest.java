package br.com.xbrain.autenticacao.modules.usuario.dto;

import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.usuario.enums.ETipoConfiguracao;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ConfiguracaoAgendaFiltrosTest {

    @Test
    public void toPredicate_deveRetornarConfiguracaoAgendaPredicate_quandoSolicitado() {
        var config = new ConfiguracaoAgendaFiltros();
        config.setQtdHorasAdicionais(1);
        config.setTipoConfiguracao(ETipoConfiguracao.NIVEL);
        config.setSituacao(ESituacao.A);

        assertThat(config.toPredicate().build().toString())
            .hasToString("configuracaoAgendaReal.qtdHorasAdicionais = 1 && "
                + "configuracaoAgendaReal.tipoConfiguracao = NIVEL && "
                + "configuracaoAgendaReal.situacao = A");
    }
}
