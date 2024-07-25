package br.com.xbrain.autenticacao.modules.usuario.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClusterizacaoDto {

    private Integer cidadeId;
    private String cidadeNome;
    private Integer ufId;
    private String ufNome;
    private Integer regionalId;
    private String regionalNome;
}
