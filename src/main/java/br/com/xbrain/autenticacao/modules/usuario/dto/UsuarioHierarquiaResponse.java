package br.com.xbrain.autenticacao.modules.usuario.dto;

import lombok.Data;

@Data
public class UsuarioHierarquiaResponse {

    private Integer id;

    private String nome;

    public UsuarioHierarquiaResponse(Integer id, String nome) {
        this.id = id;
        this.nome = nome;
    }

}
