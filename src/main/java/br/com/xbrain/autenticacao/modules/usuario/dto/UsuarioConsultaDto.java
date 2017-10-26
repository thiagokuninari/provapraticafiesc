package br.com.xbrain.autenticacao.modules.usuario.dto;

import br.com.xbrain.autenticacao.modules.usuario.model.Usuario;
import lombok.Data;

@Data
public class UsuarioConsultaDto {

    private int id;
    private String nome;

    public UsuarioConsultaDto(Usuario usuario) {
        this.id = usuario.getId();
        this.nome = usuario.getNome();
    }
}
