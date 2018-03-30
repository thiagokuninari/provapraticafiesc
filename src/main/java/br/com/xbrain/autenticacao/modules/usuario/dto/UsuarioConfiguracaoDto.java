package br.com.xbrain.autenticacao.modules.usuario.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UsuarioConfiguracaoDto {

    private Integer usuario;
    private String usuarioNome;
    private Integer usuarioCadastro;
    private LocalDateTime cadastro;
    private Integer ramal;

}
