package br.com.xbrain.autenticacao.modules.comum.dto;

import lombok.Data;

@Data
public class GrupoDto {

    private Integer id;
    private String nome;

    public GrupoDto(Integer id, String nome) {
        this.id = id;
        this.nome = nome;
    }
}
