package br.com.xbrain.autenticacao.modules.usuario.dto;

import lombok.Data;

@Data
public class UsuarioDisponivelResponse {

    private Integer id;
    private String nome;
    private boolean isHibrido;

    public UsuarioDisponivelResponse(Integer id, String nome, boolean isHibrido) {
        this.id = id;
        this.nome = nome;
        this.isHibrido = isHibrido;
    }
}
