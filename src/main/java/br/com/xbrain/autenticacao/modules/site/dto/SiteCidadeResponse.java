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
    private Integer cidadeId;
    private String cidadeNome;
    private Integer ufId;
    private String ufNome;
}
