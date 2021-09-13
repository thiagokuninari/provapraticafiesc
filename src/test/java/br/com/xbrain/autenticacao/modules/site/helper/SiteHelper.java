package br.com.xbrain.autenticacao.modules.site.helper;

import br.com.xbrain.autenticacao.modules.site.model.Site;

public class SiteHelper {

    public static Site umSite(Integer id, String nome) {
        return Site
            .builder()
            .id(id)
            .nome(nome)
            .build();
    }
}
