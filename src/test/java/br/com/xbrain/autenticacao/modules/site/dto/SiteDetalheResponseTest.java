package br.com.xbrain.autenticacao.modules.site.dto;

import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.comum.enums.ETimeZone;
import br.com.xbrain.autenticacao.modules.comum.helper.UfHelper;
import br.com.xbrain.autenticacao.modules.usuario.helpers.CidadeHelper;
import org.junit.Test;

import java.util.Set;

import static br.com.xbrain.autenticacao.modules.site.service.SiteServiceTest.umSiteCompleto;
import static org.assertj.core.api.Assertions.assertThat;

public class SiteDetalheResponseTest {

    @Test
    public void of_deveRetornarSiteDetalheResponse_quandoSolicitado() {
        assertThat(SiteDetalheResponse.of(umSiteCompleto()))
            .extracting("id", "nome", "timeZone", "situacao", "coordenadoresNomes", "supervisoresNomes", "estados", "cidades")
            .containsExactly(
                200,
                "SITE COMPLETO 200",
                ETimeZone.BRT,
                ESituacao.A,
                Set.of("NOME USUARIO SITE COORDENADOR"),
                Set.of("NOME USUARIO SITE SUPERVISOR"),
                Set.of(UfHelper.ufResponseSaoPaulo()),
                Set.of(
                    CidadeHelper.cidadeResponseAldeia(),
                    CidadeHelper.cidadeResponseBarueri(),
                    CidadeHelper.cidadeResponseJardimBelval(),
                    CidadeHelper.cidadeResponseJardimSilveira(),
                    CidadeHelper.cidadeResponseJordanesiaSemCidadePai(),
                    CidadeHelper.cidadeResponseLins(),
                    CidadeHelper.cidadeResponsePolvilhoSemCidadePai()
                )
            );
    }
}
