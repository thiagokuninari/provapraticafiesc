package br.com.xbrain.autenticacao.modules.usuario.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CidadeSiteResponse {

    private Integer id;
    private Integer siteId;
    private String nome;
    private String uf;

}