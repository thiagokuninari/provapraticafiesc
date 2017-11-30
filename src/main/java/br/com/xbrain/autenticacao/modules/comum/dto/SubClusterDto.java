package br.com.xbrain.autenticacao.modules.comum.dto;

import lombok.Data;

@Data
public class SubClusterDto {

    private Integer id;
    private String nome;

    public SubClusterDto(Integer id, String nome) {
        this.id = id;
        this.nome = nome;
    }
}
