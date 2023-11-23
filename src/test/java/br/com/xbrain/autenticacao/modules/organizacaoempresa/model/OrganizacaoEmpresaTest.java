package br.com.xbrain.autenticacao.modules.organizacaoempresa.model;

import br.com.xbrain.autenticacao.modules.organizacaoempresa.dto.OrganizacaoEmpresaRequest;
import br.com.xbrain.autenticacao.modules.organizacaoempresa.enums.ESituacaoOrganizacaoEmpresa;
import br.com.xbrain.autenticacao.modules.organizacaoempresa.helper.OrganizacaoEmpresaHelper;
import br.com.xbrain.autenticacao.modules.usuario.enums.CodigoNivel;
import br.com.xbrain.autenticacao.modules.usuario.enums.ECanal;
import br.com.xbrain.autenticacao.modules.usuario.model.Nivel;
import org.junit.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class OrganizacaoEmpresaTest {

    @Test
    public void of_deveRetornarObjetoCorreto_quandoRecebeOrganizacaoEmpresa() {
        assertThat(OrganizacaoEmpresa.of(umaOrganizacaoEmpresaRequest(), 1, Nivel.builder().id(1).build()))
            .extracting("nome", "nivel", "situacao", "canal")
            .containsExactly("Organizacao 1", OrganizacaoEmpresaHelper.umNivel(), ESituacaoOrganizacaoEmpresa.A,
                ECanal.INTERNET);
    }

    @Test
    public void getNivelIdNome_deveRetornarVazio_quandoNivelNull() {
        assertThat(umaOrganizacaoEmpresa().getNivelIdNome()).isEmpty();
    }

    @Test
    public void getNivelIdNome_deveRetornarIdENome_quandoNivelNotNull() {
        assertThat(umaOutraOrganizacaoEmpresa().getNivelIdNome())
            .isPresent()
            .isEqualTo(Optional.of(OrganizacaoEmpresaHelper.umNivelResponse()));
    }

    @Test
    public void isAtivo_deveRetornarTrue_quandoOrganizacaoAtiva() {
        assertTrue(umaOrganizacaoComStatus(1, ESituacaoOrganizacaoEmpresa.A)
            .isAtivo());
    }

    @Test
    public void isAtivo_deveRetornarFalse_quandoOrganizacaoInativa() {
        assertFalse(umaOrganizacaoComStatus(1, ESituacaoOrganizacaoEmpresa.I)
            .isAtivo());
    }

    @Test
    public void isSuporteVendas_deveRetornarTrue_quandoOrganizacaoNivelSuporteVendas() {
        var organizacao = umaOutraOrganizacaoEmpresa();
        organizacao.getNivel().setCodigo(CodigoNivel.BACKOFFICE_SUPORTE_VENDAS);

        assertTrue(organizacao.isSuporteVendas());
    }

    @Test
    public void isSuporteVendas_deveRetornarFalse_quandoOrganizacaoNivelSuporteVendas() {
        assertFalse(umaOutraOrganizacaoEmpresa().isSuporteVendas());
    }

    private OrganizacaoEmpresa umaOrganizacaoComStatus(Integer id, ESituacaoOrganizacaoEmpresa situacao) {
        return OrganizacaoEmpresa.builder()
            .id(id)
            .situacao(situacao)
            .build();
    }

    private OrganizacaoEmpresaRequest umaOrganizacaoEmpresaRequest() {
        return OrganizacaoEmpresaRequest.builder()
            .nome("Organizacao 1")
            .nivelId(1)
            .situacao(ESituacaoOrganizacaoEmpresa.A)
            .canal(ECanal.INTERNET)
            .build();
    }

    private OrganizacaoEmpresa umaOrganizacaoEmpresa() {
        return OrganizacaoEmpresa.builder()
            .nome("Organizacao 1")
            .nivel(null)
            .situacao(ESituacaoOrganizacaoEmpresa.A)
            .build();
    }

    private OrganizacaoEmpresa umaOutraOrganizacaoEmpresa() {
        return OrganizacaoEmpresa.builder()
            .nome("Organizacao 1")
            .nivel(Nivel.builder()
                .id(1)
                .nome("BACKOFFICE")
                .codigo(CodigoNivel.BACKOFFICE)
                .build())
            .situacao(ESituacaoOrganizacaoEmpresa.A)
            .build();
    }
}
