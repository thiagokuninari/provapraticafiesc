package br.com.xbrain.autenticacao.modules.parceirosonline.dto;

import lombok.Data;

@Data
public class UsuarioAgenteAutorizadoResponse {

    private Integer id;
    private String nome;
    private String email;

    public UsuarioAgenteAutorizadoResponse() {
    }

    public UsuarioAgenteAutorizadoResponse(Integer id) {
        this.id = id;
    }
}
