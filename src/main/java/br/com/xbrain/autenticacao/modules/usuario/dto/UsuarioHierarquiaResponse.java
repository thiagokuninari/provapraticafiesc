package br.com.xbrain.autenticacao.modules.usuario.dto;

import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import lombok.Data;

@Data
public class UsuarioHierarquiaResponse {

    private Integer id;

    private String nome;

    public UsuarioHierarquiaResponse(Usuario usuario) {
        this.id = usuario.getId();
        this.nome = usuario.getNome();
    }

}
