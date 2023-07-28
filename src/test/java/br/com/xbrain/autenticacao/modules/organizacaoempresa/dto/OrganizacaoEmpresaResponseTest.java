package br.com.xbrain.autenticacao.modules.organizacaoempresa.dto;

import br.com.xbrain.autenticacao.modules.organizacaoempresa.enums.ESituacaoOrganizacaoEmpresa;
import br.com.xbrain.autenticacao.modules.organizacaoempresa.helper.OrganizacaoEmpresaHelper;
import br.com.xbrain.autenticacao.modules.organizacaoempresa.model.OrganizacaoEmpresa;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoNivel;
import br.com.xbrain.autenticacao.modules.usuario.enums.ECanal;
import br.com.xbrain.autenticacao.modules.usuario.model.Nivel;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class OrganizacaoEmpresaResponseTest {

    @Test
    public void of_deveRetornarOrganizacaoEmpresaResponse_seSolicitado() {
        assertThat(OrganizacaoEmpresaResponse.of(umaOrganizacaoEmpresa()))
            .extracting("id", "nome", "nivel", "situacao", "codigo", "canal", "canalDescricao")
            .containsExactly(1, "Organizacao 1", OrganizacaoEmpresaHelper.umNivelResponse(),
                ESituacaoOrganizacaoEmpresa.A, "codigo", ECanal.INTERNET, "Internet");
    }

    private static OrganizacaoEmpresa umaOrganizacaoEmpresa() {
        return OrganizacaoEmpresa
            .builder()
            .id(1)
            .nome("Organizacao 1")
            .nivel(Nivel.builder()
                .id(1)
                .nome("BACKOFFICE")
                .codigo(CodigoNivel.BACKOFFICE)
                .build())
            .situacao(ESituacaoOrganizacaoEmpresa.A)
            .codigo("codigo")
            .canal(ECanal.INTERNET)
            .build();
    }
}
