package br.com.xbrain.autenticacao.modules.site.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SiteCidadeResponse {

    private Integer siteId;
    private String siteNome;
    private Integer codigoCidadeDbm;
    private Integer ddd;
    private Integer cidadeId;
    private String cidadeNome;
    private Integer ufId;
    private String ufNome;

    public SiteCidadeResponse(Integer siteId, String siteNome,
                               Integer cidadeId, String cidadeNome,
                               Integer ufId, String ufNome) {
        this.siteId = siteId;
        this.siteNome = siteNome;
        this.cidadeId = cidadeId;
        this.cidadeNome = cidadeNome;
        this.ufId = ufId;
        this.ufNome = ufNome;
    }

    public SiteCidadeResponse(Integer siteId, String siteNome,
                              Integer codigoCidadeDbm,
                              Integer cidadeId, String cidadeNome,
                              Integer ufId, String ufNome) {
        this.siteId = siteId;
        this.siteNome = siteNome;
        this.codigoCidadeDbm = codigoCidadeDbm;
        this.cidadeId = cidadeId;
        this.cidadeNome = cidadeNome;
        this.ufId = ufId;
        this.ufNome = ufNome;
    }

    public SiteCidadeResponse(Integer siteId, String siteNome,
                              Integer cidadeId, String cidadeNome,
                              Integer ufId, String ufNome,
                              Integer ddd) {
        this.siteId = siteId;
        this.siteNome = siteNome;
        this.ddd = ddd;
        this.cidadeId = cidadeId;
        this.cidadeNome = cidadeNome;
        this.ufId = ufId;
        this.ufNome = ufNome;
    }
}
