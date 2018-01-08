package br.com.xbrain.autenticacao.modules.usuario.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UsuarioCidadeDto {

    private Integer idRegional;
    private String nomeRegional;
    private Integer idGrupo;
    private String nomeGrupo;
    private Integer idCluster;
    private String nomeCluster;
    private Integer idSubCluster;
    private Integer nomeSubCluster;
    private Integer idCidade;
    private String nomeCidade;
    private LocalDateTime dataAtual;

}
