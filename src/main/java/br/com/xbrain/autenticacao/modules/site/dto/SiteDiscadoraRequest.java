package br.com.xbrain.autenticacao.modules.site.dto;

import lombok.Data;

import java.util.List;

@Data
public class SiteDiscadoraRequest {

    private Integer discadoraId;
    private List<Integer> sites;
    private Integer siteId;
}
