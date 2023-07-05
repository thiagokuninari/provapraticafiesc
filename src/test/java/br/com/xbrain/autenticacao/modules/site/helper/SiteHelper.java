package br.com.xbrain.autenticacao.modules.site.helper;

import br.com.xbrain.autenticacao.modules.comum.enums.ESituacao;
import br.com.xbrain.autenticacao.modules.comum.enums.ETimeZone;
import br.com.xbrain.autenticacao.modules.comum.enums.Eboolean;
import br.com.xbrain.autenticacao.modules.site.dto.SiteRequest;
import br.com.xbrain.autenticacao.modules.site.dto.SiteResponse;
import br.com.xbrain.autenticacao.modules.site.model.Site;

import java.util.List;
import java.util.Set;

public class SiteHelper {

    public static Site umSite(Integer id, String nome) {
        return Site
            .builder()
            .id(id)
            .nome(nome)
            .build();
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
