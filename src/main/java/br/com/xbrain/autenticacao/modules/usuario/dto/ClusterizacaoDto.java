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
    private Integer subclusterId;
    private String subclusterNome;
    private Integer clusterId;
    private String clusterNome;
    private Integer grupoId;
    private String grupoNome;
    private Integer regionalId;
    private String regionalNome;
}
