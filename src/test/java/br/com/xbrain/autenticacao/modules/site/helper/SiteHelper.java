package br.com.xbrain.autenticacao.modules.site.helper;

import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.comum.enums.ETimeZone;
import br.com.xbrain.autenticacao.modules.comum.enums.Eboolean;
import br.com.xbrain.autenticacao.modules.site.dto.SiteRequest;
import br.com.xbrain.autenticacao.modules.site.dto.SiteResponse;
import br.com.xbrain.autenticacao.modules.site.model.Site;
import br.com.xbrain.autenticacao.modules.usuario.model.Cidade;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.List;
import java.util.Set;

import static br.com.xbrain.autenticacao.modules.usuario.helpers.UsuarioHelper.*;
import static helpers.TestBuilders.umaUf;

public class SiteHelper {

    public static Site umSite(Integer id, String nome) {
        return Site
            .builder()
            .id(id)
            .nome(nome)
            .build();
    }

    public static Site umSiteSaoPaulo() {
        return Site.builder()
            .id(100)
            .nome("São Paulo")
            .timeZone(ETimeZone.BRT)
            .estados(Set.of(
                umaUf(1, "PARANA", "PR"),
                umaUf(2, "SAO PAULO", "SP")
            ))
            .cidades(Set.of(new Cidade(5578)))
            .supervisores(Set.of(umSupervisor()))
            .coordenadores(Set.of(umCoordenador()))
            .situacao(ESituacao.A)
            .siteNacional(Eboolean.F)
            .build();
    }

    public static Site umSiteRioBranco() {
        return Site.builder()
            .id(101)
            .nome("Rio Branco")
            .timeZone(ETimeZone.ACT)
            .supervisores(Set.of(umSupervisor(), outroSupervisor()))
            .coordenadores(Set.of(umCoordenador()))
            .situacao(ESituacao.A)
            .siteNacional(Eboolean.F)
            .build();
    }

    public static Site umSiteManaus() {
        return Site.builder()
            .id(102)
            .nome("Manaus")
            .timeZone(ETimeZone.AMT)
            .supervisores(Set.of(umSupervisor(), outroSupervisor()))
            .coordenadores(Set.of(umCoordenador()))
            .situacao(ESituacao.A)
            .siteNacional(Eboolean.F)
            .discadoraId(8)
            .build();
    }

    public static Site umSiteInativo() {
        return Site.builder()
            .id(2)
            .nome("Site Inativo")
            .timeZone(ETimeZone.FNT)
            .supervisores(Set.of(umSupervisor(), outroSupervisor()))
            .coordenadores(Set.of(outroCoordenador()))
            .situacao(ESituacao.I)
            .siteNacional(Eboolean.F)
            .build();
    }

    public static SiteRequest requestUpdateSite() {
        return SiteRequest.builder()
            .id(1)
            .supervisoresIds(List.of(11123))
            .nome("Manaus 2")
            .coordenadoresIds(List.of(11122))
            .cidadesIds(List.of(1500))
            .estadosIds(List.of(200))
            .build();
    }

    public static Page<Site> umaPageDeSites() {
        return new PageImpl<>(List.of(
            umSiteSaoPaulo(),
            umSiteRioBranco(),
            umSiteManaus(),
            umSiteInativo()
        ));
    }


    public static Site umSiteCompleto() {
        return Site
            .builder()
            .id(1)
            .nome("teste um site")
            .timeZone(ETimeZone.BRT)
            .estados(Set.of())
            .cidades(Set.of())
            .supervisores(Set.of())
            .coordenadores(Set.of())
            .situacao(ESituacao.A)
            .discadoraId(2)
            .siteNacional(Eboolean.V)
            .build();
    }

    public static SiteRequest umSiteRequest() {
        return SiteRequest.builder()
            .nome("Arapa")
            .timeZone(ETimeZone.BRT)
            .estadosIds(List.of(1))
            .coordenadoresIds(List.of(102))
            .supervisoresIds(List.of(300))
            .cidadesIds(List.of(4498))
            .build();
    }

    public static SiteRequest umSiteRequestEmpty() {
        return SiteRequest.builder()
            .nome("Arapa")
            .timeZone(ETimeZone.BRT)
            .estadosIds(List.of())
            .coordenadoresIds(List.of())
            .supervisoresIds(List.of())
            .cidadesIds(List.of())
            .build();
    }

    public static SiteResponse umSiteResponse() {
        return SiteResponse.builder()
            .id(1)
            .nome("teste site detalhe")
            .timeZone(ETimeZone.BRT)
            .situacao(ESituacao.A)
            .coordenadoresIds(Set.of(4))
            .supervisoresIds(Set.of(2))
            .estadosIds(Set.of(1))
            .cidadesIds(Set.of(3))
            .discadoraId(2)
            .siteNacional(true)
            .build();
    }
}
