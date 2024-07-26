package br.com.xbrain.autenticacao.modules.site.model;

import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.comum.enums.ETimeZone;
import br.com.xbrain.autenticacao.modules.comum.enums.Eboolean;
import br.com.xbrain.autenticacao.modules.comum.model.Uf;
import br.com.xbrain.autenticacao.modules.site.dto.SiteRequest;
import br.com.xbrain.autenticacao.modules.usuario.model.Cidade;
import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import org.junit.Test;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class SiteTest {

    @Test
    public void of_deveRetornarSite_quandoSolicitado() {
        assertThat(Site.of(umSiteRequest()))
            .extracting("nome", "timeZone", "estados", "cidades", "supervisores", "coordenadores",
                "siteNacional")
            .containsExactly("SITE", ETimeZone.BRT,
                Set.of(Uf.builder().id(3).build()),
                Set.of(Cidade.builder().id(1).build()),
                Set.of(Usuario.builder().id(5).build()),
                Set.of(Usuario.builder().id(7).build()),
                Eboolean.V);
    }

    @Test
    public void inativar_deveInativarSite_quandoSolicitado() {
        var site = umSite(ESituacao.A);
        assertThat(site.getSituacao()).isEqualTo(ESituacao.A);

        site.inativar();

        assertThat(site.getSituacao()).isEqualTo(ESituacao.I);
    }

    @Test
    public void ativar_deveAtivarSite_quandoSolicitado() {
        var site = umSite(ESituacao.I);
        assertThat(site.getSituacao()).isEqualTo(ESituacao.I);

        site.ativar();

        assertThat(site.getSituacao()).isEqualTo(ESituacao.A);
    }

    @Test
    public void update_deveAlterarSite_quandoCidadesIdsForInformadoNaRequest() {
        var site = umSite(ESituacao.A);
        assertThat(site.getSituacao()).isEqualTo(ESituacao.A);

        site.update(umSiteRequest());
        assertThat(site).isEqualTo(  Site.builder().id(1).timeZone(ETimeZone.BRT)
            .estados(Set.of(Uf.builder().id(3).build()))
            .cidades(Set.of(Cidade.builder().id(1).build()))
            .supervisores(Set.of(Usuario.builder().id(5).build()))
            .coordenadores(Set.of(Usuario.builder().id(7).build()))
            .build());
    }

    @Test
    public void update_deveAlterarSite_quandoCidadesIdsNaoForInformadoNaRequest() {
        var site = umSite(ESituacao.A);
        site.setCidades(Set.of(Cidade.builder().id(879).build()));

        var request = umSiteRequest();
        request.setCidadesIds(List.of());

        assertThat(site.getSituacao()).isEqualTo(ESituacao.A);

        site.update(request);
        assertThat(site).isEqualTo(
            Site.builder().id(1).timeZone(ETimeZone.BRT)
                .estados(Set.of(Uf.builder().id(3).build()))
                .cidades(Set.of(Cidade.builder().id(879).build()))
                .supervisores(Set.of(Usuario.builder().id(5).build()))
                .coordenadores(Set.of(Usuario.builder().id(7).build()))
                .build());
    }

    @Test
    public void isSiteNacional_deveRetornarTrue_seSiteNacionalForEboleanV() {
        var site = umSite(ESituacao.A);

        site.setSiteNacional(Eboolean.V);
        assertThat(site.isSiteNacional()).isTrue();

    }

    @Test
    public void isSiteNacionala_deveRetornarTrue_seSiteNacionalForEboleanV() {
        var site = umSite(ESituacao.A);

        site.setSiteNacional(Eboolean.F);
        assertThat(site.isSiteNacional()).isFalse();

    }

    private SiteRequest umSiteRequest() {
        return SiteRequest.builder()
            .id(1)
            .nome("SITE")
            .estadosIds(List.of(3))
            .timeZone(ETimeZone.BRT)
            .supervisoresIds(List.of(5))
            .coordenadoresIds(List.of(7))
            .cidadesIds(List.of(1))
            .siteNacional(true)
            .build();
    }

    private Site umSite(ESituacao situacao) {
        return Site.builder()
            .id(1)
            .nome("SITE")
            .situacao(situacao)
            .cidades(Set.of(Cidade.builder().id(879).build()))
            .build();
    }
}
