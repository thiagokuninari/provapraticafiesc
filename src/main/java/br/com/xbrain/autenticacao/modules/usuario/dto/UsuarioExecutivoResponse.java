package br.com.xbrain.autenticacao.modules.usuario.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UsuarioExecutivoResponse {

    private Integer id;
    private String email;
    private String nome;
}
