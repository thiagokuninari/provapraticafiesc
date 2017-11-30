package br.com.xbrain.autenticacao.modules.usuario.dto;

import lombok.Data;

@Data
public class CidadeAutoCompleteDto {

    private Integer value;
    private String text;

    public CidadeAutoCompleteDto() {
    }

    public CidadeAutoCompleteDto(Integer id, String nome, String uf) {
        this.value = id;
        this.text = nome + " - " + uf;
    }
}
