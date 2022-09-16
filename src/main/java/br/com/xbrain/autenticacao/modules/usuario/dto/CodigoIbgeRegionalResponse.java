package br.com.xbrain.autenticacao.modules.usuario.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CodigoIbgeRegionalResponse {

    private Integer cidadeId;
    private String cidadeNome;
    private String codigoIbge;
    private Integer regionalId;
    private String regionalNome;
    private Integer ufId;
    private String estadoNome;
    private String uf;
}
