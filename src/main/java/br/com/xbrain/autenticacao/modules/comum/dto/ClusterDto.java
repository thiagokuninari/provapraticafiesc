package br.com.xbrain.autenticacao.modules.comum.dto;

import lombok.Data;

@Data
public class ClusterDto {

    private Integer id;
    private String nome;

    public ClusterDto(Integer id, String nome) {
        this.id = id;
        this.nome = nome;
    }
}
